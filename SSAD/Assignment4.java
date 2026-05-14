import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class Main {
    public static void main(String[] args) throws IOException {
        // Read input from the console
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(br.readLine()); // Number of input lines

        // Map to store directories by ID
        Map<Integer, Directory> directories = new HashMap<>();
        Directory root = new Directory(0, ""); // Root directory with ID 0
        directories.put(0, root);

        // Singleton factory for creating and caching file properties
        FilePropertiesFactory factory = FilePropertiesFactory.getInstance();

        // Process each input line
        for (int i = 0; i < N; i++) {
            String line = br.readLine();
            String[] parts = line.split(" +"); // Split input by spaces

            if (parts[0].equals("DIR")) { // Handle directory creation
                try {
                    int id = Integer.parseInt(parts[1]); // Directory ID
                    int parentId = 0;
                    String name;

                    if (parts.length == 3) { // If parent ID is empty
                        name = parts[2];
                    } else if (parts.length == 4) {
                        parentId = Integer.parseInt(parts[2]);
                        name = parts[3];
                    } else {
                        continue;
                    }

                    Directory parent = directories.get(parentId); // Get parent directory
                    Directory newDir = new Directory(id, name); // Create new directory
                    parent.addChild(newDir); // Add new directory to parent
                    directories.put(id, newDir); // Store new directory in the map
                } catch (NumberFormatException e) {
                }
            } else if (parts[0].equals("FILE")) { // Handle file creation
                try {
                    int parentId = Integer.parseInt(parts[1]);
                    String r = parts[2];
                    String owner = parts[3];
                    String group = parts[4];
                    double size = Double.parseDouble(parts[5]);
                    String filename = parts[6];
                    int lastDot = filename.lastIndexOf('.');
                    String ext = lastDot != -1 ? filename.substring(lastDot + 1) : ""; // File extension
                    boolean readOnly = r.equals("T"); // Check if file is read-only
                    FileProperties fp = factory.getFileProperties(ext, readOnly, owner, group); // Get file properties
                    File file = new File(filename, size, fp);
                    Directory parent = directories.get(parentId);

                    parent.addChild(file); // Add file to parent directory
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                }
            }
        }

        // Calculate the total size of all files using the SizeVisitor
        SizeVisitor sizeVisitor = new SizeVisitor();
        root.accept(sizeVisitor);
        double total = sizeVisitor.getTotal();
        total = Math.round(total * 100.0) / 100.0;

        // Print the total size
        if (total == (int) total) {
            System.out.printf("total: %dKB\n", (int) total);
        } else {
            System.out.printf("total: %sKB\n", total);
        }

        // Print the directory tree structure
        printTree(root);
    }

    // Method to print the directory tree structure
    private static void printTree(Directory root) {
        System.out.println(".");
        TreeIterator iterator = new TreeIterator(root);
        while (iterator.hasNext()) {
            TreeEntry entry = iterator.next();
            Node node = entry.getNode();
            List<Boolean> parentIsLastList = entry.getParentIsLastList();
            boolean isLast = entry.isLast();

            // Build indentation for the current node
            StringBuilder indent = new StringBuilder();
            for (Boolean isLastInParent : parentIsLastList) {
                if (isLastInParent) {
                    indent.append("    ");
                } else {
                    indent.append("│   ");
                }
            }

            // Add connector and node name
            String connector = isLast ? "└── " : "├── ";
            String line = indent.toString() + connector + node.getName();
            if (node instanceof File) {
                File file = (File) node;
                double size = file.getSize();
                String sizeFormat = size == (int) size ? "%.0fKB" : "%sKB";
                line += String.format(" (" + sizeFormat + ")", size);
            }
            System.out.println(line);
        }
    }
}

// Class representing file properties
final class FileProperties {
    private final String extension;
    private final boolean readOnly;
    private final String owner;
    private final String group;

    public FileProperties(String extension, boolean readOnly, String owner, String group) {
        this.extension = extension;
        this.readOnly = readOnly;
        this.owner = owner;
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileProperties that = (FileProperties) o;
        return readOnly == that.readOnly &&
                Objects.equals(extension, that.extension) &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extension, readOnly, owner, group);
    }
}

