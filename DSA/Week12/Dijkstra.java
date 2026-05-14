//Ilya Pushkarev

import java.util.*;

public class Dijkstra {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();

        Graph<Integer> graph = new Graph<>();
        for (int i = 0; i <= n; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int d = sc.nextInt();
            int b = sc.nextInt();
            graph.addEdge(u, v, d, b);
        }

        int s = sc.nextInt();
        int t = sc.nextInt();

        long[] result = graph.IlyaPushkarev_dijkstra(s, t);
        System.out.println(result[0] + " " + result[1]);
    }
}

class Vertex<T> {
    T label;
    List<Edge<T>> edges;

    public Vertex(T label) {
        this.label = label;
        this.edges = new LinkedList<>();
    }
}

class Edge<T> {
    Vertex<T> from;
    Vertex<T> to;
    int distance;
    int bandwidth;

    public Edge(Vertex<T> from, Vertex<T> to, int distance, int bandwidth) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.bandwidth = bandwidth;
    }
}

class Graph<T> {
    private Map<T, Vertex<T>> vertexMap = new HashMap<>();
    private List<Vertex<T>> vertices = new LinkedList<>();
    private List<Edge<T>> edges = new LinkedList<>();

    public void addVertex(T label) {
        if (!vertexMap.containsKey(label)) {
            Vertex<T> v = new Vertex<>(label);
            vertexMap.put(label, v);
            vertices.add(v);
        }
    }

    public Vertex<T> getVertex(T label) {
        return vertexMap.get(label);
    }

    public void addEdge(T fromLabel, T toLabel, int distance, int bandwidth) {
        Vertex<T> from = getVertex(fromLabel);
        Vertex<T> to = getVertex(toLabel);
        if (from != null && to != null) {
            Edge<T> edge1 = new Edge<>(from, to, distance, bandwidth);
            edges.add(edge1);
            from.edges.add(edge1);

            Edge<T> edge2 = new Edge<>(to, from, distance, bandwidth);
            edges.add(edge2);
            to.edges.add(edge2);
        }
    }

    public long[] IlyaPushkarev_dijkstra(T sourceLabel, T targetLabel) {
        Map<T, Long> distance = new HashMap<>();
        Map<T, Integer> bandwidth = new HashMap<>();
        for (Vertex<T> v : vertices) {
            distance.put(v.label, Long.MAX_VALUE);
            bandwidth.put(v.label, 0);
        }
        distance.put(sourceLabel, 0L);
        bandwidth.put(sourceLabel, Integer.MAX_VALUE);

        PriorityQueue<PQEntry<T>> pq = new PriorityQueue<>();
        pq.add(new PQEntry<>(0L, Integer.MAX_VALUE, sourceLabel));

        while (!pq.isEmpty()) {
            PQEntry<T> entry = pq.poll();
            T uLabel = entry.vertex;
            long distU = entry.distance;
            int bandU = entry.bandwidth;

            if (distU > distance.get(uLabel) || (distU == distance.get(uLabel) && bandU < bandwidth.get(uLabel))) {
                continue;
            }

            Vertex<T> uVertex = getVertex(uLabel);
            if (uVertex == null) continue;

            for (Edge<T> edge : uVertex.edges) {
                T vLabel = edge.to.label;
                long newDist = distU + edge.distance;
                int newBand = Math.min(bandU, edge.bandwidth);

                long currentDist = distance.get(vLabel);
                int currentBand = bandwidth.get(vLabel);

                if (newDist < currentDist) {
                    distance.put(vLabel, newDist);
                    bandwidth.put(vLabel, newBand);
                    pq.add(new PQEntry<>(newDist, newBand, vLabel));
                } else if (newDist == currentDist && newBand > currentBand) {
                    bandwidth.put(vLabel, newBand);
                    pq.add(new PQEntry<>(newDist, newBand, vLabel));
                }
            }
        }

        long[] result = new long[2];
        Long distResult = distance.get(targetLabel);
        Integer bandResult = bandwidth.get(targetLabel);
        if (distResult == null || distResult == Long.MAX_VALUE) {
            result[0] = 0;
            result[1] = 0;
        } else {
            result[0] = distResult;
            result[1] = bandResult;
        }
        return result;
    }

    private class PQEntry<V> implements Comparable<PQEntry<V>> {
        long distance;
        int bandwidth;
        V vertex;

        public PQEntry(long distance, int bandwidth, V vertex) {
            this.distance = distance;
            this.bandwidth = bandwidth;
            this.vertex = vertex;
        }

        @Override
        public int compareTo(PQEntry<V> other) {
            if (this.distance != other.distance) {
                return Long.compare(this.distance, other.distance);
            } else {
                return Integer.compare(other.bandwidth, this.bandwidth);
            }
        }
    }
}