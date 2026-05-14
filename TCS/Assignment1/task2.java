import java.util.Scanner;

public class task2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String C = scanner.nextLine();
        String S = scanner.nextLine();

        lastname_firstname_FST fst = new lastname_firstname_FST(C);

        int count = 0;
        for (int i = 0; i < S.length(); i++) {
            char c = S.charAt(i);
            count += fst.process(c);
        }

        if (count == 0) {
            System.out.println("NOT DETECTED");
        } else {
            System.out.println(count);
        }
    }
}

class lastname_firstname_FST {
    private int[] failure;
    private int[][] transitions;
    private int currentState;
    private String substring;
    private int m;

    public lastname_firstname_FST(String substring) {
        this.substring = substring;
        this.m = substring.length();
        buildFailureFunction();
        buildTransitionTable();
        currentState = 0;
    }

    private void buildFailureFunction() {
        failure = new int[m];
        for (int i = 1; i < m; i++) {
            int j = failure[i - 1];
            while (j > 0 && substring.charAt(i) != substring.charAt(j)) {
                j = failure[j - 1];
            }
            if (substring.charAt(i) == substring.charAt(j)) {
                j++;
            }
            failure[i] = j;
        }
    }

    private void buildTransitionTable() {
        transitions = new int[m + 1][26];
        for (int s = 0; s <= m; s++) {
            for (char c = 'a'; c <= 'z'; c++) {
                transitions[s][c - 'a'] = computeNextState(s, c);
            }
        }
    }

    private int computeNextState(int s, char c) {
        if (s == m) {
            s = failure[m - 1];
        }

        while (true) {
            if (s < m && c == substring.charAt(s)) {
                return s + 1;
            }
            if (s == 0) {
                break;
            }
            s = failure[s - 1];
        }

        return (c == substring.charAt(0)) ? 1 : 0;
    }

    public int process(char c) {
        int charIndex = c - 'a';
        currentState = transitions[currentState][charIndex];
        return currentState == m ? 1 : 0;
    }
}
