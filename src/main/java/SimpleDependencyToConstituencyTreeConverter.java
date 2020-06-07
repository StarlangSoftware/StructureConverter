import AnnotatedSentence.*;
import AnnotatedTree.ParenthesisInLayerException;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import Cookies.Tuple.Pair;
import ParseTree.*;
import ParseTree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SimpleDependencyToConstituencyTreeConverter implements DependencyToConstituencyTreeConverter {

    private ParseNodeDrawable parentOfNode;
    private ParseNodeDrawable specialWord;

    private ArrayList<Pair<AnnotatedWord, Boolean>> constructWordList(AnnotatedSentence sentence) {
        ArrayList<Pair<AnnotatedWord, Boolean>> wordList = new ArrayList<>();
        for (int j = 0; j < sentence.wordCount(); j++) {
            wordList.add(new Pair<>((AnnotatedWord) sentence.getWord(j), true));
        }
        return wordList;
    }

    private ArrayList<ParseNodeDrawable> constructNodeList(ArrayList<Pair<AnnotatedWord, Boolean>> wordList) {
        ArrayList<ParseNodeDrawable> nodeList = new ArrayList<>();
        for (Pair<AnnotatedWord, Boolean> pair : wordList) {
            ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(pair.getKey().getParse().getTreePos()));
            try {
                ParseNodeDrawable child = new ParseNodeDrawable(parent, pair.getKey().toString(), true, 0);
                nodeList.add(child);
                parent.addChild(child);
            } catch (ParenthesisInLayerException e) {
                e.printStackTrace();
            }
        }
        return nodeList;
    }

    private boolean suitable(ArrayList<WordNodePair> wordList, int i) {
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

    private ArrayList<ParseNodeDrawable> setOfNodesToBeMergedOntoNode(ArrayList<WordNodePair> wordList, WordNodePair rootWord, ArrayList<ParseNodeDrawable> nodeList, HashMap<WordNodePair, HashSet<WordNodePair>> annotatedWordMap) {
        ArrayList<ParseNodeDrawable> list = new ArrayList<>();
        for (int i = 0; i < wordList.size(); i++) {
            WordNodePair word1 = wordList.get(i);
            int toWord1 = word1.getTo() - 1;
            String dependency1 = word1.getUniversalDependency();
            if (wordList.get(i).getHeap()) {
                if (suitable(wordList, i)) {
                    if (annotatedWordMap.get(wordList.get(i)).contains(rootWord)) {
                        wordList.get(i).setHeap(false);
                        if (dependency1.equals("NSUBJ") || dependency1.equals("CSUBJ")) {
                            specialWord = getParent(nodeList.get(i));
                        } else {
                            list.add(getParent(nodeList.get(i)));
                        }
                    }
                }
            } else {
                if (toWord1 > -1 && annotatedWordMap.get(wordList.get(i)).contains(rootWord)) {
                    if (dependency1.equals("NSUBJ") || dependency1.equals("CSUBJ")) {
                        specialWord = getParent(nodeList.get(i));
                    } else {
                        list.add(getParent(nodeList.get(i)));
                    }
                }
            }
        }
        return list;
    }

    private ParseTree constructTreeFromWords(ArrayList<WordNodePair> wordList, ArrayList<ParseNodeDrawable> nodeList, HashMap<WordNodePair, HashSet<WordNodePair>> annotatedWordMap) {
        for (int i = 0; i < wordList.size(); i++) {
            specialWord = null;
            ArrayList<ParseNodeDrawable> unionList = setOfNodesToBeMergedOntoNode(wordList, wordList.get(i), nodeList, annotatedWordMap);
            if (unionList.size() > 0) {
                wordList.get(i).setHeap(false);
                if (!getParent(nodeList.get(0)).equals(specialWord) && specialWord != null) {
                    ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(wordList.get(i).getTreePos()));
                    ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                    current.add(getParent(nodeList.get(i)));
                    current.addAll(unionList);
                    current.add(specialWord);
                    addChildForSubject(parent, nodeList, current);
                } else {
                    ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(wordList.get(i).getTreePos()));
                    ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                    current.add(getParent(nodeList.get(i)));
                    current.addAll(unionList);
                    addChild(parent, nodeList, current);
                    if (specialWord != null) {
                        ParseNodeDrawable grandParent = new ParseNodeDrawable(new Symbol("S"));
                        ArrayList<ParseNodeDrawable> addAll = new ArrayList<>();
                        addAll.add(specialWord);
                        addAll.add(parent);
                        addChild(grandParent, nodeList, addAll);
                    }
                }
            } else {
                if (specialWord != null) {
                    wordList.get(i).setHeap(false);
                    ParseNodeDrawable parent = new ParseNodeDrawable(new Symbol(wordList.get(i).getTreePos()));
                    ArrayList<ParseNodeDrawable> current = new ArrayList<>();
                    current.add(specialWord);
                    current.add(getParent(nodeList.get(i)));
                    addChildForJustSpecial(parent, nodeList, current);
                }
            }
        }
        if (!getParent(nodeList.get(0)).getData().equals(new Symbol("S"))) {
            getParent(nodeList.get(0)).setData(new Symbol("S"));
        }
        ParseNodeDrawable root = setRoot(nodeList);
        return new ParseTree(root);
    }

    private ParseNodeDrawable setRoot(ArrayList<ParseNodeDrawable> nodeList) {
        ParseNodeDrawable root;
        if (getParent(nodeList.get(0)).numberOfChildren() > 1) {
            root = getParent(nodeList.get(0));
        } else {
            root = (ParseNodeDrawable) getParent(nodeList.get(0)).getChild(0);
            root.setData(new Symbol("S"));
        }
        return root;
    }

    private void addChildForJustSpecial(ParseNodeDrawable parent, ArrayList<ParseNodeDrawable> nodeList, ArrayList<ParseNodeDrawable> current) {
        for (ParseNodeDrawable parseNodeDrawable : nodeList) {
            if (current.contains(getParent(parseNodeDrawable))) {
                parent.addChild(getParent(parseNodeDrawable));
            }
        }
    }

    private void addChildForSubject(ParseNodeDrawable parent, ArrayList<ParseNodeDrawable> nodeList, ArrayList<ParseNodeDrawable> current) {
        ParseNodeDrawable grandParent = new ParseNodeDrawable(parent.getData());
        boolean check = false;
        for (int i = 0; i < nodeList.size(); i++) {
            if (getParent(nodeList.get(i)).equals(specialWord)) {
                check = true;
            } else {
                if (check) {
                    if (current.contains(getParent(nodeList.get(i)))) {
                        parent.addChild(getParent(nodeList.get(i)));
                    }
                } else {
                    if (current.contains(getParent(nodeList.get(i)))) {
                        grandParent.addChild(getParent(nodeList.get(i)));
                    }
                }
            }
        }
        grandParent.addChild(specialWord);
        grandParent.addChild(parent);
    }

    private void addChild(ParseNodeDrawable parent, ArrayList<ParseNodeDrawable> nodeList, ArrayList<ParseNodeDrawable> current) {
        for (ParseNodeDrawable parseNodeDrawable : nodeList) {
            if (current.contains(getParent(parseNodeDrawable))) {
                parent.addChild(getParent(parseNodeDrawable));
            }
        }
    }

    private ParseNodeDrawable getParent(ParseNodeDrawable node) {
        if (node.getParent() != null) {
            parentOfNode = (ParseNodeDrawable) node.getParent();
            getParent((ParseNodeDrawable) node.getParent());
        }
        return parentOfNode;
    }

    private HashMap<WordNodePair, HashSet<WordNodePair>> setHashMap(ArrayList<WordNodePair> wordList) {
        HashMap<WordNodePair, HashSet<WordNodePair>> map = new HashMap<>();
        for (int i = 0; i < wordList.size(); i++) {
            WordNodePair current = wordList.get(i);
            if (current.getTo() > 0) {
                if (!map.containsKey(current)) {
                    map.put(current, new HashSet<>());
                }
                map.get(current).add(wordList.get(current.getTo() - 1));
            }
        }
        return map;
    }

    private ArrayList<WordNodePair> setListOfWord(ArrayList<Pair<AnnotatedWord, Boolean>> wordList) {
        ArrayList<WordNodePair> list = new ArrayList<>();
        for (int i = 0; i < wordList.size(); i++) {
            list.add(new WordNodePair(wordList.get(i).getKey().getName(), i + 1, wordList.get(i).getKey().getUniversalDependency().to(), wordList.get(i).getValue(), wordList.get(i).getKey().getParse().getTreePos(), wordList.get(i).getKey().getUniversalDependency().toString()));
        }
        return list;
    }

    public ParseTreeDrawable convertDrawable(AnnotatedSentence annotatedSentence) {
        ArrayList<Pair<AnnotatedWord, Boolean>> wordList = constructWordList(annotatedSentence);
        ArrayList<WordNodePair> listOfWord = setListOfWord(wordList);
        ArrayList<ParseNodeDrawable> nodeList =  constructNodeList(wordList);
        HashMap<WordNodePair, HashSet<WordNodePair>> annotatedWordMap = setHashMap(listOfWord);
        return (ParseTreeDrawable) constructTreeFromWords(listOfWord, nodeList, annotatedWordMap);
    }

    public ParseTree convert(AnnotatedSentence annotatedSentence) {
        ArrayList<Pair<AnnotatedWord, Boolean>> wordList = constructWordList(annotatedSentence);
        ArrayList<WordNodePair> listOfWord = setListOfWord(wordList);
        ArrayList<ParseNodeDrawable> nodeList =  constructNodeList(wordList);
        HashMap<WordNodePair, HashSet<WordNodePair>> annotatedWordMap = setHashMap(listOfWord);
        return constructTreeFromWords(listOfWord, nodeList, annotatedWordMap);
    }
}
