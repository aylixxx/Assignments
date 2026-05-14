//Ilya Pushkarev

import java.util.*;

public final class ShuntingYard {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(evaluatePostfix(convertToPostfix(input)));
    }

    public static String convertToPostfix(String expression) {
        String[] tokens = expression.split(" ");
        LinkedStack<String> operators = new LinkedStack<>();
        StringBuilder output = new StringBuilder();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.append(token).append(" ");
            } else if (isFunction(token)) {
                operators.push(token);
            } else if (token.equals(",")) {
                while (!operators.isEmpty() && !operators.peek().equals("(") && !isFunction(operators.peek())) {
                    output.append(operators.pop()).append(" ");
                }
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.append(operators.pop()).append(" ");
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.append(operators.pop()).append(" ");
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop();
                }
                if (!operators.isEmpty() && isFunction(operators.peek())) {
                    output.append(operators.pop()).append(" ");
                }
            }
        }

        while (!operators.isEmpty()) {
            output.append(operators.pop()).append(" ");
        }

        return output.toString().trim();
    }

    public static int evaluatePostfix(String postfix) {
        LinkedStack<Integer> stack = new LinkedStack<>();
        String[] tokens = postfix.split(" ");

        for (String token : tokens) {
            if (isNumber(token)) {
                stack.push(Integer.parseInt(token));
            } else if (isOperator(token)) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                int result = applyOperator(token, operand1, operand2);
                stack.push(result);
            }else if (isFunction(token)) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                int result = applyFunction(token, operand1, operand2);
                stack.push(result);
            }
        }

        return stack.pop();
    }

    private static boolean isNumber(String token) {
        return token.matches("\\d");
    }

    private static boolean isFunction(String token) {
        return token.equals("min") || token.equals("max");
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private static int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    private static int applyOperator(String operator, int operand1, int operand2) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                return operand1 / operand2;
            default:
                return 0;
        }
    }

    private static int applyFunction(String function, int operand1, int operand2) {
        switch (function) {
            case "min":
                return Math.min(operand1, operand2);
            case "max":
                return Math.max(operand1, operand2);
            default:
                return 0;
        }
    }
}

interface Stack<T> {
    void push(T element);
    T pop();
    T peek();
    boolean isEmpty();
}

class LinkedStack<T> implements Stack<T> {
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<T> top;

    @Override
    public void push(T element) {
        top = new Node<>(element, top);
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = top.data;
        top = top.next;
        return data;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }

    @Override
    public boolean isEmpty() {
        return top == null;
    }
}