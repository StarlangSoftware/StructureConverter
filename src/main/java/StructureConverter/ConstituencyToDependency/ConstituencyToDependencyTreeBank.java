package StructureConverter.ConstituencyToDependency;

import AnnotatedSentence.*;
import AnnotatedTree.TreeBankDrawable;
import StructureConverter.MorphologicalAnalysisNotExistsException;
import StructureConverter.ParserConverterType;

public class ConstituencyToDependencyTreeBank {
    private TreeBankDrawable treeBank;

    public ConstituencyToDependencyTreeBank(TreeBankDrawable treeBank){
        this.treeBank = treeBank;
    }

    public AnnotatedCorpus convert(ConstituencyToDependencyTreeConverter constituencyToDependencyTreeConverter) {
        AnnotatedCorpus annotatedCorpus = new AnnotatedCorpus();
        for (int i = 0; i < treeBank.size(); i++){
            try {
                annotatedCorpus.addSentence(constituencyToDependencyTreeConverter.convert(treeBank.get(i), ParserConverterType.BASIC_ORACLE));
            } catch (MorphologicalAnalysisNotExistsException ignored) {}
        }
        return annotatedCorpus;
    }

}
