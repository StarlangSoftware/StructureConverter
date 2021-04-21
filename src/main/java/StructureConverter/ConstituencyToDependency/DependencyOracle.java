package StructureConverter.ConstituencyToDependency;/* Created by oguzkeremyildiz on 6.02.2021 */

import AnnotatedTree.ParseNodeDrawable;
import Classification.Model.Model;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public interface DependencyOracle {
    ArrayList<Decision> makeDecisions(int firstIndex, int lastIndex, ArrayList<WordNodePair> wordNodePairList, ParseNodeDrawable node, Model model);
}
