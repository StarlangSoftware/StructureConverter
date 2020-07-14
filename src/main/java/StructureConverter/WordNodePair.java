package StructureConverter;

import AnnotatedSentence.AnnotatedWord;
import AnnotatedTree.ParenthesisInLayerException;
import AnnotatedTree.ParseNodeDrawable;
import ParseTree.Symbol;

public class WordNodePair {

    private AnnotatedWord annotatedWord;
    private ParseNodeDrawable node;
    private int no;
    private boolean done;

    public WordNodePair(AnnotatedWord annotatedWord, int no) throws ParenthesisInLayerException {
        this.annotatedWord = annotatedWord;
        ParseNodeDrawable parent;
        if (getUniversalDependency().equals("ADVMOD")) {
            parent = new ParseNodeDrawable(new Symbol("ADVP"));
        } else if (getUniversalDependency().equals("ACL")) {
            parent = new ParseNodeDrawable(new Symbol("ADJP"));
        } else {
            parent = new ParseNodeDrawable(new Symbol(annotatedWord.getParse().getTreePos()));
        }
        this.node = new ParseNodeDrawable(parent, annotatedWord.toString().replaceAll("\\(", "-LRB-").replaceAll("\\)", "-RRB-"), true, 0);
        parent.addChild(node);
        this.no = no;
        this.done = false;
    }

    public WordNodePair(ParseNodeDrawable parseNodeDrawable, int no) {
        this.node = parseNodeDrawable;
        annotatedWord = new AnnotatedWord(parseNodeDrawable.getLayerData());
        this.done = false;
        this.no = no;
    }

    public String getWordName() {
        return annotatedWord.getName();
    }

    public int getNo() {
        return no;
    }

    public ParseNodeDrawable getNode(){
        return node;
    }

    public AnnotatedWord getWord() {
        return annotatedWord;
    }

    public void updateNode(){
        node = (ParseNodeDrawable) node.getParent();
    }

    public int getTo() {
        return annotatedWord.getUniversalDependency().to();
    }

    public boolean isDone() {
        return done;
    }

    public void done() {
        this.done = true;
    }

    public String getTreePos() {
        return annotatedWord.getParse().getTreePos();
    }

    public String getUniversalDependency() {
        return annotatedWord.getUniversalDependency().toString();
    }
}
