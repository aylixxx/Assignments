import java.util.*;
import java.util.stream.Collectors;

public class TEST {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, String> input = new HashMap<>();
        Set<String> mandatoryKeys = new HashSet<>(Arrays.asList("type", "states", "alphabet", "initial", "accepting", "transitions"));

        if (scanner.hasNextLine()) {
            String firstLine = scanner.nextLine().trim();
            if (!firstLine.startsWith("type=")) {
                System.out.println("Input is malformed.");
                return;
            }
            String[] parts = firstLine.split("=", 2);
            if (parts.length != 2) {
                System.out.println("Input is malformed.");
                return;
            }
            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();
            input.put(key, value);
        } else {
            System.out.println("Input is malformed.");
            return;
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("=", 2);
            if (parts.length != 2) {
                System.out.println("Input is malformed.");
                return;
            }
            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();

            if (mandatoryKeys.contains(key)) {
                if (input.containsKey(key)) {
                    System.out.println("Input is malformed.");
                    return;
                }
                input.put(key, value);
            }
        }

        try {
            String typeStr = input.get("type");
            if (!typeStr.matches("\\[(deterministic|non-deterministic)\\]")) {
                throw new IllegalArgumentException();
            }
            String type = typeStr.substring(1, typeStr.length() - 1);

            List<String> statesList = parseList(input.get("states"));
            if (statesList.isEmpty()) throw new IllegalArgumentException();
            Set<String> states = new LinkedHashSet<>(statesList);
            for (String s : states) {
                if (!s.matches("[A-Za-z0-9_]+")) throw new IllegalArgumentException();
            }

            List<String> alphabetList = parseList(input.get("alphabet"));
            if (alphabetList.isEmpty()) {
                System.out.println("Input is malformed.");
                return;
            }
            Set<String> alphabet = new HashSet<>(alphabetList);
            for (String sym : alphabetList) {
                if (sym.equals("eps") && type.equals("deterministic")) {
                    System.out.println("FSA is non-deterministic.");
                    return;
                }
                if (!sym.matches("[A-Za-z0-9_]+") || sym.equals("eps")) {
                    System.out.println("Input is malformed.");
                    return;
                }
            }

            List<String> initialList = parseList(input.get("initial"));
            if (initialList.size() != 1) {
                System.out.println(initialList.isEmpty() ? "Initial state is not defined." : "Input is malformed.");
                return;
            }
            String initial = initialList.get(0);
            if (!states.contains(initial)) {
                System.out.println("A state '" + initial + "' is not in the set of states.");
                return;
            }

            List<String> acceptingList = parseList(input.get("accepting"));
            if (acceptingList.isEmpty()) {
                System.out.println("Set of accepting states is empty.");
                return;
            }
            Set<String> accepting = new TreeSet<>(acceptingList);
            for (String acc : accepting) {
                if (!states.contains(acc)) {
                    System.out.println("A state '" + acc + "' is not in the set of states.");
                    return;
                }
            }

            List<String> transList = parseList(input.get("transitions"));
            if (transList.isEmpty() || transList.stream().anyMatch(String::isEmpty)) {
                System.out.println("Input is malformed.");
                return;
            }

            Set<String> uniqueTransitions = new HashSet<>();
            for (String t : transList) {
                if (!uniqueTransitions.add(t)) {
                    System.out.println("Input is malformed.");
                    return;
                }
            }

            Map<String, Map<String, Set<String>>> transitions = new HashMap<>();
            for (String t : transList) {
                // Split the transition into parts using '>'
                String[] p = t.split(">");

                // Check if the transition has exactly three parts
                if (p.length != 3) {
                    System.out.println("Input is malformed.");
                    return;
                }

                // Check if any part of the transition is empty
                if (Arrays.stream(p).anyMatch(String::isEmpty)) {
                    System.out.println("Input is malformed.");
                    return;
                }

                // Extract the parts of the transition
                String from = p[0].trim();
                String sym = p[1].trim();
                String to = p[2].trim();

                // Check if the 'from' state is valid
                if (!states.contains(from)) {
                    System.out.println("A state '" + from + "' is not in the set of states.");
                    return;
                }

                // Ensure 'eps' is explicitly checked in transitions
                if (sym.equals("eps") && !alphabet.contains("eps")) {
                    System.out.println("A transition symbol 'eps' is not in the alphabet.");
                    return;
                }

                // Check if the symbol is valid
                if (sym.equals("eps")) {
                    if (!alphabet.contains("eps")) {
                        System.out.println("A transition symbol 'eps' is not in the alphabet.");
                        return;
                    }
                } else if (!alphabet.contains(sym)) {
                    System.out.println("A transition symbol '" + sym + "' is not in the alphabet.");
                    return;
                }

                // Check if the 'to' state is valid
                if (!states.contains(to)) {
                    System.out.println("A state '" + to + "' is not in the set of states.");
                    return;
                }

                // Add the transition to the map
                transitions.computeIfAbsent(from, k -> new LinkedHashMap<>())
                           .computeIfAbsent(sym, k -> new LinkedHashSet<>())
                           .add(to);
            }

            if (type.equals("deterministic")) {
                for (var e : transitions.entrySet()) {
                    for (var t : e.getValue().entrySet()) {
                        if (t.getKey().equals("eps") || t.getValue().size() > 1) {
                            System.out.println("FSA is non-deterministic.");
                            return;
                        }
                    }
                }
            }

            Set<String> reachable = getReachable(initial, transitions);
            if (!reachable.containsAll(states)) {
                System.out.println("Some states are disjoint.");
                return;
            }
            for (String acc : accepting) {
                if (!reachable.contains(acc)) {
                    System.out.println("Some states are disjoint.");
                    return;
                }
            }

            System.out.println(convertToRegex(new ArrayList<>(states), initial, new ArrayList<>(accepting), transitions));
        } catch (IllegalArgumentException e) {
            System.out.println("Input is malformed.");
        }
    }

    private static List<String> parseList(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Input is malformed.");
        }
        if (!s.startsWith("[") || !s.endsWith("]")) {
            throw new IllegalArgumentException();
        }
        String content = s.substring(1, s.length() - 1).trim();
        if (content.isEmpty()) {
            return new ArrayList<>();
        }

        if (content.startsWith(",") || content.endsWith(",") || content.contains(",,")) {
            throw new IllegalArgumentException();
        }

        List<String> tokens = Arrays.stream(content.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        // Check for empty strings after spliting
        if (tokens.stream().anyMatch(String::isEmpty)) {
            System.out.println("Input is malformed.");
            throw new IllegalArgumentException();
        }

        return tokens;
    }

    private static Set<String> getReachable(String start, Map<String, Map<String, Set<String>>> trans) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);
            Map<String, Set<String>> transitions = trans.getOrDefault(current, Collections.emptyMap());
            for (Set<String> targets : transitions.values()) {
                queue.addAll(targets);
            }
        }
        return visited;
    }

    private static String convertToRegex(List<String> states, String initial, List<String> accepting,
                                     Map<String, Map<String, Set<String>>> trans) {
    int n = states.size();
    Map<String, Integer> indexMap = new HashMap<>();
    for (int i = 0; i < n; i++) {
        indexMap.put(states.get(i), i);
    }

    String[][][] R = new String[n + 1][n][n];
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            final int currentJ = j;
            Set<String> symbols = new LinkedHashSet<>();
            Map<String, Set<String>> fromTrans = trans.get(states.get(i));
            if (fromTrans != null) {
                fromTrans.forEach((sym, targets) -> {
                    if (targets.contains(states.get(currentJ))) {
                        symbols.add(sym);
                    }
                });
            }
            if (i == j) {
                symbols.add("eps");
            }

            List<String> ordered = new ArrayList<>();
            for (String s : symbols) {
                if (!s.equals("eps")) ordered.add(s);
            }
            if (symbols.contains("eps")) ordered.add("eps");

            R[0][i][j] = ordered.isEmpty() ? "({})" : 
                "(" + String.join("|", ordered) + ")";
        }
    }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    String a = R[k][i][k];
                    String b = R[k][k][k];
                    String c = R[k][k][j];
                    String path = a + b + "*" + c;
                    R[k + 1][i][j] = "(" + path + "|" + R[k][i][j] + ")";
                }
            }
        }

        int startIdx = indexMap.get(initial);
        List<String> regexParts = accepting.stream()
                .sorted()
                .map(acc -> R[n][startIdx][indexMap.get(acc)])
                .collect(Collectors.toList());

        return regexParts.size() == 1 ? regexParts.get(0) : String.join("|", regexParts);
    }
}