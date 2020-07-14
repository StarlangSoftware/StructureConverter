package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

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
            default:
                return "DEP";
        }
    }

    private void setToAndAddUniversalDependency(int index, int currentIndex, ArrayList<WordNodePair> wordNodePairList) {
        for (int i = index; i < currentIndex; i++) {
            wordNodePairList.get(i).done();
            wordNodePairList.get(i).getWord().setUniversalDependency(wordNodePairList.get(currentIndex).getNo(), findData(wordNodePairList.get(i).getNode().getData().getName(), wordNodePairList.get(currentIndex).getNode().getData().getName(), wordNodePairList.get(i).getNode().getData().isPunctuation(), wordNodePairList.get(currentIndex).getNode().getData().isPunctuation()));
        }
        if (wordNodePairList.get(currentIndex).getNode().getParent() != null) {
            wordNodePairList.get(currentIndex).updateNode();
            if (wordNodePairList.get(currentIndex).getNode().getParent() != null && wordNodePairList.get(currentIndex).getNode().getParent().numberOfChildren() == 1) {
                wordNodePairList.get(currentIndex).updateNode();
            }
        }
    }

    private boolean allEquals(ArrayList<ParseNodeDrawable> parseNodeDrawableList) {
        for (int i = 0; i < parseNodeDrawableList.size() - 1; i++) {
            if (!parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    private void addUniversalDependency(ArrayList<ParseNodeDrawable> parseNodeDrawableList, ArrayList<WordNodePair> wordNodePairList) {
        if (allEquals(parseNodeDrawableList) && wordNodePairList.get(wordNodePairList.size() - 1).getWord().isPunctuation()) {
            WordNodePair temporary = wordNodePairList.get(wordNodePairList.size() - 1);
            wordNodePairList.set(wordNodePairList.size() - 1, wordNodePairList.get(wordNodePairList.size() - 2));
            wordNodePairList.set(wordNodePairList.size() - 2, temporary);
        }
        for (int i = 0; i < parseNodeDrawableList.size() - 1; i++) {
            if (parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                int currentIndex = 0;
                for (int k = i; k < parseNodeDrawableList.size(); k++) {
                    if (k + 1 < parseNodeDrawableList.size() && parseNodeDrawableList.get(k).equals(parseNodeDrawableList.get(k + 1))) {
                        currentIndex = k + 1;
                    } else {
                        break;
                    }
                }
                if (currentIndex - i + 1 == parseNodeDrawableList.get(i).numberOfChildren()) {
                    setToAndAddUniversalDependency(i, currentIndex, wordNodePairList);
                    break;
                }
            }
        }
    }

    private void constructDependenciesFromTree(ArrayList<WordNodePair> wordNodePairList) {
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
