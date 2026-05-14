//Ilya Pushkarev

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class Sorter {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        int N = Integer.parseInt(reader.readLine());
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());

        List<Integer>[] indexMap = new ArrayList[1001];
        for (int i = 0; i < 1001; i++) {
            indexMap[i] = new ArrayList<>();
        }

        for (int i = 0; i < N; i++) {
            int num = Integer.parseInt(tokenizer.nextToken());
            indexMap[num].add(i);
        }

        List<Integer> uniqueNumbers = new ArrayList<>();
        for (int i = 0; i < 1001; i++) {
            if (!indexMap[i].isEmpty()) {
                uniqueNumbers.add(i);
            }
        }

        IlyaPushkarev_radix_srt(uniqueNumbers, num -> indexMap[num].size());

        int currentFreq = -1;
        int start = 0;
        for (int i = 0; i <= uniqueNumbers.size(); i++) {
            if (i == uniqueNumbers.size() || indexMap[uniqueNumbers.get(i)].size() != currentFreq) {
                if (currentFreq != -1) {
                    List<Integer> group = uniqueNumbers.subList(start, i);
                    IlyaPushkarev_count_srt(group, num -> num);
                }
                if (i < uniqueNumbers.size()) {
                    currentFreq = indexMap[uniqueNumbers.get(i)].size();
                    start = i;
                }
            }
        }

        for (int num : uniqueNumbers) {
            for (int index : indexMap[num]) {
                writer.write(num + " " + index + "\n");
            }
        }

        writer.flush();
        writer.close();
    }

    public static <T> void IlyaPushkarev_radix_srt(List<T> list, Function<T, Integer> keyExtractor) {
        if (list.isEmpty()) return;

        int max = list.stream().map(keyExtractor).max(Integer::compare).get();
        for (int exp = 1; max / exp > 0; exp *= 10) {
            IlyaPushkarev_counting_srt_bydigit(list, keyExtractor, exp);
        }
    }

    private static <T> void IlyaPushkarev_counting_srt_bydigit(List<T> list, Function<T, Integer> keyExtractor, int exp) {
        int n = list.size();
        ArrayList<T> output = new ArrayList<>(Collections.nCopies(n, null));
        int[] count = new int[10];

        for (T item : list) {
            int key = keyExtractor.apply(item);
            int digit = (key / exp) % 10;
            count[digit]++;
        }

        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        for (int i = n - 1; i >= 0; i--) {
            T item = list.get(i);
            int key = keyExtractor.apply(item);
            int digit = (key / exp) % 10;
            output.set(count[digit] - 1, item);
            count[digit]--;
        }

        for (int i = 0; i < n; i++) {
            list.set(i, output.get(i));
        }
    }

    public static <T> void IlyaPushkarev_count_srt(List<T> list, Function<T, Integer> keyExtractor) {
        if (list.isEmpty()) return;

        int min = list.stream().map(keyExtractor).min(Integer::compare).get();
        int max = list.stream().map(keyExtractor).max(Integer::compare).get();
        int range = max - min + 1;

        int[] count = new int[range];
        ArrayList<T> output = new ArrayList<>(Collections.nCopies(list.size(), null));

        for (T item : list) {
            int key = keyExtractor.apply(item) - min;
            count[key]++;
        }

        for (int i = 1; i < range; i++) {
            count[i] += count[i - 1];
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            T item = list.get(i);
            int key = keyExtractor.apply(item) - min;
            output.set(count[key] - 1, item);
            count[key]--;
        }

        for (int i = 0; i < list.size(); i++) {
            list.set(i, output.get(i));
        }
    }
}