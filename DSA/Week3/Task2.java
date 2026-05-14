//Ilya Pushkarev

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        String[] firstText = new String[n];
        for (int i = 0; i < n; i++) {
            firstText[i] = scanner.next();
        }

        int m = scanner.nextInt();
        String[] secondText = new String[m];
        for (int i = 0; i < m; i++) {
            secondText[i] = scanner.next();
        }

        MapADT<String, Boolean> secondTextMap = new CustomHashMap<>();
        for (String word : secondText) {
            secondTextMap.put(word, true);
        }

        SetADT<String> uniqueWords = new CustomHashSet<>();
        List<String> result = new ArrayList<>();

        for (String word : firstText) {
            if (!secondTextMap.containsKey(word) && !uniqueWords.contains(word)) {
                uniqueWords.add(word);
                result.add(word);
            }
        }

        System.out.println(result.size());
        for (String word : result) {
            System.out.println(word);
        }
    }
}

interface MapADT<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean containsKey(K key);
    int size();
}

class CustomHashMap<K, V> implements MapADT<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry<K, V>[] table;
    private int size;

    public CustomHashMap() {
        table = new Entry[DEFAULT_CAPACITY];
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    @Override
    public void put(K key, V value) {
        int index = hash(key);
        Entry<K, V> entry = table[index];

        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;

        if ((double) size / table.length > LOAD_FACTOR) {
            resize();
        }
    }

    @Override
    public V get(K key) {
        int index = hash(key);
        Entry<K, V> entry = table[index];

        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }

        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int index = hash(key);
        Entry<K, V> entry = table[index];

        while (entry != null) {
            if (entry.key.equals(key)) {
                return true;
            }
            entry = entry.next;
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = new Entry[table.length * 2];
        size = 0;

        for (Entry<K, V> entry : oldTable) {
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }
}

interface SetADT<T> {
    void add(T value);
    boolean contains(T value);
    int size();
}

class CustomHashSet<T> implements SetADT<T> {
    private final CustomHashMap<T, Boolean> map;

    public CustomHashSet() {
        map = new CustomHashMap<>();
    }

    @Override
    public void add(T value) {
        map.put(value, true);
    }

    @Override
    public boolean contains(T value) {
        return map.containsKey(value);
    }

    @Override
    public int size() {
        return map.size();
    }
}