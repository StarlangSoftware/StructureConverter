package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import StructureConverter.WordNodePair;

import java.util.ArrayList;
import java.util.HashMap;

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
        for (int i = startIndex; i <= finishIndex; i++) {
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

    private HashMap<String, Integer> setMap() {
        HashMap<String, Integer> set = new HashMap<>();
        set.put("PUNCT", 0);
        set.put("VP", 1);
        set.put("NOMP", 1);
        set.put("NP", 2);
        set.put("S", 2);
        set.put("ADJP", 3);
        set.put("ADVP", 3);
        set.put("DP", 4);
        set.put("NUM", 4);
        set.put("QP", 5);
        set.put("PP", 5);
        set.put("NEG", 5);
        set.put("CONJP", 5);
        set.put("INTJ", 5);
        set.put("WP", 5);
        return set;
    }

    private int findEndingNode(int start, ArrayList<WordNodePair> wordNodePairList){
        int i = start + 1;
        while (i < wordNodePairList.size() - 1 && wordNodePairList.get(i).getNode().getParent().equals(wordNodePairList.get(i + 1).getNode().getParent())) {
            i++;
        }
        return i;
    }

    private int getPriority(HashMap<String, Integer> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return 6;
    }

    private int findNodeIndex(int start, int last, ArrayList<WordNodePair> wordNodePairList) {
        HashMap<String, Integer> map = setMap();
        int bestPriority = getPriority(map, wordNodePairList.get(last).getNode().getData().getName());
        int currentIndex = last;
        for (int i = last - 1; i >= start; i--) {
            int priority = getPriority(map, wordNodePairList.get(i).getNode().getData().getName());
            if (priority < bestPriority){
                bestPriority = priority;
                currentIndex = i;
            }
        }
        return currentIndex;
    }

    private void addUniversalDependency(ArrayList<ParseNodeDrawable> parseNodeDrawableList, ArrayList<WordNodePair> wordNodePairList) {
        for (int i = 0; i < parseNodeDrawableList.size() - 1; i++) {
            if (parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                int last = findEndingNode(i, wordNodePairList);
                if (last - i + 1 == parseNodeDrawableList.get(i).numberOfChildren()){
                    int currentIndex = findNodeIndex(i, last, wordNodePairList);
                    setToAndAddUniversalDependency(i, currentIndex, wordNodePairList, last);
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
