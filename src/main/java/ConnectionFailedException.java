public class ConnectionFailedException extends Exception {

    private String fileName;

    public ConnectionFailedException(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return "Connection Failed: " + fileName;
    }
}
