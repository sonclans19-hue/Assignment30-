import java.util.Map;

public interface DictionaryRepository {
    Map<String, String> load();

    void save(Map<String, String> data);
}

