package com.calculator.main;

import com.calculator.controller.CalculatorController;
import com.calculator.model.CalculatorModel;
import com.calculator.service.FileService;
import com.calculator.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        // Создаем все компоненты MVC
        CalculatorModel model = new CalculatorModel();
        ConsoleView view = new ConsoleView();
        FileService fileService = new FileService();

        // Создаем контроллер и передаем ему остальные компоненты
        CalculatorController controller = new CalculatorController(model, view, fileService);

        // Запускаем приложение
        controller.run();
    }
}
