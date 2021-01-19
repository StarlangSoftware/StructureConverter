import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import AnnotatedTree.TreeBankDrawable;
import ParseTree.ParseTree;
import ParseTree.ParseNode;
import java.util.AbstractMap.SimpleEntry;

import java.io.File;
import java.util.*;

public class CompareOutputs2 {

    private static int traverseTrees(ParseNodeDrawable node1, ParseNodeDrawable node2, HashSet<ParseNodeDrawable> set, int correct, HashMap<String, HashMap<String, Integer>> tagMap) {
        for (int i = 0; i < node1.numberOfChildren(); i++) {
            ParseNodeDrawable node1Child = (ParseNodeDrawable) node1.getChild(i);
            if (node1Child.getLayerInfo() == null) {
                if (!tagMap.containsKey(node1Child.getData().getName())) {
                    tagMap.put(node1Child.getData().getName(), new HashMap<>());
                }
            }
            if (node2.numberOfChildren() > i) {
                ParseNodeDrawable node2Child = (ParseNodeDrawable) node2.getChild(i);
                if (!set.contains(node1Child)) {
                    if (node1Child.getLayerInfo() == null && node2Child.getLayerInfo() == null) {
                        if (!tagMap.get(node1Child.getData().getName()).containsKey(node2Child.getData().getName())) {
                            tagMap.get(node1Child.getData().getName()).put(node2Child.getData().getName(), 0);
                        }
                        tagMap.get(node1Child.getData().getName()).put(node2Child.getData().getName(), tagMap.get(node1Child.getData().getName()).get(node2Child.getData().getName()) + 1);
                        if (node1Child.getData().getName().equals(node2Child.getData().getName())) {
                            correct++;
                        }
                        set.add(node1Child);
                        correct = traverseTrees(node1Child, node2Child, set, correct, tagMap);
                    }
                }
            } else {
                if (!tagMap.get(node1Child.getData().getName()).containsKey("null")) {
                    tagMap.get(node1Child.getData().getName()).put("null", 0);
                }
                tagMap.get(node1Child.getData().getName()).put("null", tagMap.get(node1Child.getData().getName()).get("null") + 1);
            }
        }
        return correct;
    }

    private static void fillMap(int correct, int nodeCount, HashMap<Integer, Integer> map) {
        int wrong = nodeCount - correct;
        map.put(1, map.get(1) + correct);
        map.put(0, map.get(0) + wrong);
    }

    private static void printScores(HashMap<String, HashMap<String, Integer>> tagMap) {
        System.out.println("Precision:");
        double totalCorrect = 0.0, total = 0.0;
        for (String key1 : tagMap.keySet()) {
            int correct = 0, keyTotal = 0;
            if (tagMap.get(key1).containsKey(key1)) {
                correct += tagMap.get(key1).get(key1);
            }
            for (String key2 : tagMap.get(key1).keySet()) {
                keyTotal += tagMap.get(key1).get(key2);
            }
            System.out.println(key1 + " -> " + correct / (keyTotal + 0.0));
            totalCorrect += correct + 0.0;
            total += keyTotal + 0.0;
        }
        System.out.println(totalCorrect / total);
        System.out.println("Recall:");
        totalCorrect = 0.0;
        total = 0.0;
        for (String key1 : tagMap.keySet()) {
            int correct = 0, keyTotal = 0;
            for (String key2 : tagMap.keySet()) {
                if (tagMap.containsKey(key2) && tagMap.get(key2).containsKey(key1)) {
                    if (key1.equals(key2)) {
                        correct += tagMap.get(key2).get(key1);
                    }
                    keyTotal += tagMap.get(key2).get(key1);
                }
            }
            System.out.println(key1 + " -> " + correct / (keyTotal + 0.0));
            totalCorrect += correct + 0.0;
            total += keyTotal + 0.0;
        }
        System.out.println(totalCorrect / total);
    }