// Singleton factory for creating and caching file properties
class FilePropertiesFactory {
    private static final FilePropertiesFactory instance = new FilePropertiesFactory();
    private final Map<FileProperties, FileProperties> cache = new HashMap<>();

    private FilePropertiesFactory() {}

    public static FilePropertiesFactory getInstance() {
        return instance;
    }

    public FileProperties getFileProperties(String extension, boolean readOnly, String owner, String group) {
        FileProperties key = new FileProperties(extension, readOnly, owner, group);
        return cache.computeIfAbsent(key, k -> k); // Cache file properties
    }
}

// Abstract class representing a node in the file system
abstract class Node {
    protected String name;

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void accept(Visitor visitor);
}

// Class representing a directory
class Directory extends Node {
    private final List<Node> children = new ArrayList<>();
    private final int id;

    public Directory(int id, String name) {
        super(name);
        this.id = id;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public List<Node> getChildren() {
        return children;
    }

    public int getId() {
        return id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Iterator<TreeEntry> createIterator() {
        return new TreeIterator(this);
    }
}

// Class representing a file
class File extends Node {
    private final double size;
    private FileProperties fileProperties;

    public File(String name, double size, FileProperties fileProperties) {
        super(name);
        this.size = size;
        this.fileProperties = fileProperties;
    }

    public double getSize() {
        return size;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

// Visitor interface for traversing nodes
interface Visitor {
    void visit(File file);
    void visit(Directory directory);
}

// Visitor implementation for calculating total file size
class SizeVisitor implements Visitor {
    private double total = 0;

    @Override
    public void visit(File file) {
        total += file.getSize();
    }

    @Override
    public void visit(Directory directory) {
        for (Node child : directory.getChildren()) {
            child.accept(this);
        }
    }

    public double getTotal() {
        return total;
    }
}

// Class representing an entry in the tree
class TreeEntry {
    private final Node node;
    private final int depth;
    private final List<Boolean> parentIsLastList;
    private final boolean isLast;

    public TreeEntry(Node node, int depth, List<Boolean> parentIsLastList, boolean isLast) {
        this.node = node;
        this.depth = depth;
        this.parentIsLastList = parentIsLastList;
        this.isLast = isLast;
    }

    public Node getNode() {
        return node;
    }

    public int getDepth() {
        return depth;
    }

    public List<Boolean> getParentIsLastList() {
        return parentIsLastList;
    }

    public boolean isLast() {
        return isLast;
    }
}

// Iterator for traversing the tree
class TreeIterator implements Iterator<TreeEntry> {
    private final Stack<StackEntry> stack = new Stack<>();

    public TreeIterator(Directory root) {
        List<Node> children = root.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            Node child = children.get(i);
            boolean isLast = (i == children.size() - 1);
            stack.push(new StackEntry(child, 1, new ArrayList<>(), isLast));
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public TreeEntry next() {
        StackEntry entry = stack.pop();
        Node node = entry.node;
        int depth = entry.depth;
        List<Boolean> parentIsLastList = entry.parentIsLastList;
        boolean isLast = entry.isLast;

        if (node instanceof Directory) {
            Directory dir = (Directory) node;
            List<Node> children = dir.getChildren();
            List<Boolean> newParentIsLastList = new ArrayList<>(parentIsLastList);
            newParentIsLastList.add(isLast);
            for (int i = children.size() - 1; i >= 0; i--) {
                Node child = children.get(i);
                boolean childIsLast = (i == children.size() - 1);
                stack.push(new StackEntry(child, depth + 1, newParentIsLastList, childIsLast));
            }
        }

        return new TreeEntry(node, depth, parentIsLastList, isLast);
    }

    private static class StackEntry {
        Node node;
        int depth;
        List<Boolean> parentIsLastList;
        boolean isLast;

        StackEntry(Node node, int depth, List<Boolean> parentIsLastList, boolean isLast) {
            this.node = node;
            this.depth = depth;
            this.parentIsLastList = parentIsLastList;
            this.isLast = isLast;
        }
    }
}