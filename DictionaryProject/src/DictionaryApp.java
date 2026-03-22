
import java.util.Scanner;

public class DictionaryApp {
    public static void main(String[] args) {
        DictionaryRepository repository = new FileDictionaryRepository();
        FavoriteRepository favoriteRepository = new FileFavoriteRepository();
        Dictionary dictionary = new Dictionary();
        DictionaryService service = new DictionaryService(dictionary, repository, favoriteRepository);

        service.load();

        DictionaryCLI cli = new DictionaryCLI(service, new Scanner(System.in));
        cli.run();
    }
}
