package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import StructureConverter.WordNodePair;
import Util.Tuple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class SimpleConstituencyToDependencyTreeConverter implements ConstituencyToDependencyTreeConverter {

    private String findData(String data1, String data2, boolean condition1, boolean condition2) {
        if (condition1 || condition2) {
            return "PUNCT";
        }
        switch (data1) {
            case "ADVP":
                switch (data2) {
                    case "VP":
                    case "NP":
                        return "ADVMOD";
                    default:
                        return "DEP";
                }
            case "ADJP":
                switch (data2) {
                    case "NP":
                        return "AMOD";
                    default:
                        return "DEP";
                }
            case "PP":
                switch (data2) {
                    case "NP":
                        return "CASE";
                    default:
                        return "DEP";
                }
            case "DP":
                switch (data2) {
                    case "NP":
                        return "DET";
                    default:
                        return "DEP";
                }
            case "NP":
                switch (data2) {
                    case "NP":
                        return "NMOD";
                    default:
                        return "DEP";
                }
            case "S":
                switch (data2) {
                    case "VP":
                        return "ACL";
                    default:
                        return "DEP";
                }
            case "NUM":
                return "NUMMOD";
            default:
                return "DEP";
        }
    }

    private void setToAndAddUniversalDependency(int startIndex, int currentIndex, ArrayList<WordNodePair> wordNodePairList, int finishIndex) {
        if (wordNodePairList.get(currentIndex).getWord().isPunctuation()) {
            WordNodePair temporary = wordNodePairList.get(currentIndex);
            int current = findLast(wordNodePairList, startIndex, currentIndex);
            WordNodePair wordNodePair = wordNodePairList.get(current);
            wordNodePairList.set(currentIndex, wordNodePair);
            wordNodePairList.set(current, temporary);
        }
        for (int i = startIndex; i < finishIndex; i++) {
            if (i != currentIndex) {
                wordNodePairList.get(i).done();
                wordNodePairList.get(i).getWord().setUniversalDependency(wordNodePairList.get(currentIndex).getNo(), findData(wordNodePairList.get(i).getNode().getData().getName(), wordNodePairList.get(currentIndex).getNode().getData().getName(), wordNodePairList.get(i).getNode().getData().isPunctuation(), wordNodePairList.get(currentIndex).getNode().getData().isPunctuation()));
            }
        }
        if (wordNodePairList.get(currentIndex).getNode().getParent() != null) {
            wordNodePairList.get(currentIndex).updateNode();
            if (wordNodePairList.get(currentIndex).getNode().getParent() != null && wordNodePairList.get(currentIndex).getNode().getParent().numberOfChildren() == 1) {
                wordNodePairList.get(currentIndex).updateNode();
            }
        }
    }

    private int findLast(ArrayList<WordNodePair> wordNodePairList, int index, int currentIndex) {
        int nodeIndex = -1;
        int iterate = 0;
        for (int i = index; i < currentIndex; i++) {
            if (!wordNodePairList.get(currentIndex - iterate - 1).getWord().isPunctuation()) {
                nodeIndex = currentIndex - iterate - 1;
                break;
            }
            iterate++;
        }
        return nodeIndex;
    }

    private LinkedHashMap<HashSet<String>, Integer> setMap() {
        LinkedHashMap<HashSet<String>, Integer> map = new LinkedHashMap<>();
        HashSet<String> set = new HashSet<>();
        set.add("PUNCT");
        map.put((HashSet<String>) set.clone(), null);
        set.clear();
        set.add("VP");
        set.add("NOMP");
        map.put((HashSet<String>) set.clone(), null);
        set.clear();
        set.add("NP");
        set.add("S");
        map.put((HashSet<String>) set.clone(), null);
        set.clear();
        set.add("ADJP");
        set.add("ADVP");
        map.put((HashSet<String>) set.clone(), null);
        set.clear();
        set.add("DT");
        set.add("NUM");
        map.put((HashSet<String>) set.clone(), null);
        set.clear();
        set.add("QP");
        set.add("PP");
        set.add("NEG");
        set.add("CONJP");
        set.add("INTJ");
        set.add("WP");
        map.put((HashSet<String>) set.clone(), null);
        return map;
    }

    private Tuple findNodeIndex(int start, ArrayList<ParseNodeDrawable> parseNodeDrawableList) {
        LinkedHashMap<HashSet<String>, Integer> map = setMap();
        int last = parseNodeDrawableList.size() - 1;
        int currentIndex = 0;
        for (int i = start; i < parseNodeDrawableList.size() - 1; i++) {
            if (parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                for (HashSet<String> set : map.keySet()) {
                    if (parseNodeDrawableList.get(i + 1).getParent() != null) {
                        if (set.contains(parseNodeDrawableList.get(i + 1).getParent().getData().getName())) {
                            map.put(set, i + 1);
                            break;
                        }
                    } else {
                        if (set.contains(parseNodeDrawableList.get(i + 1).getData().getName())) {
                            map.put(set, i + 1);
                            break;
                        }
                    }
                }
            } else {
                last = i;
                break;
            }
        }
        for (HashSet<String> set : map.keySet()) {
            if (map.get(set) != null) {
                currentIndex = map.get(set);
                break;
            }
        }
        return new Tuple(currentIndex, last);
    }

    private void addUniversalDependency(ArrayList<ParseNodeDrawable> parseNodeDrawableList, ArrayList<WordNodePair> wordNodePairList) {
        for (int i = 0; i < parseNodeDrawableList.size() - 1; i++) {
            if (parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                Tuple tuple = findNodeIndex(i, parseNodeDrawableList);
                int currentIndex = tuple.getFirst();
                if (currentIndex - i + 1 == parseNodeDrawableList.get(i).numberOfChildren()) {
                    setToAndAddUniversalDependency(i, currentIndex, wordNodePairList, tuple.getLast());
                    break;
                }
            }
        }
    }

    private void constructDependenciesFromTree(ArrayList<WordNodePair> wordNodePairList) {
        setRoot(wordNodePairList);
        ArrayList<ParseNodeDrawable> parseNodeDrawableList = new ArrayList<>();
        ArrayList<WordNodePair> wordNodePairs = new ArrayList<>(wordNodePairList);
        for (WordNodePair wordNodePair : wordNodePairList) {
            parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
        }
        while (parseNodeDrawableList.size() > 1) {
            addUniversalDependency(parseNodeDrawableList, wordNodePairs);
            parseNodeDrawableList.clear();
            wordNodePairs.clear();
            for (WordNodePair wordNodePair : wordNodePairList) {
                if (!wordNodePair.isDone()) {
                    parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
                    wordNodePairs.add(wordNodePair);
                }
            }
        }
    }

    private void setRoot(ArrayList<WordNodePair> wordNodePairList) {
        AnnotatedWord last = null;
        for (int i = 0; i < wordNodePairList.size(); i++) {
            WordNodePair wordNodePair = wordNodePairList.get(wordNodePairList.size() - i - 1);
            if (!wordNodePair.getWord().isPunctuation()) {
                last = wordNodePair.getWord();
                break;
            }
        }
        if (last != null) {
            last.setUniversalDependency(0, "root");
        }
    }

    @Override
    public AnnotatedSentence convert(ParseTreeDrawable parseTree) {
        if (parseTree != null) {
            AnnotatedSentence annotatedSentence = new AnnotatedSentence();
            NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsLeafNode());
            ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
            ArrayList<WordNodePair> wordNodePairList = new ArrayList<>();
            for (int i = 0; i < leafList.size(); i++) {
                ParseNodeDrawable parseNode = leafList.get(i);
                WordNodePair wordNodePair = new WordNodePair(parseNode, i + 1);
                wordNodePair.updateNode();
                if (wordNodePair.getNode().getParent() != null && wordNodePair.getNode().getParent().numberOfChildren() == 1) {
                    wordNodePair.updateNode();
                    System.out.println("check this");
                    return null;
                }
                annotatedSentence.addWord(wordNodePair.getWord());
                wordNodePairList.add(wordNodePair);
            }
            constructDependenciesFromTree(wordNodePairList);
            return annotatedSentence;
        }
        return null;
    }
}
