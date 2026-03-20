
import java.util.Scanner;

public class DictionaryApp {
    public static void main(String[] args) {
        DictionaryRepository repository = new FileDictionaryRepository();
        Dictionary dictionary = new Dictionary();
        DictionaryService service = new DictionaryService(dictionary, repository);

        service.load();

        DictionaryCLI cli = new DictionaryCLI(service, new Scanner(System.in));
        cli.run();
    }
}
