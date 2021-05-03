package StructureConverter.DependencyToConstituency;

import AnnotatedSentence.*;
import AnnotatedTree.ParenthesisInLayerException;
import AnnotatedTree.ParseNodeDrawable;
import Classification.Model.TreeEnsembleModel;
import ParseTree.*;
import ParseTree.ParseTree;
import StructureConverter.*;

import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;
import DependencyParser.UniversalDependencyRelation;

import java.util.*;

public class SimpleDependencyToConstituencyTreeConverter implements DependencyToConstituencyTreeConverter {

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
            if (!word.isDoneForConnect() && i != j && toWord > -1 && toWord < wordList.size()) {
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
     * @return the index of the inserted <code>wordNodePair</code>.
     */

    private int updateUnionCandidateLists(ArrayList<WordNodePair> list, WordNodePair wordNodePair) {
        if (list.size() < 2) {
            if (list.size() == 1 && list.get(0).getNo() > wordNodePair.getNo()) {
                list.add(0, wordNodePair);
                return 0;
            } else {
                list.add(wordNodePair);
                return list.size() - 1;
            }
        } else {
            if (list.get(0).getNo() > wordNodePair.getNo()) {
                list.add(0, wordNodePair);
                return 0;
            } else if (list.get(list.size() - 1).getNo() < wordNodePair.getNo()) {
                list.add(wordNodePair);
                return list.size() - 1;
            } else {
                for (int i = 0; i < list.size() - 1; i++) {
                    if (wordNodePair.getNo() > list.get(i).getNo() && wordNodePair.getNo() < list.get(i + 1).getNo()) {
                        list.add(i + 1, wordNodePair);
                        return i + 1;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Finds words to combine.
     * @param wordNodePairs {@link WordNodePair} {@link ArrayList}.
     * @param headWord the head of words to combine.
     * @return the {@link ArrayList} of {@link WordNodePair}s to combine.
     */

    private ArrayList<WordNodePair> setOfNodesToBeMergedOntoNode(ArrayList<WordNodePair> wordNodePairs, WordNodePair headWord) {
        ArrayList<WordNodePair> list = new ArrayList<>();
        for (int i = 0; i < wordNodePairs.size(); i++) {
            WordNodePair wordNodePair = wordNodePairs.get(i);
            int toWordIndex = wordNodePair.getTo() - 1;
            if (!wordNodePair.isDoneForConnect()) {
                if (noIncomingNodes(wordNodePairs, i) && toWordIndex == headWord.getNo()) {
                    wordNodePair.doneForConnect();
                    updateUnionCandidateLists(list, wordNodePair);
                }
            } else {
                if (toWordIndex > -1 && toWordIndex == headWord.getNo()) {
                    updateUnionCandidateLists(list, wordNodePair);
                }
            }
        }
        return list;
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
     * Merges {@link WordNodePair}s in <code>unionlist</code>.
     * @param wordNodePairs {@link WordNodePair} {@link ArrayList} of all words.
     * @param unionList {@link ArrayList} of {@link WordNodePair}s to merge.
     * @param i index of headWord.
     */

    private void merge(ArrayList<WordNodePair> wordNodePairs, ArrayList<WordNodePair> unionList, int i, ArrayList<TreeEnsembleModel> models) {
        int index = updateUnionCandidateLists(unionList, wordNodePairs.get(i));
        ProjectionOracle oracle;
        if (models == null || unionList.size() > 8) {
            oracle = new BasicOracle();
        } else {
            oracle = new ClassifierOracle();
        }
        ArrayList<SimpleEntry<Command, String>> list = oracle.makeCommands(unionList, index, models);
        ArrayList<WordNodePair> currentUnionList = new ArrayList<>();
        currentUnionList.add(unionList.get(index));
        int leftIndex = 0, rightIndex = 0, iterate = 0;
        while (iterate < list.size()) {
            Command command = list.get(iterate).getKey();
            switch (command) {
                case MERGE:
                    String treePos = list.get(iterate).getValue();
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

    /**
     * Merges {@link ParseNodeDrawable}s in <code>list</code>.
     * @param list {@link WordNodePair} {@link ArrayList}.
     * @param treePos pos tag of {@link ParseNodeDrawable}s.
     */

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

    /**
     * @param map dependency {@link HashMap}.
     * @param current key of {@link ParseNodeDrawable}.
     * @param total all connections.
     * @return Checks all {@link ParseNodeDrawable}s are merging.
     */

    private boolean isThereAll(HashMap<Integer, ArrayList<Integer>> map, int current, int total) {
        return map.get(current).size() == total;
    }

    /**
     * Get root of <code>node</code>.
     * @param node {@link ParseNodeDrawable}.
     * @return a {@link ParseNodeDrawable} (root).
     */

    private ParseNodeDrawable getParent(ParseNodeDrawable node) {
        if (node.getParent() != null) {
            return getParent((ParseNodeDrawable) node.getParent());
        } else {
            return node;
        }
    }

    /**
     * Creates a {@link HashMap} which contains all connections of {@link ParseNodeDrawable}s.
     * @param wordNodePairs {@link WordNodePair} {@link ArrayList}.
     * @return a {@link HashMap}.
     */

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

    private boolean isSpecialState(ArrayList<WordNodePair> unionList, ArrayList<WordNodePair> wordNodePairs, int headIndex) {
        WordNodePair head = wordNodePairs.get(headIndex);
        if (head.getTo() > 0 && head.getTo() < wordNodePairs.size() && headIndex - 1 == head.getTo()) {
            WordNodePair first = wordNodePairs.get(head.getTo() - 1);
            WordNodePair second = wordNodePairs.get(head.getTo());
            if (!first.isDoneForConnect() && head.getUniversalDependency().equals("CONJ") && second.getUniversalDependency().equals("CC") && second.getTo() - 1 == headIndex) {
                updateUnionCandidateLists(unionList, first);
                if (noIncomingNodes(wordNodePairs, head.getTo() - 1)) {
                    first.doneForConnect();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Converts {@link WordNodePair} {@link ArrayList} to {@link ParseTree}.
     * @param wordNodePairs {@link WordNodePair} {@link ArrayList}.
     * @param dependencyMap {@link UniversalDependencyRelation}'s {@link HashMap}.
     * @return a {@link ParseTree}.
     */

    private ParseTree constructTreeFromWords(ArrayList<WordNodePair> wordNodePairs, HashMap<Integer, ArrayList<Integer>> dependencyMap, ArrayList<TreeEnsembleModel> models) throws FileNotFoundException {
        int total;
        while (true) {
            int j = 0;
            ArrayList<WordNodePair> unionList = new ArrayList<>();
            do {
                WordNodePair head = wordNodePairs.get(j);
                if (!head.isDoneForHead()) {
                    unionList = setOfNodesToBeMergedOntoNode(wordNodePairs, head);
                    if (isSpecialState(unionList, wordNodePairs, j)) {
                        j++;
                        break;
                    } else {
                        j++;
                        total = unionList.size();
                        if (dependencyMap.containsKey(j) && isThereAll(dependencyMap, j, total) && (unionList.size() != 0)) {
                            break;
                        }
                    }
                } else {
                    j++;
                }
            } while (j < wordNodePairs.size());
            wordNodePairs.get(j - 1).doneForHead();
            if (unionList.size() > 0) {
                merge(wordNodePairs, unionList, j - 1, models);
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

    /**
     * Removes one-child {@link ParseNodeDrawable}s.
     * @param parseNodeDrawables {@link ParseNodeDrawable}s to be deleted.
     */

    private void setTree(ArrayList<ParseNodeDrawable> parseNodeDrawables) {
        for (ParseNodeDrawable p : parseNodeDrawables) {
            ParseNodeDrawable child = (ParseNodeDrawable) p.getChild(0);
            p.removeChild(child);
            for (int i = 0; i < child.numberOfChildren(); i++) {
                p.addChild(child.getChild(i));
            }
        }
    }

    /**
     * @param node Root of {@link ParseNodeDrawable}s.
     * @return one-child {@link ParseNodeDrawable}s.
     */

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

    public ParseTree convert(AnnotatedSentence annotatedSentence, ArrayList<TreeEnsembleModel> models) {
        try {
            ArrayList<WordNodePair> wordNodePairs = constructWordPairList(annotatedSentence, annotatedSentence.getFileName());
            HashMap<Integer, ArrayList<Integer>> dependencyMap = setDependencyMap(wordNodePairs);
            if (wordNodePairs.size() > 1) {
                return constructTreeFromWords(wordNodePairs, dependencyMap, models);
            } else {
                ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol("S"));
                parent.addChild(wordNodePairs.get(0).getNode());
                return new ParseTree(parent);
            }
        } catch (MorphologicalAnalysisNotExistsException | UniversalDependencyNotExistsException | ParenthesisInLayerException | NonProjectiveDependencyException | FileNotFoundException e) {
            System.out.println(e);
        }
        return null;
    }
}
