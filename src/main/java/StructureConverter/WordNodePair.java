package StructureConverter;

import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ParenthesisInLayerException;
import AnnotatedTree.ParseNodeDrawable;
import ParseTree.Symbol;
import StructureConverter.ConstituencyToDependency.Language;

public class WordNodePair {

    private final AnnotatedWord annotatedWord;
    private ParseNodeDrawable node;
    private final int no;
    private boolean doneForConnect;
    private boolean doneForHead;

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
        this.doneForConnect = false;
        this.doneForHead = false;
    }

    public WordNodePair(ParseNodeDrawable parseNodeDrawable, Language language, int no) {
        this.node = parseNodeDrawable;
        if (language.equals(Language.TURKISH)) {
            parseNodeDrawable.getLayerInfo().removeLayer(ViewLayerType.ENGLISH_WORD);
            annotatedWord = new AnnotatedWord(parseNodeDrawable.getLayerData());
        } else {
            annotatedWord = new AnnotatedWord("{english=" + parseNodeDrawable.getData().getName() + "}{posTag=" + parseNodeDrawable.getParent().getData().getName() + "}");
        }
        this.doneForConnect = false;
        this.no = no;
    }

    public WordNodePair(AnnotatedWord annotatedWord, ParseNodeDrawable parseNodeDrawable, int no) {
        this.node = parseNodeDrawable;
        this.annotatedWord = annotatedWord;
        this.doneForConnect = false;
        this.no = no;
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

    public boolean isDoneForConnect() {
        return doneForConnect;
    }

    public boolean isDoneForHead() {
        return doneForHead;
    }

    public void doneForConnect() {
        this.doneForConnect = true;
    }

    public void doneForHead() {
        this.doneForHead = true;
    }

    public String getTreePos() {
        return annotatedWord.getParse().getTreePos();
    }

    public String getUniversalDependency() {
        return annotatedWord.getUniversalDependency().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WordNodePair)) {
            return false;
        }
        WordNodePair wordNodePair = (WordNodePair) obj;
        return this.annotatedWord.equals(wordNodePair.annotatedWord) && this.no == wordNodePair.no && this.doneForConnect == wordNodePair.doneForConnect;
    }
}
