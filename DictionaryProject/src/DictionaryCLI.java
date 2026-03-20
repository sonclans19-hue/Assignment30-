import java.util.List;
import java.util.Scanner;

public class DictionaryCLI {
    private final DictionaryService service;
    private final Scanner scanner;

    public DictionaryCLI(DictionaryService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("\n1. Add word");
            System.out.println("2. Lookup word");
            System.out.println("3. Update word");
            System.out.println("4. Delete word");
            System.out.println("5. Exit");
            System.out.print("Choose: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1: {
                    System.out.print("Enter word: ");
                    String word = scanner.nextLine();

                    System.out.print("Enter meaning: ");
                    String meaning = scanner.nextLine();

                    DictionaryService.AddWordResult result = service.addWord(word, meaning);
                    switch (result) {
                        case ADDED:
                            System.out.println("Word added!");
                            break;
                        case EXISTS:
                            System.out.println("Word already exists! Cannot add again.");
                            break;
                        default:
                            System.out.println("Invalid word! Cannot add.");
                    }
                    break;
                }

                case 2: {
                    System.out.print("Enter word: ");
                    String word = scanner.nextLine();

                    List<String> suggestions = service.autocomplete(word);
                    if (!suggestions.isEmpty()) {
                        System.out.println("Suggestions:");
                        for (int i = 0; i < Math.min(5, suggestions.size()); i++) {
                            System.out.println("- " + suggestions.get(i));
                        }
                    }

                    Word found = service.lookupWord(word);
                    if (found != null) {
                        System.out.println("Meaning: " + found.getMeaning());
                    } else {
                        System.out.println("Word not found!");
                    }
                    break;
                }

                case 3: {
                    System.out.print("Enter word to update: ");
                    String word = scanner.nextLine();

                    String currentMeaning = service.lookupMeaning(word);
                    if (currentMeaning == null) {
                        System.out.println("Word not found!");
                        break;
                    }

                    System.out.println("Current meaning: " + currentMeaning);
                    System.out.print("Enter new meaning: ");
                    String newMeaning = scanner.nextLine();

                    service.updateWord(word, newMeaning);
                    System.out.println("Word updated!");
                    break;
                }

                case 4: {
                    System.out.print("Enter word to delete: ");
                    String word = scanner.nextLine();

                    boolean deleted = service.deleteWord(word);
                    if (deleted) {
                        System.out.println("Word deleted!");
                    } else {
                        System.out.println("Word not found!");
                    }
                    break;
                }

                case 5:
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice! Please choose 1-5.");
            }
        }

        scanner.close();
    }
}

