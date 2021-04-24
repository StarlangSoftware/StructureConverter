package StructureConverter.DependencyToConstituency;

import AnnotatedSentence.AnnotatedSentence;
import Classification.Model.TreeEnsembleModel;
import ParseTree.ParseTree;

import java.util.ArrayList;

public interface DependencyToConstituencyTreeConverter {
    ParseTree convert(AnnotatedSentence annotatedSentence, ArrayList<TreeEnsembleModel> models);
}
