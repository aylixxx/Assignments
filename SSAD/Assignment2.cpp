#include <iostream>
#include <string>
#include <vector>
#include <set>
#include <map>
#include <algorithm>
#include <memory>
#include <sstream>

using namespace std;

// Forward declarations
class Animal;
class Fish;
class Bird;
class Mouse;
class BetterFish;
class BetterBird;
class BetterMouse;
class Monster;
template<typename T> class Cage;
template<typename T> class Aquarium;
template<typename T> class Freedom;

template<> class Cage<Fish> { public: Cage() = delete; };
template<> class Aquarium<Bird> { public: Aquarium() = delete; };

// Base Animal class
class Animal {
protected:
    const string name;
    int daysLived;
public:
    Animal(const string& name, int days) : name(name), daysLived(days) {}
    virtual ~Animal() {}
    int getDaysLived() const { return daysLived; }
    string getName() const { return name; }
    virtual void sayName() const = 0;
    virtual void attack(Animal* other) = 0;
    virtual void setDaysLived(int newValue) { daysLived = newValue; }
    virtual bool isBetter() const { return false; }
    virtual bool isMonster() const { return false; }
    virtual int getMaxDays() const { return 10; }
};

// Comparator for sorting animals
struct AnimalComparator {
    bool operator()(const Animal* a, const Animal* b) const {
        if (a->getDaysLived() != b->getDaysLived())
            return a->getDaysLived() < b->getDaysLived();
        return a->getName() < b->getName();
    }
};

// Base container class
class BaseContainer {
public:
    virtual ~BaseContainer() {}
    virtual void add(Animal* animal) = 0;
    virtual void removeAt(int pos) = 0;
    virtual Animal* get(int pos) = 0;
    virtual size_t size() const = 0;
    virtual void clear() = 0;
    virtual void clearWithoutDelete() = 0;
    virtual void incrementDays() = 0;
};

// Generic container implementation
template<typename T>
class Container : public BaseContainer {
    multiset<T*, AnimalComparator> animals;
public:
    void add(Animal* animal) override {
        T* t = dynamic_cast<T*>(animal);
        if (t) animals.insert(t);
        else delete animal;
    }
    void removeAt(int pos) override {
        if (pos < 0 || pos >= animals.size()) return;
        auto it = animals.begin();
        advance(it, pos);
        delete *it;
        animals.erase(it);
    }
    Animal* get(int pos) override {
        if (pos < 0 || pos >= animals.size()) return nullptr;
        auto it = animals.begin();
        advance(it, pos);
        return *it;
    }
    size_t size() const override { return animals.size(); }
    void clear() override {
        for (auto a : animals) delete a;
        animals.clear();
    }
    void clearWithoutDelete() override { animals.clear(); }
    void incrementDays() override {
        vector<T*> temp;
        for (auto a : animals) temp.push_back(a);
        clearWithoutDelete();
        for (auto a : temp) {
            a->setDaysLived(a->getDaysLived() + 1);
            animals.insert(a);
        }
    }
};

template<typename T> class Cage : public Container<T> {};
template<typename T> class Aquarium : public Container<T> {};
template<typename T> class Freedom : public Container<T> {};

// Animals implementations
class Fish : public Animal {
public:
    Fish(const string& name, int days) : Animal(name, days) {}
    void sayName() const override {
        cout << "My name is " << name << ", days lived: " << daysLived << endl;
    }
    void attack(Animal* other) override {
        other->setDaysLived(11);
    }
};

class Bird : public Animal {
public:
    Bird(const string& name, int days) : Animal(name, days) {}
    void sayName() const override {
        cout << "My name is " << name << ", days lived: " << daysLived << endl;
    }
    void attack(Animal* other) override {
        other->setDaysLived(11);
    }
};

class Mouse : public Animal {
public:
    Mouse(const string& name, int days) : Animal(name, days) {}
    void sayName() const override {
        cout << "My name is " << name << ", days lived: " << daysLived << endl;
    }
    void attack(Animal* other) override {
        other->setDaysLived(11);
    }
};

class BetterFish : public Fish {
public:
    BetterFish(const string& name, int days) : Fish(name, days) {}
    BetterFish(Fish& fish) : Fish(fish.getName(), (fish.getDaysLived() + 1) / 2) {}
    bool isBetter() const override { return true; }
};

class BetterBird : public Bird {
public:
    BetterBird(const string& name, int days) : Bird(name, days) {}
    BetterBird(Bird& bird) : Bird(bird.getName(), (bird.getDaysLived() + 1) / 2) {}
    bool isBetter() const override { return true; }
};

