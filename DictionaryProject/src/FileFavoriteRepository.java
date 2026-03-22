import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class FileFavoriteRepository implements FavoriteRepository {
    private final File file;

    public FileFavoriteRepository() {
        this.file = new File("data/favorites.txt");
    }

    public FileFavoriteRepository(File file) {
        this.file = file;
    }

    @Override
    public Set<String> load() {
        Set<String> favorites = new LinkedHashSet<>();

        try {
            if (!file.exists()) {
                return favorites;
            }

            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim().toLowerCase();
                    if (!line.isEmpty()) {
                        favorites.add(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return favorites;
    }

    @Override
    public void save(Set<String> favorites) {
        try {
            File dir = file.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }

            List<String> sorted = new ArrayList<>(favorites);
            Collections.sort(sorted);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String word : sorted) {
                    bw.write(word);
                    bw.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
