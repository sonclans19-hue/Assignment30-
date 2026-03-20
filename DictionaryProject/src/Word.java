public class Word {
    private final String text;
    private final String meaning;

    public Word(String text, String meaning) {
        this.text = text;
        this.meaning = meaning;
    }

    public String getText() {
        return text;
    }

    public String getMeaning() {
        return meaning;
    }

    @Override
    public String toString() {
        return text + "=" + meaning;
    }
}

