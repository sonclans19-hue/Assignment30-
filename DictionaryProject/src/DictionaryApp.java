
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DictionaryApp {

    static HashMap<String, String> dictionary = new HashMap<>();

    public static void main(String[] args) {
        loadFromFile();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add word");
            System.out.println("2. Lookup word");
            System.out.println("3.Update word");
            System.out.println("4.Delete word");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter word: ");
                    String word = sc.nextLine().toLowerCase();

                    if (dictionary.containsKey(word)) {
                        System.out.println("Word already exists! Cannot add again.");
                        break; // 👈 thoát luôn, không cho nhập meaning
                    }

                    System.out.print("Enter meaning: ");
                    String meaning = sc.nextLine();

                    dictionary.put(word, meaning);
                    saveToFile();
                    System.out.println("Word added!");
                    break;

  
                case 2:
                    System.out.print("Enter word: ");
                    word = sc.nextLine();

                    List<String> suggestions = autocomplete(word, dictionary);

                    if (!suggestions.isEmpty()) {
                        System.out.println("Suggestions:");
                        for (int i = 0; i < Math.min(5, suggestions.size()); i++) {
                            System.out.println("- " + suggestions.get(i));
                        }
                    }

                    if (dictionary.containsKey(word)) {
                        System.out.println("Meaning: " + dictionary.get(word));
                    } else {
                        System.out.println("Word not found!");
                    }
                    break;

                case 3:
                    updateWord(sc);
                    break;

                case 4:
                    System.out.print("Enter word to delete: ");
                    String words = sc.nextLine().toLowerCase();

                    deleteWord(words); // 👈 chỉ gọi hàm

                    break;

            }
        }
    }

    public static List<String> autocomplete(String input, HashMap<String, String> dictionary) {
        List<String> suggestions = new ArrayList<>();

        input = input.toLowerCase();

        for (String word : dictionary.keySet()) {
            if (word.toLowerCase().startsWith(input)) {
                suggestions.add(word);
            }
        }

        Collections.sort(suggestions); // optional: sắp xếp

        return suggestions;
    }

    public static void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("data/dictionary.txt"));

            for (String word : dictionary.keySet()) {
                bw.write(word + "=" + dictionary.get(word));
                bw.newLine();
            }

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFromFile() {
        try {
            File file = new File("data/dictionary.txt");

            // Nếu file chưa tồn tại thì không làm gì
            if (!file.exists()) {
                return;
            }

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                // Tách theo dấu =
                String[] parts = line.split("=");

                if (parts.length == 2) {
                    String word = parts[0];
                    String meaning = parts[1];

                    dictionary.put(word, meaning);
                }
            }

            sc.close();
            System.out.println("Data loaded!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateWord(Scanner sc) {
        System.out.print("Enter word to update: ");
        String word = sc.nextLine();

        if (dictionary.containsKey(word)) {
            System.out.print("Enter new meaning: ");
            String meaning = sc.nextLine();

            dictionary.put(word, meaning);
            saveToFile();
            System.out.println("Word updated!");
        } else {
            System.out.println("Word not found!");
        }
    }

    public static void deleteWord(String word) {
        word = word.toLowerCase();

        if (dictionary.containsKey(word)) {
            dictionary.remove(word);
            saveToFile();
            System.out.println("Word deleted!");
        } else {
            System.out.println("Word not found!");
        }
    }

}
