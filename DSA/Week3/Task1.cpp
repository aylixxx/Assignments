//Ilya Pushkarev

#include <iostream>
#include <vector>
#include <list>

using namespace std;

template <typename K, typename V>
class Map {
public:
    virtual void put(const K &key, const V &value) = 0;
    virtual bool get(const K &key, V &value) = 0;
    virtual bool contains(const K &key) = 0;
    virtual void increment(const K &key) = 0;
    virtual vector<pair<K, V>> getAll() = 0;
    virtual ~Map() {}
};

template <typename K, typename V>
class HashTable : public Map<K, V> {
private:
    static const int SIZE = 100;
    vector<list<pair<K, V>>> table;

    int hash(const K &key) const {
        int hashValue = 0;
        for (char ch : key) {
            hashValue = (hashValue * 31 + ch) % SIZE;
        }
        return hashValue;
    }

public:
    HashTable() : table(SIZE) {}

    void put(const K &key, const V &value) override {
        int index = hash(key);
        for (auto &p : table[index]) {
            if (p.first == key) {
                p.second = value;
                return;
            }
        }
        table[index].emplace_back(key, value);
    }

    bool get(const K &key, V &value) override {
        int index = hash(key);
        for (const auto &p : table[index]) {
            if (p.first == key) {
                value = p.second;
                return true;
            }
        }
        return false;
    }

    bool contains(const K &key) override {
        int index = hash(key);
        for (const auto &p : table[index]) {
            if (p.first == key) {
                return true;
            }
        }
        return false;
    }

    void increment(const K &key) override {
        int index = hash(key);
        for (auto &p : table[index]) {
            if (p.first == key) {
                p.second++;
                return;
            }
        }
        table[index].emplace_back(key, 1);
    }

    vector<pair<K, V>> getAll() override {
        vector<pair<K, V>> elements;
        for (const auto &bucket : table) {
            for (const auto &p : bucket) {
                elements.push_back(p);
            }
        }
        return elements;
    }
};

void insertionSort(vector<pair<string, int>> &arr, const vector<string> &order) {
    int n = arr.size();
    for (int i = 1; i < n; i++) {
        auto key = arr[i];
        int j = i - 1;
        while (j >= 0 && (arr[j].second < key.second ||
                          (arr[j].second == key.second &&
                           find(order.begin(), order.end(), arr[j].first) > find(order.begin(), order.end(), key.first)))) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}

int main() {
    int n, k;
    cin >> n >> k;
    
    HashTable<string, int> freqMap;
    vector<string> order;
    
    for (int i = 0; i < n; i++) {
        string word;
        cin >> word;
        if (!freqMap.contains(word)) {
            order.push_back(word);
            freqMap.put(word, 1);
        } else {
            freqMap.increment(word);
        }
    }
    
    vector<pair<string, int>> freqList = freqMap.getAll();
    insertionSort(freqList, order);
    
    cout << freqList[k - 1].first << endl;
    
    return 0;
}