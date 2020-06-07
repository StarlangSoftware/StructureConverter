public class WordNodePair {

    private String wordName;
    private int no;
    private int to;
    private boolean heap;
    private String treePos;
    private String universalDependency;

    public WordNodePair(String wordName, int no, int to, boolean heap, String treePos, String universalDependency) {
        this.wordName = wordName;
        this.no = no;
        this.to = to;
        this.heap = heap;
        this.treePos = treePos;
        this.universalDependency = universalDependency;
    }

    public String getWordName() {
        return wordName;
    }

    public int getNo() {
        return no;
    }

    public int getTo() {
        return to;
    }

    public boolean getHeap() {
        return heap;
    }

    public void setHeap(Boolean heap) {
        this.heap = heap;
    }

    public String getTreePos() {
        return treePos;
    }

    public String getUniversalDependency() {
        return universalDependency;
    }
}
