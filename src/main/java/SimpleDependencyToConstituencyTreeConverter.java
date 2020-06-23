import AnnotatedSentence.*;
import AnnotatedTree.ParenthesisInLayerException;
import AnnotatedTree.ParseNodeDrawable;
import ParseTree.*;
import ParseTree.ParseTree;
import Dictionary.Word;

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
            if (i != j && toWord2 > -1) {
                if (wordList.get(i).equals(wordList.get(toWord2))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateUnionCandidateLists(ArrayList<ParseNodeDrawable> list, WordNodePair wordNodePair, ArrayList<ParseNodeDrawable> punctuations, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap){
        ParseNodeDrawable node = wordNodePair.getNode();
        String dependency1 = wordNodePair.getUniversalDependency();
        if (dependency1.equals("NSUBJ") || dependency1.equals("CSUBJ")) {
            specialWord = getParent(node);
        } else if (Word.isPunctuation(node.getParent().getData().getName()) && dependency1.equals("PUNCT")) {
            punctuations.add(getParent(node));
        } else specialsMap.getOrDefault(dependency1, list).add(getParent(node));
    }

    private ArrayList<ParseNodeDrawable> setOfNodesToBeMergedOntoNode(ArrayList<WordNodePair> wordNodePairs, WordNodePair rootWord, ArrayList<ParseNodeDrawable> punctuations, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap) {
        ArrayList<ParseNodeDrawable> list = new ArrayList<>();
        for (int i = 0; i < wordNodePairs.size(); i++) {
            WordNodePair wordNodePair = wordNodePairs.get(i);
            int toWord1 = wordNodePair.getTo() - 1;
            if (!wordNodePair.isDone()) {
                if (noIncomingNodes(wordNodePairs, i) && toWord1 == rootWord.getNo()) {
                    wordNodePairs.get(i).done();
                    updateUnionCandidateLists(list, wordNodePair, punctuations, specialsMap);
                }
            } else {
                if (toWord1 > -1 && toWord1 == rootWord.getNo()) {
                    updateUnionCandidateLists(list, wordNodePair, punctuations, specialsMap);
                }
            }
        }
        return list;
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

    // FIXME: 22.06.2020 157. satır sorunlu
    private void fillWithJustSpecialsMap(ArrayList<WordNodePair> wordNodePairs, LinkedHashMap<String, ArrayList<ParseNodeDrawable>> specialsMap, String treePos, ArrayList<ParseNodeDrawable> punctuations, int current) {
        ParseNodeDrawable grandParent = new ParseNodeDrawable(new Symbol("S"));
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
                } else if (parseNodeDrawable.equals(specialWord)) {
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
                    ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(wordNodePairs.get(i).getTreePos()));
                    ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                    current.add(getParent(node));
                    current.addAll(unionList);
                    current.add(specialWord);
                    addChildForSubject(parent, wordNodePairs, current, punctuations);
                } else {
                    ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(wordNodePairs.get(i).getTreePos()));
                    ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                    current.add(getParent(node));
                    current.addAll(unionList);
                    if (specialWord != null) {
                        addChild(parent, wordNodePairs, current, null);
                        ParseNodeDrawable grandParent = new ParseNodeDrawable(new Symbol("S"));
                        ArrayList<ParseNodeDrawable> addAll = new ArrayList<>();
                        addAll.add(specialWord);
                        addAll.add(parent);
                        addChild(grandParent, wordNodePairs, addAll, punctuations);
                    } else {
                        addChild(parent, wordNodePairs, current, punctuations);
                    }
                }
            } else if (specialWord != null) {
                wordNodePairs.get(i).done();
                ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(wordNodePairs.get(i).getTreePos()));
                ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                current.add(specialWord);
                current.add(getParent(node));
                addChild(parent, wordNodePairs, current, punctuations);
            } else if (punctuations.size() > 0) {
                wordNodePairs.get(i).done();
                ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(wordNodePairs.get(i).getTreePos()));
                ArrayList<ParseNodeDrawable> current = new ArrayList<>(punctuations);
                current.add(getParent(node));
                addChild(parent, wordNodePairs, current, null);
            }
        } else {
            wordNodePairs.get(i).done();
            if (unionList.size() > 0) {
                fillWithSpecialsMap(unionList, wordNodePairs, wordNodePairs.get(i).getTreePos(), specialsMap, punctuations, i);
            } else {
                fillWithJustSpecialsMap(wordNodePairs, specialsMap, wordNodePairs.get(i).getTreePos(), punctuations, i);
            }
        }
        return unionList.size() != 0 || punctuations.size() != 0 || !empty(specialsMap) || specialWord != null;
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
                    unionList = setOfNodesToBeMergedOntoNode(wordNodePairs, wordNodePairs.get(j), punctuations, specialsMap);
                    j++;
                    if (specialWord != null) {
                        total = unionList.size() + punctuations.size() + 1 + totalSizeOfMap(specialsMap);
                    } else {
                        total = unionList.size() + punctuations.size() + totalSizeOfMap(specialsMap);
                    }
                    if ((dependencyMap.containsKey(j) && isThereAll(dependencyMap, j, total) && (unionList.size() != 0 || punctuations.size() != 0 || !empty(specialsMap) || specialWord != null))) {
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
        if (!getParent(wordNodePairs.get(0).getNode()).getData().equals(new Symbol("S"))) {
            getParent(wordNodePairs.get(0).getNode()).setData(new Symbol("S"));
        }
        ParseNodeDrawable root = setRoot(wordNodePairs);
        return new ParseTree(root);
    }

    private boolean isThereAll(HashMap<Integer, ArrayList<Integer>> map, int current, int total) {
        return map.get(current).size() == total;
    }

    private ParseNodeDrawable setRoot(ArrayList<WordNodePair> wordNodePairs) {
        ParseNodeDrawable root;
        ParseNodeDrawable parent = getParent(wordNodePairs.get(0).getNode());
        if (parent.numberOfChildren() > 1) {
            root = parent;
        } else {
            root = (ParseNodeDrawable) parent.getChild(0);
            root.setData(new Symbol("S"));
        }
        return root;
    }

    private void addChildForSubject(ParseNodeDrawable parent, ArrayList<WordNodePair> wordNodePairs, ArrayList<ParseNodeDrawable> current, ArrayList<ParseNodeDrawable> punctuations) {
        ParseNodeDrawable grandParent = new ParseNodeDrawable(parent.getData());
        boolean check = false;
        if (punctuations.size() == 0) {
            for (WordNodePair wordNodePair : wordNodePairs) {
                ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
                if (getParent(parseNodeDrawable).equals(specialWord)) {
                    check = true;
                } else {
                    if (check) {
                        if (current.contains(getParent(parseNodeDrawable))) {
                            parent.addChild(getParent(parseNodeDrawable));
                        }
                    } else {
                        if (current.contains(getParent(parseNodeDrawable))) {
                            grandParent.addChild(getParent(parseNodeDrawable));
                        }
                    }
                }
            }
            grandParent.addChild(specialWord);
            grandParent.addChild(parent);
        } else {
            boolean punctuationCheck = true;
            for (WordNodePair wordNodePair : wordNodePairs) {
                ParseNodeDrawable parseNodeDrawable = wordNodePair.getNode();
                if (punctuations.contains(getParent(parseNodeDrawable))) {
                    if (check && punctuationCheck) {
                        punctuationCheck = false;
                        grandParent.addChild(parent);
                    }
                    grandParent.addChild(getParent(parseNodeDrawable));
                } else if (getParent(parseNodeDrawable).equals(specialWord)) {
                    grandParent.addChild(specialWord);
                    check = true;
                } else {
                    if (check) {
                        if (current.contains(getParent(parseNodeDrawable))) {
                            parent.addChild(getParent(parseNodeDrawable));
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