class BetterMouse : public Mouse {
public:
    BetterMouse(const string& name, int days) : Mouse(name, days) {}
    BetterMouse(Mouse& mouse) : Mouse(mouse.getName(), (mouse.getDaysLived() + 1) / 2) {}
    bool isBetter() const override { return true; }
};

class Monster : public Animal {
public:
    Monster(const string& name) : Animal(name, 1) {}
    Monster(Animal& animal) : Animal(animal.getName(), 1) {}
    void sayName() const override {
        cout << "My name is " << name << ", days lived: " << daysLived << endl;
    }
    void attack(Animal* other) override {
        other->setDaysLived(11);
    }
    bool isMonster() const override { return true; }
    int getMaxDays() const override { return 1; }
};

// Global containers
Cage<Bird> birdCage;
Cage<BetterBird> betterBirdCage;
Cage<Mouse> mouseCage;
Cage<BetterMouse> betterMouseCage;
Aquarium<Fish> fishAquarium;
Aquarium<BetterFish> betterFishAquarium;
Aquarium<Mouse> mouseAquarium;
Aquarium<BetterMouse> betterMouseAquarium;
Freedom<Animal> freedom;

map<string, BaseContainer*> containerMap = {
    {"Cage<B>", &birdCage}, {"Cage<BB>", &betterBirdCage},
    {"Cage<M>", &mouseCage}, {"Cage<BM>", &betterMouseCage},
    {"Aquarium<F>", &fishAquarium}, {"Aquarium<BF>", &betterFishAquarium},
    {"Aquarium<M>", &mouseAquarium}, {"Aquarium<BM>", &betterMouseAquarium},
    {"Freedom", &freedom}
};

map<string, string> typeMap = {
    {"M", "Mouse"}, {"BM", "BetterMouse"},
    {"F", "Fish"}, {"BF", "BetterFish"},
    {"B", "Bird"}, {"BB", "BetterBird"}
};

// Helper functions
Animal* createAnimal(const string& type, const string& name, int days) {
    if (type == "M") return new Mouse(name, days);
    if (type == "BM") return new BetterMouse(name, days);
    if (type == "F") return new Fish(name, days);
    if (type == "BF") return new BetterFish(name, days);
    if (type == "B") return new Bird(name, days);
    if (type == "BB") return new BetterBird(name, days);
    return nullptr;
}

string getContainerKey(const string& cont, const string& type) {
    if (cont == "Freedom") return "Freedom";
    return cont + "<" + type + ">";
}

// Command handlers
void handleCreate(const vector<string>& tokens) {
    if (tokens.size() != 6) return;
    string type = tokens[1], name = tokens[2], cont = tokens[4];
    int days;
    try {
        days = stoi(tokens[5]);
    } catch (...) {
        return;
    }
    string key = getContainerKey(cont, type);
    if (containerMap.find(key) == containerMap.end()) return;
    Animal* animal = createAnimal(type, name, days);
    if (!animal) return;
    containerMap[key]->add(animal);
    animal->sayName();
}

void handleApplySubstance(const vector<string>& tokens) {
    if (tokens.size() != 4) return;
    if (tokens.size() < 3) return;
    string cont = tokens[1], type = tokens[2];
    int pos;
    try {
        pos = stoi(tokens[3]);
    } catch (...) {
        return;
    }
    if (cont == "Freedom") {
        cout << "Substance cannot be applied in freedom" << endl;
        return;
    } else {
        if (tokens.size() != 4) return;
        string type = tokens[2];
        int pos = stoi(tokens[3]);
    }
    string key = getContainerKey(cont, type);
    BaseContainer* container = containerMap[key];
    if (!container || pos < 0 || pos >= container->size()) {
        cout << "Animal not found" << endl;
        return;
    }
    Animal* animal = container->get(pos);
    if (!animal || animal->isMonster()) return;

    if (!animal->isBetter()) {
        Animal* newAnimal = nullptr;
        string newType;
        if (type == "M") { newAnimal = new BetterMouse(*dynamic_cast<Mouse*>(animal)); newType = "BM"; }
        else if (type == "F") { newAnimal = new BetterFish(*dynamic_cast<Fish*>(animal)); newType = "BF"; }
        else if (type == "B") { newAnimal = new BetterBird(*dynamic_cast<Bird*>(animal)); newType = "BB"; }
        if (newAnimal) {
            container->removeAt(pos);
            containerMap[getContainerKey(cont, newType)]->add(newAnimal);
        }
    } else {
        Monster* monster = new Monster(*animal);
        container->removeAt(pos);
        freedom.add(monster);
        container->clear();
    }
}

