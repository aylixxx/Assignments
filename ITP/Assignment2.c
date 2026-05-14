#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#define MAX_STUDENTS 1000
#define MAX_EXAMS 500
#define MAX_GRADES 1000

// Enum for Exam Type
typedef enum {
    WRITTEN,
    DIGITAL
} ExamType;

// Union for Exam Information
typedef union {
    int duration;
    char software[20];
} ExamInfo;

// Structure for Student
typedef struct {
    int student_id;
    char name[20];
    char faculty[30];
} Student;

// Structure for Exam
typedef struct {
    int exam_id;
    ExamType exam_type;
    ExamInfo exam_info;
} Exam;

// Structure for Exam Grade
typedef struct {
    int exam_id;
    int student_id;
    int grade;
} ExamGrade;

// Arrays to store students, exams, and grades
Student students[MAX_STUDENTS];
int student_count = 0;

Exam exams[MAX_EXAMS];
int exam_count = 0;

ExamGrade grades[MAX_GRADES];
int grade_count = 0;

// Function to check if a string is valid
int is_valid_string(const char *str, int min_len, int max_len) {
    int len = strlen(str);

    if (len < min_len || len > max_len) return 0;

    // Check if the string contains only valid characters
    for (int i = 0; i < len; i++) {
        char c = str[i];
        if (!(isalpha(c))) {
            return 0;
        }
    }

    return 1;
}

// Function to find a student by ID
int find_student_by_id(int student_id) {
    for (int i = 0; i < student_count; i++) {
        if (students[i].student_id == student_id) {
            return i;
        }
    }
    return -1;
}

// Function to find an exam by ID
int find_exam_by_id(int exam_id) {
    for (int i = 0; i < exam_count; i++) {
        if (exams[i].exam_id == exam_id) {
            return i;
        }
    }
    return -1;
}

// Function to validate grade
int is_valid_grade(int grade) {
    return grade >= 0 && grade <= 100;
}

// Function to add a student
void add_student(FILE *output, int student_id, const char *name, const char *faculty) {
    if (student_id <= 0 || student_id >= 1000) {
        fprintf(output, "Invalid student id\n");
        return;
    }
    if (!is_valid_string(name, 2, 19)) {
        fprintf(output, "Invalid name\n");
        return;
    }
    if (!is_valid_string(faculty, 5, 29)) {
        fprintf(output, "Invalid faculty\n");
        return;
    }
    if (find_student_by_id(student_id) != -1) {
        fprintf(output, "Student: %d already exists\n", student_id);
        return;
    }

    students[student_count].student_id = student_id;
    strcpy(students[student_count].name, name);
    strcpy(students[student_count].faculty, faculty);
    student_count++;
    fprintf(output, "Student: %d added\n", student_id);
}

// Function to add an exam
void add_exam(FILE *output, int exam_id, const char *exam_type_str, const char *exam_info_str) {
    if (exam_id <= 0 || exam_id >= 500) {
        fprintf(output, "Invalid exam id\n");
        return;
    }
    if (find_exam_by_id(exam_id) != -1) {
        fprintf(output, "Exam: %d already exists\n", exam_id);
        return;
    }

    Exam new_exam;
    new_exam.exam_id = exam_id;

    // Determining the type of exam
    if (strcmp(exam_type_str, "WRITTEN") == 0) {
        new_exam.exam_type = WRITTEN;
        int duration = atoi(exam_info_str);
        if (duration < 40 || duration > 180) {
            fprintf(output, "Invalid duration\n");
            return;
        }
        new_exam.exam_info.duration = duration;
    } else if (strcmp(exam_type_str, "DIGITAL") == 0) {
        new_exam.exam_type = DIGITAL;
        if (!is_valid_string(exam_info_str, 2, 19)) {
            fprintf(output, "Invalid software\n");
            return;
        }
        strcpy(new_exam.exam_info.software, exam_info_str);
    } else {
        fprintf(output, "Invalid exam type\n");
        return;
    }

    // Add new exam
    exams[exam_count] = new_exam;
    exam_count++;
    fprintf(output, "Exam: %d added\n", exam_id);
}

