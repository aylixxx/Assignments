import sys
import collections

SIZE = 13
DIRS = [(0, 1), (1, 0), (0, -1), (-1, 0)]

def flush_print(s):
    print(s, flush=True)

def in_bounds(x, y):
    return 0 <= x < SIZE and 0 <= y < SIZE

def calculate_lethal_cells(known, ring_on, has_mithril):
    lethal = set()
    
    for (ex, ey), enemy_type in known['enemies'].items():
        lethal.add((ex, ey))  # Enemy position is always lethal
        
        if enemy_type == 'O':
            radius = 1
            if ring_on or has_mithril:
                radius = 0
            for dx in range(-radius, radius + 1):
                for dy in range(-radius, radius + 1):
                    if abs(dx) + abs(dy) <= radius:  # Manhattan distance
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
                    if max(abs(dx), abs(dy)) <= radius:  # Chebyshev distance
                        nx, ny = ex + dx, ey + dy
                        if in_bounds(nx, ny):
                            lethal.add((nx, ny))
            extension = radius + 1
            for dx, dy in [(extension,0), (-extension,0), (0,extension), (0,-extension)]:
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
                extension = radius + 1
                for delta in [-1, 0, 1]:
                    for nx, ny in [(ex+extension, ey+delta), (ex-extension, ey+delta)]:
                        if in_bounds(nx, ny):
                            lethal.add((nx, ny))  
                    for nx, ny in [(ex+delta, ey+extension), (ex+delta, ey-extension)]:
                        if in_bounds(nx, ny):
                            lethal.add((nx, ny))
    
    lethal.update(known['danger_zones'])
    return lethal

def manhattan_distance(pos1, pos2):
    return abs(pos1[0] - pos2[0]) + abs(pos1[1] - pos2[1])

def is_reachable(pos, goal, known, ring_on, has_mithril):
    # BFS to check if goal is reachable
    queue = collections.deque([(pos, ring_on)])
    visited = {(pos, ring_on)}
    lethal = calculate_lethal_cells(known, ring_on, has_mithril)
    enemy_cells = set(known['enemies'].keys())

    while queue:
        curr_pos, curr_ring = queue.popleft()
        if curr_pos == goal:
            return True
            
        # Try toggling ring
        new_ring = not curr_ring
        new_lethal = calculate_lethal_cells(known, new_ring, has_mithril)
        if curr_pos not in new_lethal and \
           curr_pos not in enemy_cells and \
           (curr_pos, new_ring) not in visited:
            queue.append((curr_pos, new_ring))
            visited.add((curr_pos, new_ring))
        
        # Try moving to neighbors
        for dx, dy in DIRS:
            nx, ny = curr_pos[0] + dx, curr_pos[1] + dy
            new_pos = (nx, ny)
            
            if not in_bounds(nx, ny) or \
               new_pos in lethal or \
               new_pos in enemy_cells or \
               (new_pos, curr_ring) in visited:
                continue
                
            queue.append((new_pos, curr_ring))
            visited.add((new_pos, curr_ring))
            
    return False

def backtracking_search(start, goal, known, start_ring, start_mithril, max_depth=100):
    if not is_reachable(start, goal, known, start_ring, start_mithril):
        return None
        
    lethal_cache = {}
    
    def get_lethal(ring, mithril):
        key = (ring, mithril)
        if key not in lethal_cache:
            lethal_cache[key] = calculate_lethal_cells(known, ring, mithril)
        return lethal_cache[key]
    
    def backtrack(pos, ring_on, has_mithril, depth, path, visited, best_path, best_len):
        MAX_VISITED = 10000
        if len(visited) > MAX_VISITED:
            best_len[0] = -1
            return

        if depth >= best_len[0] or depth >= max_depth:
            return

        if pos == goal:  # Found path to goal
            if depth < best_len[0]:
                best_len[0] = depth
                best_path[0] = path[:]
            return

        # Prune if current path + heuristic is worse than best
        if depth + manhattan_distance(pos, goal) >= best_len[0]:
            return

        lethal = get_lethal(ring_on, has_mithril)

        moves = []

        # Decide when to try toggling ring
        curr_dist = manhattan_distance(pos, goal)
        should_try_toggle = curr_dist <= 5 or \
                           pos in lethal or \
                           pos in get_lethal(not ring_on, has_mithril)

        if should_try_toggle and \
           pos not in known['enemies'] and \
           (pos, not ring_on, has_mithril) not in visited:
            moves.append(('toggle', pos, not ring_on, has_mithril))

        # Sort directions towards goal for better performance
        dx_to_goal = goal[0] - pos[0]
        dy_to_goal = goal[1] - pos[1]

        sorted_dirs = []
        for dx, dy in DIRS:
            priority = 0
            if (dx > 0 and dx_to_goal > 0) or (dx < 0 and dx_to_goal < 0):
                priority += 1
            if (dy > 0 and dy_to_goal > 0) or (dy < 0 and dy_to_goal < 0):
                priority += 1
            sorted_dirs.append((priority, (dx, dy)))

        sorted_dirs.sort(reverse=True)

        # Generate possible moves
        for _, (dx, dy) in sorted_dirs:
            new_x, new_y = pos[0] + dx, pos[1] + dy
            new_pos = (new_x, new_y)

            if not in_bounds(new_x, new_y) or \
               new_pos in lethal or \
               new_pos in known['enemies']:
                continue

            new_mithril = has_mithril or (known['mithril'] == new_pos)
            if (new_pos, ring_on, new_mithril) not in visited:
                moves.append(('move', new_pos, ring_on, new_mithril))

        # Try all possible moves
        for move_type, new_pos, new_ring, new_mithril in moves:
            visited.add((new_pos, new_ring, new_mithril))

            if move_type == 'toggle':
                new_path = path + [('toggle',)]
            else:
                new_path = path + [('move', new_pos)]

            backtrack(new_pos, new_ring, new_mithril, depth + 1,
                     new_path, visited, best_path, best_len)

            if best_len[0] == -1:
                return

            visited.remove((new_pos, new_ring, new_mithril))
    
    visited = {(start, start_ring, start_mithril)}
    best_path = [None]
    best_len = [max_depth + 1]
    
    backtrack(start, start_ring, start_mithril, 0, [], visited, best_path, best_len)
    return best_path[0]

