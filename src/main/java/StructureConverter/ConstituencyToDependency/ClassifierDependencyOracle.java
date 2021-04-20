package StructureConverter.ConstituencyToDependency;/* Created by oguzkeremyildiz on 6.02.2021 */

import AnnotatedSentence.AnnotatedWord;
import AnnotatedTree.ParseNodeDrawable;
import DataStructure.CounterHashMap;
import MorphologicalAnalysis.MorphologicalTag;
import StructureConverter.WordNodePair;
import Util.FileUtils;

import java.util.AbstractMap;
import java.util.AbstractMap.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class ClassifierDependencyOracle implements DependencyOracle {

    private static ArrayList<String[]> dataList;

    public ClassifierDependencyOracle() {
        dataList = new ArrayList<>();
        try {
            Scanner source = new Scanner(FileUtils.getInputStream("ConsToDep/dataset.txt"));
            while (source.hasNext()) {
                String line = source.nextLine();
                dataList.add(line.split(" "));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String testKnn(String[] testData, String pathName, int length1, int length2) {
        CounterHashMap<String> counts = new CounterHashMap<>();
        String[][] trainData = new String[length1][length2];
        Scanner input = new Scanner(FileUtils.getInputStream("ConsToDep/" + pathName + ".txt"));
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

    private ArrayList<SimpleEntry<Integer, Integer>> findList(int length, String classInfo) {
        ArrayList<SimpleEntry<Integer, Integer>> list = new ArrayList<>();
        for (String[] array : dataList) {
            for (int j = 0; j < array.length; j++) {
                if (array[0].equals(Integer.toString(length)) && array[1].equals(classInfo)) {
                    for (int k = 2; k < array.length; k += 2) {
                        list.add(new SimpleEntry<>(Integer.parseInt(array[k]), Integer.parseInt(array[k + 1])));
                    }
                    return list;
                }
            }
        }
        return null;
    }

    private boolean contains(int i, ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> list) {
        for (AbstractMap.SimpleEntry<Integer, Integer> entry : list) {
            if (entry.getKey() == i) {
                return true;
            }
        }
        return false;
    }

    private int findHeadIndex(ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> list, int first, int last) {
        int index = -1;
        for (int i = 0; i <= Math.abs(last - first); i++) {
            if (!contains(i, list)) {
                index = i;
                break;
            }
        }
        return index + first;
    }

    private void addHeadToDecisions(ArrayList<Decision> decisions, int index) {
        for (int i = 0; i < decisions.size(); i++) {
            if (i == 0) {
                if (decisions.get(i).getNo() > index) {
                    decisions.add(0, new Decision(-1, 0, null));
                    break;
                }
            }
            if (i + 1 < decisions.size()) {
                if (decisions.get(i).getNo() < index && decisions.get(i + 1).getNo() > index) {
                    decisions.add(i + 1, new Decision(-1, 0, null));
                    break;
                }
            }
            if (i + 1 == decisions.size()) {
                if (decisions.get(i).getNo() < index) {
                    decisions.add(i + 1, new Decision(-1, 0, null));
                    break;
                }
            }
        }
    }

    @Override
    public ArrayList<Decision> makeDecisions(int firstIndex, int lastIndex, ArrayList<WordNodePair> wordNodePairList, ParseNodeDrawable node) {
        String[] testData = new String[lastIndex + 1 - firstIndex];
        String classInfo;
        ArrayList<SimpleEntry<Integer, Integer>> list = new ArrayList<>();
        ArrayList<Decision> decisions = new ArrayList<>();
        for (int i = 0; i < testData.length; i++) {
            testData[i] = wordNodePairList.get(firstIndex + i).getWord().getParse().getPos();
        }
        switch (lastIndex + 1 - firstIndex) {
            case 2:
                classInfo = testKnn(testData, "2", 22940, 3);
                list = findList(2, classInfo);
                break;
            case 3:
                classInfo = testKnn(testData, "3", 8062, 4);
                list = findList(3, classInfo);
                break;
            case 4:
                classInfo = testKnn(testData, "4", 1667, 5);
                list = findList(4, classInfo);
                break;
            case 5:
                classInfo = testKnn(testData, "5", 598, 6);
                list = findList(5, classInfo);
                break;
            case 6:
                classInfo = testKnn(testData, "6", 167, 7);
                list = findList(6, classInfo);
                break;
            case 7:
                classInfo = testKnn(testData, "7", 60, 8);
                list = findList(7, classInfo);
                break;
            default:
                break;
        }
        int headIndex = findHeadIndex(list, firstIndex, lastIndex);
        for (int i = 0; i < Objects.requireNonNull(list).size(); i++) {
            String[] posDatas = new String[25];
            AnnotatedWord fromWord = wordNodePairList.get(firstIndex + list.get(i).getKey()).getWord();
            AnnotatedWord toWord = wordNodePairList.get(firstIndex + list.get(i).getValue()).getWord();
            AnnotatedWord headWord = wordNodePairList.get(headIndex).getWord();
            posDatas[0] = fromWord.getParse().getPos();
            posDatas[1] = fromWord.getParse().getRootPos();
            posDatas[2] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.ABLATIVE));
            posDatas[3] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.DATIVE));
            posDatas[4] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.GENITIVE));
            posDatas[5] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.NOMINATIVE));
            posDatas[6] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE));
            posDatas[7] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.PROPERNOUN));
            posDatas[8] = toWord.getParse().getPos();
            posDatas[9] = toWord.getParse().getRootPos();
            posDatas[10] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.ABLATIVE));
            posDatas[11] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.DATIVE));
            posDatas[12] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.GENITIVE));
            posDatas[13] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.NOMINATIVE));
            posDatas[14] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE));
            posDatas[15] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.PROPERNOUN));
            posDatas[16] = headWord.getParse().getPos();
            posDatas[17] = headWord.getParse().getRootPos();
            posDatas[18] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.ABLATIVE));
            posDatas[19] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.DATIVE));
            posDatas[20] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.GENITIVE));
            posDatas[21] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.NOMINATIVE));
            posDatas[22] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE));
            posDatas[23] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.PROPERNOUN));
            if (fromWord.getSemantic() == null || headWord.getSemantic() == null) {
                posDatas[24] = "null";
            } else {
                posDatas[24] = Boolean.toString(fromWord.getSemantic().equals(headWord.getSemantic()));
            }
            decisions.add(new Decision(firstIndex + list.get(i).getKey(), list.get(i).getValue() - list.get(i).getKey(), testKnn(posDatas, "posNames", 47703, 26)));
        }
        addHeadToDecisions(decisions, headIndex);
        return decisions;
    }
}