// Function to add a grade
void add_grade(FILE *output, int exam_id, int student_id, int grade) {
    if (find_exam_by_id(exam_id) == -1) {
        fprintf(output, "Exam not found\n");
        return;
    }
    if (find_student_by_id(student_id) == -1) {
        fprintf(output, "Student not found\n");
        return;
    }
    if (!is_valid_grade(grade)) {
        fprintf(output, "Invalid grade\n");
        return;
    }

    // Add the grade
    grades[grade_count].exam_id = exam_id;
    grades[grade_count].student_id = student_id;
    grades[grade_count].grade = grade;
    grade_count++;
    fprintf(output, "Grade %d added for the student: %d\n", grade, student_id);
}

// Function to search for a student by ID
void search_student(FILE *output, int student_id) {
    int index = find_student_by_id(student_id);
    if (index == -1) {
        fprintf(output, "Student not found\n");
        return;
    }
    fprintf(output, "ID: %d, Name: %s, Faculty: %s\n", students[index].student_id, students[index].name, students[index].faculty);
}

// Function to search for a grade by exam ID and student ID
void search_grade(FILE *output, int exam_id, int student_id) {
    int exam_index = find_exam_by_id(exam_id);
    if (exam_index == -1) {
        fprintf(output, "Exam not found\n");
        return;
    }
    int student_index = find_student_by_id(student_id);
    if (student_index == -1) {
        fprintf(output, "Student not found\n");
        return;
    }

    for (int i = 0; i < grade_count; i++) {
        if (grades[i].exam_id == exam_id && grades[i].student_id == student_id) {
            char exam_info[50];
            if (exams[exam_index].exam_type == WRITTEN) {
                sprintf(exam_info, "%d", exams[exam_index].exam_info.duration);
            } else {
                sprintf(exam_info, "%s", exams[exam_index].exam_info.software);
            }
            fprintf(output, "Exam: %d, Student: %d, Name: %s, Grade: %d, Type: %s, Info: %s\n",
                    exam_id, student_id, students[student_index].name, grades[i].grade,
                    exams[exam_index].exam_type == WRITTEN ? "WRITTEN" : "DIGITAL", exam_info);
            return;
        }
    }
    fprintf(output, "Grade not found\n");
}

// Function to update exam
void update_exam(FILE *output, int exam_id, const char *new_exam_type_str, const char *new_exam_info_str) {
    int exam_index = find_exam_by_id(exam_id);
    if (exam_index == -1) {
        fprintf(output, "Exam not found\n");
        return;
    }

    // Update exam type and info
    if (strcmp(new_exam_type_str, "WRITTEN") == 0) {
        exams[exam_index].exam_type = WRITTEN;
        int duration = atoi(new_exam_info_str);
        if (duration < 40 || duration > 180) {
            fprintf(output, "Invalid duration\n");
            return;
        }
        exams[exam_index].exam_info.duration = duration;
    } else if (strcmp(new_exam_type_str, "DIGITAL") == 0) {
        exams[exam_index].exam_type = DIGITAL;
        if (!is_valid_string(new_exam_info_str, 2, 19)) {
            fprintf(output, "Invalid software\n");
            return;
        }
        strcpy(exams[exam_index].exam_info.software, new_exam_info_str);
    } else {
        fprintf(output, "Invalid exam type\n");
        return;
    }

    fprintf(output, "Exam: %d updated\n", exam_id);
}

// Function to update a grade
void update_grade(FILE *output, int exam_id, int student_id, int new_grade) {
    if (!is_valid_grade(new_grade)) {
        fprintf(output, "Invalid grade\n");
        return;
    }

    for (int i = 0; i < grade_count; i++) {
        if (grades[i].exam_id == exam_id && grades[i].student_id == student_id) {
            grades[i].grade = new_grade;
            fprintf(output, "Grade %d updated for the student: %d\n", new_grade, student_id);
            return;
        }
    }
    fprintf(output, "Grade not found\n");
}

