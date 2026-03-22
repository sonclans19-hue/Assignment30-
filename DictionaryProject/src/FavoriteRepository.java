import java.util.Set;

public interface FavoriteRepository {
    Set<String> load();

    void save(Set<String> favorites);
}
