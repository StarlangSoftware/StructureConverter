package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.ParseTreeDrawable;
import AnnotatedTree.Processor.Condition.IsLeafNode;
import AnnotatedTree.Processor.NodeDrawableCollector;
import StructureConverter.ParserConverterType;
import StructureConverter.WordNodePair;

import java.util.ArrayList;

public class SimpleConstituencyToDependencyTreeConverter implements ConstituencyToDependencyTreeConverter {

    private int findEndingNode(int start, ArrayList<WordNodePair> wordNodePairList) {
        int i = start + 1;
        while (i < wordNodePairList.size() - 1 && wordNodePairList.get(i).getNode().getParent().equals(wordNodePairList.get(i + 1).getNode().getParent())) {
            i++;
        }
        return i;
    }

    private WordNodePair convertParseNodeDrawableToWordNodePair(ParseNodeDrawable parseNodeDrawable, ArrayList<WordNodePair> wordNodePairList) {
        for (WordNodePair wordNodePair : wordNodePairList) {
            if (wordNodePair.getNode().equals(parseNodeDrawable)) {
                return wordNodePair;
            }
        }
        return null;
    }

    private void addUniversalDependency(ArrayList<ParseNodeDrawable> parseNodeDrawableList, ArrayList<WordNodePair> wordNodePairList, DependencyOracle oracle) {
        for (int i = 0; i < parseNodeDrawableList.size() - 1; i++) {
            if (parseNodeDrawableList.get(i).equals(parseNodeDrawableList.get(i + 1))) {
                int last = findEndingNode(i, wordNodePairList);
                if (last - i + 1 == parseNodeDrawableList.get(i).numberOfChildren()) {
                    if (parseNodeDrawableList.get(i).numberOfChildren() == 3 && parseNodeDrawableList.get(i).getChild(1).getData().getName().equals("CONJP")) {
                        WordNodePair first = convertParseNodeDrawableToWordNodePair((ParseNodeDrawable) parseNodeDrawableList.get(i).getChild(0), wordNodePairList);
                        WordNodePair second = convertParseNodeDrawableToWordNodePair((ParseNodeDrawable) parseNodeDrawableList.get(i).getChild(1), wordNodePairList);
                        WordNodePair third = convertParseNodeDrawableToWordNodePair((ParseNodeDrawable) parseNodeDrawableList.get(i).getChild(2), wordNodePairList);
                        if (first != null && second != null && third != null) {
                            second.done();
                            third.done();
                            second.getWord().setUniversalDependency(third.getNo(), "CC");
                            third.getWord().setUniversalDependency(first.getNo(), "CONJ");
                            if (first.getNode().getParent() != null) {
                                first.updateNode();
                                if (first.getNode().getParent() != null && first.getNode().getParent().numberOfChildren() == 1) {
                                    first.updateNode();
                                }
                            }
                        }
                    } else {
                        ArrayList<Decision> decisions = oracle.makeDecisions(i, last, wordNodePairList, parseNodeDrawableList.get(i));
                        for (int j = 0; j < decisions.size(); j++) {
                            Decision decision = decisions.get(j);
                            if (decision.getTo() == 0) {
                                if (wordNodePairList.get(i + j).getNode().getParent() != null) {
                                    wordNodePairList.get(i + j).updateNode();
                                    if (wordNodePairList.get(i + j).getNode().getParent() != null && wordNodePairList.get(i + j).getNode().getParent().numberOfChildren() == 1) {
                                        wordNodePairList.get(i + j).updateNode();
                                    }
                                }
                            } else {
                                wordNodePairList.get(i + j).done();
                                wordNodePairList.get(i + j).getWord().setUniversalDependency(wordNodePairList.get(i + j + decision.getTo()).getNo(), decision.getData());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void constructDependenciesFromTree(ArrayList<WordNodePair> wordNodePairList, ParserConverterType type) {
        DependencyOracle oracle;
        if (type.equals(ParserConverterType.BASIC_ORACLE)) {
            oracle = new BasicDependencyOracle();
        } else {
            oracle = new ClassifierDependencyOracle();
        }
        setRoot(wordNodePairList);
        ArrayList<ParseNodeDrawable> parseNodeDrawableList = new ArrayList<>();
        ArrayList<WordNodePair> wordNodePairs = new ArrayList<>(wordNodePairList);
        for (WordNodePair wordNodePair : wordNodePairList) {
            parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
        }
        while (parseNodeDrawableList.size() > 1) {
            addUniversalDependency(parseNodeDrawableList, wordNodePairs, oracle);
            parseNodeDrawableList.clear();
            wordNodePairs.clear();
            for (WordNodePair wordNodePair : wordNodePairList) {
                if (!wordNodePair.isDone()) {
                    parseNodeDrawableList.add((ParseNodeDrawable) wordNodePair.getNode().getParent());
                    wordNodePairs.add(wordNodePair);
                }
            }
        }
    }

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

    @Override
    public AnnotatedSentence convert(ParseTreeDrawable parseTree, ParserConverterType type) {
        if (parseTree != null) {
            AnnotatedSentence annotatedSentence = new AnnotatedSentence();
            NodeDrawableCollector nodeDrawableCollector = new NodeDrawableCollector((ParseNodeDrawable) parseTree.getRoot(), new IsLeafNode());
            ArrayList<ParseNodeDrawable> leafList = nodeDrawableCollector.collect();
            ArrayList<WordNodePair> wordNodePairList = new ArrayList<>();
            for (int i = 0; i < leafList.size(); i++) {
                ParseNodeDrawable parseNode = leafList.get(i);
                WordNodePair wordNodePair = new WordNodePair(parseNode, i + 1);
                wordNodePair.updateNode();
                if (wordNodePair.getNode().getParent() != null && wordNodePair.getNode().getParent().numberOfChildren() == 1) {
                    wordNodePair.updateNode();
                    System.out.println("check this");
                    return null;
                }
                annotatedSentence.addWord(wordNodePair.getWord());
                wordNodePairList.add(wordNodePair);
            }
            constructDependenciesFromTree(wordNodePairList, type);
            return annotatedSentence;
        }
        return null;
    }
}
