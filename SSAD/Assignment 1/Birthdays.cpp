#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

// Date class to represent a date with day, month, year
class Date {
public:
    Date(int d, int m, int y) : day(d), month(m), year(y) {}
    
    // Getters for day, month, year
    int getYear() const { return year; }
    int getMonth() const { return month; }
    int getDay() const { return day; }
    
    // Check if the date is valid
    bool isValid() {
        if (month < 1 || month > 12 || day < 1 || year < 0) return false;
        
        // Handle months with 30 days
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return day <= 30;
        }
        // Handle February and leap years
        else if (month == 2) {
            bool isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            return day <= (isLeap ? 29 : 28);
        }
        // Months with 31 days
        else {
            return day <= 31;
        }
    }
    
    // Overloaded subtraction operator to calculate days between two dates
    int operator-(const Date& other) const {
        int days = 0;
        Date start = other;
        Date end = *this;
        
        // Ensure start is the earlier date
        if (year < other.year || (year == other.year && month < other.month) ||
            (year == other.year && month == other.month && day < other.day)) {
            start = *this;
            end = other;
        }
        
        // Add days for each full year between start and end
        for (int y = start.year; y < end.year; y++) {
            days += ((y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)) ? 366 : 365;
        }
        
        // Subtract days from start date's year
        int monthDays[] = {0,31,28,31,30,31,30,31,31,30,31,30,31};
        for (int m = 1; m < start.month; m++) {
            days -= monthDays[m];
            if (m == 2 && ((start.year % 4 == 0 && start.year % 100 != 0) || 
                (start.year % 400 == 0))) days--;
        }
        days -= start.day;
        
        // Add days from end date's year
        for (int m = 1; m < end.month; m++) {
            days += monthDays[m];
            if (m == 2 && ((end.year % 4 == 0 && end.year % 100 != 0) || 
                (end.year % 400 == 0))) days++;
        }
        days += end.day;
        
        return abs(days);
    }
    
private:
    int day, month, year;
};

// Comparator function to sort dates in ascending order
bool cmp(const Date& a, const Date& b) {
    if (a.getYear() != b.getYear()) return a.getYear() < b.getYear();
    if (a.getMonth() != b.getMonth()) return a.getMonth() < b.getMonth();
    return a.getDay() < b.getDay();
}

int main() {
    int d1, m1, y1, d2, m2, y2, d3, m3, y3;
    cin >> d1 >> m1 >> y1 >> d2 >> m2 >> y2 >> d3 >> m3 >> y3;
    
    Date a(d1, m1, y1);
    Date b(d2, m2, y2);
    Date c(d3, m3, y3);

    vector<Date> v = {a, b, c};
    
    if(!a.isValid() || !b.isValid() || !c.isValid()) {
        cout << "Invalid Input" << endl; 
        return 0;
    }
    // define your comparator function globally
    sort(v.begin(), v.end(), cmp);
    
    // overload the - operator for this to work
    cout << v[2] - v[0] << endl;
    return 0;
}