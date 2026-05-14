//Ilya Pushkarev

import java.util.*;

public class AVL {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AVLTree<Integer, String> tree = new AVLTree<>();
        
        int n = scanner.nextInt();
        scanner.nextLine();
        
        for (int i = 0; i < n; i++) {
            String[] command = scanner.nextLine().split(" ");
            
            switch (command[0]) {
                case "ADD":
                    tree.add(Integer.parseInt(command[1]), command[2]);
                    break;
                case "LOOKUP":
                    tree.lookup(Integer.parseInt(command[1]));
                    break;
                case "DELETE":
                    tree.delete(Integer.parseInt(command[1]));
                    break;
                case "PRINT_ROTATIONS":
                    tree.printRotations();
                    break;
            }
        }
        
        scanner.close();
    }
}

class Node<K extends Comparable<K>, V> {
    K key;
    int height;
    V value;
    Node<K, V> left, right;

    Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.height = 1;
    }
}

class AVLTree<K extends Comparable<K>, V> {
    private Node<K, V> root;
    private int rotationCount;

    private int height(Node<K, V> node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node<K, V> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node<K, V> rightRotate(Node<K, V> y) {
        Node<K, V> x = y.left;
        Node<K, V> T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        rotationCount++;
        return x;
    }

    private Node<K, V> leftRotate(Node<K, V> x) {
        Node<K, V> y = x.right;
        Node<K, V> T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        rotationCount++;
        return y;
    }

    public void add(K key, V value) {
        Node<K, V> existingNode = find(root, key);
        if (existingNode != null) {
            System.out.println("KEY ALREADY EXISTS");
            return;
        }
        root = insert(root, key, value);
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null)
            return new Node<>(key, value);

        int cmp = key.compareTo(node.key);
        if (cmp < 0)
            node.left = insert(node.left, key, value);
        else if (cmp > 0)
            node.right = insert(node.right, key, value);

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);

        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);

        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void lookup(K key) {
        Node<K, V> node = find(root, key);
        if (node == null) {
            System.out.println("KEY NOT FOUND");
        } else {
            System.out.println(node.value);
        }
    }

    private Node<K, V> find(Node<K, V> node, K key) {
        if (node == null)
            return null;

        int cmp = key.compareTo(node.key);
        if (cmp == 0)
            return node;

        return cmp < 0 ? find(node.left, key) : find(node.right, key);
    }

    public void delete(K key) {
        if (find(root, key) == null) {
            System.out.println("KEY NOT FOUND");
            return;
        }
        root = deleteNode(root, key);
    }

    private Node<K, V> deleteNode(Node<K, V> node, K key) {
        if (node == null)
            return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0)
            node.left = deleteNode(node.left, key);
        else if (cmp > 0)
            node.right = deleteNode(node.right, key);
        else {
            if (node.left == null || node.right == null) {
                Node<K, V> temp = (node.left != null) ? node.left : node.right;

                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                Node<K, V> temp = minValueNode(node.right);
                node.key = temp.key;
                node.value = temp.value;
                node.right = deleteNode(node.right, temp.key);
            }
        }

        if (node == null)
            return null;

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0)
            return rightRotate(node);

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0)
            return leftRotate(node);

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private Node<K, V> minValueNode(Node<K, V> node) {
        Node<K, V> current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    public void printRotations() {
        System.out.println(rotationCount);
    }
}