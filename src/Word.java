public class Word {
    private String text;
    private int frequency;
    private String type;
    private Word next;

    public Word(String text, int frequency, String type) {
        this.text = text;
        this.frequency = frequency;
        this.type = type;
        this.next = null;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Word)) return false;
        Word other = (Word) obj;
        return this.text.equals(other.text);
    }

    public String getText() {
        return text;
    }

    public int getFrequency() {
        return frequency;
    }
    
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    
    public void addFrequency(int amount) {
        this.frequency += amount;
    }
    
    public String getType() {
        return type;
    }
    
    public Word getNext() {
        return next;
    }
    
    public void setNext(Word next) {
        this.next = next;
    }
}
