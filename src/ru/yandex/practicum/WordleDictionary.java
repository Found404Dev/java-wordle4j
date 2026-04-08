package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WordleDictionary {
    private final List<String> words;

    public WordleDictionary(List<String> rawWords) {
        this.words = rawWords.stream()
                .map(word -> word.toLowerCase().replace('ё', 'е'))
                .filter(word -> word.length() == 5)
                .collect(Collectors.toList());

        if (this.words.isEmpty()) {
            throw new RuntimeException("Словарь пуст после нормализации. Проверьте файл words_ru.txt");
        }
    }

    public boolean contains(String word) {
        String normalized = word.toLowerCase().replace('ё', 'е');
        return words.contains(normalized);
    }

    public String getRandomWord() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public List<String> getAllWords() {
        return new ArrayList<>(words);
    }
}