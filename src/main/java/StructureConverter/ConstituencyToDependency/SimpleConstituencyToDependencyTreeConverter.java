package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import Dictionary.Word;
import MorphologicalAnalysis.MorphologicalTag;
import StructureConverter.WordNodePair;

import java.util.ArrayList;
import java.util.HashMap;

public class SimpleConstituencyToDependencyTreeConverter implements ConstituencyToDependencyTreeConverter {

    private String findData(String dependent, String head, boolean condition1, boolean condition2, AnnotatedWord dependentWord, AnnotatedWord headWord) {
        if (condition1 || condition2) {
            return "PUNCT";
        }
        switch (dependent) {
            case "ADVP":
                if (dependentWord.getParse().getRootPos().equals("VERB")) {
                    return "ADVCL";
                }
                return "ADVMOD";
            case "ADJP":
                switch (head) {
                    case "NP":
                        if (dependentWord.getParse().getRootPos().equals("VERB")) {
                            return "ACL";
                        }
                        return "AMOD";
                }
                return "ADVMOD";
            case "PP":
                switch (head) {
                    case "NP":
                        return "CASE";
                    default:
                        return "ADVMOD";
                }
            case "DP":
                return "DET";
            case "NP":
                switch (head) {
                    case "NP":
                        if (dependentWord.getParse().containsTag(MorphologicalTag.PROPERNOUN) && headWord.getParse().containsTag(MorphologicalTag.PROPERNOUN)) {
                            return "FLAT";
                        }
                        if (dependentWord.getSemantic() != null && headWord.getSemantic() != null && dependentWord.getSemantic().equals(headWord.getSemantic())) {
                            return "COMPOUND";
                        }
                        return "NMOD";
                    case "VP":
                        if (dependentWord.getSemantic() != null && headWord.getSemantic() != null && dependentWord.getSemantic().equals(headWord.getSemantic())) {
                            return "COMPOUND";
                        }
                        if (dependentWord.getParse().containsTag(MorphologicalTag.NOMINATIVE) || dependentWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE)) {
                            return "OBJ";
                        }
                        return "OBL";
                }
                return "NMOD";
            case "S":
                switch (head) {
                    case "VP":
                        return "CCOMP";
                    default:
                        return "DEP";
                }
            case "NUM":
                return "NUMMOD";
            case "INTJ":
                return "DISCOURSE";
            case "NEG":
                return "NEG";
            case "CONJP":
                return "CC";
            default:
                return "DEP";
        }
    }

    private void setToAndAddUniversalDependency(int startIndex, int headIndex, ArrayList<WordNodePair> wordNodePairList, int finishIndex, ParseNodeDrawable parent) {
        for (int i = startIndex; i <= finishIndex; i++) {
            if (i != headIndex) {
                wordNodePairList.get(i).done();
                String parentData = parent.getData().getName();
                String firstChild = parent.getChild(0).getData().getName();
                String secondChild = null, thirdChild = null;
                if (parent.numberOfChildren() > 1){
                    secondChild = parent.getChild(1).getData().getName();
                }
                if (parent.numberOfChildren() > 2){
                    thirdChild = parent.getChild(2).getData().getName();
                }
                if (parent.numberOfChildren() == 2 && parentData.equals("S") && firstChild.equals("NP")) {
                    if (wordNodePairList.get(headIndex).getWord().getParse().containsTag(MorphologicalTag.PASSIVE)) {
                        wordNodePairList.get(i).getWord().setUniversalDependency(wordNodePairList.get(headIndex).getNo(), "NSUBJPASS");
                    } else {
                        wordNodePairList.get(i).getWord().setUniversalDependency(wordNodePairList.get(headIndex).getNo(), "NSUBJ");
                    }
                } else if (parent.numberOfChildren() == 3 && parentData.equals("S") && firstChild.equals("NP") && secondChild.equals("VP") && Word.isPunctuation(thirdChild)) {
                    if (!wordNodePairList.get(i).getWord().isPunctuation()) {
                        if (wordNodePairList.get(headIndex).getWord().getParse().containsTag(MorphologicalTag.PASSIVE)) {
                            wordNodePairList.get(i).getWord().setUniversalDependency(wordNodePairList.get(headIndex).getNo(), "NSUBJPASS");
                        } else {
                            wordNodePairList.get(i).getWord().setUniversalDependency(wordNodePairList.get(headIndex).getNo(), "NSUBJ");
                        }
                    } else {
                        wordNodePairList.get(i).getWord().setUniversalDependency(wordNodePairList.get(headIndex).getNo(), "PUNCT");
                    }
                } else {
                    String dependent = wordNodePairList.get(i).getNode().getData().getName();
                    String head = wordNodePairList.get(headIndex).getNode().getData().getName();
                    boolean condition1 = wordNodePairList.get(i).getNode().getData().isPunctuation();
                    boolean condition2 = wordNodePairList.get(headIndex).getNode().getData().isPunctuation();
                    wordNodePairList.get(i).getWord().setUniversalDependency(wordNodePairList.get(headIndex).getNo(), findData(dependent, head, condition1, condition2, wordNodePairList.get(i).getWord(), wordNodePairList.get(headIndex).getWord()));
                }
            }
        }
        if (wordNodePairList.get(headIndex).getNode().getParent() != null) {
            wordNodePairList.get(headIndex).updateNode();
            if (wordNodePairList.get(headIndex).getNode().getParent() != null && wordNodePairList.get(headIndex).getNode().getParent().numberOfChildren() == 1) {
                wordNodePairList.get(headIndex).updateNode();
            }
        }
    }

    private HashMap<String, Integer> setMap() {
        HashMap<String, Integer> set = new HashMap<>();
        set.put("PUNCT", 0);
        set.put("VP", 1);
        set.put("NOMP", 1);
        set.put("S", 2);
        set.put("NP", 2);
        set.put("ADJP", 2);
        set.put("ADVP", 2);
        set.put("PP", 3);
        set.put("DP", 4);
        set.put("NUM", 4);
        set.put("QP", 5);
        set.put("NEG", 5);
        set.put("CONJP", 5);
        set.put("INTJ", 5);
        set.put("WP", 5);
        return set;
    }

    private int findEndingNode(int start, ArrayList<WordNodePair> wordNodePairList) {
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

    private WordNodePair convertParseNodeDrawableToWordNodePair(ParseNodeDrawable parseNodeDrawable, ArrayList<WordNodePair> wordNodePairList) {
        for (WordNodePair wordNodePair : wordNodePairList) {
            if (wordNodePair.getNode().equals(parseNodeDrawable)) {
                return wordNodePair;
            }
        }
        return null;
    }

    private void addUniversalDependency(ArrayList<ParseNodeDrawable> parseNodeDrawableList, ArrayList<WordNodePair> wordNodePairList) {
        for (int i = 0; i < parseNodeDrawableList.size() - 1; i++) {
            if (parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                int last = findEndingNode(i, wordNodePairList);
                if (last - i + 1 == parseNodeDrawableList.get(i).numberOfChildren()) {
                    if (parseNodeDrawableList.get(i).numberOfChildren() == 3 && parseNodeDrawableList.get(i).getChild(1).getData().getName().equals("CONJP")) {
                        WordNodePair first = convertParseNodeDrawableToWordNodePair((ParseNodeDrawable) parseNodeDrawableList.get(i).getChild(0), wordNodePairList);
                        WordNodePair second = convertParseNodeDrawableToWordNodePair((ParseNodeDrawable) parseNodeDrawableList.get(i).getChild(1), wordNodePairList);
                        WordNodePair third = convertParseNodeDrawableToWordNodePair((ParseNodeDrawable) parseNodeDrawableList.get(i).getChild(2), wordNodePairList);
                        if (first != null && second != null && third != null) {
                            second.done();
                            third.done();
                            second.getWord().setUniversalDependency(third.getNo(), "CC");
                            third.getWord().setUniversalDependency(first.getNo(), "CONJ");
                            if (first.getNode().getParent() != null) {
                                first.updateNode();
                                if (first.getNode().getParent() != null && first.getNode().getParent().numberOfChildren() == 1) {
                                    first.updateNode();
                                }
                            }
                        }
                    } else {
                        int currentIndex = findNodeIndex(i, last, wordNodePairList);
                        setToAndAddUniversalDependency(i, currentIndex, wordNodePairList, last, parseNodeDrawableList.get(i));
                    }
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
