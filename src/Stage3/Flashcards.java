package Stage3;

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
}