//Ilya Pushkarev

import java.util.*;

public class MCP {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        int M = sc.nextInt();
        
        int[][] g = new int[N][M];
        int[][] d = new int[N][M];
        int[][] p = new int[N][M];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                g[i][j] = sc.nextInt();
            }
        }
        
        d[0][0] = g[0][0];
        for (int i = 1; i < N; i++) {
            d[i][0] = d[i - 1][0] + g[i][0];
            p[i][0] = 0;
        }
        for (int j = 1; j < M; j++) {
            d[0][j] = d[0][j - 1] + g[0][j];
            p[0][j] = 1;
        }
        
        for (int i = 1; i < N; i++) {
            for (int j = 1; j < M; j++) {
                if (d[i - 1][j] < d[i][j - 1]) {
                    d[i][j] = d[i - 1][j] + g[i][j];
                    p[i][j] = 0;
                } else {
                    d[i][j] = d[i][j - 1] + g[i][j];
                    p[i][j] = 1;
                }
            }
        }
        
        System.out.println(d[N - 1][M - 1]);
        
        List<Integer> pth = new ArrayList<>();
        int x = N - 1, y = M - 1;
        while (x != 0 || y != 0) {
            pth.add(g[x][y]);
            if (p[x][y] == 0) {
                x--;
            } else {
                y--;
            }
        }
        pth.add(g[0][0]);
        Collections.reverse(pth);
        
        for (int c : pth) {
            System.out.print(c + " ");
        }
    }
}