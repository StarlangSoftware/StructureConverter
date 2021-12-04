package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedWord;
import AnnotatedTree.ParseNodeDrawable;
import Dictionary.Word;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public abstract class BasicDependencyOracle implements DependencyOracle {

    protected abstract String findData(String dependent, String head, boolean condition1, boolean condition2, AnnotatedWord dependentWord, AnnotatedWord headWord);

    protected ArrayList<Decision> setToAndAddUniversalDependency(int startIndex, int headIndex, ArrayList<WordNodePair> wordNodePairList, int finishIndex, ParseNodeDrawable parent) {
        ArrayList<Decision> decisions = new ArrayList<>();
        for (int i = startIndex; i <= finishIndex; i++) {
            if (i != headIndex) {
                String parentData = parent.getData().getName();
                String firstChild = parent.getChild(0).getData().getName();
                String secondChild = null, thirdChild = null;
                if (parent.numberOfChildren() > 1){
                    secondChild = parent.getChild(1).getData().getName();
                }
                if (parent.numberOfChildren() > 2){
                    thirdChild = parent.getChild(2).getData().getName();
                }
                if (parent.numberOfChildren() == 2 && parentData.equals("S") && firstChild.equals("NP")) {
                    decisions.add(new Decision(startIndex + decisions.size(), headIndex - i, "NSUBJ"));
                } else if (parent.numberOfChildren() == 3 && parentData.equals("S") && firstChild.equals("NP") && secondChild.equals("VP") && Word.isPunctuation(thirdChild)) {
                    if (!wordNodePairList.get(i).getWord().isPunctuation()) {
                        decisions.add(new Decision(startIndex + decisions.size(), headIndex - i, "NSUBJ"));
                    } else {
                        decisions.add(new Decision(startIndex + decisions.size(), headIndex - i, "PUNCT"));
                    }
                } else {
                    String dependent = wordNodePairList.get(i).getNode().getData().getName();
                    String head = wordNodePairList.get(headIndex).getNode().getData().getName();
                    boolean condition1 = wordNodePairList.get(i).getNode().getData().isPunctuation();
                    boolean condition2 = wordNodePairList.get(headIndex).getNode().getData().isPunctuation();
                    decisions.add(new Decision(startIndex + decisions.size(), headIndex - i, findData(dependent, head, condition1, condition2, wordNodePairList.get(i).getWord(), wordNodePairList.get(headIndex).getWord())));
                }
            } else {
                decisions.add(new Decision(-1, 0, null));
            }
        }
        return decisions;
    }
}
