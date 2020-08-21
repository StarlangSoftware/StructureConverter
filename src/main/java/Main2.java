import AnnotatedSentence.*;
import ParseTree.ParseTree;
import StructureConverter.DependencyToConstituency.DependencyToConstituencyTreeConverter;
import StructureConverter.DependencyToConstituency.SimpleDependencyToConstituencyTreeConverter;

import java.io.File;

public class Main2 {
    public static void main(String[]args) {
        DependencyToConstituencyTreeConverter dependencyToConstituencyTreeConverter = new SimpleDependencyToConstituencyTreeConverter();
        AnnotatedCorpus annotatedCorpus = new AnnotatedCorpus(new File("Turkish-Phrase"), ".test");
        for (int i = 0; i < annotatedCorpus.sentenceCount(); i++) {
            AnnotatedSentence annotatedSentence = (AnnotatedSentence) annotatedCorpus.getSentence(i);
            ParseTree parseTree = dependencyToConstituencyTreeConverter.convert(annotatedSentence);
            String fileName = annotatedSentence.getFileName();
            if (parseTree != null) {
                parseTree.save("Turkish/" + fileName);
            } else {
                System.out.println(fileName + " Null done");
            }
            System.out.println(fileName + " done");
        }
    }
}
