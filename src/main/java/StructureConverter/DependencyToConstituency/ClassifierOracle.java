package StructureConverter.DependencyToConstituency;/* Created by oguzkeremyildiz on 4.02.2021 */

import StructureConverter.WordNodePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

public class ClassifierOracle implements ProjectionOracle {
    @Override
    public ArrayList<SimpleEntry<Command, String>> makeCommands(HashMap<String, Integer> specialsMap, ArrayList<WordNodePair> unionList, int currentIndex) {
        return null;
    }
}
