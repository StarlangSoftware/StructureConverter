package StructureConverter.DependencyToConstituency;

import AnnotatedSentence.AnnotatedSentence;
import ParseTree.ParseTree;
import StructureConverter.ParserConverterType;

public interface DependencyToConstituencyTreeConverter {
    ParseTree convert(AnnotatedSentence annotatedSentence, ParserConverterType type);
}
