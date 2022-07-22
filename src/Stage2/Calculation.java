package Stage2;

import java.util.Locale;
import java.util.Scanner;

public class Calculation {
    public static void main(String[] args) {
        System.out.println("Enter a term");
        Scanner scanner = new Scanner(System.in);
        String term = scanner.nextLine();
        System.out.println("Enter a definition");
        String definition = scanner.nextLine().toLowerCase(Locale.ROOT);
        Flashcards card = new Flashcards(term, definition);
        System.out.println();
        System.out.println(card.getTerm());
        System.out.println("Enter your answer: ");
        String answer = scanner.nextLine().toLowerCase();
        System.out.println("Your answer is " + (card.checkAnswer(answer) ? "right!" : "wrong..."));
    }
}
