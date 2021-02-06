package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;
import StructureConverter.ParserConverterType;

public interface ConstituencyToDependencyTreeConverter {
    AnnotatedSentence convert(ParseTreeDrawable parseTree, ParserConverterType type);
}
