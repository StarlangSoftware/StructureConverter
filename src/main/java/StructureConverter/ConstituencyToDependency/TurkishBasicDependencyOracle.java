package StructureConverter.ConstituencyToDependency;/* Created by oguzkeremyildiz on 6.02.2021 */

import AnnotatedSentence.AnnotatedWord;
import AnnotatedTree.ParseNodeDrawable;
import MorphologicalAnalysis.MorphologicalTag;
import StructureConverter.WordNodePair;

import java.util.ArrayList;
import java.util.HashMap;

public class TurkishBasicDependencyOracle extends BasicDependencyOracle {

    protected String findData(String dependent, String head, boolean condition1, boolean condition2, AnnotatedWord dependentWord, AnnotatedWord headWord) {
        if (condition1 || condition2) {
            return "PUNCT";
        }
        switch (dependent) {
            case "ADVP":
                /**
                 * If an ADVP node has a verbal head, then it is linked to the VP head within its clause with the tag ADVCL.
                 */
                if (dependentWord.getParse().getRootPos().equals("VERB")) {
                    return "ADVCL";
                }
                /**
                 * If an ADVP node does not have a verbal head, it is linked to either the head of the following ADJP or to the root VP head with the ADVMOD.
                 */
                if (dependentWord.getParse().getRootPos().equals("NOUN")) {
                    return "NMOD";
                }
                return "ADVMOD";
            case "ADJP":
                switch (head) {
                    case "NP":
                        /**
                         * If an NP has an ADJP sister node before it and if the head of ADJP is verbal, then the head of ADJP is linked to the head of NP with the tag ACL.
                         */
                        if (dependentWord.getParse().getRootPos().equals("VERB")) {
                            return "ACL";
                        }
                        /**
                         * If an NP has an ADJP sister node before it and the head of ADJP is not verbal, then the head of ADJP is linked to the head of NP with the tag AMOD.
                         */
                        return "AMOD";
                }
                /**
                 * If an ADVP node does not have a verbal head, it is linked to either the head of the following ADJP or to the root VP head with the ADVMOD.
                 */
                return "ADVMOD";
            case "PP":
                switch (head) {
                    case "NP":
                        /**
                         * The head of a PP node is linked to the head of the previous phrase with the tag CASE.
                         */
                        return "CASE";
                    default:
                        /**
                         * If an ADVP node does not have a verbal head, it is linked to either the head of the following ADJP or to the root VP head with the ADVMOD.
                         */
                        if (dependentWord.getParse() != null && dependentWord.getParse().getRootPos().equals("NOUN")) {
                            return "NMOD";
                        }
                        return "ADVMOD";
                }
            case "DP":
                /**
                 * DP nodes are linked to the head of the following NP with the tag DET.
                 */
                return "DET";
            case "NP":
                switch (head) {
                    case "NP":
                        /**
                         * The foreign words and proper names found under an NP node are linked to the head of the phrase with the tag FLAT.
                         */
                        if (dependentWord.getParse().containsTag(MorphologicalTag.PROPERNOUN) && headWord.getParse().containsTag(MorphologicalTag.PROPERNOUN)) {
                            return "FLAT";
                        }
                        /**
                         * If the daughters of a node are found together on WordNet, then the left node is linked to the right node with the tag COMPOUND. If a NUM node has two daughters which are also labeled as NUM, then the left daughter is linked to the right one with the tag COMPOUND.
                         */
                        if (dependentWord.getSemantic() != null && headWord.getSemantic() != null && dependentWord.getSemantic().equals(headWord.getSemantic())) {
                            return "COMPOUND";
                        }
                        /**
                         * In NP sequences, each word is linked to the rightmost NP with the tag NMOD.
                         */
                        return "NMOD";
                    case "VP":
                        /**
                         * If the daughters of a node are found together on WordNet, then the left node is linked to the right node with the tag COMPOUND. If a NUM node has two daughters which are also labeled as NUM, then the left daughter is linked to the right one with the tag COMPOUND.
                         */
                        if (dependentWord.getSemantic() != null && headWord.getSemantic() != null && dependentWord.getSemantic().equals(headWord.getSemantic())) {
                            return "COMPOUND";
                        }
                        /**
                         * If VP has a sister NP marked in nominative or accusative case, then it is linked to the root with the tag OBJ.
                         */
                        if (dependentWord.getParse().containsTag(MorphologicalTag.NOMINATIVE) || dependentWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE)) {
                            return "OBJ";
                        }
                        /**
                         * If there is no OBJ tagged argument, then the NP sisters of the root are linked to the root with the tag OBL.
                         */
                        return "OBL";
                }
                /**
                 * In NP sequences, each word is linked to the rightmost NP with the tag NMOD.
                 */
                return "NMOD";
            case "S":
                switch (head) {
                    case "VP":
                        /**
                         * If VP has an S node or a verbal noun as a sister, then it is linked to the root with the tag CCOMP.
                         */
                        return "CCOMP";
                    default:
                        return "DEP";
                }
            case "NUM":
                return "NUMMOD";
            case "INTJ":
                return "DISCOURSE";
            case "NEG":
                /**
                 * The node NEG is linked to the head of its sister node which is a VP or NOMP.
                 */
                return "NEG";
            case "CONJP":
                /**
                 * The head of a CONJP is linked to the head of the first following phrase which is of the same nature as the one immediately preceding the CONJP node.
                 */
                return "CC";
            default:
                return "DEP";
        }
    }

    private HashMap<String, Integer> setMap() {
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

    private int getPriority(HashMap<String, Integer> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return 6;
    }

    private int findHeadIndex(int start, int last, ArrayList<WordNodePair> wordNodePairList) {
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

    @Override
    public ArrayList<Decision> makeDecisions(int firstIndex, int lastIndex, ArrayList<WordNodePair> wordNodePairList, ParseNodeDrawable node) {
        if (node.numberOfChildren() == 3 && node.getChild(1).getData().getName().equals("CONJP")) {
            ArrayList<Decision> decisions = new ArrayList<>();
            decisions.add(new Decision(-1, 0, null));
            decisions.add(new Decision((lastIndex + firstIndex) / 2, lastIndex - ((lastIndex + firstIndex) / 2), "CC"));
            decisions.add(new Decision(lastIndex, firstIndex - lastIndex, "CONJ"));
            return decisions;
        }
        int headIndex = findHeadIndex(firstIndex, lastIndex, wordNodePairList);
        return setToAndAddUniversalDependency(firstIndex, headIndex, wordNodePairList, lastIndex, node);
    }
}
