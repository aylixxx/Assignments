import java.io.*;
import java.util.*;

public class Sorter2 {
    static class Point {
        double x, y;
        String xstr,ystr; 
        double dSquared;
        int originalIndex;

        Point(String xstr, String ystr, int index) {
            this.x = Double.parseDouble(xstr);
            this.y = Double.parseDouble(ystr);
            this.xstr = xstr;
            this.ystr = ystr;
            this.dSquared = x * x + y * y;
            this.originalIndex = index;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        int N = Integer.parseInt(reader.readLine().trim());
        List<Point> points = new ArrayList<>(N);

        for (int i = 0; i < N; i++) {
            String[] parts = reader.readLine().trim().split("\\s+");
            String xstr = parts[0];
            String ystr = parts[1];
            points.add(new Point(xstr, ystr, i));
        }

        IlyaPushkarev_bucket_srt(points);

        for (Point p : points) {
            writer.write(String.format("%s %s%n", p.xstr, p.ystr));
        }

        writer.flush();
        writer.close();
    }

    public static void IlyaPushkarev_bucket_srt(List<Point> points) {
        final int NUM_BUCKETS = points.size();
        List<List<Point>> buckets = new ArrayList<>(NUM_BUCKETS);

        for (int i = 0; i < NUM_BUCKETS; i++) {
            buckets.add(new ArrayList<>());
        }

        for (Point p : points) {
            int bucketIndex = Math.min((int) (NUM_BUCKETS*Math.sqrt(p.dSquared)), NUM_BUCKETS - 1);
            buckets.get(bucketIndex).add(p);
        }

        for (List<Point> bucket : buckets) {
            if (!bucket.isEmpty()) {
                IlyaPushkarev_merge_srt(bucket);
            }
        }

        points.clear();
        for (List<Point> bucket : buckets) {
            points.addAll(bucket);
        }
    }

    public static void IlyaPushkarev_merge_srt(List<Point> list) {
        if (list.size() < 2) return;

        int mid = list.size() / 2;
        List<Point> left = new ArrayList<>(list.subList(0, mid));
        List<Point> right = new ArrayList<>(list.subList(mid, list.size()));

        IlyaPushkarev_merge_srt(left);
        IlyaPushkarev_merge_srt(right);

        merge(list, left, right);
    }

    private static void merge(List<Point> result, List<Point> left, List<Point> right) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (comparePoints(left.get(i), right.get(j)) <= 0) {
                result.set(k++, left.get(i++));
            } else {
                result.set(k++, right.get(j++));
            }
        }

        while (i < left.size()) {
            result.set(k++, left.get(i++));
        }

        while (j < right.size()) {
            result.set(k++, right.get(j++));
        }
    }

    private static int comparePoints(Point a, Point b) {
        int cmp = Double.compare(a.dSquared, b.dSquared);
        return cmp != 0 ? cmp : Integer.compare(a.originalIndex, b.originalIndex);
    }
}