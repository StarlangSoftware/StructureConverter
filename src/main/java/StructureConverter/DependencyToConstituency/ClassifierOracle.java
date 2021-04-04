package StructureConverter.DependencyToConstituency;/* Created by oguzkeremyildiz on 4.02.2021 */

import StructureConverter.WordNodePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

public class ClassifierOracle extends ProjectionOracle {

    @Override
    public ArrayList<SimpleEntry<Command, String>> makeCommands(HashMap<String, Integer> specialsMap, ArrayList<WordNodePair> unionList, int currentIndex) {
        ArrayList<SimpleEntry<Command, String>> list = new ArrayList<>();
        switch (unionList.size()) {
            case 2:
                if (currentIndex == 1) {
                    list.add(new SimpleEntry<>(Command.LEFT, null));
                } else {
                    list.add(new SimpleEntry<>(Command.RIGHT, null));
                }
                list.add(new SimpleEntry<>(Command.MERGE, setTreePos(unionList, unionList.get(currentIndex).getTreePos())));
                break;
            default:
                break;
        }
        return list;
    }
}