def parse_perception(known):
    try:
        first_line = sys.stdin.readline().strip()
        if not first_line:
            return False
            
        parts = first_line.split()
        
        # Handle Mount Doom coordinates from Gollum
        if len(parts) == 2 and known['gollum_found'] and known['mount_doom'] is None:
            try:
                x, y = int(parts[0]), int(parts[1])
                known['mount_doom'] = (x, y)
                
                # Read additional perception data
                count_line = sys.stdin.readline().strip()
                if count_line:
                    count = int(count_line)
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
                return True
            except ValueError:
                pass
        
        count = int(parts[0])
        
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
                    if not known['gollum_found']:
                        known['gollum_found'] = True
                elif entity_type == 'M':
                    known['mount_doom'] = (x,y)
            except:
                continue
                
        return True
    except Exception as e:
        return False

def find_backtracking_path(current_pos, target_pos, known, ring_on, has_mithril):
    for depth_limit in [50, 100, 200, 300]:
        path = backtracking_search(current_pos, target_pos, known, ring_on, has_mithril, depth_limit)
        if path:
            return path
    return None

def main():
    try:
        # Read initial game setup
        variant = int(sys.stdin.readline().strip())
        gx, gy = map(int, sys.stdin.readline().strip().split())

        known = {
            'danger_zones': set(),
            'enemies': {},
            'mithril': None,
            'gollum_pos': (gx, gy),
            'mount_doom': None,
            'gollum_found': False,
        }

        if not parse_perception(known):
            flush_print("e -1")
            sys.exit(0)

        current_pos = (0, 0)
        ring_on = False
        has_mithril = False
        total_moves = 0
        max_total_moves = 2000

        # Track state history to detect cycles
        state_history = collections.deque(maxlen=100)

        while total_moves < max_total_moves:
            current_state = (current_pos, ring_on, has_mithril, known['gollum_found'])

            # Detect and break cycles
            if current_state in state_history:
                new_ring = not ring_on
                new_lethal = calculate_lethal_cells(known, new_ring, has_mithril)
                enemy_cells = set(known['enemies'].keys())

                if current_pos not in new_lethal and current_pos not in enemy_cells:
                    ring_on = new_ring
                    if ring_on:
                        flush_print("r")
                    else:
                        flush_print("rr")
                    if not parse_perception(known):
                        flush_print("e -1")
                        sys.exit(0)
                    continue
                else:
                    flush_print("e -1")
                    sys.exit(0)

            state_history.append(current_state)

            # Check win condition
            if known['gollum_found'] and current_pos == known['mount_doom']:
                flush_print(f"e {total_moves}")
                sys.exit(0)

            # Determine target: Gollum first, then Mount Doom
            if not known['gollum_found']:
                target_pos = known['gollum_pos']
            else:
                if known['mount_doom'] is None:
                    flush_print("e -1")
                    sys.exit(0)
                target_pos = known['mount_doom']

            # Find optimal path
            path = find_backtracking_path(current_pos, target_pos, known, ring_on, has_mithril)

            if path:
                action = path[0]

                if action[0] == 'move':
                    next_pos = action[1]
                    flush_print(f"m {next_pos[0]} {next_pos[1]}")
                    total_moves += 1
                    current_pos = next_pos

                    # Check for item pickup
                    if known['mithril'] == current_pos:
                        has_mithril = True

                    # Check for Gollum encounter
                    if not known['gollum_found'] and current_pos == known['gollum_pos']:
                        known['gollum_found'] = True

                    if not parse_perception(known):
                        if known['gollum_found'] and current_pos == known['mount_doom']:
                            flush_print(f"e {total_moves}")
                            sys.exit(0)
                        else:
                            flush_print("e -1")
                            sys.exit(0)

                elif action[0] == 'toggle':
                    ring_on = not ring_on
                    if ring_on:
                        flush_print("r")
                    else:
                        flush_print("rr")

                    if not parse_perception(known):
                        flush_print("e -1")
                        sys.exit(0)
            else:
                lethal_cells = calculate_lethal_cells(known, ring_on, has_mithril)
                enemy_cells = set(known['enemies'].keys())

                found_move = False
                for dx, dy in DIRS:
                    nx, ny = current_pos[0] + dx, current_pos[1] + dy
                    neighbor = (nx, ny)

                    if (in_bounds(nx, ny) and 
                        neighbor not in lethal_cells and 
                        neighbor not in enemy_cells):

                        flush_print(f"m {nx} {ny}")
                        total_moves += 1
                        current_pos = neighbor

                        if known['mithril'] == current_pos:
                            has_mithril = True

                        if not known['gollum_found'] and current_pos == known['gollum_pos']:
                            known['gollum_found'] = True

                        if not parse_perception(known):
                            if known['gollum_found'] and current_pos == known['mount_doom']:
                                flush_print(f"e {total_moves}")
                                sys.exit(0)
                            else:
                                flush_print("e -1")
                                sys.exit(0)
                        found_move = True
                        break

                if not found_move:
                    flush_print("e -1")
                    sys.exit(0)

        flush_print("e -1")
        sys.exit(0)

    except Exception as e:
        flush_print("e -1")
        sys.exit(0)

if __name__ == "__main__":
    main()