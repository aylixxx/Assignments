//Ilya Pushkarev

import java.util.*;

public class alphabet {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        sc.nextLine();
        List<String> words = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            words.add(sc.nextLine().trim());
        }

        Set<Character> allChars = new HashSet<>();
        for (String word : words) {
            for (char c : word.toCharArray()) {
                allChars.add(c);
            }
        }

        Graph<Character, Void> graph = new Graph<>();
        for (Character c : allChars) {
            graph.addVertex(c);
        }

        boolean invalid = false;
        for (int i = 0; i < words.size() - 1; i++) {
            String word1 = words.get(i);
            String word2 = words.get(i + 1);
            int minLen = Math.min(word1.length(), word2.length());
            int j;
            for (j = 0; j < minLen; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);
                if (c1 != c2) {
                    Graph<Character, Void>.Vertex<Character> v1 = graph.getVertex(c1);
                    Graph<Character, Void>.Vertex<Character> v2 = graph.getVertex(c2);
                    graph.addEdge(null, v1, v2);
                    break;
                }
            }
            if (j == minLen) {
                if (word1.length() > word2.length()) {
                    System.out.println("Doh");
                    return;
                }
            }
        }

        List<Character> order = graph.IlyaPushkarev_topsort();
        if (order == null) {
            System.out.println("Doh");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Character c : order) {
                sb.append(c);
            }
            System.out.println(sb.toString());
        }
    }
}

class Graph<V, E> {
    private LinkedList<Vertex<V>> vertices = new LinkedList<>();
    private LinkedList<Edge<V, E>> edges = new LinkedList<>();

    public Vertex<V> addVertex(V label) {
        for (Vertex<V> v : vertices) {
            if (v.label.equals(label)) {
                return v;
            }
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

    public List<V> IlyaPushkarev_topsort() {
        Stack<Vertex<V>> stack = new Stack<>();
        Set<Vertex<V>> visited = new HashSet<>();
        Set<Vertex<V>> recursionStack = new HashSet<>();
        boolean hasCycle = false;

        for (Vertex<V> vertex : vertices) {
            if (!visited.contains(vertex)) {
                if (IlyaPushkarev_dfs(vertex, visited, recursionStack, stack)) {
                    hasCycle = true;
                    break;
                }
            }
        }

        if (hasCycle) {
            return null;
        }

        List<V> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop().label);
        }

        return result;
    }

    private boolean IlyaPushkarev_dfs(Vertex<V> vertex, Set<Vertex<V>> visited, Set<Vertex<V>> recursionStack, Stack<Vertex<V>> stack) {
        if (recursionStack.contains(vertex)) {
            return true;
        }
        if (visited.contains(vertex)) {
            return false;
        }

        visited.add(vertex);
        recursionStack.add(vertex);

        for (Edge<V, E> edge : vertex.outgoingEdges) {
            Vertex<V> neighbor = edge.destination;
            if (IlyaPushkarev_dfs(neighbor, visited, recursionStack, stack)) {
                return true;
            }
        }

        recursionStack.remove(vertex);
        stack.push(vertex);
        return false;
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