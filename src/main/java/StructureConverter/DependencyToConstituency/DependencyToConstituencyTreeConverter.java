package StructureConverter.DependencyToConstituency;

import AnnotatedSentence.AnnotatedSentence;
import ParseTree.ParseTree;

public interface DependencyToConstituencyTreeConverter {
    ParseTree convert(AnnotatedSentence annotatedSentence);
}
