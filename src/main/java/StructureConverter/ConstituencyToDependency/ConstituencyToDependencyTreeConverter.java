package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;
import Classification.Model.Model;
import StructureConverter.MorphologicalAnalysisNotExistsException;

public interface ConstituencyToDependencyTreeConverter {
    AnnotatedSentence convert(ParseTreeDrawable parseTree, Model model) throws MorphologicalAnalysisNotExistsException;
}
