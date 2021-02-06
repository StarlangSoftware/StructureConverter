package StructureConverter.ConstituencyToDependency;/* Created by oguzkeremyildiz on 6.02.2021 */

import AnnotatedTree.ParseNodeDrawable;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public class ClassifierDependencyOracle implements DependencyOracle {

    @Override
    public ArrayList<Decision> makeDecisions(int firstIndex, int lastIndex, ArrayList<WordNodePair> wordNodePairList, ParseNodeDrawable node) {
        return null;
    }
}
