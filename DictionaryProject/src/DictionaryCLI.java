import java.util.List;
import java.util.Scanner;

public class DictionaryCLI {
    private final DictionaryService service;
    private final Scanner scanner;
    private static final int PAGE_SIZE = 10;

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
            System.out.println("5. Show all words");
            System.out.println("6. Statistics");
            System.out.println("7. Delete all words");
            System.out.println("8. Favorite word");
            System.out.println("9. Show favorite words");
            System.out.println("10. Exit");
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
                            System.out.println("Invalid data! Word cannot be empty and meaning must be 1-300 characters.");
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
                        if (service.isFavorite(word)) {
                            System.out.println("[Favorite] Meaning: " + found.getMeaning());
                        } else {
                            System.out.println("Meaning: " + found.getMeaning());
                        }
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

                    DictionaryService.UpdateWordResult updateResult = service.updateWord(word, newMeaning);
                    switch (updateResult) {
                        case UPDATED:
                            System.out.println("Word updated!");
                            break;
                        case NOT_FOUND:
                            System.out.println("Word not found!");
                            break;
                        default:
                            System.out.println("Invalid meaning! Please enter 1-300 characters.");
                            break;
                    }
                    break;
                }

                case 4: {
                    System.out.print("Enter word to delete: ");
                    String word = scanner.nextLine();

                    String meaning = service.lookupMeaning(word);
                    if (meaning == null) {
                        System.out.println("Word not found!");
                        break;
                    }

                    System.out.println("Found: " + word.trim().toLowerCase() + " = " + meaning);
                    System.out.print("Are you sure you want to delete this word? (y/n): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (!confirm.equals("y") && !confirm.equals("yes")) {
                        System.out.println("Delete canceled.");
                        break;
                    }

                    boolean deleted = service.deleteWord(word);
                    if (deleted) {
                        System.out.println("Word deleted!");
                    } else {
                        System.out.println("Word not found!");
                    }
                    break;
                }

                case 5: {
                    List<Word> allWords = service.listAllWords();
                    if (allWords.isEmpty()) {
                        System.out.println("Dictionary is empty.");
                    } else {
                        showWordsWithPagination(allWords);
                    }
                    break;
                }

                case 6:
                    DictionaryService.Statistics stats = service.getStatistics();
                    System.out.println("\nStatistics:");
                    System.out.println("- Total words: " + stats.getTotalWords());
                    System.out.println("- Words added today: " + stats.getWordsAddedToday());
                    if (stats.getMostSearchedWord() == null) {
                        System.out.println("- Most searched word: No lookup data yet");
                    } else {
                        System.out.println("- Most searched word: " + stats.getMostSearchedWord()
                                + " (" + stats.getMostSearchedCount() + " times)");
                    }
                    break;

                case 7: {
                    List<Word> before = service.listAllWords();
                    if (before.isEmpty()) {
                        System.out.println("Dictionary is already empty.");
                        break;
                    }
                    System.out.println("This will delete ALL " + before.size() + " word(s) permanently.");
                    System.out.print("Type DELETE ALL to confirm: ");
                    String confirm = scanner.nextLine().trim();
                    if (!confirm.equals("DELETE ALL")) {
                        System.out.println("Canceled.");
                        break;
                    }
                    service.clearAll();
                    System.out.println("All words deleted.");
                    break;
                }

                case 8: {
                    System.out.print("Enter word: ");
                    String word = scanner.nextLine();
                    DictionaryService.FavoriteToggleResult favResult = service.toggleFavorite(word);
                    switch (favResult) {
                        case ADDED:
                            System.out.println("Added to favorites.");
                            break;
                        case REMOVED:
                            System.out.println("Removed from favorites.");
                            break;
                        case NOT_IN_DICTIONARY:
                            System.out.println("Word is not in the dictionary. Add it first.");
                            break;
                        default:
                            System.out.println("Invalid word.");
                    }
                    break;
                }

                case 9: {
                    List<Word> favs = service.listFavoriteWords();
                    if (favs.isEmpty()) {
                        System.out.println("No favorite words yet.");
                    } else {
                        System.out.println("\nFavorite words:");
                        for (int i = 0; i < favs.size(); i++) {
                            Word w = favs.get(i);
                            System.out.println((i + 1) + ". " + w.getText());
                        }
                    }
                    break;
                }

                case 10:
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice! Please choose 1-10.");
            }
        }

        scanner.close();
    }

    private void showWordsWithPagination(List<Word> allWords) {
        int total = allWords.size();
        int totalPages = (total + PAGE_SIZE - 1) / PAGE_SIZE;
        int page = 0;

        while (true) {
            int start = page * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, total);

            System.out.println("\nSaved words (page " + (page + 1) + "/" + totalPages + "):");
            for (int i = start; i < end; i++) {
                Word item = allWords.get(i);
                System.out.println((i + 1) + ". " + item.getText() + ": " + item.getMeaning());
            }

            if (totalPages == 1) {
                break;
            }

            System.out.print("Command [n=next, p=prev, q=quit]: ");
            String command = scanner.nextLine().trim().toLowerCase();
            if (command.equals("n")) {
                if (page < totalPages - 1) {
                    page++;
                } else {
                    System.out.println("This is the last page.");
                }
            } else if (command.equals("p")) {
                if (page > 0) {
                    page--;
                } else {
                    System.out.println("This is the first page.");
                }
            } else if (command.equals("q")) {
                break;
            } else {
                System.out.println("Invalid command. Use n, p, or q.");
            }
        }
    }
}

