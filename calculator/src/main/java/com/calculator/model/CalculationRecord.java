package com.calculator.model;

// Используем record для неизменяемого класса-контейнера
public record CalculationRecord(String expression, double result) {
    @Override
    public String toString() {
        return expression + " = " + result;
    }
}