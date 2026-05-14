//Ilya Pushkarev

import java.util.*;

public class Bellman {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int[][] matrix = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }

        Graph<Integer> graph = new Graph<>();
        Vertex<Integer> superNode = graph.addVertex(0);
        for (int i = 1; i <= N; i++) {
            graph.addVertex(i);
        }

        for (int i = 1; i <= N; i++) {
            Vertex<Integer> to = graph.vertices.get(i);
            graph.addEdge(superNode, to, 0);
        }

        for (int i = 1; i <= N; i++) {
            Vertex<Integer> from = graph.vertices.get(i);
            for (int j = 1; j <= N; j++) {
                int weight = matrix[i-1][j-1];
                if (weight != 0) {
                    Vertex<Integer> to = graph.vertices.get(j);
                    graph.addEdge(from, to, weight);
                }
            }
        }

        Map<Vertex<Integer>, Integer> distance = new HashMap<>();
        Map<Vertex<Integer>, Vertex<Integer>> predecessor = new HashMap<>();
        boolean hasNegativeCycle = graph.IlyaPushkarev_bellman_ford(superNode, distance, predecessor);

        if (!hasNegativeCycle) {
            System.out.println("NO");
            return;
        }

        Vertex<Integer> v = null;
        for (Edge<Integer> edge : graph.edges) {
            Vertex<Integer> u = edge.from;
            Vertex<Integer> to = edge.to;
            int weight = edge.weight;

            if (distance.get(u) != Integer.MAX_VALUE && distance.get(u) + weight < distance.get(to)) {
                v = to;
                break;
            }
        }

        Vertex<Integer> x = v;
        for (int i = 0; i < N; i++) {
            x = predecessor.get(x);
        }

        List<Vertex<Integer>> cycle = new ArrayList<>();
        Vertex<Integer> current = x;
        do {
            cycle.add(current);
            current = predecessor.get(current);
        } while (current != x);

        List<Vertex<Integer>> result = new ArrayList<>();
        for (int i = 0; i < cycle.size(); i++) {
            if (i == 0 || !cycle.get(i).equals(cycle.get(0))) {
                result.add(cycle.get(i));
            }
        }
        Collections.reverse(result);

        System.out.println("YES");
        System.out.println(result.size());
        for (Vertex<Integer> node : result) {
            System.out.print(node.label + " ");
        }
        System.out.println();
    }
}

class Vertex<T> {
    T label;
    List<Edge<T>> edges;

    public Vertex(T label) {
        this.label = label;
        this.edges = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return Objects.equals(label, vertex.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}

class Edge<T> {
    Vertex<T> from;
    Vertex<T> to;
    int weight;

    public Edge(Vertex<T> from, Vertex<T> to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}

class Graph<T> {
    List<Vertex<T>> vertices = new ArrayList<>();
    List<Edge<T>> edges = new ArrayList<>();

    public Vertex<T> addVertex(T label) {
        Vertex<T> v = new Vertex<>(label);
        vertices.add(v);
        return v;
    }

    public Edge<T> addEdge(Vertex<T> from, Vertex<T> to, int weight) {
        Edge<T> e = new Edge<>(from, to, weight);
        from.edges.add(e);
        edges.add(e);
        return e;
    }

    public boolean IlyaPushkarev_bellman_ford(Vertex<T> source, Map<Vertex<T>, Integer> distance, Map<Vertex<T>, Vertex<T>> predecessor) {
        int numVertices = vertices.size();
        for (Vertex<T> v : vertices) {
            distance.put(v, Integer.MAX_VALUE);
            predecessor.put(v, null);
        }
        distance.put(source, 0);

        for (int i = 0; i < numVertices - 1; i++) {
            for (Edge<T> edge : edges) {
                Vertex<T> u = edge.from;
                Vertex<T> v = edge.to;
                int weight = edge.weight;

                if (distance.get(u) != Integer.MAX_VALUE && distance.get(u) + weight < distance.get(v)) {
                    distance.put(v, distance.get(u) + weight);
                    predecessor.put(v, u);
                }
            }
        }

        for (Edge<T> edge : edges) {
            Vertex<T> u = edge.from;
            Vertex<T> v = edge.to;
            int weight = edge.weight;

            if (distance.get(u) != Integer.MAX_VALUE && distance.get(u) + weight < distance.get(v)) {
                return true;
            }
        }

        return false;
    }
}