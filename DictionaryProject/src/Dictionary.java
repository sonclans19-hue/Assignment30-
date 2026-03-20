import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary {
    private final Map<String, String> entries = new HashMap<>();

    public boolean contains(String word) {
        return entries.containsKey(word);
    }

    public String getMeaning(String word) {
        return entries.get(word);
    }

    public void put(String word, String meaning) {
        entries.put(word, meaning);
    }

    public void remove(String word) {
        entries.remove(word);
    }

    public List<String> autocomplete(String prefix) {
        List<String> suggestions = new ArrayList<>();
        for (String word : entries.keySet()) {
            if (word.startsWith(prefix)) {
                suggestions.add(word);
            }
        }
        Collections.sort(suggestions);
        return suggestions;
    }

    public void loadFromMap(Map<String, String> data) {
        entries.clear();
        entries.putAll(data);
    }

    public Map<String, String> snapshot() {
        return new HashMap<>(entries);
    }
}

