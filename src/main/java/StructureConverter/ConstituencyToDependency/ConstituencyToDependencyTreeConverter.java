package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseTreeDrawable;
import Classification.Model.TreeEnsembleModel;
import StructureConverter.MorphologicalAnalysisNotExistsException;

import java.util.ArrayList;

public interface ConstituencyToDependencyTreeConverter {
    AnnotatedSentence convert(ParseTreeDrawable parseTree, ArrayList<TreeEnsembleModel> model) throws MorphologicalAnalysisNotExistsException;
}
