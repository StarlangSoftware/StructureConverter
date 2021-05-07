package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import Classification.Model.Model;
import Classification.Model.TreeEnsembleModel;
import StructureConverter.MorphologicalAnalysisNotExistsException;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public class SimpleConstituencyToDependencyTreeConverter implements ConstituencyToDependencyTreeConverter {

    /**
     * Finds last {@link WordNodePair}'s index.
     * @param start start index of <code>wordNodePairList</code>.
     * @param wordNodePairList {@link WordNodePair} {@link ArrayList}.
     * @return a {@link Integer} ({@link WordNodePair}'s index).
     */

    private int findEndingNode(int start, ArrayList<WordNodePair> wordNodePairList) {
        int i = start + 1;
        while (i < wordNodePairList.size() - 1 && wordNodePairList.get(i).getNode().getParent().equals(wordNodePairList.get(i + 1).getNode().getParent())) {
            i++;
        }
        return i;
    }

    /**
     * Adds {@link DependencyParser.UniversalDependencyRelation} to <code>wordNodePairList</code>.
     * @param parseNodeDrawableList {@link ParseNodeDrawable} {@link ArrayList}.
     * @param wordNodePairList {@link WordNodePair} {@link ArrayList}.
     * @param models {@link ArrayList} of {@link Model}s.
     */

    private void addUniversalDependency(ArrayList<ParseNodeDrawable> parseNodeDrawableList, ArrayList<WordNodePair> wordNodePairList, ArrayList<TreeEnsembleModel> models) {
        for (int i = 0; i < parseNodeDrawableList.size() - 1; i++) {
            if (parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                int last = findEndingNode(i, wordNodePairList);
                if (last - i + 1 == parseNodeDrawableList.get(i).numberOfChildren()) {
                    DependencyOracle oracle;
                    if (models == null || last - i + 1 > 7) {
                        oracle = new BasicDependencyOracle();
                    } else {
                        oracle = new ClassifierDependencyOracle(models);
                    }
                    ArrayList<Decision> decisions = oracle.makeDecisions(i, last, wordNodePairList, parseNodeDrawableList.get(i));
                    for (int j = 0; j < decisions.size(); j++) {
                        Decision decision = decisions.get(j);
                        if (decision.getNo() < 0) {
                            if (wordNodePairList.get(i + j).getNode().getParent() != null) {
                                wordNodePairList.get(i + j).updateNode();
                                if (wordNodePairList.get(i + j).getNode().getParent() != null && wordNodePairList.get(i + j).getNode().getParent().numberOfChildren() == 1) {
                                    wordNodePairList.get(i + j).updateNode();
                                }
                            }
                        } else {
                            wordNodePairList.get(decision.getNo()).doneForConnect();
                            wordNodePairList.get(decision.getNo()).getWord().setUniversalDependency(wordNodePairList.get(decision.getNo() + decision.getTo()).getNo(), decision.getData());
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Sets <code>wordNodePairList</code>.
     * @param wordNodePairList {@link WordNodePair} {@link ArrayList}.
     * @param models {@link ArrayList} of {@link Model}s.
     */

    private void constructDependenciesFromTree(ArrayList<WordNodePair> wordNodePairList, ArrayList<TreeEnsembleModel> models) {
        setRoot(wordNodePairList);
        ArrayList<ParseNodeDrawable> parseNodeDrawableList = new ArrayList<>();
        ArrayList<WordNodePair> wordNodePairs = new ArrayList<>(wordNodePairList);
        for (WordNodePair wordNodePair : wordNodePairList) {
            parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
        }
        while (parseNodeDrawableList.size() > 1) {
            addUniversalDependency(parseNodeDrawableList, wordNodePairs, models);
            parseNodeDrawableList.clear();
            wordNodePairs.clear();
            for (WordNodePair wordNodePair : wordNodePairList) {
                if (!wordNodePair.isDoneForConnect()) {
                    parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
                    wordNodePairs.add(wordNodePair);
                }
            }
        }
    }

    /**
     * Changes root's {@link DependencyParser.UniversalDependencyType}.
     * @param wordNodePairList {@link WordNodePair} {@link ArrayList}.
     */

    private void setRoot(ArrayList<WordNodePair> wordNodePairList) {
        AnnotatedWord last = null;
        for (int i = 0; i < wordNodePairList.size(); i++) {
            WordNodePair wordNodePair = wordNodePairList.get(wordNodePairList.size() - i - 1);
            if (!wordNodePair.getWord().isPunctuation()) {
                last = wordNodePair.getWord();
                break;
            }
        }
        if (last != null) {
            last.setUniversalDependency(0, "root");
        }
    }

    /**
     * Converts {@link ParseTreeDrawable} to {@link AnnotatedSentence}.
     * @param parseTree {@link ParseTreeDrawable} to convert.
     * @param models {@link ArrayList} of {@link Model}s.
     * @return a {@link AnnotatedSentence}.
     */

    @Override
    public AnnotatedSentence convert(ParseTreeDrawable parseTree, ArrayList<TreeEnsembleModel> models) throws MorphologicalAnalysisNotExistsException {
        if (parseTree != null) {
            AnnotatedSentence annotatedSentence = new AnnotatedSentence();
            NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsLeafNode());
            ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
            ArrayList<WordNodePair> wordNodePairList = new ArrayList<>();
            for (int i = 0; i < leafList.size(); i++) {
                ParseNodeDrawable parseNode = leafList.get(i);
                WordNodePair wordNodePair = new WordNodePair(parseNode, i + 1);
                if (wordNodePair.getWord().getParse() == null) {
                    throw new MorphologicalAnalysisNotExistsException(parseTree.getFileDescription().getFileName());
                }
                wordNodePair.updateNode();
                if (wordNodePair.getNode().getParent() != null && wordNodePair.getNode().getParent().numberOfChildren() == 1) {
                    wordNodePair.updateNode();
                    System.out.println("check this");
                    return null;
                }
                annotatedSentence.addWord(wordNodePair.getWord());
                wordNodePairList.add(wordNodePair);
            }
            constructDependenciesFromTree(wordNodePairList, models);
            return annotatedSentence;
        }
        return null;
    }
}
