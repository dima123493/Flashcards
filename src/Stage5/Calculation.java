package Stage5;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Calculation {
    static final Scanner SCANNER = new Scanner(System.in);
    Map<String, String> flashCards = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException {
        String fileName = "D:/Test/test.txt";
        try {
            Files.createFile(Path.of(fileName));
        } catch (FileAlreadyExistsException ignored) {
        }
        File file = new File("D:/Test/test.txt");
        if (!file.exists()) {
            file.mkdir();
            new File(file, "test.txt");
        }
        Calculation flashcards = new Calculation();
        flashcards.start();
    }

    void start() {
        State state = new WelcomeScreen();
        while (state != null) {
            state = state.showMenu();
        }
    }

    interface State {
        State showMenu();
    }

    class WelcomeScreen implements State {
        @Override
        public State showMenu() {
            while (true) {
                System.out.println("Input the action (add, remove, import, export, ask, exit):");
                Scanner scanner = new Scanner(System.in);
                String choice = scanner.nextLine().toLowerCase(Locale.ROOT);
                switch (choice) {
                    case "add":
                        return new Add();
                    case "remove":
                        return new Remove();
                    case "import":
                        return new Import();
                    case "export":
                        return new Export();
                    case "ask":
                        return new Ask();
                    case "exit":
                        System.out.println("Bye bye!");
                        return null;
                    default:
                        System.out.println("Invalid input! Try again...");
                }
            }
        }
    }

    class Add implements State {
        @Override
        public State showMenu() {
            System.out.println("The card:");
            String term = SCANNER.nextLine();

            if (flashCards.containsKey(term)) {
                System.out.println("The card " + "\"" + term + "\"" + " already exists.");
                System.out.println();
                return new WelcomeScreen();
            }

            System.out.println("The definition of the card:");
            String definition = SCANNER.nextLine();

            if (flashCards.containsValue(definition)) {
                System.out.println("The definition " + "\"" + definition + "\"" + " already exists.");
                System.out.println();
                return new WelcomeScreen();
            }

            flashCards.put(term, definition);
            System.out.println("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
            System.out.println();
            return new WelcomeScreen();
        }
    }

    class Remove implements State {
        @Override
        public State showMenu() {
            System.out.println("Which card?");
            String term = SCANNER.nextLine();
            for (Iterator<Map.Entry<String, String>> it = flashCards.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                if (entry.getKey().equals(term)) {
                    it.remove();
                    System.out.println("The card has been removed.");
                } else if (!term.contentEquals(entry.getKey())) {
                    System.out.println("Can't remove \"" + term + "\":\"" + " there is no such card.");
                }
            }
            System.out.println();
            return new WelcomeScreen();
        }
    }

    class Import implements State {
        @Override
        public State showMenu() {
            System.out.println("File name:");
            String fileName = SCANNER.nextLine();
            try {
                System.out.println(readFile(fileName) + " cards have been loaded.");
            } catch (IOException e) {
                System.out.println("File not found.");
            }
            System.out.println();
            return new WelcomeScreen();
        }
    }

    public int readFile(String fileName) throws IOException {
        var map = Files.lines(Path.of(fileName))
                .map(line -> line.split(" : "))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        flashCards.putAll(map);
        return map.size();
    }

    class Ask implements State {
        @Override
        public State showMenu() {
            System.out.println("How many times to ask?");
            int numberOfCards = SCANNER.nextInt();
            SCANNER.nextLine();
            for (int i = 0; i < numberOfCards; i++) {
                int n = new Random().nextInt(flashCards.size());
                String key = flashCards.keySet().stream().skip(n).findAny().get();
                System.out.println("Print the definition of " + "\"" + key + "\"" + ":");
                String answer = SCANNER.nextLine();
                if (flashCards.get(key).equals(answer)) {
                    System.out.println("Correct!");
                } else {
                    if (flashCards.containsValue(answer)) {
                        flashCards.forEach((key1, value) -> {
                            if (value.equals(answer)) {
                                System.out.println("Wrong. The right answer is " + "\"" + flashCards.get(key) + "\"" + ", " +
                                        "but your definition is correct for " + "\"" + key1 + "\"" + ".");
                            }
                        });
                    } else {
                        System.out.println("Wrong. The right answer is " + "\"" + flashCards.get(key) + "\"" + ".");
                    }
                }
            }
            System.out.println();
            return new WelcomeScreen();
        }
    }

    class Export implements State {
        @Override
        public State showMenu() {
            System.out.println("File name:");
            String fileName = SCANNER.nextLine();
            Path file = Path.of(fileName);
            try {
                Files.createFile(file);
            } catch (IOException ignored) {
            }

            try (var writer = new PrintWriter(Files.newBufferedWriter(file))) {

                for (var entry : flashCards.entrySet()) {
                    writer.println(entry.getKey() + " : " + entry.getValue());
                }

                System.out.println(flashCards.size() + " cards have been saved.");

            } catch (IOException e) {
                System.out.println("File not found.");
            }
            System.out.println();
            return new WelcomeScreen();
        }
    }
}