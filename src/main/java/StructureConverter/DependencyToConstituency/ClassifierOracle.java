package StructureConverter.DependencyToConstituency;/* Created by oguzkeremyildiz on 4.02.2021 */

import DataStructure.CounterHashMap;
import StructureConverter.WordNodePair;
import Util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Scanner;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String testKnn(String[] testData, int pathName, int length1, int length2) {
        CounterHashMap<String> counts = new CounterHashMap<>();
        String[][] trainData = new String[length1][length2];
        Scanner input = new Scanner(FileUtils.getInputStream("DepToCons/" + pathName + ".txt"));
        for (int i = 0; i < length1; i++){
            String[] items = input.nextLine().split(" ");
            for (int j = 0; j < length2; j++){
                trainData[i][j] = items[j];
            }
        }
        input.close();
        int minDistance = length2 - 1;
        for (int i = 0; i < length1; i++){
            int count = 0;
            for (int j = 0; j < length2 - 1; j++){
                if (!testData[j].equals(trainData[i][j])){
                    count++;
                }
            }
            if (count < minDistance){
                minDistance = count;
            }
        }
        for (int i = 0; i < length1; i++){
            int count = 0;
            for (int j = 0; j < length2 - 1; j++){
                if (!testData[j].equals(trainData[i][j])){
                    count++;
                }
            }
            if (count == minDistance){
                counts.put(trainData[i][length2 - 1]);
            }
        }
        return counts.max();
    }

    private ArrayList<SimpleEntry<Command, String>> findList(int unionListSize, int classInfo, int headIndex, ArrayList<WordNodePair> unionList, String currentPos) {
        for (int i = 0; i < dataList.size(); i++) {
            String[] array = dataList.get(i);
            if (Integer.parseInt(array[0]) == unionListSize && Integer.parseInt(array[1]) == classInfo && Integer.parseInt(array[2]) == headIndex) {
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
                return list;
            }
        }
        return null;
    }

    @Override
    public ArrayList<SimpleEntry<Command, String>> makeCommands(HashMap<String, Integer> specialsMap, ArrayList<WordNodePair> unionList, int currentIndex) throws FileNotFoundException {
        String[] testData = new String[unionList.size() + ((unionList.size() - 1) * 3)];
        int iterate = 0, classInfo;
        for (int i = 0; i < unionList.size(); i++) {
            testData[i] = unionList.get(i).getWord().getParse().getPos();
            if (i != currentIndex) {
                testData[unionList.size() + (3 * iterate)] = Integer.toString(i);
                testData[unionList.size() + (3 * iterate) + 1] = Integer.toString(currentIndex);
                testData[unionList.size() + (3 * iterate) + 2] = unionList.get(i).getUniversalDependency();
                iterate++;
            }
        }
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
                classInfo = Integer.parseInt(testKnn(testData, unionList.size(), 11239, 10));
                return findList(3, classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 4:
                classInfo = Integer.parseInt(testKnn(testData, unionList.size(), 6360, 14));
                return findList(4, classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 5:
                classInfo = Integer.parseInt(testKnn(testData, unionList.size(), 4265, 18));
                return findList(5, classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 6:
                classInfo = Integer.parseInt(testKnn(testData, unionList.size(), 2115, 22));
                return findList(6, classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 7:
                classInfo = Integer.parseInt(testKnn(testData, unionList.size(), 814, 26));
                return findList(7, classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            case 8:
                classInfo = Integer.parseInt(testKnn(testData, unionList.size(), 221, 30));
                return findList(8, classInfo, currentIndex, unionList, unionList.get(currentIndex).getTreePos());
            default:
                break;
        }
        return null;
    }
}
