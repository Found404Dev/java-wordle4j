package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {
    public static void main(String[] args) {
        try (PrintWriter logger = new PrintWriter(new OutputStreamWriter(new FileOutputStream("game.log", true), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {

            logger.println("=== Новая игра ===");

            WordleDictionary dictionary = WordleDictionaryLoader.load("words_ru.txt");
            logger.println("Словарь загружен, количество слов: " + dictionary.getAllWords().size());

            String answer = dictionary.getRandomWord();
            logger.println("Загадано слово: " + answer);

            WordleGame game = new WordleGame(dictionary, answer);

            System.out.println("Добро пожаловать в Wordle!");
            System.out.println("У вас 6 попыток угадать слово из 5 букв.");
            System.out.println("После ввода слова вы увидите подсказку:");
            System.out.println("  +  — буква на своём месте");
            System.out.println("  ^  — буква есть, но не на этом месте");
            System.out.println("  -  — такой буквы нет");
            System.out.println("Для получения подсказки нажмите Enter без ввода слова.\n");

            while (!game.isGameOver()) {
                System.out.print("Введите слово (или Enter для подсказки): ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    String hint = game.getHint();
                    if (hint == null) {
                        System.out.println("Нет доступных подсказок. Попробуйте ввести слово сами.");
                    } else {
                        System.out.println("Подсказка: " + hint);
                    }
                    continue;
                }

                try {
                    String result = game.makeGuess(input);
                    System.out.println(input);
                    System.out.println(result);
                    if (game.isWordGuessed()) {
                        System.out.println("Поздравляю! Вы угадали слово \"" + answer + "\"!");
                        break;
                    }
                    System.out.println("Осталось попыток: " + game.getRemainingAttempts());
                } catch (WordNotFoundException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                    System.out.println("Попробуйте другое слово.");
                    logger.println("Игрок ввёл неверное слово: " + input);
                } catch (Exception e) {
                    System.out.println("Внутренняя ошибка. Подробности в логе.");
                    logger.println("Непредвиденная ошибка:");
                    e.printStackTrace(logger);
                }
            }

            if (!game.isWordGuessed()) {
                System.out.println("Вы проиграли. Загаданное слово: " + answer);
            }

            logger.println("Игра завершена. Результат: " + (game.isWordGuessed() ? "победа" : "поражение"));
            System.out.println("Спасибо за игру!");

        } catch (FileNotFoundException e) {
            System.err.println("Файл словаря words_ru.txt не найден. Поместите его в корень проекта.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении словаря: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
