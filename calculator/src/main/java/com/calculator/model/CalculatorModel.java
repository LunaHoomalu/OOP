package com.calculator.model;

import java.util.*;

public class CalculatorModel {

    private final List<CalculationRecord> history = new ArrayList<>();

    public List<CalculationRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void loadHistory(List<CalculationRecord> records) {
        history.clear();
        history.addAll(records);
    }

    public double calculate(String expression) {
        // Упрощаем выражение для парсера: заменяем ** и // на односимвольные операторы
        // Юникод символы для степени и целочисленного деления
        String preparedExpression = expression.replace("**", "^").replace("//", "÷");

        List<String> rpn = infixToRpn(preparedExpression);
        double result = evaluateRpn(rpn);

        // Округляем для красивого вывода, если необходимо
        if (result == (long) result) {
            result = (long) result;
        } else {
            result = Math.round(result * 1000000.0) / 1000000.0;
        }

        history.add(new CalculationRecord(expression, result));
        return result;
    }

    private List<String> infixToRpn(String expression) {
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/%^÷()", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue;

            if (isNumber(token)) {
                output.add(token);
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && isOperator(stack.peek()) &&
                        (getPrecedence(token) <= getPrecedence(stack.peek()))) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new IllegalArgumentException("Ошибка: несогласованные скобки.");
                stack.pop(); // Выкидываем '('
            } else {
                throw new IllegalArgumentException("Ошибка: неверный символ в выражении: " + token);
            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) throw new IllegalArgumentException("Ошибка: несогласованные скобки.");
            output.add(stack.pop());
        }

        return output;
    }

    private double evaluateRpn(List<String> rpn) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                if (stack.size() < 2) throw new IllegalArgumentException("Ошибка: не хватает операндов для операции " + token);
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/":
                        if (b == 0) throw new ArithmeticException("Ошибка: деление на ноль.");
                        stack.push(a / b);
                        break;
                    case "^": stack.push(Math.pow(a, b)); break;
                    case "÷": // Наш кастомный оператор для //
                        if (b == 0) throw new ArithmeticException("Ошибка: деление на ноль.");
                        stack.push(Math.floor(a / b));
                        break;
                    case "%": stack.push(a % b); break;
                }
            }
        }
        if (stack.size() != 1) throw new IllegalArgumentException("Ошибка: неверный формат выражения.");
        return stack.pop();
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String token) {
        return "+-*/%^÷".contains(token);
    }

    private int getPrecedence(String operator) {
        switch (operator) {
            case "+": case "-": return 1;
            case "*": case "/": case "%": case "÷": return 2;
            case "^": return 3;
            default: return 0;
        }
    }
}