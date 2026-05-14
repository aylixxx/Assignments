//Ilya Pushkarev

import java.util.Scanner;

public class RedBlack {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RedBlackTree<Integer, String> tree = new RedBlackTree<>();

        int n = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine().trim();
            String[] parts = line.split(" ");
            String operation = parts[0];

            switch (operation) {
                case "ADD":
                    int key = Integer.parseInt(parts[1]);
                    String value = parts[2];
                    tree.add(key, value);
                    break;
                case "LOOKUP":
                    key = Integer.parseInt(parts[1]);
                    tree.lookup(key);
                    break;
                case "DELETE":
                    key = Integer.parseInt(parts[1]);
                    tree.delete(key);
                    break;
                case "PRINT_ROTATIONS":
                    tree.printRotations();
                    break;
                case "PRINT_COUNT_BLACK_KEYS":
                    System.out.println(tree.getBlackCount());
                    break;
                case "PRINT_COUNT_RED_KEYS":
                    System.out.println(tree.getRedCount());
                    break;
            }
        }

        scanner.close();
    }
}

class Node<K extends Comparable<K>, V> {
    K key;
    V value;
    Node<K, V> left, right, parent;
    boolean isRed;

    Node(K key, V value, boolean isRed) {
        this.key = key;
        this.value = value;
        this.isRed = isRed;
        this.left = null;
        this.right = null;
        this.parent = null;
    }
}

class RedBlackTree<K extends Comparable<K>, V> {
    private Node<K, V> root;
    private Node<K, V> nil;
    private int rotationCount;
    private int redCount;
    private int blackCount;

    public RedBlackTree() {
        nil = new Node<>(null, null, false);
        nil.left = nil;
        nil.right = nil;
        nil.parent = nil;
        root = nil;
        rotationCount = 0;
        redCount = 0;
        blackCount = 0;
    }

    public void add(K key, V value) {
        Node<K, V> existing = find(key);
        if (existing != nil) {
            System.out.println("KEY ALREADY EXISTS");
            return;
        }

        Node<K, V> z = new Node<>(key, value, true);
        z.left = nil;
        z.right = nil;
        z.parent = nil;

        Node<K, V> y = nil;
        Node<K, V> x = root;

        while (x != nil) {
            y = x;
            int cmp = key.compareTo(x.key);
            if (cmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        z.parent = y;
        if (y == nil) {
            root = z;
        } else if (key.compareTo(y.key) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }

        redCount++;
        insertFixup(z);
    }

    private void insertFixup(Node<K, V> z) {
        while (z.parent.isRed) {
            if (z.parent == z.parent.parent.left) {
                Node<K, V> y = z.parent.parent.right;
                if (y.isRed) {
                    setColor(z.parent, false);
                    setColor(y, false);
                    setColor(z.parent.parent, true);
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    setColor(z.parent, false);
                    setColor(z.parent.parent, true);
                    rightRotate(z.parent.parent);
                }
            } else {
                Node<K, V> y = z.parent.parent.left;
                if (y.isRed) {
                    setColor(z.parent, false);
                    setColor(y, false);
                    setColor(z.parent.parent, true);
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    setColor(z.parent, false);
                    setColor(z.parent.parent, true);
                    leftRotate(z.parent.parent);
                }
            }
        }
        setColor(root, false);
    }

    private void leftRotate(Node<K, V> x) {
        Node<K, V> y = x.right;
        x.right = y.left;

        if (y.left != nil) {
            y.left.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;

        rotationCount++;
    }

    private void rightRotate(Node<K, V> x) {
        Node<K, V> y = x.left;
        x.left = y.right;

        if (y.right != nil) {
            y.right.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }

        y.right = x;
        x.parent = y;

        rotationCount++;
    }

    public void lookup(K key) {
        Node<K, V> node = find(key);
        if (node == nil) {
            System.out.println("KEY NOT FOUND");
        } else {
            System.out.println(node.value);
        }
    }

    private Node<K, V> find(K key) {
        Node<K, V> current = root;
        while (current != nil) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return nil;
    }

    public void delete(K key) {
        Node<K, V> z = find(key);
        if (z == nil) {
            System.out.println("KEY NOT FOUND");
            return;
        }

        Node<K, V> y = z;
        Node<K, V> x;
        boolean yOriginalColor = y.isRed;

        if (z.left == nil) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == nil) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            z.key = y.key;
            z.value = y.value;
            yOriginalColor = y.isRed;
            x = y.right;
            transplant(y, x);
        }

        if (!yOriginalColor) {
            deleteFixup(x);
        }

        if (yOriginalColor) {
            redCount--;
        } else {
            blackCount--;
        }
    }

    private void transplant(Node<K, V> u, Node<K, V> v) {
        if (u.parent == nil) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    private Node<K, V> minimum(Node<K, V> node) {
        while (node.left != nil) {
            node = node.left;
        }
        return node;
    }

    private void deleteFixup(Node<K, V> x) {
        while (x != root && !x.isRed) {
            if (x == x.parent.left) {
                Node<K, V> w = x.parent.right;
                if (w.isRed) {
                    setColor(w, false);
                    setColor(x.parent, true);
                    leftRotate(x.parent);
                    w = x.parent.right;
                }

                if (!w.left.isRed && !w.right.isRed) {
                    setColor(w, true);
                    x = x.parent;
                } else {
                    if (!w.right.isRed) {
                        setColor(w.left, false);
                        setColor(w, true);
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    setColor(w, x.parent.isRed);
                    setColor(x.parent, false);
                    setColor(w.right, false);
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node<K, V> w = x.parent.left;
                if (w.isRed) {
                    setColor(w, false);
                    setColor(x.parent, true);
                    rightRotate(x.parent);
                    w = x.parent.left;
                }

                if (!w.right.isRed && !w.left.isRed) {
                    setColor(w, true);
                    x = x.parent;
                } else {
                    if (!w.left.isRed) {
                        setColor(w.right, false);
                        setColor(w, true);
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    setColor(w, x.parent.isRed);
                    setColor(x.parent, false);
                    setColor(w.left, false);
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        setColor(x, false);
    }

    private void setColor(Node<K, V> node, boolean isRed) {
        if (node == nil) {
            return;
        }

        if (node.isRed) {
            redCount--;
        } else {
            blackCount--;
        }

        node.isRed = isRed;

        if (isRed) {
            redCount++;
        } else {
            blackCount++;
        }
    }

    public void printRotations() {
        System.out.println(rotationCount);
    }

    public int getRedCount() {
        return redCount;
    }

    public int getBlackCount() {
        return blackCount;
    }
}