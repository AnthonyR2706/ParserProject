package com.company;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static String input;
    public static int pos;
    public static Map<String, Integer> variables;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a program: ");
        input = scanner.nextLine();
        input = input.replaceAll("\\s", "");
        pos = 0;
        variables = new HashMap<>();

        try {
            parseProgram();
            executeProgram();
        } catch (Exception e) {
            System.out.println("Parsing error: " + e.getMessage());
        }
    }

    public static void parseProgram() {
        while (!isEndOfInput()) {
            assignment();
            consume(';');
        }
    }

    public static void assignment() {
        String identifier = identifier();
        consume('=');
        int value = exp();
        if (!variables.containsKey(identifier)) {
            variables.put(identifier, null);
        }
        variables.put(identifier, value);
    }

    public static int exp() {
        int result = term();

        while (match('+') || match('-')) {
            if (previousChar() == '+') {
                result += term();
            } else {
                result -= term();
            }
        }
        return result;
    }

    public static int term() {
        int result = fact();
        while (match('*')) {
            result *= fact();
        }
        return result;
    }

    public static int fact() {
        if (match('(')) {
            int result = exp();
            consume(')');
            return result;
        } else if (match('-')) {
            return -fact();
        } else if (match('+')) {
            return fact();
        } else if (matchLiteral()) {
            return literal();
        } else {
            String identifier = identifier();
            if (!variables.containsKey(identifier)) {
                throw new RuntimeException("Undeclared variable: " + identifier);
            }
            Integer value = variables.get(identifier);
            if (value == null) {
                throw new RuntimeException("Uninitialized variable: " + identifier);
            }
            return value;
        }
    }

    public static String identifier() {
        if (!matchLetter()) {
            throw new RuntimeException("expected identifier");
        }
        int start = pos - 1;
        while (matchLetter() || matchDigit()) {
        }
        return input.substring(start, pos);
    }

    public static int literal() {
        StringBuilder literal = new StringBuilder();
        if (match('-')) {
            literal.append('-');
        }
        if (match('0')) {
            if (isEndOfInput() || !matchDigit()) {
                return 0;
            }
            throw new RuntimeException("Leading zeros are not allowed");
        } else if (matchDigit()) {
            literal.append(previousChar());
            while (matchDigit()) {
                literal.append(previousChar());
            }
            return Integer.parseInt(literal.toString());
        }
        throw new RuntimeException("Expected literal");
    }

    public static boolean match(char c) {
        if (isEndOfInput() || input.charAt(pos) != c) {
            return false;
        }
        pos++;
        return true;
    }

    public static boolean matchLetter() {
        if (isEndOfInput()) {
            return false;
        }
        char c = input.charAt(pos);
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
            pos++;
            return true;
        }
        return false;
    }

    public static boolean matchDigit() {
        if (isEndOfInput()) {
            return false;
        }
        char c = input.charAt(pos);
        if (c >= '0' && c <= '9') {
            pos++;
            return true;
        }
        return false;
    }

    public static boolean matchLiteral() {
        if (isEndOfInput()) {
            return false;
        }
        char c = input.charAt(pos);
        return (c >= '0' && c <= '9');
    }

    public static char previousChar() {
        if (pos > 0) {
            return input.charAt(pos - 1);
        }
        throw new RuntimeException("No previous character");
    }

    public static void consume(char c) {
        if (!match(c)) {
            throw new RuntimeException("Expected '" + c + "'");
        }
    }

    public static boolean isEndOfInput() {
        return pos >= input.length();
    }

    public static void executeProgram() {
        for (String variable : variables.keySet()) {
            Integer value = variables.get(variable);
            System.out.println(variable + " = " + (value != null ? value : "uninitialized"));
        }
    }
}