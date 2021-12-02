package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
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
     * Adds {@link DependencyParser.Universal.UniversalDependencyRelation} to <code>wordNodePairList</code>.
     * @param parseNodeDrawableList {@link ParseNodeDrawable} {@link ArrayList}.
     * @param wordNodePairList {@link WordNodePair} {@link ArrayList}.
     * @param parameter {@link Parameter}.
     */

    private void addUniversalDependency(ArrayList<ParseNodeDrawable> parseNodeDrawableList, ArrayList<WordNodePair> wordNodePairList, Parameter parameter) {
        for (int i = 0; i < parseNodeDrawableList.size() - 1; i++) {
            if (parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                int last = findEndingNode(i, wordNodePairList);
                if (last - i + 1 == parseNodeDrawableList.get(i).numberOfChildren()) {
                    DependencyOracle oracle;
                    if (parameter.getLanguage().equals(Language.TURKISH)) {
                        if (!parameter.getModel() || last - i + 1 > 7) {
                            oracle = new BasicDependencyOracle();
                        } else {
                            oracle = new ClassifierDependencyOracle(parameter.getModels());
                        }
                    } else {
                        oracle = new EnglishBasicDependencyOracle();
                    }
                    ArrayList<Decision> decisions = oracle.makeDecisions(i, last, wordNodePairList, parseNodeDrawableList.get(i));
                    for (int j = 0; j < decisions.size(); j++) {
                        Decision decision = decisions.get(j);
                        if (decision.getNo() < 0) {
                            WordNodePair wordNodePair = wordNodePairList.get(i + j);
                            if (wordNodePair.getNode().getParent() != null) {
                                wordNodePair.updateNode();
                                if (wordNodePair.getNode().getParent() == null) {
                                    wordNodePair.getWord().setUniversalDependency(0, "root");
                                } else if (wordNodePair.getNode().getParent().numberOfChildren() == 1) {
                                    wordNodePair.updateNode();
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
     * @param parameter {@link Parameter}.
     */

    private void constructDependenciesFromTree(ArrayList<WordNodePair> wordNodePairList, Parameter parameter) {
        ArrayList<ParseNodeDrawable> parseNodeDrawableList = new ArrayList<>();
        ArrayList<WordNodePair> wordNodePairs = new ArrayList<>(wordNodePairList);
        for (WordNodePair wordNodePair : wordNodePairList) {
            parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
        }
        while (parseNodeDrawableList.size() > 1) {
            addUniversalDependency(parseNodeDrawableList, wordNodePairs, parameter);
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

    private boolean isNone(ParseNodeDrawable child) {
        ParseNodeDrawable grandChild = child;
        while (grandChild.numberOfChildren() == 1) {
            if (grandChild.getData().getName().equals("-NONE-")) {
                return true;
            }
            grandChild = (ParseNodeDrawable) grandChild.firstChild();
        }
        return false;
    }

    private ParseNodeDrawable collect(ArrayList<ParseNodeDrawable> parseNodeDrawables, ArrayList<ParseNodeDrawable> update, ParseNodeDrawable parent) {
        ArrayList<ParseNodeDrawable> remove = new ArrayList<>();
        for (int i = 0; i < parent.numberOfChildren(); i++) {
            ParseNodeDrawable child = (ParseNodeDrawable) parent.getChild(i);
            if (!child.isLeaf()) {
                if (!isNone(child)) {
                    ParseNodeDrawable node = collect(parseNodeDrawables, update, child);
                    if (node != null) {
                        remove.add(node);
                    }
                } else {
                    remove.add(child);
                }
            } else {
                parseNodeDrawables.add(child);
            }
        }
        for (ParseNodeDrawable nodeDrawable : remove) {
            parent.removeChild(nodeDrawable);
        }
        if (!remove.isEmpty() && parent.numberOfChildren() == 1) {
            if (!parent.firstChild().firstChild().isLeaf()) {
                update.add(parent);
            }
        } else if (parent.numberOfChildren() == 0) {
            return parent;
        }
        return null;
    }

    private ArrayList<ParseNodeDrawable> collectAndPrune(ParseTreeDrawable parseTreeDrawable) {
        ParseNodeDrawable root = (ParseNodeDrawable) parseTreeDrawable.getRoot();
        ArrayList<ParseNodeDrawable> nodeDrawables = new ArrayList<>();
        ArrayList<ParseNodeDrawable> update = new ArrayList<>();
        collect(nodeDrawables, update, root);
        for (ParseNodeDrawable node : update) {
            ParseNodeDrawable child = ((ParseNodeDrawable) node.getChild(0));
            ParseNodeDrawable parent = (ParseNodeDrawable) node.getParent();
            parent.removeChild(node);
            parent.addChild(child);
        }
        return nodeDrawables;
    }

    /**
     * Converts {@link ParseTreeDrawable} to {@link AnnotatedSentence}.
     * @param parseTree {@link ParseTreeDrawable} to convert.
     * @param parameter {@link Parameter}.
     * @return a {@link AnnotatedSentence}.
     */

    @Override
    public AnnotatedSentence convert(ParseTreeDrawable parseTree, Parameter parameter) throws MorphologicalAnalysisNotExistsException {
        if (parseTree != null) {
            AnnotatedSentence annotatedSentence = new AnnotatedSentence();
            ArrayList<ParseNodeDrawable> leafList;
            if (parameter.getLanguage().equals(Language.TURKISH)) {
                NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
                leafList = nodeDrawableCollector.collect();
            } else {
                leafList = collectAndPrune(parseTree);
            }
            ArrayList<WordNodePair> wordNodePairList = new ArrayList<>();
            for (int i = 0; i < leafList.size(); i++) {
                ParseNodeDrawable parseNode = leafList.get(i);
                WordNodePair wordNodePair = new WordNodePair(parseNode, parameter.getLanguage(), i + 1);
                if (parameter.getLanguage().equals(Language.TURKISH) && wordNodePair.getWord().getParse() == null) {
                    throw new MorphologicalAnalysisNotExistsException(parseTree.getFileDescription().getFileName());
                }
                while (wordNodePair.getNode().getParent() != null && wordNodePair.getNode().getParent().numberOfChildren() == 1) {
                    wordNodePair.updateNode();
                }
                annotatedSentence.addWord(wordNodePair.getWord());
                wordNodePairList.add(wordNodePair);
            }
            constructDependenciesFromTree(wordNodePairList, parameter);
            return annotatedSentence;
        }
        return null;
    }
}
