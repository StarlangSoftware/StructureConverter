public class WordIntersectionException extends Exception {

    private String fileName;

    public WordIntersectionException(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return "Word Intersection Failed: " + fileName;
    }
}
