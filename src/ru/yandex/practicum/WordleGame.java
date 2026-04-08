package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.GameAlreadyOverException;
import ru.yandex.practicum.exceptions.WordNotFoundException;

import java.util.*;

public class WordleGame {
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;

    private final String answer;
    private int remainingAttempts;
    private final WordleDictionary dictionary;
    private final List<String> attempts;
    private final List<String> givenHints;
    private boolean won;

    private final Set<Character> absentLetters;
    private final Map<Integer, Character> exactLetters;
    private final Map<Integer, Set<Character>> wrongPositionLetters;
    private final Set<Character> presentLetters;

    public WordleGame(WordleDictionary dictionary, String answer) {
        this.dictionary = dictionary;
        this.answer = answer.toLowerCase().replace('ё', 'е');
        this.remainingAttempts = MAX_ATTEMPTS;
        this.attempts = new ArrayList<>();
        this.givenHints = new ArrayList<>();
        this.won = false;

        this.absentLetters = new HashSet<>();
        this.exactLetters = new HashMap<>();
        this.wrongPositionLetters = new HashMap<>();
        for (int i = 0; i < WORD_LENGTH; i++) {
            wrongPositionLetters.put(i, new HashSet<>());
        }
        this.presentLetters = new HashSet<>();
    }

    public boolean isGameOver() {
        return remainingAttempts == 0 || won;
    }

    public boolean isWordGuessed() {
        return won;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public String makeGuess(String rawGuess) throws WordNotFoundException, GameAlreadyOverException {
        if (isGameOver()) {
            throw new GameAlreadyOverException("Игра уже завершена. Начните новую.");
        }
        String guess = normalize(rawGuess);
        if (!dictionary.contains(guess)) {
            throw new WordNotFoundException("Слово '" + guess + "' отсутствует в словаре.");
        }

        attempts.add(guess);
        remainingAttempts--;

        String result = computeResult(guess, answer);
        if (guess.equals(answer)) {
            won = true;
        }

        updateHintData(guess, result);

        return result;
    }

    public String getHint() {
        List<String> candidates = dictionary.getAllWords();

        for (Map.Entry<Integer, Character> entry : exactLetters.entrySet()) {
            int pos = entry.getKey();
            char ch = entry.getValue();
            candidates.removeIf(word -> word.charAt(pos) != ch);
        }

        for (Map.Entry<Integer, Set<Character>> entry : wrongPositionLetters.entrySet()) {
            int pos = entry.getKey();
            Set<Character> forbidden = entry.getValue();
            if (!forbidden.isEmpty()) {
                candidates.removeIf(word -> forbidden.contains(word.charAt(pos)));
            }
        }

        for (char ch : presentLetters) {
            candidates.removeIf(word -> word.indexOf(ch) == -1);
        }

        for (char ch : absentLetters) {
            candidates.removeIf(word -> word.indexOf(ch) != -1);
        }

        Set<String> used = new HashSet<>(attempts);
        used.addAll(givenHints);
        candidates.removeAll(used);

        if (candidates.isEmpty()) {
            return null;
        }
        Random random = new Random();
        String hint = candidates.get(random.nextInt(candidates.size()));
        givenHints.add(hint);
        return hint;
    }


    private String normalize(String word) {
        return word.toLowerCase().replace('ё', 'е');
    }

    private String computeResult(String guess, String answer) {
        char[] guessChars = guess.toCharArray();
        char[] answerChars = answer.toCharArray();
        char[] result = new char[WORD_LENGTH];
        boolean[] usedInAnswer = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guessChars[i] == answerChars[i]) {
                result[i] = '+';
                usedInAnswer[i] = true;
            } else {
                result[i] = '-';
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == '+') continue;
            char ch = guessChars[i];
            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!usedInAnswer[j] && ch == answerChars[j]) {
                    result[i] = '^';
                    usedInAnswer[j] = true;
                    break;
                }
            }
        }
        return new String(result);
    }

    private void updateHintData(String guess, String result) {
        char[] guessChars = guess.toCharArray();
        char[] resultChars = result.toCharArray();

        for (int i = 0; i < WORD_LENGTH; i++) {
            char ch = guessChars[i];
            char res = resultChars[i];
            if (res == '+') {
                exactLetters.put(i, ch);
                presentLetters.add(ch);
            } else if (res == '^') {
                wrongPositionLetters.get(i).add(ch);
                presentLetters.add(ch);
            } else {
                boolean appearsElsewhere = false;
                for (int j = 0; j < WORD_LENGTH; j++) {
                    if (resultChars[j] != '-' && guessChars[j] == ch) {
                        appearsElsewhere = true;
                        break;
                    }
                }
                if (!appearsElsewhere) {
                    absentLetters.add(ch);
                } else {
                    wrongPositionLetters.get(i).add(ch);
                }
            }
        }
    }
}
