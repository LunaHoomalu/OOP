package com.calculator.controller;

import com.calculator.model.CalculationRecord;
import com.calculator.model.CalculatorModel;
import com.calculator.service.FileService;
import com.calculator.view.ConsoleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CalculatorController {
    private final CalculatorModel model;
    private final ConsoleView view;
    private final FileService fileService;

    public CalculatorController(CalculatorModel model, ConsoleView view, FileService fileService) {
        this.model = model;
        this.view = view;
        this.fileService = fileService;
        // Загружаем историю при старте
        this.model.loadHistory(this.fileService.loadHistory());
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                view.printMenu();
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1": handleCalculation(scanner); break;
                    case "2": handleShowHistory(); break;
                    case "3": handleExportAll(scanner); break;
                    case "4": handleExportSelected(scanner); break;
                    case "5": running = false; break;
                    default: view.showError("Неверная опция, попробуйте снова.");
                }
            }
        }
        // Сохраняем историю при выходе
        try {
            fileService.saveHistory(model.getHistory());
            view.showMessage("История сохранена. Выход из программы.");
        } catch (IOException e) {
            view.showError("Не удалось сохранить историю: " + e.getMessage());
        }
    }

    private void handleCalculation(Scanner scanner) {
        view.promptForExpression();
        String expression = scanner.nextLine();
        try {
            double result = model.calculate(expression);
            view.showResult(expression, result);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    private void handleShowHistory() {
        view.showHistory(model.getHistory());
    }

    private void handleExportAll(Scanner scanner) {
        view.promptForFilePath();
        String pathInput = scanner.nextLine();

        if (pathInput.isBlank()) {
            view.showMessage("Файл с полной историей хранится здесь: " + fileService.getDefaultHistoryPath());
            return;
        }

        try {
            String finalPath = fileService.exportToFile(model.getHistory(), pathInput);
            view.showMessage("История успешно сохранена в файл: " + finalPath);
        } catch (IOException e) {
            view.showError("Не удалось сохранить файл: " + e.getMessage());
        }
    }

    private void handleExportSelected(Scanner scanner) {
        handleShowHistory();
        List<CalculationRecord> history = model.getHistory();
        if (history.isEmpty()) {
            return;
        }

        view.promptForSelection();
        String selectionInput = scanner.nextLine();
        String[] indicesStr = selectionInput.split(",");
        List<CalculationRecord> selectedRecords = new ArrayList<>();

        try {
            for (String indexStr : indicesStr) {
                int index = Integer.parseInt(indexStr.trim()) - 1; // -1 т.к. пользователь видит нумерацию с 1
                if (index >= 0 && index < history.size()) {
                    selectedRecords.add(history.get(index));
                } else {
                    view.showError("Неверный номер в списке: " + (index + 1));
                }
            }
        } catch (NumberFormatException e) {
            view.showError("Неверный формат ввода номеров.");
            return;
        }

        if (selectedRecords.isEmpty()) {
            view.showMessage("Не выбрано ни одного уравнения для сохранения.");
            return;
        }

        view.promptForFilePath();
        String pathInput = scanner.nextLine();

        try {
            String finalPath = fileService.exportToFile(selectedRecords, pathInput);
            view.showMessage("Выбранные уравнения сохранены в файл: " + finalPath);
        } catch (IOException | IllegalArgumentException e) {
            view.showError("Не удалось сохранить файл: " + e.getMessage());
        }
    }
}