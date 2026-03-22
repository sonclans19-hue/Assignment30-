import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileDictionaryRepository implements DictionaryRepository {
    private final File file;

    public FileDictionaryRepository() {
        this.file = new File("data/dictionary.txt");
    }

    public FileDictionaryRepository(File file) {
        this.file = file;
    }

    @Override
    public Map<String, String> load() {
        Map<String, String> data = new HashMap<>();

        try {
            if (!file.exists()) {
                return data;
            }

            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();

                    String[] parts = line.split("=", 2);
                    if (parts.length != 2) {
                        continue;
                    }

                    String word = parts[0].trim().toLowerCase();
                    String meaning = parts[1];
                    data.put(word, meaning);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public void save(Map<String, String> data) {
        try {
            File dir = file.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }

            File tempFile = new File(file.getPath() + ".tmp");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                for (String word : data.keySet()) {
                    bw.write(word + "=" + data.get(word));
                    bw.newLine();
                }
            }

            Path target = file.toPath();
            Path temp = tempFile.toPath();

            if (Files.exists(target)) {
                Path backup = new File(file.getPath() + ".bak").toPath();
                Files.copy(target, backup, StandardCopyOption.REPLACE_EXISTING);
            }

            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

