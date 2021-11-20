package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;
import StructureConverter.MorphologicalAnalysisNotExistsException;

public interface ConstituencyToDependencyTreeConverter {
    AnnotatedSentence convert(ParseTreeDrawable parseTree, Parameter parameter) throws MorphologicalAnalysisNotExistsException;
}
