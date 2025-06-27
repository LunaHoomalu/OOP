package com.calculator.view;

import com.calculator.model.CalculationRecord;

import java.util.List;

public class ConsoleView {

    public void printMenu() {
        System.out.println("\n--- Меню калькулятора ---");
        System.out.println("1. Ввести уравнение для расчета");
        System.out.println("2. Посмотреть историю расчетов");
        System.out.println("3. Сохранить всю историю в файл");
        System.out.println("4. Сохранить выбранные уравнения в файл");
        System.out.println("5. Выход");
        System.out.print("Выберите опцию: ");
    }

    public void showResult(String expression, double result) {
        System.out.println("Результат: " + expression + " = " + result);
    }

    public void showHistory(List<CalculationRecord> history) {
        if (history.isEmpty()) {
            System.out.println("История пуста.");
            return;
        }
        System.out.println("\n--- История расчетов ---");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }

    public void promptForExpression() {
        System.out.print("Введите уравнение (например, (2+2)*2 ): ");
    }

    public void promptForFilePath() {
        System.out.println("\nВведите путь и/или имя файла для сохранения.");
        System.out.println("  - Просто имя (results.txt) -> сохранится в текущей папке.");
        System.out.println("  - Путь к папке (C:/docs/) -> сохранится как log.log в этой папке.");
        System.out.println("  - Полный путь (C:/docs/my_log.txt) -> сохранится как указано.");
        System.out.println("  - Если не указывать путь (нажать Enter) -> покажем путь к файлу истории по умолчанию.");
        System.out.print("Ваш ввод: ");
    }

    public void promptForSelection() {
        System.out.print("Введите номера уравнений для сохранения через запятую (например, 1, 3, 4): ");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String error) {
        System.err.println("ОШИБКА: " + error);
    }
}
