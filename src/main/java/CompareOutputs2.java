import AnnotatedTree.*;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import ParseTree.*;
import Translation.Tree.ParallelTreeBankDrawable;

import java.io.File;
import java.util.*;

public class CompareOutputs2 {

    private static void traverseTree(HashMap<ArrayList<Integer>, String> map, HashMap<ParseNodeDrawable, Integer> nodeMap, ParseNode node) {
        ArrayList<ParseNodeDrawable> list = new NodeDrawableCollector((ParseNodeDrawable) node, new IsTurkishLeafNode()).collect();
        ArrayList<Integer> key = new ArrayList<>();
        for (ParseNodeDrawable nodeDrawable : list) {
            key.add(nodeMap.get(nodeDrawable));
        }
        map.put(key, node.getData().getName());
        for (int i = 0; i < node.numberOfChildren(); i++) {
            ParseNode child = node.getChild(i);
            if (!child.isLeaf()) {
                traverseTree(map, nodeMap, child);
            }
        }
    }

    private static HashMap<ArrayList<Integer>, String> generateMap(ParseTreeDrawable tree) {
        HashMap<ArrayList<Integer>, String> map = new HashMap<>();
        HashMap<ParseNodeDrawable, Integer> nodeMap = new HashMap<>();
        ParseNode root = tree.getRoot();
        ArrayList<ParseNodeDrawable> list = new NodeDrawableCollector((ParseNodeDrawable) root, new IsTurkishLeafNode()).collect();
        for (int i = 0; i < list.size(); i++) {
            nodeMap.put(list.get(i), i);
        }
        traverseTree(map, nodeMap, root);
        return map;
    }

    private static void printScores(HashMap<String, HashMap<String, Integer>> tagMap) {
        System.out.println("Recall:");
        for (String key1 : tagMap.keySet()) {
            int correct = 0, keyTotal = 0;
            if (tagMap.get(key1).containsKey(key1)) {
                correct += tagMap.get(key1).get(key1);
            }
            for (String key2 : tagMap.get(key1).keySet()) {
                keyTotal += tagMap.get(key1).get(key2);
            }
            System.out.println(key1 + " -> " + correct / (keyTotal + 0.0));
        }
        System.out.println("Precision:");
        HashSet<String> stringSet = new HashSet<>();
        for (String key1 : tagMap.keySet()) {
            stringSet.add(key1);
            stringSet.addAll(tagMap.get(key1).keySet());
        }
        stringSet.remove(null);
        for (String key1 : stringSet) {
            int correct = 0, keyTotal = 0;
            for (String key2 : stringSet) {
                if (tagMap.containsKey(key2) && tagMap.get(key2).containsKey(key1)) {
                    if (key1.equals(key2)) {
                        correct += tagMap.get(key2).get(key1);
                    }
                    keyTotal += tagMap.get(key2).get(key1);
                }
            }
            System.out.println(key1 + " -> " + correct / (keyTotal + 0.0));
        }
    }

    private static void generateSolutions(HashMap<ArrayList<Integer>, String> drawableMap1, HashMap<ArrayList<Integer>, String> drawableMap2, int[] accuracy, int[] precision, int[] recall, HashMap<String, HashMap<String, Integer>> tagMap) {
        precision[1] += drawableMap2.size();
        recall[1] += drawableMap1.size();
        for (ArrayList<Integer> key : drawableMap1.keySet()) {
            if (drawableMap1.get(key).equals(drawableMap2.get(key))) {
                accuracy[0]++;
                precision[0]++;
                recall[0]++;
            }
            accuracy[1]++;
            if (!tagMap.containsKey(drawableMap1.get(key))) {
                tagMap.put(drawableMap1.get(key), new HashMap<>());
            }
            if (!tagMap.get(drawableMap1.get(key)).containsKey(drawableMap2.get(key))) {
                tagMap.get(drawableMap1.get(key)).put(drawableMap2.get(key), 0);
            }
            tagMap.get(drawableMap1.get(key)).put(drawableMap2.get(key), tagMap.get(drawableMap1.get(key)).get(drawableMap2.get(key)) + 1);
            drawableMap2.remove(key);
        }
        for (int i = 0; i < drawableMap2.size(); i++) {
            accuracy[1]++;
        }
    }

    public static void main(String[]args) {
        int[] accuracy = new int[2];
        int[] precision = new int[2];
        int[] recall = new int[2];
        HashMap<String, HashMap<String, Integer>> tagMap = new HashMap<>();
        String bankName = "Turkish3";
        int totalTreeNumber = 0;
        ParallelTreeBankDrawable parallelTreeBankDrawable = new ParallelTreeBankDrawable(new File("Turkish2"), new File(bankName));
        for (int i = 0; i < parallelTreeBankDrawable.size(); i++) {
            ParseTreeDrawable drawable1 = parallelTreeBankDrawable.fromTree(i);
            if (!drawable1.getName().contains("train")) {
                ParseTreeDrawable drawable2 = parallelTreeBankDrawable.toTree(i);
                String first = drawable1.generateAnnotatedSentence().toWords();
                String second = drawable2.generateAnnotatedSentence().toWords();
                if (first.toLowerCase(new Locale("tr")).equals(second.toLowerCase(new Locale("tr")))) {
                    HashMap<ArrayList<Integer>, String> drawableMap1 = generateMap(drawable1);
                    HashMap<ArrayList<Integer>, String> drawableMap2 = generateMap(drawable2);
                    generateSolutions(drawableMap1, drawableMap2, accuracy, precision, recall, tagMap);
                }
                totalTreeNumber++;
            }
        }
        double precisions = ((precision[0] * 100.00) / precision[1]);
        double recalls = ((recall[0] * 100.00) / recall[1]);
        System.out.println("correct trees: Turkish2" + " computed trees: " + bankName);
        System.out.println("precision: " + precisions);
        System.out.println("recall: " + recalls);
        System.out.println("F-score: " + (2 * precisions * recalls) / (precisions + recalls));
        System.out.println("accuracy: " + ((accuracy[0] * 100.00) / accuracy[1]));
        System.out.println("total tree: " + totalTreeNumber);
        printScores(tagMap);
    }
}
