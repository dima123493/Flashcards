package Stage4;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Calculation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, String> flashCards = new LinkedHashMap<>();

        System.out.println("Input the number of cards:");
        int numberOfCards = scanner.nextInt();
        scanner.nextLine();

        for (int i = 1; i <= numberOfCards; i++) {

            System.out.println("Card #" + i + ":");
            String term = scanner.nextLine();

            while (flashCards.containsKey(term)) {
                System.out.println("The term " + "\"" + term + "\"" + " already exists. Try again:");
                term = scanner.nextLine();
            }

            System.out.println("The definition for card #" + i + ":");
            String definition = scanner.nextLine();

            while (flashCards.containsValue(definition)) {
                System.out.println("The definition " + "\"" + definition + "\"" + " already exists. Try again:");
                definition = scanner.nextLine();
            }
            flashCards.put(term, definition);

        }

        for (String key : flashCards.keySet()) {

            System.out.println("Print the definition of " + "\"" + key + "\"" + ":");
            String answer = scanner.nextLine();

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
    }
}