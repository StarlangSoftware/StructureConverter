package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedWord;
import AnnotatedTree.ParseNodeDrawable;
import ParseTree.ParseNode;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public class EnglishBasicDependencyOracle extends BasicOracle {

    @Override
    protected String findData(String dependent, String head, boolean condition1, boolean condition2, AnnotatedWord dependentWord, AnnotatedWord headWord) {
        if (condition1 || condition2) {
            return "PUNCT";
        }
        switch (dependent) {
            case "ADVP":
                if (dependentWord.getPosTag().startsWith("VB")) {
                    return "ADVCL";
                }
                if (dependentWord.getPosTag().startsWith("NN")) {
                    return "NMOD";
                }
                return "ADVMOD";
            case "ADJP":
                switch (head) {
                    case "NP":
                        if (dependentWord.getPosTag().startsWith("VB")) {
                            return "ACL";
                        }
                        return "AMOD";
                }
                return "ADVMOD";
            case "PP":
                switch (head) {
                    case "NP":
                        return "CASE";
                    default:
                        if (dependentWord.getPosTag().startsWith("NN")) {
                            return "NMOD";
                        }
                        return "ADVMOD";
                }
            case "DP":
                return "DET";
            case "NP":
                switch (head) {
                    case "NP":
                        if (dependentWord.getPosTag().startsWith("NNP") && headWord.getPosTag().startsWith("NNP")) {
                            return "FLAT";
                        }
                        return "NMOD";
                    case "VP":
                        return "OBL";
                }
                return "NMOD";
            case "S":
                switch (head) {
                    case "VP":
                        return "CCOMP";
                    default:
                        return "DEP";
                }
            case "NUM":
                return "NUMMOD";
            case "INTJ":
                return "DISCOURSE";
            case "NEG":
                return "NEG";
            case "CONJP":
                return "CC";
            default:
                return "DEP";
        }
    }

    @Override
    public ArrayList<Decision> makeDecisions(int firstIndex, int lastIndex, ArrayList<WordNodePair> wordNodePairList, ParseNodeDrawable node) {
        ParseNode parent = wordNodePairList.get(firstIndex).getNode().getParent();
        int headIndex = parent.getChildIndex(parent.headChild());
        return setToAndAddUniversalDependency(firstIndex, headIndex, wordNodePairList, lastIndex, (ParseNodeDrawable) parent);
    }
}
