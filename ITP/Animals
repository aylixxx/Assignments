import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public final class Main {
    public List<Animal> readAnimals() {
        return null;
    }
    public void runSimulation(int days, float grassAmount, List<Animal> animals) { }
    public void printAnimals(List<Animal> animals) { }

    public void removeDeadAnimals(List<Animal> animals) { }
    /**
     * Main method to drive the simulation. Reads input data, validates parameters,
     * initializes objects, and performs animal interactions in the field.
     **/
    public static void main(String[] args) {
        try {
            File input = new File("input.txt");
            Scanner scanner = new Scanner(input);
            int d = Integer.parseInt(scanner.nextLine());
            float g = Float.parseFloat(scanner.nextLine());
            int n = Integer.parseInt(scanner.nextLine());
            List<Animal> animals = new ArrayList<>();
            final int maxGRASS = 100;
            if (g < 0 || g > maxGRASS) {
                System.out.println(new GrassOutOfBoundsException().getMessage());
                System.exit(0);
            }
            Field field = new Field(g);
            final int maxDAYS = 30;
            final int maxANIMALS = 20;
            if (d < 1 || d > maxDAYS || n < 1 || n > maxANIMALS) {
                System.out.println(new InvalidInputsException().getMessage());
                System.exit(0);
            }
            for (int i = 0; i < n; i++) {
                String inputs = scanner.nextLine();
                String[] subSTR = inputs.split(" ");
                final int const4 = 4;
                if (subSTR.length != const4) {
                    System.out.println(new InvalidNumberOfAnimalParametersException().getMessage());
                    System.exit(0);
                }
                String animal = subSTR[0];
                float weight;
                float energy;
                float speed;
                final int const1 = 1;
                final int const2 = 2;
                final int const3 = 3;
                weight = Float.parseFloat(subSTR[const1]);
                energy = Float.parseFloat(subSTR[const3]);
                speed = Float.parseFloat(subSTR[const2]);
                final int minWEIGHT = 5;
                final int maxWEIGHT = 200;
                if (weight < minWEIGHT || weight > maxWEIGHT) {
                    System.out.println(new WeightOutOfBoundsException().getMessage());
                    System.exit(0);
                }
                final int minSPEED = 5;
                final int maxSPEED = 60;
                if (speed < minSPEED || speed > maxSPEED) {
                    System.out.println(new SpeedOutOfBoundsException().getMessage());
                    System.exit(0);
                }
                if (energy < 0 || energy > maxGRASS) {
                    System.out.println(new EnergyOutOfBoundsException().getMessage());
                    System.exit(0);
                }
                switch (animal) {
                    case "Zebra":
                        animals.add(new Zebra(weight, speed, energy));
                        break;
                    case "Boar":
                        animals.add(new Boar(weight, speed, energy));
                        break;
                    case "Lion":
                        animals.add(new Lion(weight, speed, energy));
                        break;
                    default:
                        System.out.println(new InvalidInputsException().getMessage());
                        System.exit(0);
                }
            }
            int j = 0;
            while (j < animals.size()) {
                if (animals.get(j).getEnergy() <= 0) {
                    animals.remove(j);
                    j--;
                }
                j++;
            }
            for (int i = 0; i < d; i++) {
                j = 0;
                while (j < animals.size()) {
                    String animal = animals.get(j).getClass().getName();
                    switch (animal) {
                        case "Zebra":
                            ((Zebra) animals.get(j)).grazelnTheField((Zebra) animals.get(j), field);
                            break;
                        case "Lion":
                            String output = ((Lion) animals.get(j)).choosePrey(animals, (Lion) animals.get(j), j);
                            if (!output.equals("check")) {
                                System.out.println(output);
                            }
                            break;
                        case "Boar":
                            ((Boar) animals.get(j)).grazelnTheField((Boar) animals.get(j), field);
                            String out = ((Boar) animals.get(j)).choosePrey(animals, (Boar) animals.get(j), j);
                            if (!out.equals("check")) {
                                System.out.println(out);
                            }
                            break;
                        default:
                            System.out.println(new InvalidInputsException().getMessage());
                            System.exit(0);
                    }
                    j++;
                }
                field.makeGrassGrow();
                j = 0;
                while (j < animals.size()) {
                    animals.get(j).decrementEnergy(-1);
                    if (animals.get(j).getEnergy() <= 0) {
                        animals.remove(j);
                        j--;
                    }
                    j++;
                }
            }
            j = 0;
            while (j < animals.size()) {
                animals.get(j).makeSound();
                j++;
            }
        } catch (Exception e) {
            System.out.println(new InvalidInputsException().getMessage());
        }
    }
}
/**
 * Abstract base class representing a generic animal with properties like weight, speed, and energy.
 */
