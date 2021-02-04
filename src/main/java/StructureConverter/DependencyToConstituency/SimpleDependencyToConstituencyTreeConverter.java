package StructureConverter.DependencyToConstituency;

import AnnotatedSentence.*;
import AnnotatedTree.ParenthesisInLayerException;
import AnnotatedTree.ParseNodeDrawable;
import ParseTree.*;
import ParseTree.ParseTree;
import StructureConverter.*;
import java.util.AbstractMap.SimpleEntry;
import DependencyParser.UniversalDependencyRelation;

import java.util.*;

public class SimpleDependencyToConstituencyTreeConverter implements DependencyToConstituencyTreeConverter {

    /**
     * Creates the {@link HashMap} of priority {@link UniversalDependencyRelation}s.
     * @return Priority {@link HashMap}.
     */

    private HashMap<String, Integer> setSpecialMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("COMPOUND", 8);
        map.put("AUX", 7);
        map.put("DET", 6);
        map.put("AMOD", 5);
        map.put("NUMMOD", 4);
        map.put("CASE", 3);
        map.put("CCOMP", 2);
        map.put("NEG", 1);
        return map;
    }

    /**
     * Converts all elements in the sentence to the {@link WordNodePair} {@link ArrayList}.
     * @param sentence {@link AnnotatedSentence}.
     * @param fileName Filename of the {@link AnnotatedSentence}.
     * @return List of {@link WordNodePair}s.
     */

    private ArrayList<WordNodePair> constructWordPairList(AnnotatedSentence sentence, String fileName) throws MorphologicalAnalysisNotExistsException, UniversalDependencyNotExistsException, ParenthesisInLayerException, NonProjectiveDependencyException {
        ArrayList<WordNodePair> wordNodePairs = new ArrayList<>();
        for (int i = 0; i < sentence.wordCount(); i++) {
            AnnotatedWord annotatedWord1 = (AnnotatedWord) sentence.getWord(i);
            if (annotatedWord1.getParse() == null) {
                throw new MorphologicalAnalysisNotExistsException(fileName);
            }
            if (annotatedWord1.getUniversalDependency() == null) {
                throw new UniversalDependencyNotExistsException(fileName);
            }
            int toWord1 = annotatedWord1.getUniversalDependency().to() - 1;
            wordNodePairs.add(new WordNodePair(annotatedWord1, i));
            for (int j = 0; j < sentence.wordCount(); j++) {
                if (i == j){
                    continue;
                }
                AnnotatedWord annotatedWord2 = (AnnotatedWord) sentence.getWord(j);
                if (annotatedWord2.getUniversalDependency() == null) {
                    throw new UniversalDependencyNotExistsException(fileName);
                }
                int toWord2 = annotatedWord2.getUniversalDependency().to() - 1;
                if (i > j) {
                    if (toWord2 > i && toWord1 > toWord2) {
                        throw new NonProjectiveDependencyException(fileName);
                    }
                } else {
                    if (toWord1 > j && toWord1 < toWord2) {
                        throw new NonProjectiveDependencyException(fileName);
                    }
                }
            }
        }
        return wordNodePairs;
    }

    /**
     * @param wordList {@link WordNodePair} {@link ArrayList}.
     * @param i indexed element.
     * @return checks if there is any connection to the indexed element.
     */

    private boolean noIncomingNodes(ArrayList<WordNodePair> wordList, int i) {
        for (int j = 0; j < wordList.size(); j++) {
            WordNodePair word = wordList.get(j);
            int toWord = word.getTo() - 1;
            if (!word.isDone() && i != j && toWord > -1 && toWord < wordList.size()) {
                if (wordList.get(i).equals(wordList.get(toWord))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Adds a new element to the {@link ArrayList} of elements to be connected sequentially.
     * @param list the {@link ArrayList} of elements to be connected sequentially.
     * @param wordNodePair element to add to the {@link ArrayList}.
     */

    private void updateUnionCandidateLists(ArrayList<WordNodePair> list, WordNodePair wordNodePair) {
        if (list.size() < 2) {
            if (list.size() == 1 && list.get(0).getNo() > wordNodePair.getNo()) {
                list.add(0, wordNodePair);
            } else {
                list.add(wordNodePair);
            }
        } else {
            if (list.get(0).getNo() > wordNodePair.getNo()) {
                list.add(0, wordNodePair);
            } else if (list.get(list.size() - 1).getNo() < wordNodePair.getNo()) {
                list.add(wordNodePair);
            } else {
                for (int i = 0; i < list.size() - 1; i++) {
                    if (wordNodePair.getNo() > list.get(i).getNo() && wordNodePair.getNo() < list.get(i + 1).getNo()) {
                        list.add(i + 1, wordNodePair);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Finds words to combine.
     * @param wordNodePairs {@link WordNodePair} {@link ArrayList}.
     * @param headWord the head of words to combine.
     * @return the {@link ArrayList} of {@link WordNodePair}s to combine.
     */

    private SimpleEntry<ArrayList<WordNodePair>, Boolean> setOfNodesToBeMergedOntoNode(ArrayList<WordNodePair> wordNodePairs, WordNodePair headWord) {
        ArrayList<WordNodePair> list = new ArrayList<>();
        boolean isFinished = false;
        for (int i = 0; i < wordNodePairs.size(); i++) {
            WordNodePair wordNodePair = wordNodePairs.get(i);
            int toWord1 = wordNodePair.getTo() - 1;
            if (!wordNodePair.isDone()) {
                if (noIncomingNodes(wordNodePairs, i) && toWord1 == headWord.getNo()) {
                    wordNodePair.done();
                    updateUnionCandidateLists(list, wordNodePair);
                    if (!isFinished && headWord.getTo() - 1 < wordNodePairs.size() && headWord.getTo() - 1 > -1 && !wordNodePairs.get(headWord.getTo() - 1).isDone() && Math.abs(headWord.getTo() - headWord.getNo()) == 1 && headWord.getUniversalDependency().equals("CONJ") && Math.abs(wordNodePair.getTo() - wordNodePair.getNo()) == 2 && wordNodePair.getUniversalDependency().equals("CC")) {
                        if (noIncomingNodes(wordNodePairs, headWord.getTo() - 1)) {
                            wordNodePairs.get(headWord.getTo() - 1).done();
                        }
                        isFinished = true;
                        updateUnionCandidateLists(list, wordNodePairs.get(headWord.getTo() - 1));
                    }
                }
            } else {
                if (toWord1 > -1 && toWord1 == headWord.getNo()) {
                    updateUnionCandidateLists(list, wordNodePair);
                }
            }
        }
        return new SimpleEntry<>(list, isFinished);
    }

    /**
     * @param parent {@link ParseNodeDrawable}.
     * @param child {@link ParseNodeDrawable}.
     * @return if <code>parent</code> has a <code>child</code> -> true, if not -> false.
     */

    private boolean containsChild(ParseNodeDrawable parent, ParseNodeDrawable child) {
        for (int i = 0; i < parent.numberOfChildren(); i++) {
            if (getParent((ParseNodeDrawable) parent.getChild(i)).equals(getParent(child))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param unionList {@link ArrayList} of those who will merge.
     * @return Checks if the {@link ParseNodeDrawable}s in <code>unionList</code> are all the same.
     */

    private boolean allSame(ArrayList<WordNodePair> unionList) {
        for (int i = 1; i < unionList.size(); i++) {
            if (!getParent(unionList.get(i - 1).getNode()).equals(getParent(unionList.get(i).getNode()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Merges two {@link WordNodePair}s.
     * @param unionList {@link ArrayList} of those who will merge.
     * @param treePos treePos of two {@link ParseNodeDrawable}.
     */

    private void simpleMerge(ArrayList<WordNodePair> unionList, String treePos) {
        ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(treePos));
        for (WordNodePair wordNodePair : unionList) {
            ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
            if (!containsChild(parent, getParent(parseNodeDrawable))) {
                parent.addChild(getParent(parseNodeDrawable));
            }
        }
    }

    /**
     * Merges {@link WordNodePair}s in <code>unionlist</code>.
     * @param wordNodePairs {@link WordNodePair} {@link ArrayList} of all words.
     * @param specialsMap priority {@link HashMap}.
     * @param unionList {@link ArrayList} of {@link WordNodePair}s to merge.
     * @param i index of headWord.
     */

    private void merge(ArrayList<WordNodePair> wordNodePairs, HashMap<String, Integer> specialsMap, ArrayList<WordNodePair> unionList, int i, ProjectionOracle oracle) {
        updateUnionCandidateLists(unionList, wordNodePairs.get(i));
        if (unionList.size() == 2) {
            String treePos = setTreePos(unionList, wordNodePairs.get(i).getTreePos());
            simpleMerge(unionList, treePos);
        } else {
            int index = -1;
            for (int j = 0; j < unionList.size(); j++) {
                if (unionList.get(j).equals(wordNodePairs.get(i))) {
                    index = j;
                    break;
                }
            }
            ArrayList<SimpleEntry<Command, String>> list = oracle.makeCommands(specialsMap, unionList, index);
            ArrayList<WordNodePair> currentUnionList = new ArrayList<>();
            currentUnionList.add(unionList.get(index));
            int leftIndex = 0, rightIndex = 0, iterate = 0;
            while (iterate < list.size()) {
                Command command = list.get(iterate).getKey();
                switch (command) {
                    case MERGE:
                        String treePos = setTreePos(currentUnionList, unionList.get(index).getTreePos());
                        if (list.get(iterate).getValue() != null) {
                            treePos = list.get(iterate).getValue();
                        }
                        mergeNodes(currentUnionList, treePos);
                        currentUnionList.clear();
                        currentUnionList.add(unionList.get(index));
                        break;
                    case LEFT:
                        leftIndex++;
                        updateUnionCandidateLists(currentUnionList, unionList.get(index - leftIndex));
                        break;
                    case RIGHT:
                        rightIndex++;
                        updateUnionCandidateLists(currentUnionList, unionList.get(index + rightIndex));
                        break;
                    default:
                        break;
                }
                iterate++;
            }
        }
    }

    private void mergeNodes(ArrayList<WordNodePair> list, String treePos) {
        ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(treePos));
        if (!allSame(list)) {
            for (WordNodePair wordNodePair : list) {
                if (!containsChild(parent, wordNodePair.getNode())) {
                    parent.addChild(getParent(wordNodePair.getNode()));
                }
            }
        }
    }

    private String setTreePos(ArrayList<WordNodePair> list, String currentPos) {
        String treePos = currentPos;
        for (WordNodePair current : list) {
            if (current != null && current.getTreePos().equals("PP")) {
                treePos = current.getTreePos();
            }
        }
        return treePos;
    }

    private boolean isThereAll(HashMap<Integer, ArrayList<Integer>> map, int current, int total) {
        return map.get(current).size() == total;
    }
    private ParseNodeDrawable getParent(ParseNodeDrawable node) {
        if (node.getParent() != null) {
            return getParent((ParseNodeDrawable) node.getParent());
        } else {
            return node;
        }
    }

    private HashMap<Integer, ArrayList<Integer>> setDependencyMap(ArrayList<WordNodePair> wordNodePairs) {
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        for (int i = 0; i < wordNodePairs.size(); i++) {
            int to;
            if (wordNodePairs.get(i).getTo() == 0) {
                to = wordNodePairs.size();
            } else {
                to = wordNodePairs.get(i).getTo();
            }
            if (!map.containsKey(to)) {
                map.put(to, new ArrayList<>());
            }
            map.get(to).add(i);
        }
        return map;
    }

    /**
     * Converts {@link WordNodePair} {@link ArrayList} to {@link ParseTree}.
     * @param wordNodePairs {@link WordNodePair} {@link ArrayList}.
     * @param dependencyMap {@link UniversalDependencyRelation}'s {@link HashMap}.
     * @return a {@link ParseTree}.
     */

    private ParseTree constructTreeFromWords(ArrayList<WordNodePair> wordNodePairs, HashMap<Integer, ArrayList<Integer>> dependencyMap, ParserConverterType type) {
        ProjectionOracle oracle;
        if (type.equals(ParserConverterType.BASIC_ORACLE)) {
            oracle = new BasicOracle();
        } else {
            oracle = new ClassifierOracle();
        }
        HashMap<String, Integer> specialsMap = setSpecialMap();
        int total;
        while (true) {
            int j = 0;
            ArrayList<WordNodePair> unionList = new ArrayList<>();
            do {
                if (!wordNodePairs.get(j).isFinished()) {
                    SimpleEntry<ArrayList<WordNodePair>, Boolean> simpleEntry = setOfNodesToBeMergedOntoNode(wordNodePairs, wordNodePairs.get(j));
                    unionList = simpleEntry.getKey();
                    boolean isFinished = simpleEntry.getValue();
                    j++;
                    total = unionList.size();
                    if (isFinished || (dependencyMap.containsKey(j) && isThereAll(dependencyMap, j, total) && (unionList.size() != 0))) {
                        break;
                    }
                } else {
                    j++;
                }
                if (j == wordNodePairs.size()) {
                    break;
                }
            } while (true);
            wordNodePairs.get(j - 1).finished();
            if (unionList.size() > 0) {
                merge(wordNodePairs, specialsMap, unionList, j - 1, oracle);
            } else {
                break;
            }
        }
        ParseNodeDrawable root = getParent(wordNodePairs.get(0).getNode());
        if (!root.getData().equals(new Symbol("S"))) {
            root.setData(new Symbol("S"));
        }
        ArrayList<ParseNodeDrawable> parseNodeDrawables = findNodes(root);
        setTree(parseNodeDrawables);
        return new ParseTree(root);
    }

    private void setTree(ArrayList<ParseNodeDrawable> parseNodeDrawables) {
        for (ParseNodeDrawable p : parseNodeDrawables) {
            ParseNodeDrawable child = (ParseNodeDrawable) p.getChild(0);
            p.removeChild(child);
            for (int i = 0; i < child.numberOfChildren(); i++) {
                p.addChild(child.getChild(i));
            }
        }
    }

    private ArrayList<ParseNodeDrawable> findNodes(ParseNodeDrawable node) {
        ArrayList<ParseNodeDrawable> list = new ArrayList<>();
        for (int i = 0; i < node.numberOfChildren(); i++) {
            ParseNodeDrawable child = (ParseNodeDrawable) node.getChild(i);
            if (node.getLayerInfo() == null) {
                if (node.numberOfChildren() == 1 && ((ParseNodeDrawable) node.getChild(0)).getLayerInfo() == null) {
                    list.add(node);
                }
                list.addAll(findNodes(child));
            }
        }
        return list;
    }

    /**
     * Converts {@link AnnotatedSentence} to {@link ParseTree}.
     * @param annotatedSentence {@link AnnotatedSentence} to convert.
     * @return a {@link ParseTree}.
     */

    public ParseTree convert(AnnotatedSentence annotatedSentence, ParserConverterType type) {
        try {
            ArrayList<WordNodePair> wordNodePairs = constructWordPairList(annotatedSentence, annotatedSentence.getFileName());
            HashMap<Integer, ArrayList<Integer>> dependencyMap = setDependencyMap(wordNodePairs);
            if (wordNodePairs.size() > 1) {
                return constructTreeFromWords(wordNodePairs, dependencyMap, type);
            } else {
                ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol("S"));
                parent.addChild(wordNodePairs.get(0).getNode());
                return new ParseTree(parent);
            }
        } catch (MorphologicalAnalysisNotExistsException | UniversalDependencyNotExistsException | ParenthesisInLayerException | NonProjectiveDependencyException e) {
            System.out.println(e.toString());
        }
        return null;
    }
}
