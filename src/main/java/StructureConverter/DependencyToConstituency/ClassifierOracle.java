package StructureConverter.DependencyToConstituency;/* Created by oguzkeremyildiz on 4.02.2021 */

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteAttribute;
import Classification.Instance.Instance;
import Classification.Model.TreeEnsembleModel;
import MorphologicalAnalysis.MorphologicalTag;
import StructureConverter.WordNodePair;
import Util.FileUtils;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class ClassifierOracle extends ProjectionOracle {

    private static ArrayList<String[]> dataList;

    public ClassifierOracle() {
        dataList = new ArrayList<>();
        try {
            Scanner source = new Scanner(FileUtils.getInputStream("DepToCons/dataset.txt"));
            while (source.hasNext()) {
                String line = source.nextLine();
                dataList.add(line.split(" "));
            }
            source.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Attribute> setTestData(ArrayList<WordNodePair> unionList, int currentIndex) {
        Attribute[] array = new Attribute[(unionList.size() * 8) + ((unionList.size() - 1) * 3)];
        int iterate = 0;
        for (int i = 0; i < unionList.size(); i++) {
            array[i * 8] = new DiscreteAttribute(unionList.get(i).getWord().getParse().getPos());
            array[(i * 8) + 1] = new DiscreteAttribute(unionList.get(i).getWord().getParse().getRootPos());
            array[(i * 8) + 2] = new DiscreteAttribute(Boolean.toString(unionList.get(i).getWord().getParse().containsTag(MorphologicalTag.ABLATIVE)));
            array[(i * 8) + 3] = new DiscreteAttribute(Boolean.toString(unionList.get(i).getWord().getParse().containsTag(MorphologicalTag.DATIVE)));
            array[(i * 8) + 4] = new DiscreteAttribute(Boolean.toString(unionList.get(i).getWord().getParse().containsTag(MorphologicalTag.GENITIVE)));
            array[(i * 8) + 5] = new DiscreteAttribute(Boolean.toString(unionList.get(i).getWord().getParse().containsTag(MorphologicalTag.NOMINATIVE)));
            array[(i * 8) + 6] = new DiscreteAttribute(Boolean.toString(unionList.get(i).getWord().getParse().containsTag(MorphologicalTag.ACCUSATIVE)));
            array[(i * 8) + 7] = new DiscreteAttribute(Boolean.toString(unionList.get(i).getWord().getParse().containsTag(MorphologicalTag.PROPERNOUN)));
            if (i != currentIndex) {
                array[(unionList.size() * 8) + (3 * iterate)] = new DiscreteAttribute(Integer.toString(i));
                array[(unionList.size() * 8) + (3 * iterate) + 1] = new DiscreteAttribute(Integer.toString(currentIndex));
                array[(unionList.size() * 8) + (3 * iterate) + 2] = new DiscreteAttribute(unionList.get(i).getUniversalDependency());
                iterate++;
            }
        }
        return new ArrayList<>(Arrays.asList(array));
    }

    private ArrayList<SimpleEntry<Command, String>> findList(HashMap<String, Double> classInfo, int headIndex, ArrayList<WordNodePair> unionList, String currentPos) {
        ArrayList<SimpleEntry<Command, String>> best = new ArrayList<>();
        double bestValue = Integer.MIN_VALUE;
        HashMap<ArrayList<SimpleEntry<Command, String>>, Double> listMap = new HashMap<>();
        for (String[] array : dataList) {
            if (Integer.parseInt(array[0]) == unionList.size() && classInfo.containsKey(array[1]) && Integer.parseInt(array[2]) == headIndex) {
                ArrayList<SimpleEntry<Command, String>> list = new ArrayList<>();
                for (int j = 3; j < array.length; j++) {
                    if (array[j].equals("MERGE")) {
                        list.add(new SimpleEntry<>(Command.MERGE, setTreePos(unionList, currentPos)));
                    } else if (array[j].equals("RIGHT")) {
                        list.add(new SimpleEntry<>(Command.RIGHT, null));
                    } else {
                        list.add(new SimpleEntry<>(Command.LEFT, null));
                    }
                }
                listMap.put(list, classInfo.get(array[1]));
            }
        }
        for (ArrayList<SimpleEntry<Command, String>> key : listMap.keySet()) {
            if (listMap.get(key) > bestValue) {
                best = key;
                bestValue = listMap.get(key);
            }
        }
        return best;
    }

    @Override
    public ArrayList<SimpleEntry<Command, String>> makeCommands(ArrayList<WordNodePair> unionList, int currentIndex, ArrayList<TreeEnsembleModel> models) {
        ArrayList<Attribute> testData = setTestData(unionList, currentIndex);
        HashMap<String, Double> classInfo;
        switch (unionList.size()) {
            case 2:
                ArrayList<SimpleEntry<Command, String>> list = new ArrayList<>();
                if (currentIndex == 1) {
                    list.add(new SimpleEntry<>(Command.LEFT, null));
                } else {
                    list.add(new SimpleEntry<>(Command.RIGHT, null));
                }
                list.add(new SimpleEntry<>(Command.MERGE, setTreePos(unionList, unionList.get(currentIndex).getTreePos())));
                return list;
            case 3:
                classInfo = models.get(0).predictProbability(new Instance("", testData));
                return findList(classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 4:
                classInfo = models.get(1).predictProbability(new Instance("", testData));
                return findList(classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 5:
                classInfo = models.get(2).predictProbability(new Instance("", testData));
                return findList(classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 6:
                classInfo = models.get(3).predictProbability(new Instance("", testData));
                return findList(classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 7:
                classInfo = models.get(4).predictProbability(new Instance("", testData));
                return findList(classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 8:
                classInfo = models.get(5).predictProbability(new Instance("", testData));
                return findList(classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            default:
                break;
        }
        return null;
    }
}