abstract class Animal {
    public static final float MIN_SPEED = 5;
    public static final float MAX_SPEED = 60;
    public static final float MIN_ENERGY = 0;
    public static final float MAX_ENERGY = 100;
    public static final float MIN_WEIGHT = 5;
    public static final float MAX_WEIGHT = 200;
    private float weight;
    private float speed;
    private float energy;
    /**
     * Constructor to initialize an animal with weight, speed, and energy.
     *
     * @param weight Weight of the animal.
     * @param speed Speed of the animal.
     * @param energy Energy level of the animal.
     */
    protected Animal(float weight, float speed, float energy) {
        this.weight = weight;
        this.speed = speed;
        this.energy = energy;
    }

    public float getWeight() {
        return weight;
    }
    public float getSpeed() {
        return speed;
    }
    public float getEnergy() {
        return energy;
    }
    /**
     * This method modifies the animal's energy level by incrementing it with a specified value,
     * ensuring that the energy remains within the acceptable range.
     *
     * @param addEnergy Amount to adjust energy by.
     */
    public void decrementEnergy(float addEnergy) {
        this.energy += addEnergy;
        if (this.energy > MAX_ENERGY) {
            this.energy = MAX_ENERGY;
        }
    }

    abstract void makeSound();

    public void eat(List<Animal> animals, Field field) { }


}
/**
 * Class representing a lion, a carnivorous animal.
 */
class Lion extends Animal implements Carnivore {
    public Lion(float weight, float speed, float energy) {
        super(weight, speed, energy);
    }
    public void makeSound() {
        System.out.println("Roar");
    }

    @Override
    public String choosePrey(List<Animal> animals, Animal t, int i) {
        return Carnivore.super.choosePrey(animals, t, i);
    }
}
/**
 * Class representing a zebra, a herbivorous animal.
 */
class Zebra extends Animal implements Herbivore {
    public Zebra(float weight, float speed, float energy) {
        super(weight, speed, energy);
    }
    @Override
    void makeSound() {
        System.out.println("Ihoho");
    }

    @Override
    public void grazelnTheField(Animal animal, Field field) {
        Herbivore.super.grazelnTheField(animal, field);
    }
}
/**
 * Class representing a boar, an omnivorous animal.
 */
class Boar extends Animal implements Omnivores {
    public Boar(float weight, float speed, float energy) {
        super(weight, speed, energy);
    }
    @Override
    void makeSound() {
        System.out.println("Oink");
    }

    @Override
    public void grazelnTheField(Animal animal, Field field) {
        Omnivores.super.grazelnTheField(animal, field);
    }

    @Override
    public String choosePrey(List<Animal> animals, Animal t, int i) {
        return Omnivores.super.choosePrey(animals, t, i);
    }
}

class WeightOutOfBoundsException extends Exception {
    public String getMessage() {
        return "The weight is out of bounds";
    }
}
class EnergyOutOfBoundsException extends Exception {
    public String getMessage() {
        return "The energy is out of bounds";
    }
}
class SpeedOutOfBoundsException extends Exception {
    public String getMessage() {
        return "The speed is out of bounds";
    }
}
class GrassOutOfBoundsException extends Exception {
    public String getMessage() {
        return "The grass is out of bounds";
    }
}
class InvalidInputsException extends Exception {
    public String getMessage() {
        return "Invalid inputs";
    }
}
class InvalidNumberOfAnimalParametersException extends Exception {
    public String getMessage() {
        return "Invalid number of animal parameters";
    }
}
/**
 * Represents a field with a specific amount of grass.
 * Provides methods for grass growth and modification.
 */
