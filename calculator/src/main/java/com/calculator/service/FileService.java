package com.calculator.service;

import com.calculator.model.CalculationRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileService {

    private static final String DEFAULT_HISTORY_FILE = "calculator_history.log";
    private static final String SEPARATOR = " ; "; // Разделитель для данных в файле

    public Path getDefaultHistoryPath() {
        return Paths.get(DEFAULT_HISTORY_FILE).toAbsolutePath();
    }

    // Загрузка истории из файла по умолчанию
    public List<CalculationRecord> loadHistory() {
        List<CalculationRecord> history = new ArrayList<>();
        Path path = Paths.get(DEFAULT_HISTORY_FILE);
        if (!Files.exists(path)) {
            return history;
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(SEPARATOR, 2);
                if (parts.length == 2) {
                    history.add(new CalculationRecord(parts[0], Double.parseDouble(parts[1])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Не удалось загрузить историю: " + e.getMessage());
        }
        return history;
    }

    // Сохранение всей истории в файл по умолчанию
    public void saveHistory(List<CalculationRecord> records) throws IOException {
        List<String> lines = records.stream()
                .map(r -> r.expression() + SEPARATOR + r.result())
                .collect(Collectors.toList());
        Files.write(Paths.get(DEFAULT_HISTORY_FILE), lines);
    }

    // Экспорт выбранных записей в файл с умной логикой путей
    public String exportToFile(List<CalculationRecord> recordsToExport, String userInput) throws IOException {
        Path finalPath = resolvePath(userInput);

        // Убедимся, что родительская директория существует
        if (finalPath.getParent() != null) {
            Files.createDirectories(finalPath.getParent());
        }

        List<String> lines = recordsToExport.stream()
                .map(CalculationRecord::toString) // Используем форматированный вывод
                .collect(Collectors.toList());

        Files.write(finalPath, lines);
        return finalPath.toAbsolutePath().toString();
    }

    private Path resolvePath(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым.");
        }

        Path inputPath = Paths.get(userInput);

        // Случай 3: Указан только путь к директории
        if (Files.isDirectory(inputPath)) {
            return inputPath.resolve("log.log");
        }

        // Случай 4: Указан абсолютный путь с именем файла
        if (inputPath.isAbsolute()) {
            return inputPath;
        }

        // Случай 2: Указано только имя файла (возможно, с расширением)
        // Path считает "my_log.txt" файлом без родительской директории
        if (inputPath.getParent() == null) {
            String fileName = inputPath.toString();
            // Проверяем расширение
            if (fileName.endsWith(".txt") || fileName.endsWith(".log") || fileName.endsWith(".md")) {
                return Paths.get(fileName);
            } else {
                // Если расширение не указано или неверное, добавляем .log
                return Paths.get(fileName + ".log");
            }
        }

        // Если ничего не подошло, считаем это путем относительно текущей папки
        return inputPath.toAbsolutePath();
    }
}