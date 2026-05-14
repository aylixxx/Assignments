//Ilya Pushkarev

import java.util.*;

public class WIS {
    
    static class J {
        int s, f, p;
        
        J(int s, int f, int p) {
            this.s = s;
            this.f = f;
            this.p = p;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        List<J> js = new ArrayList<>();
        
        int[] st = new int[n];
        int[] ft = new int[n];
        int[] pr = new int[n];
        
        for (int i = 0; i < n; i++) st[i] = sc.nextInt();
        for (int i = 0; i < n; i++) ft[i] = sc.nextInt();
        for (int i = 0; i < n; i++) pr[i] = sc.nextInt();
        
        for (int i = 0; i < n; i++) {
            js.add(new J(st[i], ft[i], pr[i]));
        }
        
        System.out.println(maxP(js));
    }
    
    private static void mergeSort(List<J> js, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(js, left, mid);
            mergeSort(js, mid + 1, right);
            merge(js, left, mid, right);
        }
    }
    
    private static void merge(List<J> js, int left, int mid, int right) {
        List<J> leftList = new ArrayList<>(js.subList(left, mid + 1));
        List<J> rightList = new ArrayList<>(js.subList(mid + 1, right + 1));
        
        int i = 0, j = 0, k = left;
        while (i < leftList.size() && j < rightList.size()) {
            if (leftList.get(i).f <= rightList.get(j).f) {
                js.set(k++, leftList.get(i++));
            } else {
                js.set(k++, rightList.get(j++));
            }
        }
        while (i < leftList.size()) js.set(k++, leftList.get(i++));
        while (j < rightList.size()) js.set(k++, rightList.get(j++));
    }

    private static int bs(List<J> js, int idx) {
        int l = 0, r = idx - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (js.get(m).f <= js.get(idx).s) {
                if (js.get(m + 1).f <= js.get(idx).s) {
                    l = m + 1;
                } else {
                    return m;
                }
            } else {
                r = m - 1;
            }
        }
        return -1;
    }

    public static int maxP(List<J> js) {
        int n = js.size();
        mergeSort(js, 0, n - 1);
        
        int[] dp = new int[n];
        dp[0] = js.get(0).p;
        
        for (int i = 1; i < n; i++) {
            int ip = js.get(i).p;
            int l = bs(js, i);
            if (l != -1) {
                ip += dp[l];
            }
            dp[i] = Math.max(ip, dp[i - 1]);
        }
        
        return dp[n - 1];
    }
}