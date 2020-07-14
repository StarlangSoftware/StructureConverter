package StructureConverter.DependencyToConstituency;

public class UniversalDependencyNotExistsException extends Exception {

    private String fileName;

    public UniversalDependencyNotExistsException(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return "Universal Dependency Not Existed Failed: " + fileName;
    }
}
