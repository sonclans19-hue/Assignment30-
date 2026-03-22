import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DictionaryService {
    private final Dictionary dictionary;
    private final DictionaryRepository repository;
    private final FavoriteRepository favoriteRepository;
    private static final int MAX_MEANING_LENGTH = 300;
    private final Map<String, Integer> lookupCounts = new HashMap<>();
    private final Map<String, LocalDate> addedDates = new HashMap<>();
    private final Set<String> favoriteWords = new LinkedHashSet<>();

    public enum AddWordResult {
        ADDED,
        EXISTS,
        INVALID
    }

    public enum UpdateWordResult {
        UPDATED,
        NOT_FOUND,
        INVALID
    }

    public enum FavoriteToggleResult {
        ADDED,
        REMOVED,
        NOT_IN_DICTIONARY,
        INVALID
    }

    public static class Statistics {
        private final int totalWords;
        private final int wordsAddedToday;
        private final String mostSearchedWord;
        private final int mostSearchedCount;

        public Statistics(int totalWords, int wordsAddedToday, String mostSearchedWord, int mostSearchedCount) {
            this.totalWords = totalWords;
            this.wordsAddedToday = wordsAddedToday;
            this.mostSearchedWord = mostSearchedWord;
            this.mostSearchedCount = mostSearchedCount;
        }

        public int getTotalWords() {
            return totalWords;
        }

        public int getWordsAddedToday() {
            return wordsAddedToday;
        }

        public String getMostSearchedWord() {
            return mostSearchedWord;
        }

        public int getMostSearchedCount() {
            return mostSearchedCount;
        }
    }

    public DictionaryService(Dictionary dictionary, DictionaryRepository repository,
            FavoriteRepository favoriteRepository) {
        this.dictionary = dictionary;
        this.repository = repository;
        this.favoriteRepository = favoriteRepository;
    }

    public void load() {
        Map<String, String> data = repository.load();
        dictionary.loadFromMap(data);

        favoriteWords.clear();
        Set<String> loadedFavorites = favoriteRepository.load();
        boolean pruned = false;
        for (String w : loadedFavorites) {
            if (dictionary.contains(w)) {
                favoriteWords.add(w);
            } else {
                pruned = true;
            }
        }
        if (pruned) {
            favoriteRepository.save(favoriteWords);
        }

        System.out.println("Data loaded!");
    }

    public AddWordResult addWord(String rawWord, String meaning) {
        String word = normalizeWord(rawWord);
        String normalizedMeaning = normalizeMeaning(meaning);
        if (word.isEmpty() || normalizedMeaning == null) {
            return AddWordResult.INVALID;
        }
        if (dictionary.contains(word)) {
            return AddWordResult.EXISTS;
        }

        dictionary.put(word, normalizedMeaning);
        addedDates.put(word, LocalDate.now());
        repository.save(dictionary.snapshot());
        return AddWordResult.ADDED;
    }

    public String lookupMeaning(String rawWord) {
        String word = normalizeWord(rawWord);
        if (word.isEmpty()) {
            return null;
        }
        return dictionary.getMeaning(word);
    }

    public Word lookupWord(String rawWord) {
        String text = normalizeWord(rawWord);
        if (text.isEmpty()) {
            return null;
        }

        String meaning = dictionary.getMeaning(text);
        if (meaning == null) {
            return null;
        }
        lookupCounts.put(text, lookupCounts.getOrDefault(text, 0) + 1);
        return new Word(text, meaning);
    }

    public UpdateWordResult updateWord(String rawWord, String newMeaning) {
        String word = normalizeWord(rawWord);
        String normalizedMeaning = normalizeMeaning(newMeaning);
        if (word.isEmpty()) {
            return UpdateWordResult.INVALID;
        }
        if (normalizedMeaning == null) {
            return UpdateWordResult.INVALID;
        }
        if (!dictionary.contains(word)) {
            return UpdateWordResult.NOT_FOUND;
        }

        dictionary.put(word, normalizedMeaning);
        repository.save(dictionary.snapshot());
        return UpdateWordResult.UPDATED;
    }

    public boolean deleteWord(String rawWord) {
        String word = normalizeWord(rawWord);
        if (word.isEmpty()) {
            return false;
        }
        if (!dictionary.contains(word)) {
            return false;
        }

        dictionary.remove(word);
        lookupCounts.remove(word);
        addedDates.remove(word);
        if (favoriteWords.remove(word)) {
            favoriteRepository.save(favoriteWords);
        }
        repository.save(dictionary.snapshot());
        return true;
    }

    public void clearAll() {
        dictionary.clear();
        lookupCounts.clear();
        addedDates.clear();
        favoriteWords.clear();
        favoriteRepository.save(favoriteWords);
        repository.save(dictionary.snapshot());
    }

    public FavoriteToggleResult toggleFavorite(String rawWord) {
        String word = normalizeWord(rawWord);
        if (word.isEmpty()) {
            return FavoriteToggleResult.INVALID;
        }
        if (!dictionary.contains(word)) {
            return FavoriteToggleResult.NOT_IN_DICTIONARY;
        }
        if (favoriteWords.contains(word)) {
            favoriteWords.remove(word);
            favoriteRepository.save(favoriteWords);
            return FavoriteToggleResult.REMOVED;
        }
        favoriteWords.add(word);
        favoriteRepository.save(favoriteWords);
        return FavoriteToggleResult.ADDED;
    }

    public boolean isFavorite(String rawWord) {
        String word = normalizeWord(rawWord);
        if (word.isEmpty()) {
            return false;
        }
        return favoriteWords.contains(word);
    }

    public List<Word> listFavoriteWords() {
        List<String> words = new ArrayList<>(favoriteWords);
        Collections.sort(words);
        List<Word> results = new ArrayList<>();
        for (String word : words) {
            if (dictionary.contains(word)) {
                results.add(new Word(word, dictionary.getMeaning(word)));
            }
        }
        return results;
    }

    public List<String> autocomplete(String rawPrefix) {
        String prefix = normalizeWord(rawPrefix);
        if (prefix.isEmpty()) {
            return new ArrayList<>();
        }
        return dictionary.autocomplete(prefix);
    }

    public List<Word> listAllWords() {
        return dictionary.listAllWords();
    }

    public Statistics getStatistics() {
        int totalWords = dictionary.snapshot().size();
        LocalDate today = LocalDate.now();
        int wordsAddedToday = 0;

        for (String word : addedDates.keySet()) {
            if (today.equals(addedDates.get(word)) && dictionary.contains(word)) {
                wordsAddedToday++;
            }
        }

        String topWord = null;
        int topCount = 0;
        for (String word : lookupCounts.keySet()) {
            int count = lookupCounts.get(word);
            if (count > topCount) {
                topCount = count;
                topWord = word;
            }
        }

        return new Statistics(totalWords, wordsAddedToday, topWord, topCount);
    }

    private String normalizeWord(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private String normalizeMeaning(String rawMeaning) {
        if (rawMeaning == null) {
            return null;
        }
        String meaning = rawMeaning.trim().replaceAll("\\s+", " ");
        if (meaning.isEmpty()) {
            return null;
        }
        if (meaning.length() > MAX_MEANING_LENGTH) {
            return null;
        }
        return meaning;
    }
}

