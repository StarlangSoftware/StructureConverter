package StructureConverter.DependencyToConstituency;

import AnnotatedSentence.*;
import AnnotatedTree.ParenthesisInLayerException;
import AnnotatedTree.ParseNodeDrawable;
import ParseTree.*;
import ParseTree.ParseTree;
import Dictionary.Word;
import StructureConverter.*;
import java.util.AbstractMap.SimpleEntry;

import java.util.*;

public class SimpleDependencyToConstituencyTreeConverter implements DependencyToConstituencyTreeConverter {

    private ParseNodeDrawable specialWord;

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

    private boolean noIncomingNodes(ArrayList<WordNodePair> wordList, int i) {
        for (int j = 0; j < wordList.size(); j++) {
            WordNodePair word2 = wordList.get(j);
            int toWord2 = word2.getTo() - 1;
            if (i != j && toWord2 > -1 && toWord2 < wordList.size()) {
                if (wordList.get(i).equals(wordList.get(toWord2))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateUnionCandidateLists(ArrayList<ParseNodeDrawable> list, WordNodePair wordNodePair, ArrayList<ParseNodeDrawable> punctuations, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap) {
        ParseNodeDrawable node = wordNodePair.getNode();
        String dependency1 = wordNodePair.getUniversalDependency();
        if (dependency1.equals("NSUBJ") || dependency1.equals("CSUBJ")) {
            specialWord = getParent(node);
        } else if (Word.isPunctuation(node.getParent().getData().getName()) && dependency1.equals("PUNCT")) {
            punctuations.add(getParent(node));
        } else specialsMap.getOrDefault(dependency1, list).add(getParent(node));
    }

    private SimpleEntry<ArrayList<ParseNodeDrawable>, Boolean> setOfNodesToBeMergedOntoNode(ArrayList<WordNodePair> wordNodePairs, WordNodePair rootWord, ArrayList<ParseNodeDrawable> punctuations, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap, HashSet<Coordinates> set) {
        ArrayList<ParseNodeDrawable> list = new ArrayList<>();
        boolean isFinished = false;
        for (int i = 0; i < wordNodePairs.size(); i++) {
            WordNodePair wordNodePair = wordNodePairs.get(i);
            int toWord1 = wordNodePair.getTo() - 1;
            if (!wordNodePair.isDone()) {
                if (noIncomingNodes(wordNodePairs, i) && toWord1 == rootWord.getNo()) {
                    wordNodePair.done();
                    updateUnionCandidateLists(list, wordNodePair, punctuations, specialsMap);
                    if (rootWord.getTo() - 1 < wordNodePairs.size() && rootWord.getTo() - 1 > -1 && !wordNodePairs.get(rootWord.getTo() - 1).isDone() && Math.abs(rootWord.getTo() - rootWord.getNo()) == 1 && rootWord.getUniversalDependency().equals("CONJ") && Math.abs(wordNodePair.getTo() - wordNodePair.getNo()) == 2 && wordNodePair.getUniversalDependency().equals("CC")) {
                        wordNodePairs.get(rootWord.getTo() - 1).done();
                        updateUnionCandidateLists(list, wordNodePairs.get(rootWord.getTo() - 1), punctuations, specialsMap);
                        isFinished = true;
                        set.add(new Coordinates(rootWord.getTo() - 1, wordNodePairs.get(rootWord.getTo() - 1).getTo()));
                    }
                }
            } else {
                if (toWord1 > -1 && toWord1 == rootWord.getNo()) {
                    updateUnionCandidateLists(list, wordNodePair, punctuations, specialsMap);
                }
            }
        }
        return new SimpleEntry<>(list, isFinished);
    }

    private boolean empty(LinkedHashMap<String, ArrayList<ParseNodeDrawable>> map) {
        for (String key : map.keySet()) {
            if (map.get(key).size() > 0) {
                return false;
            }
        }
        return true;
    }

    private void fillWithSpecialsMap(ArrayList<ParseNodeDrawable> unionList, ArrayList<WordNodePair> wordNodePairs, String treePos, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap, ArrayList<ParseNodeDrawable> punctuations, int current) {
        ParseNodeDrawable grandParent = new ParseNodeDrawable(new Symbol(treePos));
        ParseNodeDrawable parent = null;
        for (String key : specialsMap.keySet()) {
            if (specialsMap.get(key).size() > 0) {
                parent = new ParseNodeDrawable(new Symbol(treePos));
                boolean visited = false;
                for (WordNodePair wordNodePair : wordNodePairs) {
                    ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
                    if (specialsMap.get(key).contains(getParent(parseNodeDrawable))) {
                        parent.addChild(getParent(parseNodeDrawable));
                    } else if (!visited && getParent(wordNodePairs.get(current).getNode()).equals(getParent(parseNodeDrawable))) {
                        parent.addChild(getParent(parseNodeDrawable));
                        visited = true;
                    }
                }
            }
        }
        boolean visited = false;
        ArrayList<ParseNodeDrawable> list = new ArrayList<>();
        for (WordNodePair wordNodePair : wordNodePairs) {
            ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
            if (unionList.contains(getParent(parseNodeDrawable))) {
                list.add(getParent(parseNodeDrawable));
            } else if (getParent(parseNodeDrawable).equals(specialWord)) {
                list.add(specialWord);
            } else if (!visited && parent != null && getParent(parseNodeDrawable).toString().equals(getParent(parent).toString())) {
                visited = true;
                list.add(getParent(parent));
            }
        }
        if (punctuations.size() > 0) {
            addChild(grandParent, wordNodePairs, list, punctuations);
        } else {
            addChild(grandParent, wordNodePairs, list, null);
        }
    }

    private void fillWithJustSpecialsMap(ArrayList<WordNodePair> wordNodePairs, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap, String treePos, ArrayList<ParseNodeDrawable> punctuations, int current) {
        ParseNodeDrawable grandParent = new ParseNodeDrawable(new Symbol(treePos));
        ParseNodeDrawable parent = null;
        for (String key : specialsMap.keySet()) {
            if (specialsMap.get(key).size() > 0) {
                parent = new ParseNodeDrawable(new Symbol(treePos));
                boolean visited = false;
                for (WordNodePair wordNodePair : wordNodePairs) {
                    ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
                    if (specialsMap.get(key).contains(getParent(parseNodeDrawable))) {
                        parent.addChild(getParent(parseNodeDrawable));
                    } else if (!visited && getParent(wordNodePairs.get(current).getNode()).equals(getParent(parseNodeDrawable))) {
                        parent.addChild(getParent(parseNodeDrawable));
                        visited = true;
                    }
                }
            }
        }
        boolean visited = false;
        if (punctuations.size() > 0 || specialWord != null) {
            for (WordNodePair wordNodePair : wordNodePairs) {
                ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
                if (punctuations.contains(getParent(parseNodeDrawable)) || punctuations.contains(parseNodeDrawable)) {
                    grandParent.addChild(getParent(parseNodeDrawable));
                } else if (!visited && parent != null && (parseNodeDrawable.equals(getParent(parent)) || getParent(parseNodeDrawable).equals(getParent(parent)))) {
                    grandParent.addChild(getParent(parent));
                    visited = true;
                } else if (parseNodeDrawable.equals(specialWord) || getParent(parseNodeDrawable).equals(specialWord)) {
                    grandParent.addChild(specialWord);
                }
            }
        } else {
            for (WordNodePair wordNodePair : wordNodePairs) {
                ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
                if (!visited && parent != null && parseNodeDrawable.equals(getParent(parent))) {
                    grandParent.addChild(getParent(parent));
                    visited = true;
                }
            }
        }
    }

    private boolean merge(ArrayList<WordNodePair> wordNodePairs, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap, ArrayList<ParseNodeDrawable> unionList, ArrayList<ParseNodeDrawable> punctuations, int i) {
        ParseNodeDrawable node = wordNodePairs.get(i).getNode();
        if (empty(specialsMap)) {
            if (unionList.size() > 0) {
                wordNodePairs.get(i).done();
                if (!getParent(wordNodePairs.get(0).getNode()).equals(specialWord) && specialWord != null) {
                    ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                    current.add(getParent(node));
                    current.addAll(unionList);
                    current.add(specialWord);
                    addChildForSubject(setTreePos(wordNodePairs, current, specialsMap, wordNodePairs.get(i).getTreePos()), wordNodePairs, current, punctuations, i);
                } else {
                    ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                    current.add(getParent(node));
                    current.addAll(unionList);
                    ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(setTreePos(wordNodePairs, current, specialsMap, wordNodePairs.get(i).getTreePos())));
                    if (specialWord != null) {
                        addChild(parent, wordNodePairs, current, null);
                        ArrayList<ParseNodeDrawable> addAll = new ArrayList<>();
                        addAll.add(specialWord);
                        addAll.add(parent);
                        ParseNodeDrawable grandParent = new ParseNodeDrawable(new Symbol(setTreePos(wordNodePairs, addAll, specialsMap, wordNodePairs.get(i).getTreePos())));
                        addChild(grandParent, wordNodePairs, addAll, punctuations);
                    } else {
                        addChild(parent, wordNodePairs, current, punctuations);
                    }
                }
            } else if (specialWord != null) {
                wordNodePairs.get(i).done();
                ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                current.add(specialWord);
                current.add(getParent(node));
                ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(setTreePos(wordNodePairs, current, specialsMap, wordNodePairs.get(i).getTreePos())));
                addChild(parent, wordNodePairs, current, punctuations);
            } else if (punctuations.size() > 0) {
                wordNodePairs.get(i).done();
                ArrayList<ParseNodeDrawable> current = new ArrayList<>(punctuations);
                current.add(getParent(node));
                ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(setTreePos(wordNodePairs, current, specialsMap, wordNodePairs.get(i).getTreePos())));
                addChild(parent, wordNodePairs, current, null);
            }
        } else {
            wordNodePairs.get(i).done();
            if (controlMap(specialsMap, wordNodePairs, i)) {
                if (unionList.size() > 0) {
                    fillWithSpecialsMap(unionList, wordNodePairs, setTreePos(wordNodePairs, unionList, specialsMap, wordNodePairs.get(i).getTreePos()), specialsMap, punctuations, i);
                } else {
                    fillWithJustSpecialsMap(wordNodePairs, specialsMap, setTreePos(wordNodePairs, unionList, specialsMap, wordNodePairs.get(i).getTreePos()), punctuations, i);
                }
            } else {
                for (String key : specialsMap.keySet()) {
                    unionList.addAll(specialsMap.get(key));
                }
                specialsMap = setSpecialMap();
                merge(wordNodePairs, specialsMap, unionList, punctuations, i);
            }
        }
        return unionList.size() != 0 || punctuations.size() != 0 || !empty(specialsMap) || specialWord != null;
    }

    private String setTreePos(ArrayList<WordNodePair> wordNodePairs, ArrayList<ParseNodeDrawable> list, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap, String currentPos) {
        String treePos = currentPos;
        for (ParseNodeDrawable parseNodeDrawable : list) {
            WordNodePair current = convertParseNodeDrawableToWordNodePair(wordNodePairs, parseNodeDrawable);
            if (current != null && current.getTreePos().equals("PP")) {
                treePos = current.getTreePos();
            }
        }
        for (String key : specialsMap.keySet()) {
            for (int i = 0; i < specialsMap.get(key).size(); i++) {
                WordNodePair current = convertParseNodeDrawableToWordNodePair(wordNodePairs, specialsMap.get(key).get(i));
                if (current != null && current.getTreePos().equals("PP")) {
                    treePos = current.getTreePos();
                }
            }
        }
        return treePos;
    }

    private WordNodePair convertParseNodeDrawableToWordNodePair(ArrayList<WordNodePair> wordNodePairs, ParseNodeDrawable parseNodeDrawable) {
        for (WordNodePair wordNodePair : wordNodePairs) {
            if (getParent(wordNodePair.getNode()).equals(getParent(parseNodeDrawable))) {
                return wordNodePair;
            }
        }
        return null;
    }

    private boolean controlMap(LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap, ArrayList<WordNodePair> wordNodePairs, int current) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(current);
        for (String key : specialsMap.keySet()) {
            for (int i = 0; i < specialsMap.get(key).size(); i++) {
                WordNodePair node = convertParseNodeDrawableToWordNodePair(wordNodePairs, specialsMap.get(key).get(i));
                if (node != null) {
                    list.add(node.getNo());
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(i) < list.get(j)) {
                    int temporary = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temporary);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i + 1 < list.size()) {
                if (list.get(i) + 1 != list.get(i + 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int totalSizeOfMap(LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap) {
        int total = 0;
        for (String key : specialsMap.keySet()) {
            total += specialsMap.get(key).size();
        }
        return total;
    }

    private ParseTree constructTreeFromWords(ArrayList<WordNodePair> wordNodePairs, HashMap<Integer, ArrayList<Integer>> dependencyMap) {
        HashSet<Coordinates> set = new HashSet<>();
        boolean done = true;
        int total;
        while (done) {
            int j = 0;
            ArrayList<ParseNodeDrawable> unionList = new ArrayList<>();
            ArrayList<ParseNodeDrawable> punctuations = new ArrayList<>();
            LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap = setSpecialMap();
            do {
                specialWord = null;
                if (!set.contains(new Coordinates(j, wordNodePairs.get(j).getTo()))) {
                    punctuations = new ArrayList<>();
                    specialsMap = setSpecialMap();
                    SimpleEntry<ArrayList<ParseNodeDrawable>, Boolean> simpleEntry = setOfNodesToBeMergedOntoNode(wordNodePairs, wordNodePairs.get(j), punctuations, specialsMap, set);
                    unionList = simpleEntry.getKey();
                    boolean isFinished = simpleEntry.getValue();
                    j++;
                    if (specialWord != null) {
                        total = unionList.size() + punctuations.size() + 1 + totalSizeOfMap(specialsMap);
                    } else {
                        total = unionList.size() + punctuations.size() + totalSizeOfMap(specialsMap);
                    }
                    if ((dependencyMap.containsKey(j) && isThereAll(dependencyMap, j, total) && (unionList.size() != 0 || punctuations.size() != 0 || !empty(specialsMap) || specialWord != null))) {
                        break;
                    }
                    if (isFinished) {
                        break;
                    }
                } else {
                    j++;
                }
                if (j == wordNodePairs.size()) {
                    break;
                }
            } while (true);
            set.add(new Coordinates(j - 1, wordNodePairs.get(j - 1).getTo()));
            done = merge(wordNodePairs, specialsMap, unionList, punctuations, j - 1);
        }
        ParseNodeDrawable root = getParent(wordNodePairs.get(0).getNode());
        if (!root.getData().equals(new Symbol("S"))) {
            root.setData(new Symbol("S"));
        }
        return new ParseTree(root);
    }

    private boolean isThereAll(HashMap<Integer, ArrayList<Integer>> map, int current, int total) {
        return map.get(current).size() == total;
    }

    private void addChildForSubject(String treePos, ArrayList<WordNodePair> wordNodePairs, ArrayList<ParseNodeDrawable> current, ArrayList<ParseNodeDrawable> punctuations, int index) {
        ParseNodeDrawable parent = null;
        ParseNodeDrawable grandParent = new ParseNodeDrawable(new Symbol(treePos));
        boolean check = false;
        boolean checkForSize = true;
        if (punctuations.size() == 0) {
            for (int i = 0; i < wordNodePairs.size(); i++) {
                WordNodePair wordNodePair = wordNodePairs.get(i);
                ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
                if (getParent(parseNodeDrawable).equals(specialWord)) {
                    check = true;
                } else {
                    if (check) {
                        if (i + 1 < wordNodePairs.size()) {
                            if (current.contains(getParent(parseNodeDrawable))) {
                                if (parent == null) {
                                    parent = new ParseNodeDrawable(new Symbol(treePos));
                                }
                                parent.addChild(getParent(parseNodeDrawable));
                            }
                        } else {
                            checkForSize = false;
                            if (current.contains(getParent(parseNodeDrawable))) {
                                grandParent.addChild(getParent(parseNodeDrawable));
                            }
                        }
                    } else {
                        if (current.contains(getParent(parseNodeDrawable))) {
                            grandParent.addChild(getParent(parseNodeDrawable));
                        }
                    }
                }
            }
            grandParent.addChild(specialWord);
            if (checkForSize && parent != null) {
                grandParent.addChild(parent);
            }
        } else {
            int punctuationCheck = setPunctuationCheck(wordNodePairs);
            for (int i = 0; i < wordNodePairs.size(); i++) {
                WordNodePair wordNodePair = wordNodePairs.get(i);
                ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
                if (i == punctuationCheck + 1) {
                    if (!containsChild(grandParent, parent) && parent != null) {
                        grandParent.addChild(parent);
                    }
                }
                if (punctuations.contains(getParent(parseNodeDrawable))) {
                    grandParent.addChild(getParent(parseNodeDrawable));
                } else if (getParent(parseNodeDrawable).equals(specialWord)) {
                    grandParent.addChild(specialWord);
                    check = true;
                } else {
                    if (check) {
                        if (controlForSpecial(wordNodePairs, index, current)) {
                            if (current.contains(getParent(parseNodeDrawable))) {
                                grandParent.addChild(getParent(parseNodeDrawable));
                            }
                        } else {
                            if (current.contains(getParent(parseNodeDrawable))) {
                                if (parent == null) {
                                    parent = new ParseNodeDrawable(new Symbol(treePos));
                                }
                                parent.addChild(getParent(parseNodeDrawable));
                            }
                        }
                    } else {
                        if (current.contains(getParent(parseNodeDrawable))) {
                            grandParent.addChild(getParent(parseNodeDrawable));
                        }
                    }
                }
            }
        }
    }

    private int setPunctuationCheck(ArrayList<WordNodePair> wordNodePairs) {
        int check = 0;
        for (int i = 0; i < wordNodePairs.size(); i++) {
            if (!wordNodePairs.get(wordNodePairs.size() - i - 1).getWord().isPunctuation()) {
                check = wordNodePairs.size() - i - 1;
                break;
            }
        }
        return check;
    }

    private boolean containsChild(ParseNodeDrawable parseNodeDrawable, ParseNodeDrawable parent) {
        for (int i = 0; i < parseNodeDrawable.numberOfChildren(); i++) {
            if (parseNodeDrawable.getChild(i).equals(parent)) {
                return true;
            }
        }
        return false;
    }

    private boolean controlForSpecial(ArrayList<WordNodePair> wordNodePairs, int index, ArrayList<ParseNodeDrawable> current) {
        if (getParent(wordNodePairs.get(index - 1).getNode()).equals(specialWord) || getParent(wordNodePairs.get(index - 1).getNode()).equals(getParent(specialWord))) {
            if (index + 1 < wordNodePairs.size()) {
                for (int i = index + 1; i < wordNodePairs.size(); i++) {
                    if (current.contains(getParent(wordNodePairs.get(i).getNode()))) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private void addChild(ParseNodeDrawable parent, ArrayList<WordNodePair> wordNodePairs, ArrayList<ParseNodeDrawable> current, ArrayList<ParseNodeDrawable> punctuations) {
        for (WordNodePair wordNodePair : wordNodePairs) {
            ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
            if (current.contains(getParent(parseNodeDrawable))) {
                parent.addChild(getParent(parseNodeDrawable));
            }
            if (punctuations != null && punctuations.contains(getParent(parseNodeDrawable))) {
                parent.addChild(getParent(parseNodeDrawable));
            }
        }
    }

    private ParseNodeDrawable getParent(ParseNodeDrawable node) {
        if (node.getParent() != null) {
            return getParent((ParseNodeDrawable) node.getParent());
        } else {
            return node;
        }
    }

    private LinkedHashMap<String, ArrayList<ParseNodeDrawable>> setSpecialMap() {
        LinkedHashMap<String, ArrayList<ParseNodeDrawable>> map = new LinkedHashMap<>();
        map.put("COMPOUND", new ArrayList<>());
        map.put("AUX", new ArrayList<>());
        map.put("DET", new ArrayList<>());
        map.put("AMOD", new ArrayList<>());
        map.put("NUMMOD", new ArrayList<>());
        map.put("CASE", new ArrayList<>());
        map.put("CCOMP", new ArrayList<>());
        map.put("NEG", new ArrayList<>());
        return map;
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

    public ParseTree convert(AnnotatedSentence annotatedSentence) {
        try {
            ArrayList<WordNodePair> wordNodePairs = constructWordPairList(annotatedSentence, annotatedSentence.getFileName());
            HashMap<Integer, ArrayList<Integer>> dependencyMap = setDependencyMap(wordNodePairs);
            if (wordNodePairs.size() > 1) {
                return constructTreeFromWords(wordNodePairs, dependencyMap);
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