class Field {
    private float grassAmount;
    public Field(float grassAmount) {
        this.grassAmount = grassAmount;
    }
    public float getGrassAmount() {
        return grassAmount;
    }
    protected final int maxGrass = 100;
    /**
     * Simulates grass growth by doubling the current grass amount.
     * Ensures that the grass amount does not exceed the maximum allowed limit.
     */
    public void makeGrassGrow() {
        grassAmount = 2 * grassAmount;
        if (grassAmount > maxGrass) {
            grassAmount = maxGrass;
        }
    }
    public void editField(float grass) {
        this.grassAmount -= grass;
    }
}
/**
 * Interface representing a carnivorous animal capable of choosing and hunting prey.
 * Provides a default implementation for selecting a prey from a list of animals.
 */
interface Carnivore {
    /**
     * Chooses a prey for the carnivorous animal from the provided list of animals.
     * Ensures that self-hunting, cannibalism, and attacking stronger or faster prey are not allowed.
     *
     * @param animals The list of animals among which the prey is selected.
     * @param t The current carnivorous animal selecting its prey.
     * @param i The index of the current carnivorous animal in the list.
     * @return A message indicating the result of the prey selection:
     *         <ul>
     *             <li>"check" if the prey selection was successful.</li>
     *             <li>"Self-hunting is not allowed" if the animal tries to hunt itself.</li>
     *             <li>"Cannibalism is not allowed" if the animal tries to hunt another of its kind.</li>
     *             <li>"The prey is too strong or too fast to attack" if the prey is stronger or faster.</li>
     *         </ul>
     */
    default String choosePrey(List<Animal> animals, Animal t, int i) {
        String message = "check";
        String animal = t.getClass().getName();
        String prey;
        if (animals.size() == 1) {
            message = "Self-hunting is not allowed";
        } else if (i == (animals.size() - 1)) {
            prey = animals.get(0).getClass().getName();
            if (!(prey.equals(animal))) {
                if ((animals.get(i).getEnergy() > animals.get(0).getEnergy()) || (animals.get(i).getSpeed()
                        > animals.get(0).getSpeed())) {
                    animals.get(i).decrementEnergy(animals.get(0).getWeight());
                    animals.get(0).decrementEnergy(-1 * (animals.get(0).getEnergy()));
                    animals.remove(0);
                } else {
                    message = "The prey is too strong or too fast to attack";
                }
            } else {
                message = "Cannibalism is not allowed";
            }
        } else {
            if (!((animals.get(i + 1).getClass().getName()).equals(animal))) {
                if ((animals.get(i).getEnergy() > animals.get(i + 1).getEnergy()) || (animals.get(i).getSpeed()
                        > animals.get(i + 1).getSpeed())) {
                    animals.get(i).decrementEnergy(animals.get(i + 1).getWeight());
                    animals.get(i + 1).decrementEnergy(-1 * (animals.get(i + 1).getEnergy()));
                    animals.remove(i + 1);
                } else {
                    message = "The prey is too strong or too fast to attack";
                }
            } else {
                message = "Cannibalism is not allowed";
            }
        }
        return message;
    }
}
/**
 * Interface representing herbivorous animals that are capable of grazing in a field.
 * This interface provides a default implementation for grazing, where herbivores consume grass from a field
 * based on their weight, and their energy decreases accordingly.
 */
interface Herbivore {
    int CONST_10 = 10;
    /**
     * Allows a herbivorous animal to graze in the field. The amount of grass consumed is determined by the
     * animal's weight divided by {@link #CONST_10}. If there is enough grass available in the field,
     * the animal's energy is decreased by the amount corresponding to the grass consumed,
     * and the field's grass amount is reduced.
     *
     * @param grazer The herbivorous animal that is grazing in the field.
     * @param field The field in which the animal is grazing.
     */
    default void grazelnTheField(Animal grazer, Field field) {
        if ((grazer.getWeight() / CONST_10) < field.getGrassAmount()) {
            grazer.decrementEnergy(grazer.getWeight() / CONST_10);
            field.editField(grazer.getWeight() / CONST_10);
        }
    }
}
interface Omnivores extends Herbivore, Carnivore {
}
