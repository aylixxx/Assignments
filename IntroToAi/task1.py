import sys
import heapq

SIZE = 13
DIRS = [(0,1), (1,0), (0,-1), (-1,0)]  

def flush_print(s):
    print(s, flush=True)

def in_bounds(x, y):
    return 0 <= x < SIZE and 0 <= y < SIZE

def heuristic(a, b):
    return abs(a[0] - b[0]) + abs(a[1] - b[1])

def astar(start, goal, blocked):
    if start == goal:
        return [start]

    open_heap = []
    # heap entries are tuples: (f_score, g_score, position)
    heapq.heappush(open_heap, (heuristic(start, goal), 0, start))
    came_from = {}
    g_score = {start: 0}
    closed = set()

    while open_heap:
        _, g, current = heapq.heappop(open_heap)

        # Skip nodes already expanded with a better path
        if current in closed:
            continue
        closed.add(current)

        # Goal reached: reconstruct path by following came_from
        if current == goal:
            path = [current]
            while current in came_from:
                current = came_from[current]
                path.append(current)
            path.reverse()
            return path

        # Expand neighbors
        for dx, dy in DIRS:
            nx, ny = current[0] + dx, current[1] + dy
            neighbor = (nx, ny)

            # Skip out-of-bounds or explicitly blocked cells
            if not in_bounds(nx, ny) or neighbor in blocked:
                continue

            if neighbor in closed:
                continue

            tentative_g = g + 1

            # If this path to neighbor is better, record it and push to heap
            if neighbor not in g_score or tentative_g < g_score[neighbor]:
                g_score[neighbor] = tentative_g
                f_score = tentative_g + heuristic(neighbor, goal)
                heapq.heappush(open_heap, (f_score, tentative_g, neighbor))
                came_from[neighbor] = current

    return None

def calculate_lethal_cells(known, ring_on, has_mithril):
    lethal = set()
    lethal.update(known['danger_zones'])

    for (ex, ey), enemy_type in known['enemies'].items():
        lethal.add((ex, ey))

        if enemy_type == 'O':
            radius = 1
            if ring_on or has_mithril:
                radius = 0
            for dx in range(-radius, radius + 1):
                for dy in range(-radius, radius + 1):
                    if abs(dx) + abs(dy) <= radius:
                        nx, ny = ex + dx, ey + dy
                        if in_bounds(nx, ny):
                            lethal.add((nx, ny))

        elif enemy_type == 'U':
            radius = 2
            if ring_on or has_mithril:
                radius = 1
            for dx in range(-radius, radius + 1):
                for dy in range(-radius, radius + 1):
                    if abs(dx) + abs(dy) <= radius:
                        nx, ny = ex + dx, ey + dy
                        if in_bounds(nx, ny):
                            lethal.add((nx, ny))

        elif enemy_type == 'N':
            radius = 1
            if ring_on:
                radius = 2
            for dx in range(-radius, radius + 1):
                for dy in range(-radius, radius + 1):
                    if max(abs(dx), abs(dy)) <= radius:
                        nx, ny = ex + dx, ey + dy
                        if in_bounds(nx, ny):
                            lethal.add((nx, ny))
            # ring_on grants additional distant lethal cells in cardinal directions
            if ring_on:
                for dx, dy in [(3,0), (-3,0), (0,3), (0,-3)]:
                    nx, ny = ex + dx, ey + dy
                    if in_bounds(nx, ny):
                        lethal.add((nx, ny))

        elif enemy_type == 'W':
            radius = 2
            for dx in range(-radius, radius + 1):
                for dy in range(-radius, radius + 1):
                    if max(abs(dx), abs(dy)) <= radius:
                        nx, ny = ex + dx, ey + dy
                        if in_bounds(nx, ny):
                            lethal.add((nx, ny))
            if ring_on:
                for dx, dy in [(3,0), (-3,0), (0,3), (0,-3)]:
                    nx, ny = ex + dx, ey + dy
                    if in_bounds(nx, ny):
                        lethal.add((nx, ny))

    return lethal

