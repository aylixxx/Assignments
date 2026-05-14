//Ilya Pushkarev

#include <iostream>
using namespace std;
int main() {
    long n;
    int k;
    cin >> n >> k;
    string array1[n];
    int array2[n];
    for (int i = 0; i < n; i++) {
        cin >> array1[i] >> array2[i];
    }
    long max = array2[0];
    int max_index = 0;
    if (k < n) {
        int i = 0;
        while (i < k) {
            for (int j = i; j < n; j++) {
                if (array2[j] > max) {
                    max = array2[j];
                    max_index = j;
                }
            }
            string swap = array1[max_index];
            array2[max_index] = array2[i];
            array2[i] = max;
            max = 0;
            array1[max_index] = array1[i];
            array1[i] = swap;
            i++;
        }
    } else {
        int i = 0;
        while (i < n) {
            for (int j = i; j < n; j++) {
                if (array2[j] > max) {
                    max = array2[j];
                    max_index = j;
                }
            }
            string swap = array1[max_index];
            array2[max_index] = array2[i];
            array2[i] = max;
            max = 0;
            array1[max_index] = array1[i];
            array1[i] = swap;
            i++;
        }
    }
    if (k<n) {
        for (int i = 0; i < k; i++) {
            cout << array1[i] << " " << array2[i] << endl;
        }
    } else {
        for (int i = 0; i < n; i++) {
            cout << array1[i] << " " << array2[i] << endl;
        }
    }
    return 0;
}