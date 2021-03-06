package StructureConverter.DependencyToConstituency;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedTree.TreeBankDrawable;
import ParseTree.ParseTree;

import java.util.ArrayList;

public class DependencyToConstituencyTreeBank {
    private AnnotatedCorpus annotatedCorpus;

    public DependencyToConstituencyTreeBank(AnnotatedCorpus annotatedCorpus) {
        this.annotatedCorpus = annotatedCorpus;
    }

    public TreeBankDrawable convert(DependencyToConstituencyTreeConverter dependencyToConstituencyTreeConverter) {
        ArrayList<ParseTree> parseTrees = new ArrayList<ParseTree>();
        for (int i = 0; i < annotatedCorpus.sentenceCount(); i++){
            parseTrees.add(dependencyToConstituencyTreeConverter.convert((AnnotatedSentence) annotatedCorpus.getSentence(i), null));
        }
        return new TreeBankDrawable(parseTrees);
    }
}
