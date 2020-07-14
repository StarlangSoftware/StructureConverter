package StructureConverter.DependencyToConstituency;

public class NonProjectiveDependencyException extends Exception {

    private String fileName;

    public NonProjectiveDependencyException(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return "Non-Projective Dependency Failed: " + fileName;
    }
}
