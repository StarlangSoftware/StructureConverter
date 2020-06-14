public class MorphologicException extends Exception {

    private String fileName;

    public MorphologicException(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return "Morphologic Failed: " + fileName;
    }
}
