package Stage2;

import java.util.Locale;

public class Flashcards {
    private final String term;
    private final String definition;

    public Flashcards(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public boolean checkAnswer(String answer) {
        return answer != null && getDefinition().equals(answer.toLowerCase(Locale.ROOT));
    }
}