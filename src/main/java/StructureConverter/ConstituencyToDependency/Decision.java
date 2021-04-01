package StructureConverter.ConstituencyToDependency;/* Created by oguzkeremyildiz on 6.02.2021 */

public class Decision {

    private final int no;
    private final int to;
    private final String data;

    public Decision(int no, int to, String data) {
        this.no = no;
        this.to = to;
        this.data = data;
    }

    public int getNo() {
        return no;
    }

    public int getTo() {
        return to;
    }

    public String getData() {
        return data;
    }
}
