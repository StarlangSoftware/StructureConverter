package StructureConverter.DependencyToConstituency;/* Created by oguzkeremyildiz on 4.04.2021 */

import Classification.Model.TreeEnsembleModel;
import StructureConverter.WordNodePair;

import java.util.AbstractMap;
import java.util.ArrayList;

public abstract class ProjectionOracle {

    public ProjectionOracle() {
    }

    protected String setTreePos(ArrayList<WordNodePair> list, String currentPos) {
        String treePos = currentPos;
        for (WordNodePair current : list) {
            if (current != null && current.getTreePos().equals("PP")) {
                treePos = current.getTreePos();
            }
        }
        return treePos;
    }

    public abstract ArrayList<AbstractMap.SimpleEntry<Command, String>> makeCommands(ArrayList<WordNodePair> unionList, int currentIndex, ArrayList<TreeEnsembleModel> models);
}
