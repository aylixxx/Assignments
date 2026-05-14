// Ilya Pushkarev

import java.util.*;

public class Springfield {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = Integer.parseInt(scanner.nextLine().trim());

        Graph<String, Double> graph = new Graph<>();

        for (int i = 0; i < N; i++) {
            String line = scanner.nextLine().trim();
            String[] parts = line.split(" ");
            String cmd = parts[0];

            switch (cmd) {
                case "ADD":
                    String name = parts[1];
                    double tax = Double.parseDouble(parts[2]);
                    graph.addVertex(name, tax);
                    break;
                case "CONNECT":
                    String stall1 = parts[1];
                    String stall2 = parts[2];
                    double distance = Double.parseDouble(parts[3]);
                    graph.addEdge(stall1, stall2, distance);
                    break;
                case "PRINT_MIN":
                    List<Edge<String, Double>> mstEdges = graph.IlyaPushkarev_mst_prim();
                    Set<String> printed = new HashSet<>();
                    List<String> outputList = new ArrayList<>();
                    for (Edge<String, Double> edge : mstEdges) {
                        String u = edge.u.label.toString();
                        String v = edge.v.label.toString();
                        String forward = u + ":" + v;
                        String backward = v + ":" + u;
                        if (!printed.contains(forward) && !printed.contains(backward)) {
                            outputList.add(forward);
                            printed.add(forward);
                            printed.add(backward);
                        }
                    }
                    System.out.println(String.join(" ", outputList));
                    break;
                default:
                    break;
            }
        }
    }
}

class Vertex<V> {
    V label;
    double tax;

    public Vertex(V label, double tax) {
        this.label = label;
        this.tax = tax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return label.equals(vertex.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}

class Edge<V, E> {
    Vertex<V> u;
    Vertex<V> v;
    E label;

    public Edge(Vertex<V> u, Vertex<V> v, E label) {
        this.u = u;
        this.v = v;
        this.label = label;
    }

    public double getWeight() {
        double distance = ((Number) label).doubleValue();
        return distance / (u.tax + v.tax);
    }

    public Vertex<V> getOther(Vertex<V> current) {
        if (current.equals(u)) {
            return v;
        } else if (current.equals(v)) {
            return u;
        }
        return current;
    }
}

class Graph<V, E> {
    private final List<Vertex<V>> vertices;
    private final List<Edge<V, E>> edges;
    private final Map<Vertex<V>, List<Edge<V, E>>> adjacencyList;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        adjacencyList = new HashMap<>();
    }

    public void addVertex(V label) {
        addVertex(label, 0.0);
    }

    public void addVertex(V label, double tax) {
        Vertex<V> v = new Vertex<>(label, tax);
        if (!vertices.contains(v)) {
            vertices.add(v);
            adjacencyList.put(v, new LinkedList<>());
        }
    }

    public Vertex<V> getVertex(V label) {
        for (Vertex<V> v : vertices) {
            if (v.label.equals(label)) {
                return v;
            }
        }
        return null;
    }

    public void addEdge(V uLabel, V vLabel, E distance) {
        if (getVertex(uLabel) == null) {
            addVertex(uLabel);
        }
        if (getVertex(vLabel) == null) {
            addVertex(vLabel);
        }
        Vertex<V> u = getVertex(uLabel);
        Vertex<V> v = getVertex(vLabel);
        Edge<V, E> edge = new Edge<>(u, v, distance);
        edges.add(edge);
        adjacencyList.get(u).add(edge);
        adjacencyList.get(v).add(edge);
    }

    public List<Edge<V, E>> IlyaPushkarev_mst_prim() {
        List<Edge<V, E>> mst = new ArrayList<>();
        Set<Vertex<V>> visited = new HashSet<>();
        MinHeap<Edge<V, E>> heap = new MinHeap<>();

        for (Vertex<V> vertex : vertices) {
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                for (Edge<V, E> edge : adjacencyList.get(vertex)) {
                    heap.insert(edge, edge.getWeight());
                }

                while (!heap.isEmpty()) {
                    Edge<V, E> edge = heap.extractMin();
                    if (edge == null) break;

                    Vertex<V> u = edge.u;
                    Vertex<V> v = edge.v;
                    Vertex<V> next = null;

                    if (visited.contains(u) && !visited.contains(v)) {
                        next = v;
                    } else if (visited.contains(v) && !visited.contains(u)) {
                        next = u;
                    } else {
                        continue;
                    }

                    mst.add(edge);
                    visited.add(next);
                    for (Edge<V, E> e : adjacencyList.get(next)) {
                        Vertex<V> other = e.getOther(next);
                        if (!visited.contains(other)) {
                            heap.insert(e, e.getWeight());
                        }
                    }
                }
            }
        }

        return mst;
    }

    private static class MinHeap<T> {
        private final List<HeapNode<T>> heap;

        public MinHeap() {
            heap = new ArrayList<>();
        }

        public void insert(T item, double priority) {
            HeapNode<T> node = new HeapNode<>(item, priority);
            heap.add(node);
            siftUp(heap.size() - 1);
        }

        public T extractMin() {
            if (heap.isEmpty()) {
                return null;
            }
            HeapNode<T> min = heap.get(0);
            HeapNode<T> last = heap.remove(heap.size() - 1);
            if (!heap.isEmpty()) {
                heap.set(0, last);
                siftDown(0);
            }
            return min.item;
        }

        public boolean isEmpty() {
            return heap.isEmpty();
        }

        private void siftUp(int index) {
            while (index > 0) {
                int parent = (index - 1) / 2;
                if (heap.get(parent).priority <= heap.get(index).priority) {
                    break;
                }
                swap(parent, index);
                index = parent;
            }
        }

        private void siftDown(int index) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < heap.size() && heap.get(left).priority < heap.get(smallest).priority) {
                smallest = left;
            }
            if (right < heap.size() && heap.get(right).priority < heap.get(smallest).priority) {
                smallest = right;
            }

            if (smallest != index) {
                swap(index, smallest);
                siftDown(smallest);
            }
        }

        private void swap(int i, int j) {
            HeapNode<T> temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);
        }

        private static class HeapNode<T> {
            T item;
            double priority;

            HeapNode(T item, double priority) {
                this.item = item;
                this.priority = priority;
            }
        }
    }
}