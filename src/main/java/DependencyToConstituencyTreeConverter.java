import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;
import ParseTree.ParseTree;

public interface DependencyToConstituencyTreeConverter {
    ParseTree convert(AnnotatedSentence annotatedSentence);
    ParseTreeDrawable convertDrawable(AnnotatedSentence annotatedSentence);
}
