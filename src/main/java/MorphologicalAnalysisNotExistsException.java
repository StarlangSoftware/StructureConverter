public class MorphologicalAnalysisNotExistsException extends Exception {

    private String fileName;

    public MorphologicalAnalysisNotExistsException(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return "Morphologic Failed: " + fileName;
    }
}
