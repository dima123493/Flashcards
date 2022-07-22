package Stage7;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Calculation {
    static Logger log = new Logger();
    static final Logger SCANNER = log;
    static Map<String, String> flashCardsTermsDefinitions = new LinkedHashMap<>();
    static Map<String, Integer> mistakes = new LinkedHashMap<>();
    static String importFileName;
    static String exportFileName;

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {

            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equals("-import")) {
                    importFileName = args[i + 1];
                }
                if (args[i].equals("-export")) {
                    exportFileName = args[i + 1];
                }
            }
        }

        if (importFileName != null) {
            Import();
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
                        exit();
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
            log.println("");
            return new WelcomeScreen();
        }
    }

    class HardestCard implements State {

        @Override
        public State showMenu() {
            if (mistakes.isEmpty()) {
                log.println("There are no cards with errors.");
                log.println("");
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
            log.println("Card statistics have been reset.");
            log.println("");
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
                log.println("");
                return new WelcomeScreen();
            }

            log.println("The definition of the card:");
            String definition = SCANNER.nextLine();

            if (flashCardsTermsDefinitions.containsValue(definition)) {
                log.println("The definition " + "\"" + definition + "\"" + " already exists.");
                log.println("");
                return new WelcomeScreen();
            }

            flashCardsTermsDefinitions.put(term, definition);
            log.println("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
            log.println("");
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
            log.println("");
            return new WelcomeScreen();
        }
    }

    class Import implements State {
        @Override
        public State showMenu() {
            log.println("File name:");
            String fileName;
            fileName = SCANNER.nextLine();
            importToFile(fileName);
            log.println("");
            return new WelcomeScreen();
        }
    }

    public static int readFile(String fileName) throws IOException {
        var lines = Files.readAllLines(Path.of(fileName));
        for (var line : lines) {
            var arr = line.split(" : ");
            flashCardsTermsDefinitions.put(arr[0], arr[1]);
            mistakes.put(arr[0], Integer.parseInt(arr[2]));
        }
        return lines.size();
    }

    private static void Import() {
        if (importFileName != null) {
            importToFile(importFileName);
        }
    }

    static void importToFile(String importFileName) {
        Path file = Path.of(importFileName);
        try {
            log.println(readFile(String.valueOf(file)) + " cards have been loaded.");
        } catch (IOException e) {
            log.println("File not found.");
        }
    }

    class Export implements State {
        @Override
        public State showMenu() {
            log.println("File name:");
            String fileName = SCANNER.nextLine();
            exportToFile(fileName);
            log.println("");
            return new WelcomeScreen();
        }
    }

    private void exit() {
        if (exportFileName != null) {
            exportToFile(exportFileName);
        }
        log.println("Bye bye!");
    }

    void exportToFile(String fileName) {
        Path file = Path.of(fileName);
        try {
            Files.createFile(file);
        } catch (IOException ignored) {
        }

        try (var writer = new PrintWriter(Files.newBufferedWriter(file))) {

            for (var entry : flashCardsTermsDefinitions.entrySet()) {
                writer.println(entry.getKey() + " : " + entry.getValue() + " : "
                        + mistakes.getOrDefault(entry.getKey(), 0));
            }

            log.println(flashCardsTermsDefinitions.size() + " cards have been saved.");

        } catch (IOException e) {
            log.println("File not found.");
        }
        log.println("");
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
                        log.println("Wrong. The right answer is " + "\"" + flashCardsTermsDefinitions.get(key) + "\"" + ".");
                    }
                    mistakes.merge(key, 1, (oldValue, one) -> oldValue + 1);
                }
            }
            log.println("");
            return new WelcomeScreen();
        }
    }
}