    private static boolean equals(ArrayList<ParseNodeDrawable> p1, ArrayList<ParseNodeDrawable> p2) {
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            if (!p1.get(i).getParent().getData().getName().equals(p2.get(i).getParent().getData().getName())) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[]args) {
        TreeBankDrawable treeBankDrawable1 = new TreeBankDrawable(new File("Turkish2"));
        TreeBankDrawable treeBankDrawable2 = new TreeBankDrawable(new File("Turkish3"));
        List<ParseTree> parseTrees1 = treeBankDrawable1.getParseTrees();
        List<ParseTree> parseTrees2 = treeBankDrawable2.getParseTrees();
        HashMap<Integer, Integer> map = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> tagMap = new HashMap<>();
        HashMap<Integer, SimpleEntry<Integer, Integer>> sentenceMap = new HashMap<>();
        map.put(0, 0);
        map.put(1, 0);
        int i = 0, j = 0;
        while (i < parseTrees1.size() || j < parseTrees2.size()) {
            ParseTreeDrawable parseTreeDrawable1 = (ParseTreeDrawable) parseTrees1.get(i);
            ParseTreeDrawable parseTreeDrawable2 = (ParseTreeDrawable) parseTrees2.get(j);
            if (parseTreeDrawable1.getName().equals(parseTreeDrawable2.getName())) {
                String first = parseTreeDrawable1.generateAnnotatedSentence().toWords();
                String second = parseTreeDrawable2.generateAnnotatedSentence().toWords();
                NodeDrawableCollector n1 = new NodeDrawableCollector((ParseNodeDrawable) parseTreeDrawable1.getRoot(), new IsTurkishLeafNode());
                NodeDrawableCollector n2 = new NodeDrawableCollector((ParseNodeDrawable) parseTreeDrawable2.getRoot(), new IsTurkishLeafNode());
                ArrayList<ParseNodeDrawable> parseNodeDrawables1 = n1.collect();
                ArrayList<ParseNodeDrawable> parseNodeDrawables2 = n2.collect();
                if (equals(parseNodeDrawables1, parseNodeDrawables2) && first.toLowerCase(new Locale("tr")).equals(second.toLowerCase(new Locale("tr")))) {
                    ParseNode node1 = parseTreeDrawable1.getRoot();
                    ParseNode node2 = parseTreeDrawable2.getRoot();
                    if (node1.getData().getName().equals(node2.getData().getName())) {
                        int current = map.get(0);
                        HashSet<ParseNodeDrawable> set = new HashSet<>();
                        set.add((ParseNodeDrawable) node1);
                        int correct = 1;
                        if (!tagMap.containsKey(node1.getData().getName())) {
                            tagMap.put(node1.getData().getName(), new HashMap<>());
                        }
                        if (!tagMap.get(node1.getData().getName()).containsKey(node2.getData().getName())) {
                            tagMap.get(node1.getData().getName()).put(node2.getData().getName(), 0);
                        }
                        tagMap.get(node1.getData().getName()).put(node2.getData().getName(), tagMap.get(node1.getData().getName()).get(node2.getData().getName()) + 1);
                        correct = traverseTrees((ParseNodeDrawable) node1, (ParseNodeDrawable) node2, set, correct, tagMap);
                        fillMap(correct, node2.nodeCount() - node2.leafCount(), map);
                        int wordCount = parseTreeDrawable2.generateAnnotatedSentence().wordCount();
                        if (!sentenceMap.containsKey(wordCount)) {
                            sentenceMap.put(wordCount, new SimpleEntry<>(0, 0));
                        }
                        sentenceMap.put(wordCount, new SimpleEntry<>(sentenceMap.get(wordCount).getKey() + correct, sentenceMap.get(wordCount).getValue() + node2.nodeCount() - node2.leafCount()));
                        if (current != map.get(0)) {
                            System.out.println(parseTreeDrawable2.getName() + " not same trees.");
                        }
                    }
                } else {
                    System.out.println(parseTreeDrawable2.getName() + " can't done." + "\t" + first + "\t" + second);
                }
                i++;
                j++;
            } else if (parseTreeDrawable1.getName().compareTo(parseTreeDrawable2.getName()) > 0) {
                j++;
            } else {
                i++;
            }
        }
        System.out.println("0: " + map.get(0));
        System.out.println("1: " + map.get(1));
        System.out.println("success rate: %" + (map.get(1) * 100.00) / (map.get(1) + map.get(0)));
        printScores(tagMap);
        for (Integer key : sentenceMap.keySet()) {
            System.out.println(key + ": " + sentenceMap.get(key).getKey() + " " + sentenceMap.get(key).getValue());
        }
    }
}
