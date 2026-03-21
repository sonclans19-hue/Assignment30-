import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictionaryService {
    private final Dictionary dictionary;
    private final DictionaryRepository repository;

    public enum AddWordResult {
        ADDED,
        EXISTS,
        INVALID
    }

    public DictionaryService(Dictionary dictionary, DictionaryRepository repository) {
        this.dictionary = dictionary;
        this.repository = repository;
    }

    public void load() {
        Map<String, String> data = repository.load();
        dictionary.loadFromMap(data);
        System.out.println("Data loaded!");
    }

    public AddWordResult addWord(String rawWord, String meaning) {
        String word = normalize(rawWord);
        if (word.isEmpty()) {
            return AddWordResult.INVALID;
        }
        if (dictionary.contains(word)) {
            return AddWordResult.EXISTS;
        }

        dictionary.put(word, meaning);
        repository.save(dictionary.snapshot());
        return AddWordResult.ADDED;
    }

    public String lookupMeaning(String rawWord) {
        String word = normalize(rawWord);
        if (word.isEmpty()) {
            return null;
        }
        return dictionary.getMeaning(word);
    }

    public Word lookupWord(String rawWord) {
        String text = normalize(rawWord);
        if (text.isEmpty()) {
            return null;
        }

        String meaning = dictionary.getMeaning(text);
        if (meaning == null) {
            return null;
        }
        return new Word(text, meaning);
    }

    public boolean updateWord(String rawWord, String newMeaning) {
        String word = normalize(rawWord);
        if (word.isEmpty()) {
            return false;
        }
        if (!dictionary.contains(word)) {
            return false;
        }

        dictionary.put(word, newMeaning);
        repository.save(dictionary.snapshot());
        return true;
    }

    public boolean deleteWord(String rawWord) {
        String word = normalize(rawWord);
        if (word.isEmpty()) {
            return false;
        }
        if (!dictionary.contains(word)) {
            return false;
        }

        dictionary.remove(word);
        repository.save(dictionary.snapshot());
        return true;
    }

    public List<String> autocomplete(String rawPrefix) {
        String prefix = normalize(rawPrefix);
        if (prefix.isEmpty()) {
            return new ArrayList<>();
        }
        return dictionary.autocomplete(prefix);
    }

    public List<Word> listAllWords() {
        return dictionary.listAllWords();
    }

    private String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase();
    }
}

