import AnnotatedSentence.*;
import AnnotatedTree.*;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.*;
import MorphologicalAnalysis.MorphologicalTag;
import StructureConverter.ConstituencyToDependency.Decision;
import StructureConverter.DependencyToConstituency.*;
import StructureConverter.MorphologicalAnalysisNotExistsException;
import StructureConverter.WordNodePair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GenerateData2 {

    private static ArrayList<WordNodePair> constructWordPairList(AnnotatedSentence sentence, String fileName) throws MorphologicalAnalysisNotExistsException, UniversalDependencyNotExistsException, ParenthesisInLayerException, NonProjectiveDependencyException {
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

    private static boolean noIncomingNodes(ArrayList<WordNodePair> wordList, int i) {
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

    private static int updateUnionCandidateLists(ArrayList<WordNodePair> list, WordNodePair wordNodePair) {
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

    private static ArrayList<WordNodePair> setOfNodesToBeMergedOntoNode(ArrayList<WordNodePair> wordNodePairs, WordNodePair headWord) {
        ArrayList<WordNodePair> list = new ArrayList<>();
        for (int i = 0; i < wordNodePairs.size(); i++) {
            WordNodePair wordNodePair = wordNodePairs.get(i);
            int toWordIndex = wordNodePair.getTo() - 1;
            if (!wordNodePair.isDoneForConnect()) {
                if (noIncomingNodes(wordNodePairs, i) && toWordIndex == headWord.getNo()) {
                    updateUnionCandidateLists(list, wordNodePair);
                }
            }
        }
        return list;
    }

    private static int isSpecialState(ArrayList<WordNodePair> unionList, ArrayList<WordNodePair> wordNodePairs, int headIndex) {
        WordNodePair head = wordNodePairs.get(headIndex);
        if (head.getTo() > 0 && head.getTo() < wordNodePairs.size() && headIndex - 1 == head.getTo()) {
            WordNodePair first = wordNodePairs.get(head.getTo() - 1);
            WordNodePair second = wordNodePairs.get(head.getTo());
            if (!first.isDoneForConnect() && head.getUniversalDependency().equals("CONJ") && second.getUniversalDependency().equals("CC") && second.getTo() - 1 == headIndex) {
                int index = updateUnionCandidateLists(unionList, first);
                if (noIncomingNodes(wordNodePairs, head.getTo() - 1)) {
                    first.doneForConnect();
                }
                return index;
            }
        }
        return -1;
    }

    private static LinkedList<ParseNodeDrawable> generateNodeList(ParseNodeDrawable node) {
        LinkedList<ParseNodeDrawable> list = new LinkedList<>();
        ParseNodeDrawable parent = (ParseNodeDrawable) node.getParent().getParent();
        do {
            list.add(parent);
            parent = (ParseNodeDrawable) parent.getParent();
        } while (parent != null);
        return list;
    }

    private static HashMap<WordNodePair, LinkedList<ParseNodeDrawable>> createMap(ParseTreeDrawable parseTreeDrawable, ArrayList<WordNodePair> wordNodePairs) {
        HashMap<WordNodePair, LinkedList<ParseNodeDrawable>> map = new HashMap<>();
        NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTreeDrawable.getRoot(), new IsTurkishLeafNode());
        ArrayList<ParseNodeDrawable> leaves = nodeDrawableCollector.collect();
        for (int i = 0; i < leaves.size(); i++) {
            map.put(wordNodePairs.get(i), generateNodeList(leaves.get(i)));
        }
        return map;
    }

    private static ArrayList<Command> commandList(HashMap<WordNodePair, LinkedList<ParseNodeDrawable>> map, ArrayList<WordNodePair> unionList, int index) {
        ArrayList<Command> list = new ArrayList<>();
        int i = 1, j = 1;
        while (index - i > -1 || index + j < unionList.size()) {
            if (map.get(unionList.get(index)).isEmpty()) {
                return null;
            }
            int oldListSize = list.size();
            ParseNodeDrawable headNode = map.get(unionList.get(index)).getFirst();
            if (index - i > -1) {
                if (map.get(unionList.get(index - i)).isEmpty()) {
                    return null;
                }
                while (index - i > -1 && map.get(unionList.get(index - i)).getFirst().equals(headNode)) {
                    list.add(Command.LEFT);
                    i++;
                    if (index - i > -1) {
                        if (map.get(unionList.get(index - i)).isEmpty()) {
                            return null;
                        }
                    }
                }
            }
            if (index + j < unionList.size()) {
                if (map.get(unionList.get(index + j)).isEmpty()) {
                    return null;
                }
                while (index + j < unionList.size() && map.get(unionList.get(index + j)).getFirst().equals(headNode)) {
                    list.add(Command.RIGHT);
                    j++;
                    if (index + j < unionList.size()) {
                        if (map.get(unionList.get(index + j)).isEmpty()) {
                            return null;
                        }
                    }
                }
            }
            if (list.size() > oldListSize) {
                list.add(Command.MERGE);
            }
            map.get(unionList.get(index)).removeFirst();
        }
        return list;
    }

    private static boolean isThereAll(HashMap<Integer, ArrayList<Integer>> map, int current, int total) {
        return map.get(current).size() == total;
    }

    private static HashMap<Integer, ArrayList<Integer>> setDependencyMap(ArrayList<WordNodePair> wordNodePairs) {
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

    private static ArrayList<String> concatFeatures(ArrayList<String> posNameList, ArrayList<Decision> decisions, ArrayList<Command> list, String headDependency) {
        ArrayList<String> features = new ArrayList<>(posNameList);
        for (Decision decision : decisions) {
            features.add(decision.toString());
        }
        boolean isRoot = headDependency.equals("ROOT");
        features.add(Boolean.toString(isRoot));
        for (Command command : list) {
            features.add(command.toString());
        }
        return features;
    }

    public static void main(String[] args) throws NonProjectiveDependencyException, ParenthesisInLayerException, MorphologicalAnalysisNotExistsException, UniversalDependencyNotExistsException, IOException {
        AnnotatedCorpus annotatedCorpus = new AnnotatedCorpus(new File("Turkish-Phrase"));
        TreeBankDrawable treeBankDrawable = new TreeBankDrawable(new File("Turkish2"));
        int i = 0, j = 0;
        HashMap<Integer, ArrayList<ArrayList<String>>> dataMap = new HashMap<>();
        while (i < annotatedCorpus.sentenceCount() && j < treeBankDrawable.size()) {
            AnnotatedSentence sentence = (AnnotatedSentence) annotatedCorpus.getSentence(i);
            ParseTreeDrawable parseTreeDrawable = treeBankDrawable.get(j);
            if (sentence.getFileName().equals(parseTreeDrawable.getFileDescription().getFileName().substring(9)) && sentence.getFileName().contains("train")) {
                if (parseTreeDrawable.leafCount() == sentence.wordCount() && sentence.wordCount() > 1) {
                    try {
                        ArrayList<WordNodePair> wordNodePairs = constructWordPairList(sentence, sentence.getFileName());
                        HashMap<Integer, ArrayList<Integer>> dependencyMap = setDependencyMap(wordNodePairs);
                        HashMap<WordNodePair, LinkedList<ParseNodeDrawable>> map = createMap(parseTreeDrawable, wordNodePairs);
                        int total;
                        while (true) {
                            int k = 0;
                            int specialIndex = -1;
                            ArrayList<WordNodePair> unionList = new ArrayList<>();
                            do {
                                WordNodePair head = wordNodePairs.get(k);
                                if (!head.isDoneForHead()) {
                                    unionList = setOfNodesToBeMergedOntoNode(wordNodePairs, head);
                                    specialIndex = isSpecialState(unionList, wordNodePairs, k);
                                    k++;
                                    if (specialIndex > -1) {
                                        break;
                                    } else {
                                        total = unionList.size();
                                        if (dependencyMap.containsKey(k) && isThereAll(dependencyMap, k, total) && (unionList.size() != 0)) {
                                            break;
                                        }
                                    }
                                } else {
                                    k++;
                                }
                            } while (k < wordNodePairs.size());
                            for (int t = 0; t < unionList.size(); t++) {
                                if (t != specialIndex) {
                                    unionList.get(t).doneForConnect();
                                }
                            }
                            wordNodePairs.get(k - 1).doneForHead();
                            if (!unionList.isEmpty()) {
                                int index = updateUnionCandidateLists(unionList, wordNodePairs.get(k - 1));
                                ArrayList<Command> list = commandList(map, unionList, index);
                                if (list == null) {
                                    System.out.println(sentence.getFileName() + " not done.");
                                    break;
                                }
                                if (!dataMap.containsKey(unionList.size())) {
                                    dataMap.put(unionList.size(), new ArrayList<>());
                                }
                                ArrayList<String> posNameList = new ArrayList<>();
                                ArrayList<Decision> decisions = new ArrayList<>();
                                for (int l = 0; l < unionList.size(); l++) {
                                    WordNodePair wordNodePair = unionList.get(l);
                                    posNameList.add(wordNodePair.getWord().getParse().getPos());
                                    posNameList.add(wordNodePair.getWord().getParse().getRootPos());
                                    /*posNameList.add(Boolean.toString(wordNodePair.getWord().getParse().containsTag(MorphologicalTag.ABLATIVE)));
                                    posNameList.add(Boolean.toString(wordNodePair.getWord().getParse().containsTag(MorphologicalTag.DATIVE)));
                                    posNameList.add(Boolean.toString(wordNodePair.getWord().getParse().containsTag(MorphologicalTag.GENITIVE)));
                                    posNameList.add(Boolean.toString(wordNodePair.getWord().getParse().containsTag(MorphologicalTag.NOMINATIVE)));
                                    posNameList.add(Boolean.toString(wordNodePair.getWord().getParse().containsTag(MorphologicalTag.ACCUSATIVE)));*/
                                    posNameList.add(Boolean.toString(wordNodePair.getWord().getParse().containsTag(MorphologicalTag.PROPERNOUN)));
                                    if (index != l) {
                                        decisions.add(new Decision(l, index, wordNodePair.getUniversalDependency()));
                                    }
                                }
                                if (!dataMap.containsKey(unionList.size())) {
                                    dataMap.put(unionList.size(), new ArrayList<>());
                                }
                                dataMap.get(unionList.size()).add(concatFeatures(posNameList, decisions, list, unionList.get(index).getUniversalDependency()));
                            } else {
                                break;
                            }
                        }
                    } catch (NonProjectiveDependencyException ignored) {
                        System.out.println(sentence.getFileName() + " not done.");
                    }
                } else {
                    System.out.println(sentence.getFileName() + " not done.");
                }
                i++;
                j++;
            } else if (sentence.getFileName().compareTo(treeBankDrawable.get(j).getFileDescription().getFileName().substring(9)) > 0) {
                j++;
            } else {
                i++;
            }
        }
        BufferedWriter outfile;
        for (Integer key : dataMap.keySet()) {
            if (key > 2 && key < 9) {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(key + ".txt"), StandardCharsets.UTF_8);
                outfile = new BufferedWriter(writer);
                for (int k = 0; k < dataMap.get(key).size(); k++) {
                    for (int t = 0; t < dataMap.get(key).get(k).size(); t++) {
                        String pos = dataMap.get(key).get(k).get(t);
                        if (t + 1 != dataMap.get(key).get(k).size()) {
                            outfile.write(pos + " ");
                        } else {
                            outfile.write(pos);
                        }
                    }
                    outfile.newLine();
                }
                outfile.close();
            }
        }
    }
}
