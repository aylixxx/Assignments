//Ilya Pushkarev
#include <iostream>

void bubbleSort(long long array[], int size) {
    for (int i = 0; i < size-1; ++i) {
        bool isSorted = true;
        for (int j = 0; j < size - i - 1; ++j) {
            if (array[j] > array[j + 1]) {
                const long long temp = array[j];
                array[j] = array[j + 1];
                array[j + 1] = temp;
                isSorted = false;
            }
        }
        if (isSorted) {
            break;
        }
    }
}

int main() {
    int count;
    std::cin >> count;

    if (count <= 0) {
        return 0;
    }

    auto* numbers = new long long[count];
    for (int i = 0; i < count; ++i) {
        std::cin >> numbers[i];
    }

    bubbleSort(numbers, count);

    for (int i = 0; i < count; ++i) {
        std::cout << numbers[i];
        if (i < count - 1) {
            std::cout << " ";
        } else {
            std::cout << "\n";
        }
    }
    return 0;
}
