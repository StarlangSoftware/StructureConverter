import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;
import StructureConverter.DependencyToConstituency.SimpleDependencyToConstituencyTreeConverter;
import ParseTree.ParseTree;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class DependencyToConstituencyTest {

    @Test
    public void testConvert() throws FileNotFoundException {
        SimpleDependencyToConstituencyTreeConverter simpleDependencyToConstituencyTreeConverter = new SimpleDependencyToConstituencyTreeConverter();
        AnnotatedCorpus annotated = new AnnotatedCorpus(new File("DependencyTrees"), ".train");
        ParseTree tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(0), null);
        ParseTree tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0002.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(1), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0005.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(2), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0019.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(3), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0027.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(4), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0041.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(5), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0059.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(6), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0096.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(7), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0179.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(8), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0219.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(9), null);
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("ConstituencyTrees/0262.train")));
        assertEquals(tree1.toString(), tree2.toString());
    }
}