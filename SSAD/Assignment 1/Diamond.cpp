#include <iostream>
#include <vector>
#include <type_traits>
#include <assert.h>

using namespace std;

// Base class representing a person with first and last name
class Person {
public:
    virtual ~Person() = default;
    virtual string getEmail() = 0;
    static int count;
    Person(string first, string last) : fname{first}, lname{last} { count++; }
protected:
    Person(const Person&) = delete;
    string fname;
    string lname;
};
int Person::count = 0;

// Student class with student-specific email format
class Student : virtual public Person {
public:
    Student(string first, string last) : Person{first, last} {}
    
    // Returns email in format: f.lastname@students.org
    string getEmail() override {
        string lowercaseLname = lname;
        for(char& c : lowercaseLname) c = tolower(c);
        return string(1, tolower(fname[0])) + "." + lowercaseLname + "@students.org";
    }
};

// Employee class with employee-specific email format
class Employee : virtual public Person {
public:
    Employee(string first, string last) : Person{first, last} {}
    // Returns email in format: f.lastname@employees.org
    string getEmail() override {
        string lowercaseLname = lname;
        for(char& c : lowercaseLname) c = tolower(c);
        return string(1, tolower(fname[0])) + "." + lowercaseLname + "@employees.org"; 
    }
};

// Teaching Assistant class that inherits from both Student and Employee
class TA : public Student, public Employee {
public:
    TA(string first, string last) : Person{first, last}, Student{first, last}, Employee{first, last} {}
    // Uses Employee email format for TAs
    string getEmail() override {
        return Employee::getEmail();
    }
};

int main() {
    static_assert(is_abstract_v<Person>);
    static_assert(is_base_of_v<Person, Employee>);
    static_assert(is_base_of_v<Person, Student>);
    static_assert(is_base_of_v<Person, TA>);
    static_assert(is_base_of_v<Employee, TA>);
    static_assert(is_base_of_v<Student, TA>);
    static_assert(is_polymorphic_v<Student>);
    static_assert(is_polymorphic_v<Employee>);
    static_assert(is_polymorphic_v<TA>);
    static_assert(!is_copy_constructible_v<Person>);
    static_assert(has_virtual_destructor_v<Person>);
    
    string fname, lname;
    
    cin >> fname >> lname;
    Student* s = new Student(fname, lname);
    assert(Person::count == 1);
    
    cin >> fname >> lname;
    Employee* e = new Employee(fname, lname);
    assert(Person::count == 2);
    
    cin >> fname >> lname;
    TA* t = new TA(fname, lname);
    assert(Person::count == 3);

    vector<Person*> people = {s, e, t};
    for(auto& p: people) {
        cout << p->getEmail() << endl;
        delete p;
    }
}