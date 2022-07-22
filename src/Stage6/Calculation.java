package Stage6;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Calculation {
    static Logger log = new Logger();
    static final Logger SCANNER = log;
    Map<String, String> flashCardsTermsDefinitions = new LinkedHashMap<>();
    Map<String, Integer> mistakes = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException {
        String fileName = "D:/Test/test.txt";
        try {
            Files.createFile(Path.of(fileName));
        } catch (FileAlreadyExistsException ignored) {
        }
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
            new File(file, "test.txt");
        }

        Calculation flashcards = new Calculation();
        flashcards.start();
    }

    void start() throws IOException {
        State state = new WelcomeScreen();
        while (state != null) {
            state = state.showMenu();
        }
    }

    interface State {
        State showMenu() throws IOException;
    }

    class WelcomeScreen implements State {
        @Override
        public State showMenu() {
            while (true) {
                log.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
                String choice = SCANNER.nextLine().toLowerCase(Locale.ROOT);
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
                        log.println("Bye bye!");
                        return null;
                    case "log":
                        return new Log();
                    case "hardest card":
                        return new HardestCard();
                    case "reset stats":
                        return new Reset();
                    default:
                        log.println("Invalid input! Try again...");
                }
            }
        }
    }

    static class Logger {
        static Scanner SCANNER = new Scanner(System.in);
        List<String> logLines = new ArrayList<>();

        void println(String line) {
            System.out.println(line);
            logLines.add(line);
        }

        void flushLogsToFile(String filename) {
            try {
                Files.write(Path.of(filename), logLines, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String nextLine() {
            var result = SCANNER.nextLine();
            logLines.add(result);
            return result;
        }
    }

    class Log implements State {

        @Override
        public State showMenu() throws IOException {
            log.println("File name:");
            String fileName = SCANNER.nextLine();
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
                new File(file, "test.txt");
            }
            log.println(String.valueOf(LocalDateTime.now()));
            log.println("The log has been saved.");
            log.flushLogsToFile(fileName);
            System.out.println();
            return new WelcomeScreen();
        }
    }

    class HardestCard implements State {

        @Override
        public State showMenu() {
            if (mistakes.isEmpty()) {
                log.println("There are no cards with errors.");
                return new WelcomeScreen();
            }

            int maxMistake = mistakes.values().stream().mapToInt(i -> i).max().getAsInt();

            var hardestKeys = mistakes.entrySet().stream()
                    .filter(e -> e.getValue() == maxMistake)
                    .map(Map.Entry::getKey).toList();

            if (hardestKeys.size() == 1) {
                log.println(String.format("The hardest card is \"%s\". You have %d errors answering it",
                        hardestKeys.get(0), maxMistake));
            } else {
                log.println(String.format("The hardest cards are %s.  You have %d errors answering them",
                        hardestKeys.stream()
                                .collect(Collectors.joining("\", \"", "\"", "\"")
                                ), maxMistake
                ));
            }
            log.println("");
            return new WelcomeScreen();
        }
    }

    class Reset implements State {

        @Override
        public State showMenu() {
            mistakes.clear();
            System.out.println("Card statistics have been reset.");
            System.out.println();
            return new WelcomeScreen();
        }
    }

    class Add implements State {
        @Override
        public State showMenu() {
            log.println("The card:");
            String term = SCANNER.nextLine();

            if (flashCardsTermsDefinitions.containsKey(term)) {
                log.println("The card " + "\"" + term + "\"" + " already exists.");
                System.out.println();
                return new WelcomeScreen();
            }

            log.println("The definition of the card:");
            String definition = SCANNER.nextLine();

            if (flashCardsTermsDefinitions.containsValue(definition)) {
                log.println("The definition " + "\"" + definition + "\"" + " already exists.");
                System.out.println();
                return new WelcomeScreen();
            }

            flashCardsTermsDefinitions.put(term, definition);
            log.println("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
            System.out.println();
            return new WelcomeScreen();
        }
    }

    class Remove implements State {
        @Override
        public State showMenu() {
            log.println("Which card?");
            String term = SCANNER.nextLine();
            for (Iterator<Map.Entry<String, String>> it = flashCardsTermsDefinitions.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                if (entry.getKey().equals(term)) {
                    it.remove();
                    log.println("The card has been removed.");
                } else if (!term.contentEquals(entry.getKey())) {
                    log.println("Can't remove \"" + term + "\":\"" + " there is no such card.");
                }
            }
            System.out.println();
            return new WelcomeScreen();
        }
    }

    class Import implements State {
        @Override
        public State showMenu() {
            log.println("File name:");
            String fileName = SCANNER.nextLine();
            try {
                log.println(readFile(fileName) + " cards have been loaded.");
            } catch (IOException e) {
                log.println("File not found.");
            }
            System.out.println();
            return new WelcomeScreen();
        }
    }

    public int readFile(String fileName) throws IOException {
        var lines = Files.readAllLines(Path.of(fileName));
        for (var line : lines) {
            var arr = line.split(" : ");
            flashCardsTermsDefinitions.put(arr[0], arr[1]);
            mistakes.put(arr[0], Integer.parseInt(arr[2]));
        }
        return lines.size();
    }

    class Export implements State {
        @Override
        public State showMenu() {
            log.println("File name:");
            String fileName = SCANNER.nextLine();
            Path file = Path.of(fileName);
            try {
                Files.createFile(file);
            } catch (IOException ignored) {
            }

            try (var writer = new PrintWriter(Files.newBufferedWriter(file))) {

                Optional<Integer> incorrectAmount = mistakes.entrySet().stream()
                        .findAny()
                        .map(Map.Entry::getValue);

                for (var entry : flashCardsTermsDefinitions.entrySet()) {

                    writer.println(entry.getKey() + " : " + entry.getValue() + " : "
                            + mistakes.getOrDefault(entry.getKey(), 0));
                }

                log.println(flashCardsTermsDefinitions.size() + " cards have been saved.");

            } catch (IOException e) {
                log.println("File not found.");
            }
            System.out.println();
            return new WelcomeScreen();
        }
    }

    class Ask implements State {
        @Override
        public State showMenu() {
            log.println("How many times to ask?");
            int numberOfCards = Integer.parseInt(SCANNER.nextLine());
            for (int i = 0; i < numberOfCards; i++) {
                int n = new Random().nextInt(flashCardsTermsDefinitions.size());
                String key = flashCardsTermsDefinitions.keySet().stream().skip(n).findAny().get();
                log.println("Print the definition of " + "\"" + key + "\"" + ":");
                String answer = SCANNER.nextLine();
                if (flashCardsTermsDefinitions.get(key).equals(answer)) {
                    log.println("Correct!");
                } else {
                    Optional<String> otherTerm = flashCardsTermsDefinitions.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(answer))
                            .findAny()
                            .map(Map.Entry::getKey);

                    if (otherTerm.isPresent()) {
                        log.println("Wrong. The right answer is " + "\"" + flashCardsTermsDefinitions.get(key) + "\"" + ", " +
                                "but your definition is correct for " + "\"" + otherTerm.get() + "\"" + " card.");
                    } else {
                        System.out.println("Wrong. The right answer is " + "\"" + flashCardsTermsDefinitions.get(key) + "\"" + ".");
                    }
                    mistakes.merge(key, 1, (oldValue, one) -> oldValue + 1);
                }
            }
            System.out.println();
            return new WelcomeScreen();
        }
    }
}