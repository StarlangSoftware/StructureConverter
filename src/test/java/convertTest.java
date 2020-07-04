import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;
import ParseTree.ParseTree;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class convertTest {

    @Test
    void testConvert() throws FileNotFoundException {
        SimpleDependencyToConstituencyTreeConverter simpleDependencyToConstituencyTreeConverter = new SimpleDependencyToConstituencyTreeConverter();
        AnnotatedCorpus annotated = new AnnotatedCorpus(new File("train3"), ".train");
        ParseTree tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(0));
        ParseTree tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0000.train")));
        assertEquals(tree1.toString(), tree2.toString());
    }
}