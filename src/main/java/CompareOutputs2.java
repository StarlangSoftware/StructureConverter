import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.TreeBankDrawable;
import ParseTree.ParseTree;
import ParseTree.ParseNode;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class CompareOutputs2 {

    private static int traverseTrees(ParseNodeDrawable node1, ParseNodeDrawable node2, HashSet<ParseNodeDrawable> set, int correct) {
        for (int i = 0; i < node1.numberOfChildren(); i++) {
            ParseNodeDrawable node1Child = (ParseNodeDrawable) node1.getChild(i);
            if (node2.numberOfChildren() > i) {
                ParseNodeDrawable node2Child = (ParseNodeDrawable) node2.getChild(i);
                if (!set.contains(node1Child)) {
                    if (node1Child.getLayerInfo() == null && node2Child.getLayerInfo() == null) {
                        if (node1Child.getData().getName().equals(node2Child.getData().getName())) {
                            correct++;
                        }
                        set.add(node1Child);
                        correct = traverseTrees(node1Child, node2Child, set, correct);
                    }
                }
            }
        }
        return correct;
    }

    private static void fillMap(int correct, int nodeCount, HashMap<Integer, Integer> map) {
        int wrong = nodeCount - correct;
        map.put(1, map.get(1) + correct);
        map.put(0, map.get(0) + wrong);
    }

    public static void main(String[]args) {
        TreeBankDrawable treeBankDrawable1 = new TreeBankDrawable(new File("Turkish2"));
        TreeBankDrawable treeBankDrawable2 = new TreeBankDrawable(new File("Turkish3"));
        List<ParseTree> parseTrees1 = treeBankDrawable1.getParseTrees();
        List<ParseTree> parseTrees2 = treeBankDrawable2.getParseTrees();
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, 0);
        map.put(1, 0);
        int i = 0, j = 0;
        while (i < parseTrees1.size() || j < parseTrees2.size()) {
            ParseTreeDrawable parseTreeDrawable1 = (ParseTreeDrawable) parseTrees1.get(i);
            ParseTreeDrawable parseTreeDrawable2 = (ParseTreeDrawable) parseTrees2.get(j);
            if (parseTreeDrawable1.getName().equals(parseTreeDrawable2.getName())) {
                String first = parseTreeDrawable1.generateAnnotatedSentence().toWords();
                String second = parseTreeDrawable2.generateAnnotatedSentence().toWords();
                if (first.toLowerCase(new Locale("tr")).equals(second.toLowerCase(new Locale("tr")))) {
                    ParseNode node1 = parseTreeDrawable1.getRoot();
                    ParseNode node2 = parseTreeDrawable2.getRoot();
                    if (node1.getData().getName().equals(node2.getData().getName())) {
                        int current = map.get(0);
                        HashSet<ParseNodeDrawable> set = new HashSet<>();
                        set.add((ParseNodeDrawable) node1);
                        int correct = 0;
                        if (node1.getData().getName().equals(node2.getData().getName())) {
                            correct++;
                        }
                        correct = traverseTrees((ParseNodeDrawable) node1, (ParseNodeDrawable) node2, set, correct);
                        fillMap(correct, node2.nodeCount() - node2.leafCount(), map);
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
    }
}
