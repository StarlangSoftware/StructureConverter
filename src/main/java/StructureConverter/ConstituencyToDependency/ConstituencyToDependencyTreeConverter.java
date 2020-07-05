package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;

public interface ConstituencyToDependencyTreeConverter {
    AnnotatedSentence convert(ParseTreeDrawable parseTree);
}
