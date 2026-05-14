import java.util.*;


public class task1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        int N = scanner.nextInt();
        scanner.nextLine();
        
        List<Set<String>> languages = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            String line = scanner.nextLine().trim();
            String[] parts = line.split(" +");
            int Li = Integer.parseInt(parts[0]);
            Set<String> words = new HashSet<>();
            for (int j = 1; j < parts.length; j++) {
                words.add(parts[j]);
            }
            languages.add(words);
        }
        
        int M = scanner.nextInt();
        scanner.nextLine();
        String sentenceLine = scanner.nextLine();
        String[] sentenceWords = sentenceLine.split(" +");
        
        int[] counts = new int[N];
        for (int i = 0; i < N; i++) {
            Set<String> langWords = languages.get(i);
            int count = 0;
            for (String word : sentenceWords) {
                if (langWords.contains(word)) {
                    count++;
                }
            }
            counts[i] = count;
        }
        
        int maxCount = 0;
        for (int count : counts) {
            if (count > maxCount) {
                maxCount = count;
            }
        }
        
        List<Integer> resultIndices = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (counts[i] == maxCount) {
                resultIndices.add(i + 1);
            }
        }
        
        StringBuilder output = new StringBuilder();
        for (int idx : resultIndices) {
            output.append(idx).append(" ");
        }
        if (output.length() > 0) {
            output.setLength(output.length() - 1);
        }
        System.out.println(output);
    }
}