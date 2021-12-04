package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedWord;
import AnnotatedTree.ParseNodeDrawable;
import ParseTree.ParseNode;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public class EnglishBasicDependencyOracle extends BasicDependencyOracle {

    @Override
    protected String findData(String dependent, String head, boolean condition1, boolean condition2, AnnotatedWord dependentWord, AnnotatedWord headWord) {
        if (condition1 || condition2) {
            return "PUNCT";
        }
        if (dependent.startsWith("NP-SBJ")) {
            return "NSUBJ";
        }
        int index = dependent.indexOf("-");
        if (index > -1) {
            dependent = dependent.substring(0, index);
        }
        index = head.indexOf("-");
        if (index > -1) {
            head = head.substring(0, index);
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
            case "JJ":
            case "JJR":
            case "JJS":
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
            case "DT":
            case "PRP$":
            case "PDT":
                return "DET";
            case "NP":
            case "NN":
            case "NNS":
            case "NNP":
            case "NNPS":
                switch (head) {
                    case "NP":
                    case "NN":
                    case "NNS":
                    case "NNPS":
                    case "NNP":
                        if (dependentWord.getPosTag().startsWith("NNP") && headWord.getPosTag().startsWith("NNP")) {
                            return "FLAT";
                        }
                        return "NMOD";
                    case "VP":
                        return "OBL";
                }
                return "NMOD";
            case "SBAR":
            case "SBARQ":
            case "SINV":
            case "SQ":
            case "S":
                switch (head) {
                    case "VP":
                        return "CCOMP";
                    default:
                        return "DEP";
                }
            case "CD":
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
        ParseNode headChild = node.headChild();
        int headIndex = firstIndex;
        if (headChild != null) {
            headIndex += node.getChildIndex(headChild);
        }
        return setToAndAddUniversalDependency(firstIndex, headIndex, wordNodePairList, lastIndex, node);
    }
}
