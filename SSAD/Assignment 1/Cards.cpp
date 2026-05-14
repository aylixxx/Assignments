#include <iostream>
#include <vector>
#include <map>
#include <string>
#include <sstream>
#include <algorithm>

using namespace std;

int main() {
    vector<int> A, K;

    // Read Ahmed cards
    string line;
    getline(cin, line);
    istringstream issA(line);
    int x;
    while (issA >> x && x != 0) {
        A.push_back(x);
    }

    // Read Karim cards
    getline(cin, line);
    istringstream issK(line);
    while (issK >> x && x != 0) {
        K.push_back(x);
    }

    // Build maps from value to list of indices for players
    map<int, vector<int>> a_map, k_map;
    for (int i = 0; i < A.size(); ++i) {
        a_map[A[i]].push_back(i);
    }
    for (int i = 0; i < K.size(); ++i) {
        k_map[K[i]].push_back(i);
    }

    vector<int> ahmed_win_indices, karim_win_indices;
    int a_points = 0, k_points = 0;

    // Process each common X
    for (const auto& a_pair : a_map) {
        int X = a_pair.first;
        if (k_map.find(X) == k_map.end()) continue;

        const vector<int>& a_indices = a_pair.second;
        const vector<int>& k_indices = k_map[X];
        int min_count = min(a_indices.size(), k_indices.size());

        for (int i = 0; i < min_count; ++i) {
            int a_idx = a_indices[i];
            int k_idx = k_indices[i];
            if (a_idx < k_idx) {
                ahmed_win_indices.push_back(a_idx);
                a_points++;
            } else if (k_idx < a_idx) {
                karim_win_indices.push_back(k_idx);
                k_points++;
            }
        }
    }

    // Sort the winning indices to maintain original order
    sort(ahmed_win_indices.begin(), ahmed_win_indices.end());
    sort(karim_win_indices.begin(), karim_win_indices.end());

    // Output Ahmed winning cards
    if (ahmed_win_indices.empty()) {
        cout << "-\n";
    } else {
        for (int idx : ahmed_win_indices) {
            cout << A[idx] << " ";
        }
        cout << "\n";
    }

    // Output Karim winning cards
    if (karim_win_indices.empty()) {
        cout << "-\n";
    } else {
        for (int idx : karim_win_indices) {
            cout << K[idx] << " ";
        }
        cout << "\n";
    }

    // Determine the winner
    if (a_points > k_points) {
        cout << "Ahmed\n";
    } else if (k_points > a_points) {
        cout << "Karim\n";
    } else {
        cout << "Tie\n";
    }

    return 0;
}