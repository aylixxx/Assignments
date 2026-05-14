//Ilya Pushkarev

import java.util.*;

public class collector {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int r = sc.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = sc.nextInt();
        }
        Set<Integer> roots = new HashSet<>();
        for (int i = 0; i < r; i++) {
            roots.add(sc.nextInt());
        }

        Graph<Integer, Void> graph = new Graph<>();
        for (int i = 0; i < n; i++) {
            graph.addVertex(i);
        }
        for (int i = 0; i < n; i++) {
            int ptr = a[i];
            if (ptr >= 0 && ptr < n) {
                Graph<Integer, Void>.Vertex<Integer> src = graph.getVertex(i);
                Graph<Integer, Void>.Vertex<Integer> dest = graph.getVertex(ptr);
                graph.addEdge(null, src, dest);
            }
        }

        List<Graph<Integer, Void>.Vertex<Integer>> rootVertices = new ArrayList<>();
        for (int root : roots) {
            Graph<Integer, Void>.Vertex<Integer> v = graph.getVertex(root);
            if (v != null) {
                rootVertices.add(v);
            }
        }

        Set<Integer> visitedSet = graph.IlyaPushkarev_bfs(rootVertices);
        boolean[] visitedArray = new boolean[n];
        for (int idx : visitedSet) {
            visitedArray[idx] = true;
        }

        List<int[]> garbageBlocks = new ArrayList<>();
        int currentStart = -1;
        int currentCount = 0;
        for (int i = 0; i < n; i++) {
            if (!visitedArray[i]) {
                if (currentStart == -1) {
                    currentStart = i;
                }
                currentCount++;
            } else {
                if (currentStart != -1) {
                    garbageBlocks.add(new int[]{currentStart, currentCount});
                    currentStart = -1;
                    currentCount = 0;
                }
            }
        }
        if (currentStart != -1) {
            garbageBlocks.add(new int[]{currentStart, currentCount});
        }

        System.out.println(garbageBlocks.size());
        garbageBlocks.sort(Comparator.comparingInt(block -> block[0]));
        for (int[] block : garbageBlocks) {
            System.out.println(block[0] + " " + block[1]);
        }
    }
}

class Graph<V, E> {
    private LinkedList<Vertex<V>> vertices = new LinkedList<>();
    private LinkedList<Edge<V, E>> edges = new LinkedList<>();

    public Vertex<V> addVertex(V label) {
        Vertex<V> existing = getVertex(label);
        if (existing != null) {
            return existing;
        }
        Vertex<V> newVertex = new Vertex<>(label);
        vertices.add(newVertex);
        return newVertex;
    }

    public Vertex<V> getVertex(V label) {
        for (Vertex<V> v : vertices) {
            if (v.label.equals(label)) {
                return v;
            }
        }
        return null;
    }

    public void addEdge(E label, Vertex<V> source, Vertex<V> destination) {
        Edge<V, E> edge = new Edge<>(label, source, destination);
        edges.add(edge);
        source.outgoingEdges.add(edge);
    }

    public Set<V> IlyaPushkarev_bfs(List<Vertex<V>> startVertices) {
        Set<V> visited = new HashSet<>();
        Queue<Vertex<V>> queue = new LinkedList<>();
        for (Vertex<V> v : startVertices) {
            if (visited.add(v.label)) {
                queue.add(v);
            }
        }
        while (!queue.isEmpty()) {
            Vertex<V> current = queue.poll();
            for (Edge<V, E> edge : current.outgoingEdges) {
                Vertex<V> neighbor = edge.destination;
                if (visited.add(neighbor.label)) {
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }

    public List<V> IlyaPushkarev_topsort() {
        LinkedList<V> result = new LinkedList<>();
        Set<Vertex<V>> visited = new HashSet<>();
        Set<Vertex<V>> visiting = new HashSet<>();

        for (Vertex<V> vertex : vertices) {
            if (!visited.contains(vertex)) {
                Deque<Object[]> stack = new ArrayDeque<>();
                stack.push(new Object[]{vertex, false});

                while (!stack.isEmpty()) {
                    Object[] entry = stack.pop();
                    Vertex<V> current = (Vertex<V>) entry[0];
                    boolean isProcessed = (Boolean) entry[1];

                    if (isProcessed) {
                        result.addFirst(current.label);
                        visiting.remove(current);
                        visited.add(current);
                    } else {
                        if (visiting.contains(current)) {
                            return null;
                        }
                        if (visited.contains(current)) {
                            continue;
                        }
                        visiting.add(current);
                        stack.push(new Object[]{current, true});

                        List<Vertex<V>> neighbors = new ArrayList<>();
                        for (Edge<V, E> edge : current.outgoingEdges) {
                            neighbors.add(edge.destination);
                        }
                        Collections.reverse(neighbors);
                        for (Vertex<V> neighbor : neighbors) {
                            if (visiting.contains(neighbor)) {
                                return null;
                            }
                            if (!visited.contains(neighbor)) {
                                stack.push(new Object[]{neighbor, false});
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    class Vertex<V> {
        V label;
        LinkedList<Edge<V, E>> outgoingEdges = new LinkedList<>();

        Vertex(V label) {
            this.label = label;
        }
    }

    class Edge<Vert, E> {
        E label;
        Vertex<V> source;
        Vertex<V> destination;

        Edge(E label, Vertex<V> source, Vertex<V> destination) {
            this.label = label;
            this.source = source;
            this.destination = destination;
        }
    }
}