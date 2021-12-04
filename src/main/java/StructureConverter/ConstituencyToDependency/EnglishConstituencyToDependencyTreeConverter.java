package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import Classification.Model.TreeEnsembleModel;
import StructureConverter.MorphologicalAnalysisNotExistsException;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public class EnglishConstituencyToDependencyTreeConverter extends ConstituencyToDependencyTreeConverter {

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
            parent.replaceChild(node, child);
        }
        return nodeDrawables;
    }

    @Override
    protected DependencyOracle findOracle(ArrayList<TreeEnsembleModel> models, int start, int last) {
        return new EnglishBasicDependencyOracle();
    }

    /**
     * Converts {@link ParseTreeDrawable} to {@link AnnotatedSentence}.
     * @param parseTree {@link ParseTreeDrawable} to convert.
     * @param models {@link ArrayList} of {@link TreeEnsembleModel}s.
     * @return a {@link AnnotatedSentence}.
     */

    @Override
    public AnnotatedSentence convert(ParseTreeDrawable parseTree, ArrayList<TreeEnsembleModel> models) throws MorphologicalAnalysisNotExistsException {
        if (parseTree != null) {
            AnnotatedSentence annotatedSentence = new AnnotatedSentence();
            ArrayList<ParseNodeDrawable> leafList = collectAndPrune(parseTree);
            ArrayList<WordNodePair> wordNodePairList = new ArrayList<>();
            for (int i = 0; i < leafList.size(); i++) {
                ParseNodeDrawable parseNode = leafList.get(i);
                WordNodePair wordNodePair = new WordNodePair(parseNode, Language.ENGLISH, i + 1);
                while (wordNodePair.getNode().getParent() != null && wordNodePair.getNode().getParent().numberOfChildren() == 1) {
                    wordNodePair.updateNode();
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
