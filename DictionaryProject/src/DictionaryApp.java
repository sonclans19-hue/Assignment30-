
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Scanner;

public class DictionaryApp {

    static HashMap<String, String> dictionary = new HashMap<>();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add word");
            System.out.println("2. Lookup word");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter word: ");
                    String word = sc.nextLine();

                    System.out.print("Enter meaning: ");
                    String meaning = sc.nextLine();

                    dictionary.put(word, meaning);
                    System.out.println("Word added!");
                    break;

                case 2:
                    System.out.print("Enter word: ");
                    word = sc.nextLine();

                    if (dictionary.containsKey(word)) {
                        System.out.println("Meaning: " + dictionary.get(word));
                    } else {
                        System.out.println("Word not found!");
                    }
                    break;

                case 3:
                    System.exit(0);
            }
        }
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
}
