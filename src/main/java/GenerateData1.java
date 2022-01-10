import AnnotatedSentence.*;
import AnnotatedTree.*;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import AnnotatedTree.TreeBankDrawable;
import Cookies.Tuple.Triplet;
import MorphologicalAnalysis.MorphologicalTag;
import ParseTree.ParseNode;
import StructureConverter.WordNodePair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.AbstractMap.*;

public class GenerateData1 {

    private static int findEndingNode(int start, ArrayList<WordNodePair> wordNodePairList) {
        int i = start + 1;
        while (i < wordNodePairList.size() - 1 && wordNodePairList.get(i).getNode().getParent().equals(wordNodePairList.get(i + 1).getNode().getParent())) {
            i++;
        }
        return i;
    }

    private static HashMap<String, Integer> setMap() {
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

    private static int getPriority(HashMap<String, Integer> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return 6;
    }

    private static int findHeadIndex(int start, int last, ArrayList<WordNodePair> wordNodePairList, ParseNodeDrawable node) {
        if (node.numberOfChildren() == 3 && node.getChild(1).getData().getName().equals("CONJP")) {
            return start;
        } else {
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
    }

    private static int findWordIndexInWordNodePairs(ArrayList<WordNodePair> wordNodePairs, int no) {
        for (int i = 0; i < wordNodePairs.size(); i++) {
            if (wordNodePairs.get(i).getNo() == no) {
                return i;
            }
        }
        return -1;
    }

    private static int isMapContains(HashMap<ArrayList<SimpleEntry<Integer, Integer>>, Integer> commandMap, ArrayList<SimpleEntry<Integer, Integer>> list) {
        for (ArrayList<SimpleEntry<Integer, Integer>> key : commandMap.keySet()) {
            if (key.size() == list.size()) {
                boolean contains = true;
                for (SimpleEntry<Integer, Integer> integerIntegerSimpleEntry : list) {
                    if (!key.contains(integerIntegerSimpleEntry)) {
                        contains = false;
                        break;
                    }
                }
                if (contains) {
                    return commandMap.get(key);
                }
            }
        }
        return -1;
    }

    private static int findCommand(HashMap<ArrayList<SimpleEntry<Integer, Integer>>, Integer> commandMap, ArrayList<SimpleEntry<Integer, Integer>> list) {
        if (commandMap.containsKey(list)) {
            return commandMap.get(list);
        } else {
            int command = isMapContains(commandMap, list);
            if (command > 0) {
                return command;
            } else {
                int newCommand = commandMap.size() + 1;
                commandMap.put(list, newCommand);
                return newCommand;
            }
        }
    }

    private static boolean isSuitable(ArrayList<WordNodePair> wordNodePairs, int startIndex, int lastIndex, int headIndex) {
        for (int i = startIndex; i < lastIndex; i++) {
            if (headIndex != i) {
                int currentTo = wordNodePairs.get(i).getTo();
                int no = findWordIndexInWordNodePairs(wordNodePairs, currentTo);
                if (no >= lastIndex || no < startIndex) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String toString(ArrayList<SimpleEntry<Integer, Integer>> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i + 1 != list.size()) {
                sb.append(list.get(i).getKey()).append(" ").append(list.get(i).getValue()).append(" ");
            } else {
                sb.append(list.get(i).getKey()).append(" ").append(list.get(i).getValue());
            }
        }
        return sb.toString();
    }

    private static String toString(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i + 1 != array.length) {
                sb.append(array[i]).append(" ");
            } else {
                sb.append(array[i]);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        AnnotatedCorpus annotatedCorpus = new AnnotatedCorpus(new File("Turkish-Phrase2"));
        String pathName = "Turkish";
        TreeBankDrawable treeBankDrawable = new TreeBankDrawable(new File(pathName));
        int i = 0, j = 0;
        ArrayList<String[]> featureList = new ArrayList<>();
        HashSet<Triplet<Integer, Integer, ArrayList<SimpleEntry<Integer, Integer>>>> dataset = new HashSet<>();
        HashMap<Integer, ArrayList<SimpleEntry<ArrayList<String>, Integer>>> dataMap = new HashMap<>();
        HashMap<Integer, HashMap<ArrayList<SimpleEntry<Integer, Integer>>, Integer>> commandMap = new HashMap<>();
        while (i < annotatedCorpus.sentenceCount() && j < treeBankDrawable.size()) {
            AnnotatedSentence sentence = (AnnotatedSentence) annotatedCorpus.getSentence(i);
            ParseTreeDrawable parseTreeDrawable = treeBankDrawable.get(j);
            if (sentence.getFileName().equals(parseTreeDrawable.getFileDescription().getFileName().substring(8)) && sentence.getFileName().contains("train")) {
                if (parseTreeDrawable.leafCount() == sentence.wordCount() && sentence.wordCount() > 1) {
                    NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTreeDrawable.getRoot(), new IsLeafNode());
                    ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
                    ArrayList<WordNodePair> wordNodePairList = new ArrayList<>();
                    for (int l = 0; l < leafList.size(); l++) {
                        ParseNodeDrawable parseNode = leafList.get(l);
                        WordNodePair wordNodePair = new WordNodePair((AnnotatedWord) sentence.getWord(l), parseNode, l + 1);
                        wordNodePair.updateNode();
                        if (wordNodePair.getNode().getParent() != null && wordNodePair.getNode().getParent().numberOfChildren() == 1) {
                            wordNodePair.updateNode();
                            System.out.println("check this");
                        }
                        wordNodePairList.add(wordNodePair);
                    }
                    ArrayList<ParseNodeDrawable> parseNodeDrawableList = new ArrayList<>();
                    ArrayList<WordNodePair> wordNodePairs = new ArrayList<>(wordNodePairList);
                    for (WordNodePair wordNodePair : wordNodePairList) {
                        parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
                    }
                    while (parseNodeDrawableList.size() > 1) {
                        boolean bool = false;
                        ArrayList<String> posNameList = new ArrayList<>();
                        ArrayList<SimpleEntry<Integer, Integer>> list = new ArrayList<>();
                        for (int t = 0; t < parseNodeDrawableList.size() - 1; t++) {
                            if (parseNodeDrawableList.get(t).equals(parseNodeDrawableList.get(t + 1))) {
                                int last = findEndingNode(t, wordNodePairs);
                                if (last - t + 1 == parseNodeDrawableList.get(t).numberOfChildren()) {
                                    int headIndex = findHeadIndex(t, last, wordNodePairs, parseNodeDrawableList.get(t));
                                    if (isSuitable(wordNodePairs, t, last + 1, headIndex)) {
                                        for (int k = t; k < last + 1; k++) {
                                            AnnotatedWord word = wordNodePairs.get(k).getWord();
                                            ParseNode parent = parseNodeDrawableList.get(k);
                                            String firstChild = "null", secondChild = "null", thirdChild = "null";
                                            if (parent.numberOfChildren() > 0) {
                                                firstChild = parent.getChild(0).getData().getName();
                                            }
                                            if (parent.numberOfChildren() > 1) {
                                                secondChild = parent.getChild(1).getData().getName();
                                            }
                                            if (parent.numberOfChildren() > 2) {
                                                thirdChild = parent.getChild(2).getData().getName();
                                            }
                                            posNameList.add(word.getParse().getPos());
                                            posNameList.add(word.getParse().getRootPos());
                                            posNameList.add(Boolean.toString(word.getParse().containsTag(MorphologicalTag.ABLATIVE)));
                                            posNameList.add(Boolean.toString(word.getParse().containsTag(MorphologicalTag.DATIVE)));
                                            posNameList.add(Boolean.toString(word.getParse().containsTag(MorphologicalTag.GENITIVE)));
                                            posNameList.add(Boolean.toString(word.getParse().containsTag(MorphologicalTag.NOMINATIVE)));
                                            posNameList.add(Boolean.toString(word.getParse().containsTag(MorphologicalTag.ACCUSATIVE)));
                                            posNameList.add(Boolean.toString(word.getParse().containsTag(MorphologicalTag.PROPERNOUN)));
                                            if (k != headIndex) {
                                                int to = findWordIndexInWordNodePairs(wordNodePairs, word.getUniversalDependency().to());
                                                wordNodePairs.get(k).doneForConnect();
                                                list.add(new SimpleEntry<>(k - t, to - t));
                                                if (word.getSemantic() == null || wordNodePairs.get(headIndex).getWord().getSemantic() == null) {
                                                    featureList.add(new String[]{word.getParse().getPos(), word.getParse().getRootPos(), Boolean.toString(word.getParse().containsTag(MorphologicalTag.PROPERNOUN)), wordNodePairs.get(to).getWord().getParse().getPos(), wordNodePairs.get(to).getWord().getParse().getRootPos(), Boolean.toString(wordNodePairs.get(to).getWord().getParse().containsTag(MorphologicalTag.PROPERNOUN)), wordNodePairs.get(headIndex).getWord().getParse().getPos(), wordNodePairs.get(headIndex).getWord().getParse().getRootPos(), Boolean.toString(wordNodePairs.get(headIndex).getWord().getParse().containsTag(MorphologicalTag.PROPERNOUN)), "null", parent.getData().getName(), firstChild, secondChild, thirdChild, wordNodePairs.get(k).getUniversalDependency()});
                                                } else {
                                                    featureList.add(new String[]{word.getParse().getPos(), word.getParse().getRootPos(), Boolean.toString(word.getParse().containsTag(MorphologicalTag.PROPERNOUN)), wordNodePairs.get(to).getWord().getParse().getPos(), wordNodePairs.get(to).getWord().getParse().getRootPos(), Boolean.toString(wordNodePairs.get(to).getWord().getParse().containsTag(MorphologicalTag.PROPERNOUN)), wordNodePairs.get(headIndex).getWord().getParse().getPos(), wordNodePairs.get(headIndex).getWord().getParse().getRootPos(), Boolean.toString(wordNodePairs.get(headIndex).getWord().getParse().containsTag(MorphologicalTag.PROPERNOUN)), Boolean.toString(word.getSemantic().equals(wordNodePairs.get(headIndex).getWord().getSemantic())), parent.getData().getName(), firstChild, secondChild, thirdChild, wordNodePairs.get(k).getUniversalDependency()});
                                                }
                                            } else {
                                                if (wordNodePairs.get(headIndex).getNode().getParent() != null) {
                                                    wordNodePairs.get(headIndex).updateNode();
                                                    if (wordNodePairs.get(headIndex).getNode().getParent() != null && wordNodePairs.get(headIndex).getNode().getParent().numberOfChildren() == 1) {
                                                        wordNodePairs.get(headIndex).updateNode();
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        System.out.println(sentence.getFileName() + " not done.");
                                        bool = true;
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                        if (!list.isEmpty() && !posNameList.isEmpty()) {
                            if (!dataMap.containsKey(posNameList.size())) {
                                dataMap.put(posNameList.size(), new ArrayList<>());
                            }
                            if (!commandMap.containsKey(posNameList.size())) {
                                commandMap.put(posNameList.size(), new HashMap<>());
                            }
                            int command = findCommand(commandMap.get(posNameList.size()), list);
                            dataset.add(new Triplet<>(posNameList.size(), command, list));
                            dataMap.get(posNameList.size()).add(new SimpleEntry<>(posNameList, command));
                        }
                        parseNodeDrawableList.clear();
                        wordNodePairs.clear();
                        for (WordNodePair wordNodePair : wordNodePairList) {
                            if (!wordNodePair.isDoneForConnect()) {
                                parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
                                wordNodePairs.add(wordNodePair);
                            }
                        }
                        if (bool) {
                            break;
                        }
                    }
                } else {
                    System.out.println(sentence.getFileName() + " not done.");
                }
                i++;
                j++;
            } else if (sentence.getFileName().compareTo(treeBankDrawable.get(j).getFileDescription().getFileName().substring(8)) > 0) {
                j++;
            } else {
                i++;
            }
        }
        BufferedWriter outfile;
        for (Integer key : dataMap.keySet()) {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream((key / 8) + ".txt"), StandardCharsets.UTF_8);
            outfile = new BufferedWriter(writer);
            for (int k = 0; k < dataMap.get(key).size(); k++) {
                for (int l = 0; l < dataMap.get(key).get(k).getKey().size(); l++) {
                    outfile.write(dataMap.get(key).get(k).getKey().get(l) + " ");
                }
                outfile.write(dataMap.get(key).get(k).getValue().toString());
                outfile.newLine();
            }
            outfile.close();
        }
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream( "posNames.txt"), StandardCharsets.UTF_8);
        outfile = new BufferedWriter(writer);
        for (String[] array : featureList) {
            outfile.write(toString(array));
            outfile.newLine();
        }
        outfile.close();
        writer = new OutputStreamWriter(new FileOutputStream("dataset.txt"), StandardCharsets.UTF_8);
        outfile = new BufferedWriter(writer);
        for (Triplet<Integer, Integer, ArrayList<SimpleEntry<Integer, Integer>>> triplet : dataset) {
            outfile.write((triplet.getA() / 8) + " " + triplet.getB().toString() + " " + toString(triplet.getC()));
            outfile.newLine();
        }
        outfile.close();
    }
}