void handleRemoveSubstance(const vector<string>& tokens) {
    if (tokens.size() != 4) return;
    string cont = tokens[1], type = tokens[2];
    int pos;
    try {
        pos = stoi(tokens[3]);
    } catch (...) {
        return;
    }
    if (cont == "Freedom") {
        cout << "Substance cannot be removed in freedom" << endl;
        return;
    }
    string key = getContainerKey(cont, type);
    BaseContainer* container = containerMap[key];
    if (!container || pos < 0 || pos >= container->size()) {
        cout << "Animal not found" << endl;
        return;
    }
    Animal* animal = container->get(pos);
    if (!animal || !animal->isBetter()) {
        cout << "Invalid substance removal" << endl;
        return;
    }

    int newDays = animal->getDaysLived() * 2;
    Animal* newAnimal = nullptr;
    string newType;
    if (type == "BM") { newAnimal = new Mouse(animal->getName(), newDays); newType = "M"; }
    else if (type == "BF") { newAnimal = new Fish(animal->getName(), newDays); newType = "F"; }
    else if (type == "BB") { newAnimal = new Bird(animal->getName(), newDays); newType = "B"; }
    if (newAnimal) {
        container->removeAt(pos);
        containerMap[getContainerKey(cont, newType)]->add(newAnimal);
    }
}

void handleAttack(const vector<string>& tokens) {
    if (tokens.size() < 4) return;
    string cont = tokens[1];
    if (cont == "Freedom") {
        if (tokens.size() != 4) return;
        cout << "Animals cannot attack in Freedom" << endl;
        return;
    }
    if (tokens.size() != 5) return;
    string type = tokens[2];
    int pos1, pos2;
    try {
        pos1 = stoi(tokens[3]);
        pos2 = stoi(tokens[4]);
    } catch (...) {
        cout << "Animal not found" << endl;
        return;
    }
    string key = getContainerKey(cont, type);
    BaseContainer* container = containerMap[key];
    if (!container || pos1 < 0 || pos1 >= container->size() || pos2 < 0 || pos2 >= container->size() || pos1 == pos2) {
        cout << "Animal not found" << endl;
        return;
    }
    Animal* a1 = container->get(pos1);
    Animal* a2 = container->get(pos2);
    if (!a1 || !a2) return;
    cout << typeMap[type] << " is attacking" << endl;
    container->removeAt(pos2);
}

void handleTalk(const vector<string>& tokens) {
    string cont, type = "";
    int pos;
    try {
        cont = tokens[1];
        if (cont == "Freedom") {
            pos = stoi(tokens[2]);
        } else {
            type = tokens[2];
            pos = stoi(tokens[3]);
        }
    } catch (...) {
        cout << "Animal not found" << endl;
        return;
    }
    string key = getContainerKey(cont, type);
    BaseContainer* container = containerMap[key];
    if (!container || pos < 0 || pos >= container->size()) {
        cout << "Animal not found" << endl;
        return;
    }
    Animal* animal = container->get(pos);
    if (!animal) {
        cout << "Animal not found" << endl;
        return;
    }
    animal->sayName();
}

void handlePeriod() {
    vector<BaseContainer*> containers = {
        &birdCage, &betterBirdCage, &mouseCage, &betterMouseCage,
        &fishAquarium, &betterFishAquarium, &mouseAquarium, &betterMouseAquarium,
        &freedom
    };
    for (auto cont : containers) {
        vector<Animal*> temp;
        for (size_t i = 0; i < cont->size(); ++i)
            temp.push_back(cont->get(i));
        cont->clearWithoutDelete();
        for (Animal* a : temp) {
            a->setDaysLived(a->getDaysLived() + 1);
            if (a->getDaysLived() > a->getMaxDays()) {
                cout << a->getName() << " has died of old days" << endl;
                delete a;
            } else {
                cont->add(a);
            }
        }
    }
}

int main() {
    int C;
    cin >> C;
    cin.ignore();
    vector<vector<string>> commands;
    for (int i = 0; i < C; ++i) {
        string line;
        getline(cin, line);
        istringstream iss(line);
        vector<string> tokens;
        string token;
        while (iss >> token) tokens.push_back(token);
        commands.push_back(tokens);
    }

    for (const auto& tokens : commands) {
        if (tokens.empty()) continue;
        string cmd = tokens[0];
        if (cmd == "CREATE") handleCreate(tokens);
        else if (cmd == "APPLY_SUBSTANCE") handleApplySubstance(tokens);
        else if (cmd == "REMOVE_SUBSTANCE") handleRemoveSubstance(tokens);
        else if (cmd == "ATTACK") handleAttack(tokens);
        else if (cmd == "TALK") handleTalk(tokens);
        else if (cmd == "PERIOD") handlePeriod();
    }
    return 0;
}