def parse_perception(known):
    try:
        line = sys.stdin.readline().strip()
        if not line:
            return False

        parts = line.split()

        if len(parts) == 2 and known['mount_doom'] is None:
            try:
                x, y = int(parts[0]), int(parts[1])
                known['mount_doom'] = (x, y)
                if not known['gollum_found']:
                    known['gollum_found'] = True

                next_line = sys.stdin.readline().strip()
                if next_line and next_line != '0':
                    try:
                        count = int(next_line)
                        for _ in range(count):
                            perception_line = sys.stdin.readline().strip()
                            if perception_line:
                                tokens = perception_line.split()
                                if len(tokens) >= 3:
                                    px, py = int(tokens[0]), int(tokens[1])
                                    entity_type = tokens[2]
                                    if entity_type == 'P':
                                        known['danger_zones'].add((px, py))
                                    elif entity_type in ['O','U','N','W']:
                                        known['enemies'][(px, py)] = entity_type
                    except ValueError:
                        pass
                return True
            except Exception:
                pass

        # Generic case: first token is a count of following perception lines
        count = int(parts[0])
        known['danger_zones'] = set()

        for _ in range(count):
            line = sys.stdin.readline().strip()
            if not line:
                break
            tokens = line.split()
            if len(tokens) < 3:
                continue

            try:
                x, y = int(tokens[0]), int(tokens[1])
                entity_type = tokens[2]

                if entity_type == 'P':
                    known['danger_zones'].add((x,y))
                elif entity_type in ['O','U','N','W']:
                    known['enemies'][(x,y)] = entity_type
                elif entity_type == 'C':
                    known['mithril'] = (x,y)
                elif entity_type == 'G':
                    known['gollum_pos'] = (x,y)
                elif entity_type == 'M':
                    known['mount_doom'] = (x,y)
                elif entity_type == 'R':
                    known['ring_on_map'] = (x,y)
            except:
                continue

        return True
    except:
        return False

def safe_move(current_pos, target_pos, known, ring_on, has_mithril):
    # Determine a safe single-step move toward target_pos.
    # If the target cell itself is lethal or no safe path exists, return None.
    blocked_cells = calculate_lethal_cells(known, ring_on, has_mithril)

    if target_pos in blocked_cells:
        return None

    path = astar(current_pos, target_pos, blocked_cells)

    # Return the next cell on the path (one-step move) or None
    if path and len(path) > 1:
        return path[1]
    return None

def main():
    _ = int(sys.stdin.readline().strip())
    gx, gy = map(int, sys.stdin.readline().strip().split())

    known = {
        'danger_zones': set(),
        'enemies': {},
        'mithril': None,
        'gollum_pos': (gx, gy),
        'mount_doom': None,
        'gollum_found': False,
        'ring_on_map': None
    }

    # Read initial perception block
    if not parse_perception(known):
        flush_print("e -1")
        return

    current_pos = (0, 0)
    ring_on = False
    has_mithril = False
    steps = 0
    max_steps = 100

    # Track visited (position, ring, mithril) triples to detect loops
    visited_states = set()

    while steps < max_steps:
        current_state = (current_pos, ring_on, has_mithril)
        if current_state in visited_states:
            flush_print("e -1")
            return
        visited_states.add(current_state)

        # Win condition: reached Mount Doom after Gollum has been found
        if known['gollum_found'] and current_pos == known['mount_doom']:
            flush_print(f"e {steps}")
            return

        # Choose target depending on whether Gollum has been located
        if not known['gollum_found']:
            target_pos = known['gollum_pos']
        else:
            target_pos = known['mount_doom']

        # Compute safe one-step move toward target
        next_step = safe_move(current_pos, target_pos, known, ring_on, has_mithril)

        if next_step is None:
            # If no safe step, try toggling ring (r / rr) and re-perceive
            if not ring_on:
                ring_on = True
                flush_print("r")
                if not parse_perception(known):
                    flush_print("e -1")
                    return
            else:
                ring_on = False
                flush_print("rr")
                if not parse_perception(known):
                    flush_print("e -1")
                    return
        else:
            # Move to the next cell and read new perception
            flush_print(f"m {next_step[0]} {next_step[1]}")
            steps += 1
            current_pos = next_step

            if known['gollum_found'] and current_pos == known['mount_doom']:
                flush_print(f"e {steps}")
                return

            if not parse_perception(known):
                # If we can't read perception, finish depending on current goal
                if known['gollum_found'] and current_pos == known['mount_doom']:
                    flush_print(f"e {steps}")
                    return
                else:
                    flush_print("e -1")
                    return

            # Picking up mithril grants stronger protection
            if known['mithril'] == current_pos:
                has_mithril = True

            # Occasionally clear visited states to allow re-exploration
            if has_mithril or len(visited_states) > 50:
                visited_states.clear()

    flush_print("e -1")

if __name__ == "__main__":
    main()