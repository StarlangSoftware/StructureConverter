package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsTurkishLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import Classification.Model.TreeEnsembleModel;
import StructureConverter.MorphologicalAnalysisNotExistsException;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public class TurkishConstituencyToDependencyTreeConverter extends ConstituencyToDependencyTreeConverter {

    @Override
    protected DependencyOracle findOracle(ArrayList<TreeEnsembleModel> models, int start, int last) {
        DependencyOracle oracle;
        if (models == null || last - start + 1 > 7) {
            oracle = new TurkishBasicDependencyOracle();
        } else {
            oracle = new ClassifierDependencyOracle(models);
        }
        return oracle;
    }

    @Override
    public AnnotatedSentence convert(ParseTreeDrawable parseTree, ArrayList<TreeEnsembleModel> models) throws MorphologicalAnalysisNotExistsException {
        if (parseTree != null) {
            AnnotatedSentence annotatedSentence = new AnnotatedSentence();
            NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsTurkishLeafNode());
            ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
            ArrayList<WordNodePair> wordNodePairList = new ArrayList<>();
            for (int i = 0; i < leafList.size(); i++) {
                ParseNodeDrawable parseNode = leafList.get(i);
                WordNodePair wordNodePair = new WordNodePair(parseNode, Language.TURKISH, i + 1);
                if (wordNodePair.getWord().getParse() == null) {
                    throw new MorphologicalAnalysisNotExistsException(parseTree.getFileDescription().getFileName());
                }
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
