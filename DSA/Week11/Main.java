// Ilya Pushkarev

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int M = scanner.nextInt();

        Graph<Integer, Integer> graph = new Graph<>();
        for (int i = 1; i <= N; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < M; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            graph.addEdge(u, v, 0);
        }

        graph.IlyaPushkarev_mst_kruskal();;
        DSU dsu = graph.getDSU();

        int components = 0;
        for (int i = 1; i <= N; i++) {
            if (dsu.find(i) == i) {
                components++;
            }
        }

        System.out.println(components);
    }
}

class DSU {
    private final int[] parent;
    private final int[] rank;

    public DSU(int n) {
        parent = new int[n + 1];
        rank = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int x, int y) {
        int xRoot = find(x);
        int yRoot = find(y);
        if (xRoot == yRoot) return;

        if (rank[xRoot] < rank[yRoot]) {
            parent[xRoot] = yRoot;
        } else if (rank[yRoot] < rank[xRoot]) {
            parent[yRoot] = xRoot;
        } else {
            parent[yRoot] = xRoot;
            rank[xRoot]++;
        }
    }
}

class Vertex<V> {
    V label;

    public Vertex(V label) {
        this.label = label;
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
}

class Graph<V, E> {
    private final List<Vertex<V>> vertices;
    private final List<Edge<V, E>> edges;
    private final Map<Vertex<V>, LinkedList<Edge<V, E>>> adjacencyList;
    private DSU dsu;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        adjacencyList = new HashMap<>();
    }

    public void addVertex(V label) {
        Vertex<V> v = new Vertex<>(label);
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

    public void addEdge(V uLabel, V vLabel, E edgeLabel) {
        Vertex<V> u = getVertex(uLabel);
        Vertex<V> v = getVertex(vLabel);
        if (u == null) {
            addVertex(uLabel);
            u = getVertex(uLabel);
        }
        if (v == null) {
            addVertex(vLabel);
            v = getVertex(vLabel);
        }
        Edge<V, E> edge = new Edge<>(u, v, edgeLabel);
        edges.add(edge);
        adjacencyList.get(u).add(edge);
        adjacencyList.get(v).add(edge);
    }

    public void IlyaPushkarev_mst_kruskal() {
        int n = vertices.size();
        dsu = new DSU(n);

        for (Edge<V, E> edge : edges) {
            int u = (Integer) edge.u.label;
            int v = (Integer) edge.v.label;
            if (dsu.find(u) != dsu.find(v)) {
                dsu.union(u, v);
            }
        }
    }

    public DSU getDSU() {
        return dsu;
    }
}