// Function to delete a student and their grades
void delete_student(FILE *output, int student_id) {
    int student_index = find_student_by_id(student_id);
    if (student_index == -1) {
        fprintf(output, "Student not found\n");
        return;
    }

    // Delete the student
    for (int i = student_index; i < student_count - 1; i++) {
        students[i] = students[i + 1];
    }
    student_count--;

    // Delete grades for this student
    int i = 0;
    while (i < grade_count) {
        if (grades[i].student_id == student_id) {
            for (int j = i; j < grade_count - 1; j++) {
                grades[j] = grades[j + 1];
            }
            grade_count--;
        } else {
            i++;
        }
    }
    fprintf(output, "Student: %d deleted\n", student_id);
}

// Function to list all students
void list_all_students(FILE *output) {
    for (int i = 0; i < student_count; i++) {
        fprintf(output, "ID: %d, Name: %s, Faculty: %s\n", students[i].student_id, students[i].name, students[i].faculty);
    }
}


int main() {
    FILE *input = fopen("input.txt", "r");
    FILE *output = fopen("output.txt", "w");

    if (input == NULL || output == NULL) {
        printf("Error opening file.\n");
        return 1;
    }

    char command[54];
    while (fgets(command, sizeof(command), input)) {
        char cmd[20];
        sscanf(command, "%s", cmd);

        if (strcmp(cmd, "ADD_STUDENT") == 0) {
            int student_id;
            char name[20], faculty[30];
            sscanf(command, "ADD_STUDENT %d %s %s", &student_id, name, faculty);
            add_student(output, student_id, name, faculty);
        } else if (strcmp(cmd, "ADD_EXAM") == 0) {
            int exam_id;
            char exam_type[20], exam_info[20];
            sscanf(command, "ADD_EXAM %d %s %s", &exam_id, exam_type, exam_info);
            add_exam(output, exam_id, exam_type, exam_info);
        } else if (strcmp(cmd, "ADD_GRADE") == 0) {
            int exam_id, student_id, grade;
            sscanf(command, "ADD_GRADE %d %d %d", &exam_id, &student_id, &grade);
            add_grade(output, exam_id, student_id, grade);
        } else if (strcmp(cmd, "SEARCH_STUDENT") == 0) {
            int student_id;
            sscanf(command, "SEARCH_STUDENT %d", &student_id);
            search_student(output, student_id);
        } else if (strcmp(cmd, "SEARCH_GRADE") == 0) {
            int exam_id, student_id;
            sscanf(command, "SEARCH_GRADE %d %d", &exam_id, &student_id);
            search_grade(output, exam_id, student_id);
        } else if (strcmp(cmd, "UPDATE_EXAM") == 0) {
            int exam_id;
            char new_exam_type[20], new_exam_info[20];
            sscanf(command, "UPDATE_EXAM %d %s %s", &exam_id, new_exam_type, new_exam_info);
            update_exam(output, exam_id, new_exam_type, new_exam_info);
        } else if (strcmp(cmd, "UPDATE_GRADE") == 0) {
            int exam_id, student_id, new_grade;
            sscanf(command, "UPDATE_GRADE %d %d %d", &exam_id, &student_id, &new_grade);
            update_grade(output, exam_id, student_id, new_grade);
        } else if (strcmp(cmd, "DELETE_STUDENT") == 0) {
            int student_id;
            sscanf(command, "DELETE_STUDENT %d", &student_id);
            delete_student(output, student_id);
        } else if (strcmp(cmd, "LIST_ALL_STUDENTS") == 0) {
            list_all_students(output);
        } else if (strcmp(cmd, "END") == 0) {
            break;
        }
    }

    fclose(input);
    fclose(output);
    return 0;
}
