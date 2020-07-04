import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;
import ParseTree.ParseTree;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

class DependencyToConstituencyTest {

    @Test
    void testConvert() throws FileNotFoundException {
        SimpleDependencyToConstituencyTreeConverter simpleDependencyToConstituencyTreeConverter = new SimpleDependencyToConstituencyTreeConverter();
        AnnotatedCorpus annotated = new AnnotatedCorpus(new File("train3"), ".train");
        ParseTree tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(2));
        ParseTree tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0002.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(5));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0005.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(19));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0019.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(27));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0027.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(41));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0041.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(59));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0059.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(96));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0096.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(179));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0179.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(219));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0219.train")));
        assertEquals(tree1.toString(), tree2.toString());
        tree1 = simpleDependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotated.getSentence(262));
        tree2 = new ParseTreeDrawable(new FileInputStream(new File("trainTrees/0262.train")));
        assertEquals(tree1.toString(), tree2.toString());
    }
}