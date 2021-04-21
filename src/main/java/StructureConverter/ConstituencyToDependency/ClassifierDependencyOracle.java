package StructureConverter.ConstituencyToDependency;/* Created by oguzkeremyildiz on 6.02.2021 */

import AnnotatedSentence.AnnotatedWord;
import AnnotatedTree.ParseNodeDrawable;
import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteAttribute;
import Classification.Instance.Instance;
import Classification.Model.TreeEnsembleModel;
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
    public ArrayList<Decision> makeDecisions(int firstIndex, int lastIndex, ArrayList<WordNodePair> wordNodePairList, ParseNodeDrawable node, ArrayList<TreeEnsembleModel> models) {
        ArrayList<Attribute> testData = new ArrayList<>(lastIndex + 1 - firstIndex);
        String classInfo;
        ArrayList<SimpleEntry<Integer, Integer>> list = new ArrayList<>();
        ArrayList<Decision> decisions = new ArrayList<>();
        for (int i = 0; i < lastIndex + 1 - firstIndex; i++) {
            testData.add(new DiscreteAttribute(wordNodePairList.get(firstIndex + i).getWord().getParse().getPos()));
        }
        switch (lastIndex + 1 - firstIndex) {
            case 2:
                classInfo = models.get(1).predict(new Instance("", testData));
                list = findList(2, classInfo);
                break;
            case 3:
                classInfo = models.get(2).predict(new Instance("", testData));
                list = findList(3, classInfo);
                break;
            case 4:
                classInfo = models.get(3).predict(new Instance("", testData));
                list = findList(4, classInfo);
                break;
            case 5:
                classInfo = models.get(4).predict(new Instance("", testData));
                list = findList(5, classInfo);
                break;
            case 6:
                classInfo = models.get(5).predict(new Instance("", testData));
                list = findList(6, classInfo);
                break;
            case 7:
                classInfo = models.get(6).predict(new Instance("", testData));
                list = findList(7, classInfo);
                break;
            default:
                break;
        }
        int headIndex = findHeadIndex(list, firstIndex, lastIndex);
        for (int i = 0; i < Objects.requireNonNull(list).size(); i++) {
            AnnotatedWord fromWord = wordNodePairList.get(firstIndex + list.get(i).getKey()).getWord();
            AnnotatedWord toWord = wordNodePairList.get(firstIndex + list.get(i).getValue()).getWord();
            AnnotatedWord headWord = wordNodePairList.get(headIndex).getWord();
            ArrayList<Attribute> attributes = new ArrayList<>(29);
            attributes.add(new DiscreteAttribute(fromWord.getParse().getPos()));
            attributes.add(new DiscreteAttribute(fromWord.getParse().getRootPos()));
            attributes.add(new DiscreteAttribute(Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.ABLATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.DATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.GENITIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.NOMINATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.PROPERNOUN))));
            attributes.add(new DiscreteAttribute(toWord.getParse().getPos()));
            attributes.add(new DiscreteAttribute(toWord.getParse().getRootPos()));
            attributes.add(new DiscreteAttribute(Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.ABLATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.DATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.GENITIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.NOMINATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.PROPERNOUN))));
            attributes.add(new DiscreteAttribute(headWord.getParse().getPos()));
            attributes.add(new DiscreteAttribute(headWord.getParse().getRootPos()));
            attributes.add(new DiscreteAttribute(Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.ABLATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.DATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.GENITIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.NOMINATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE))));
            attributes.add(new DiscreteAttribute(Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.PROPERNOUN))));
            if (fromWord.getSemantic() == null || headWord.getSemantic() == null) {
                attributes.add(new DiscreteAttribute("null"));
            } else {
                attributes.add(new DiscreteAttribute(Boolean.toString(fromWord.getSemantic().equals(headWord.getSemantic()))));
            }
            attributes.add(new DiscreteAttribute(node.getData().getName()));
            String firstChild = "null", secondChild = "null", thirdChild = "null";
            if (node.numberOfChildren() > 0) {
                firstChild = node.getChild(0).getData().getName();
            }
            if (node.numberOfChildren() > 1) {
                secondChild = node.getChild(1).getData().getName();
            }
            if (node.numberOfChildren() > 2) {
                thirdChild = node.getChild(2).getData().getName();
            }
            attributes.add(new DiscreteAttribute(firstChild));
            attributes.add(new DiscreteAttribute(secondChild));
            attributes.add(new DiscreteAttribute(thirdChild));
            decisions.add(new Decision(firstIndex + list.get(i).getKey(), list.get(i).getValue() - list.get(i).getKey(), models.get(0).predict(new Instance("", attributes))));
        }
        addHeadToDecisions(decisions, headIndex);
        return decisions;
    }
}
