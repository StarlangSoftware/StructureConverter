package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.TreeBankDrawable;

public class ConstituencyToDependencyTreeBank {
    private TreeBankDrawable treeBank;

    public ConstituencyToDependencyTreeBank(TreeBankDrawable treeBank){
        this.treeBank = treeBank;
    }

    public AnnotatedCorpus convert(ConstituencyToDependencyTreeConverter constituencyToDependencyTreeConverter) {
        AnnotatedCorpus annotatedCorpus = new AnnotatedCorpus();
        for (int i = 0; i < treeBank.size(); i++){
            annotatedCorpus.addSentence(constituencyToDependencyTreeConverter.convert(treeBank.get(i)));
        }
        return annotatedCorpus;
    }

}
