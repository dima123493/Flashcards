package Stage3;

import java.util.Scanner;

public class Calculation {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input the number of cards:");
        int numCards = scanner.nextInt();
        scanner.nextLine();
        Flashcards[] card = new Flashcards[numCards];

        for (int i = 1; i <= card.length; i++) {
            System.out.println("Card #" + i + ":");
            String term = scanner.nextLine().toLowerCase();
            System.out.println("The definition for card #" + i + ":");
            String definition = scanner.nextLine().toLowerCase();
            card[i - 1] = new Flashcards(term, definition);
        }

        for (Flashcards cards : card) {
            System.out.println("Print the definition of " + "\"" + cards.getTerm() + "\":");
            String answer = scanner.nextLine().toLowerCase();

            if (answer.equals(cards.getDefinition())) {
                System.out.println("Correct!");
            } else {
                System.out.println("Wrong. The right answer is " + "\"" + cards.getDefinition() + "\".");
            }
        }
    }
}
