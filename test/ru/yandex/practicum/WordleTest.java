package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.WordleDictionary;
import ru.yandex.practicum.WordleGame;
import ru.yandex.practicum.exceptions.WordNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleTest {
    private WordleGame game;
    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() {
        List<String> rawWords = Arrays.asList("герой", "гора", "гонец", "гений", "герб");
        dictionary = new WordleDictionary(rawWords);
        game = new WordleGame(dictionary, "герой");
    }

    @Test
    void testMakeGuessCorrectWord() throws WordNotFoundException {
        String result = game.makeGuess("герой");
        assertEquals("+++++", result);
        assertTrue(game.isWordGuessed());
        assertTrue(game.isGameOver());
    }

    @Test
    void testMakeGuessWrongWord() throws WordNotFoundException {
        String result = game.makeGuess("гонец");
        assertEquals("+^-^-", result);
        assertFalse(game.isWordGuessed());
        assertFalse(game.isGameOver());
        assertEquals(5, game.getRemainingAttempts());
    }

    @Test
    void testGuessNotInDictionary() {
        assertThrows(WordNotFoundException.class, () -> game.makeGuess("абвгд"));
    }

    @Test
    void testHintBasic() throws WordNotFoundException {
        game.makeGuess("гонец");
        String hint = game.getHint();
        assertNotNull(hint);
        assertNotEquals("гонец", hint);
        assertTrue(dictionary.contains(hint));
    }

    @Test
    void testGameAlreadyOver() throws WordNotFoundException {
        game.makeGuess("герой");
        assertThrows(RuntimeException.class, () -> game.makeGuess("герой"));
    }
}