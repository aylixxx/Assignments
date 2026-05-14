#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
// A variable "flag" for checking the conditions of the task
int flag=1;
//A function "vbase" for checking the correspondence of numbers of number systems
int vbase(const char *number, int base) {
    //For a binary system
    if (base == 2) {
        for (int i = 0; number[i] != '\0'; i++) {
            //If number not equal to 0 and 1, flag=0
            if (number[i] != '0' && number[i] != '1') {
                flag=0;
                return 0;
            }
        }
    // For the octal system
    } else if (base == 8) {
        for (int i = 0; number[i] != '\0'; i++) {
            // If the number is not in the range from 0 to 7, flag=0
            if (number[i] < '0' || number[i] > '7') {
                flag=0;
                return 0;
            }
        }
    //For the decimal system
    } else if (base == 10) {
        for (int i = 0; number[i] != '\0'; i++) {
            // If the number is not a digit, flag=0
            if (!isdigit(number[i])) {
                flag=0;
                return 0;
            }
        }
    // For the hexadecimal system
    } else if (base == 16) {
        for (int i = 0; number[i] != '\0'; i++) {
            // If the number is not a digit and not in the range from A to F, flag=0
            if (!isdigit(number[i]) && (number[i] < 'A' || number[i] > 'F')) {
                flag=0;
                return 0;
            }
        }
    } else {
        return 0;
    }
    return 1;
}
// A function "conv" for converting to the decimal system
int conv(const char *number, int base) {
    if (!vbase(number, base)) {
        return -1;
    }
    return (int)strtol(number, NULL, base);
}

int main(){
    //Creating an input file
    FILE *input = fopen("input.txt", "r");
    //Creating an output file
    FILE *output = fopen("output.txt", "w");
    //if the file is empty the flag=0
    if (input == NULL){
        flag=0;
    }
    //scanning n
    int n;
    fscanf(input,"%d", &n);
    // checking the task condition for n
    if (n<1 || n>40){
        flag=0;
    }
    // scanning S
    char S[n][100];
    for (int i=0; i<n; i++){
        fscanf(input, "%s", S[i]);
    }
    // scanning A
    int A[n];
    for (int i = 0; i < n; i++) {
        fscanf(input, "%d", &A[i]);
        // If A does not meet the conditions of the task, flag=0
        if (A[i]!=2 && A[i]!=8 && A[i]!=10 && A[i]!=16){
            flag=0;
        }
    }
    // collecting the answer
    int ans=0;
    for(int i=0; i<n; i++){
        ans += conv(S[i], A[i]);
    }
    // If flag=0, print in a file "Invalid inputs"
    if (flag==0 ){
        fprintf(output, "Invalid inputs\n");
        // Close the input file
        fclose(input);
        // Close the output file
        fclose(output);
        return 0;
    }
    // If N is an odd number, we subtract 10 according to the condition of the problem
    if (n%2!=0){
        ans=ans-10;
    }
    // Print answer in file
    fprintf(output, "%d", ans);
    return 0;
}