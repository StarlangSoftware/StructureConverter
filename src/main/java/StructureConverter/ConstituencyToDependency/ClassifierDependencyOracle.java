package StructureConverter.ConstituencyToDependency;/* Created by oguzkeremyildiz on 6.02.2021 */

import AnnotatedSentence.AnnotatedWord;
import AnnotatedTree.ParseNodeDrawable;
import DataStructure.CounterHashMap;
import MorphologicalAnalysis.MorphologicalTag;
import ParseTree.ParseNode;
import StructureConverter.WordNodePair;
import Util.FileUtils;

import java.util.AbstractMap;
import java.util.AbstractMap.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class ClassifierDependencyOracle implements DependencyOracle {

    private static ArrayList<String[]> dataList;

    public ClassifierDependencyOracle() {
        dataList = new ArrayList<>();
        try {
            Scanner source = new Scanner(FileUtils.getInputStream("ConsToDep/dataset.txt"));
            while (source.hasNext()) {
                String line = source.nextLine();
                dataList.add(line.split(" "));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String testC45(String[] testData){
        switch (testData[0]){
            case "NOUN":
                switch (testData[25]){
                    case "S":
                        switch (testData[27]){
                            case "VP":
                                return "NSUBJ";
                            case ",":
                                switch (testData[5]){
                                    case "false":
                                        switch (testData[6]){
                                            case "false":
                                                switch (testData[26]){
                                                    case "NP":
                                                        return "NMOD";
                                                    case "S":
                                                        return "ADVCL";
                                                    case "PP":
                                                        return "NMOD";
                                                    case "ADVP":
                                                        return "NMOD";
                                                    case "INTJ":
                                                        return "OBL";
                                                    case "CONJP":
                                                        return "NMOD";
                                                }
                                                break;
                                            case "true":
                                                return "NSUBJ";
                                        }
                                        break;
                                    case "true":
                                        return "NSUBJ";
                                }
                                break;
                            case "NP":
                                switch (testData[26]){
                                    case "NP":
                                        switch (testData[28]){
                                            case "NOMP":
                                                return "NSUBJ";
                                            case "VP":
                                                switch (testData[5]){
                                                    case "true":
                                                        return "NSUBJ";
                                                    case "false":
                                                        switch (testData[6]){
                                                            case "false":
                                                                switch (testData[8]){
                                                                    case "VERB":
                                                                        switch (testData[1]){
                                                                            case "NOUN":
                                                                                switch (testData[3]){
                                                                                    case "false":
                                                                                        switch (testData[2]){
                                                                                            case "false":
                                                                                                switch (testData[7]){
                                                                                                    case "false":
                                                                                                        return "NMOD";
                                                                                                    case "true":
                                                                                                        return "OBL";
                                                                                                }
                                                                                                break;
                                                                                            case "true":
                                                                                                return "OBL";
                                                                                        }
                                                                                        break;
                                                                                    case "true":
                                                                                        return "OBJ";
                                                                                }
                                                                                break;
                                                                            case "NUM":
                                                                                return "NMOD";
                                                                            case "VERB":
                                                                                return "ADVCL";
                                                                        }
                                                                        break;
                                                                    case "NOUN":
                                                                        return "NSUBJ";
                                                                    case "ADJ":
                                                                        return "NMOD";
                                                                }
                                                                break;
                                                            case "true":
                                                                return "NSUBJ";
                                                        }
                                                        break;
                                                }
                                                break;
                                            case ":":
                                                return "NMOD";
                                            case "null":
                                                return "NMOD";
                                            case "NP":
                                                return "NMOD";
                                            case ",":
                                                switch (testData[5]){
                                                    case "true":
                                                        return "NSUBJ";
                                                    case "false":
                                                        return "OBL";
                                                }
                                                break;
                                            case "QP":
                                                return "NSUBJ";
                                            case "ADJP":
                                                return "OBL";
                                            case "ADVP":
                                                switch (testData[3]){
                                                    case "false":
                                                        switch (testData[8]){
                                                            case "NOUN":
                                                                return "NMOD";
                                                            case "ADV":
                                                                return "OBJ";
                                                        }
                                                        break;
                                                    case "true":
                                                        return "OBL";
                                                }
                                                break;
                                        }
                                        break;
                                    case "CONJP":
                                        return "NSUBJ";
                                    case "ADVP":
                                        switch (testData[28]){
                                            case "NOMP":
                                                return "NSUBJ";
                                            case "VP":
                                                switch (testData[5]){
                                                    case "true":
                                                        return "NSUBJ";
                                                    case "false":
                                                        return "NMOD";
                                                }
                                                break;
                                            case "NP":
                                                return "NMOD";
                                            case "null":
                                                return "NMOD";
                                            case ",":
                                                return "NSUBJ";
                                            case "S":
                                                return "LIST";
                                        }
                                        break;
                                    case "INTJ":
                                        return "NSUBJ";
                                    case "PP":
                                        return "NSUBJ";
                                    case "VP":
                                        return "NSUBJ";
                                    case "ADJP":
                                        return "NSUBJ";
                                    case "S":
                                        return "CCOMP";
                                    case "``":
                                        return "NSUBJ";
                                    case ":":
                                        return "NSUBJ";
                                    case "\"":
                                        return "NSUBJ";
                                    case "NOMP":
                                        return "NSUBJ";
                                    case "''":
                                        return "NMOD";
                                    case "WP":
                                        return "OBJ";
                                    case "NUM":
                                        return "OBJ";
                                }
                                break;
                            case "ADVP":
                                return "NSUBJ";
                            case "ADJP":
                                return "OBJ";
                            case "NOMP":
                                return "NSUBJ";
                            case "-LRB-":
                                return "NSUBJ";
                            case "PP":
                                return "NSUBJ";
                            case ":":
                                return "NMOD";
                            case "``":
                                return "NSUBJ";
                            case "CONJP":
                                return "NSUBJ";
                            case "S":
                                return "NSUBJ";
                            case "WP":
                                return "NSUBJ";
                            case "\"":
                                return "NSUBJ";
                            case ".":
                                return "NSUBJ";
                            case "NEG":
                                return "AUX";
                            case "DP":
                                return "NMOD";
                            case "INTJ":
                                return "NSUBJ";
                        }
                        break;
                    case "NP":
                        switch (testData[27]){
                            case "CONJP":
                                switch (testData[7]){
                                    case "false":
                                        switch (testData[5]){
                                            case "false":
                                                return "CONJ";
                                            case "true":
                                                switch (testData[9]){
                                                    case "NOUN":
                                                        switch (testData[13]){
                                                            case "true":
                                                                return "CONJ";
                                                            case "false":
                                                                switch (testData[14]){
                                                                    case "false":
                                                                        return "NMOD";
                                                                    case "true":
                                                                        return "CONJ";
                                                                }
                                                                break;
                                                        }
                                                        break;
                                                    case "VERB":
                                                        return "CONJ";
                                                    case "ADJ":
                                                        return "CONJ";
                                                    case "NUM":
                                                        return "NMOD";
                                                }
                                                break;
                                        }
                                        break;
                                    case "true":
                                        return "CONJ";
                                }
                                break;
                            case "INTJ":
                                return "OBL";
                            case "NP":
                                switch (testData[9]){
                                    case "VERB":
                                        return "NMOD";
                                    case "NOUN":
                                        switch (testData[26]){
                                            case "NP":
                                                switch (testData[28]){
                                                    case "null":
                                                        return "NMOD";
                                                    case "NP":
                                                        switch (testData[15]){
                                                            case "true":
                                                                switch (testData[23]){
                                                                    case "true":
                                                                        return "NMOD";
                                                                    case "false":
                                                                        return "FLAT";
                                                                }
                                                                break;
                                                            case "false":
                                                                return "NMOD";
                                                        }
                                                        break;
                                                    case ".":
                                                        return "NMOD";
                                                    case "DP":
                                                        return "NMOD";
                                                    case ":":
                                                        return "NMOD";
                                                    case "ADJP":
                                                        switch (testData[15]){
                                                            case "false":
                                                                return "NMOD";
                                                            case "true":
                                                                return "FLAT";
                                                        }
                                                        break;
                                                    case "PP":
                                                        return "NMOD";
                                                    case ",":
                                                        return "NMOD";
                                                    case "CONJP":
                                                        return "NMOD";
                                                    case "NUM":
                                                        return "NMOD";
                                                    case "NOMP":
                                                        return "NMOD";
                                                }
                                                break;
                                            case "PP":
                                                return "NMOD";
                                            case "ADJP":
                                                switch (testData[28]){
                                                    case "NP":
                                                        return "NMOD";
                                                    case "null":
                                                        switch (testData[1]){
                                                            case "NOUN":
                                                                return "AMOD";
                                                            case "VERB":
                                                                return "ACL";
                                                        }
                                                        break;
                                                    case "CONJP":
                                                        return "ACL";
                                                    case "ADJP":
                                                        return "NMOD";
                                                    case "DP":
                                                        return "NMOD";
                                                    case ":":
                                                        return "AMOD";
                                                }
                                                break;
                                            case "DP":
                                                return "NMOD";
                                            case "NUM":
                                                return "NMOD";
                                            case "ADVP":
                                                return "NMOD";
                                            case ":":
                                                return "NMOD";
                                            case "S":
                                                switch (testData[13]){
                                                    case "true":
                                                        return "ACL";
                                                    case "false":
                                                        return "NMOD";
                                                }
                                                break;
                                            case "\"":
                                                return "NMOD";
                                            case "''":
                                                return "NMOD";
                                            case "-LRB-":
                                                return "NMOD";
                                        }
                                        break;
                                    case "ADJ":
                                        return "NMOD";
                                    case "ADV":
                                        return "NMOD";
                                    case "NUM":
                                        return "NMOD";
                                    case "DUP":
                                        return "COMPOUND";
                                    case "PRON":
                                        return "NMOD";
                                    case "INTERJ":
                                        return "NMOD";
                                    case "TIME":
                                        return "NMOD";
                                    case "POSTP":
                                        return "NMOD";
                                }
                                break;
                            case "ADVP":
                                return "NMOD";
                            case "PP":
                                return "NMOD";
                            case "ADJP":
                                return "NMOD";
                            case ",":
                                return "NMOD";
                            case "S":
                                return "NMOD";
                            case ":":
                                switch (testData[28]){
                                    case "NP":
                                        return "NMOD";
                                    case "CONJP":
                                        return "NMOD";
                                    case "NOMP":
                                        return "LIST";
                                    case "S":
                                        return "NSUBJ";
                                    case "\"":
                                        return "PARATAXIS";
                                }
                                break;
                            case "DP":
                                return "NMOD";
                            case "VP":
                                switch (testData[13]){
                                    case "false":
                                        return "OBJ";
                                    case "true":
                                        return "NMOD";
                                }
                                break;
                            case "NUM":
                                return "NMOD";
                            case ".":
                                return "NMOD";
                            case "''":
                                return "NMOD";
                            case "NOMP":
                                return "NSUBJ";
                            case "\"":
                                return "NMOD";
                            case "NEG":
                                return "NMOD";
                            case "``":
                                return "NMOD";
                        }
                        break;
                    case "VP":
                        switch (testData[24]){
                            case "false":
                                switch (testData[1]){
                                    case "NOUN":
                                        switch (testData[6]){
                                            case "false":
                                                switch (testData[5]){
                                                    case "false":
                                                        switch (testData[3]){
                                                            case "true":
                                                                switch (testData[28]){
                                                                    case "NP":
                                                                        return "OBL";
                                                                    case "null":
                                                                        switch (testData[26]){
                                                                            case "NP":
                                                                                switch (testData[27]){
                                                                                    case "VP":
                                                                                        return "OBJ";
                                                                                    case "NP":
                                                                                        return "NMOD";
                                                                                    case "ADJP":
                                                                                        return "IOBJ";
                                                                                }
                                                                                break;
                                                                            case "PP":
                                                                                return "IOBJ";
                                                                            case "ADVP":
                                                                                return "IOBJ";
                                                                            case "ADJP":
                                                                                return "COMPOUND";
                                                                        }
                                                                        break;
                                                                    case "VP":
                                                                        switch (testData[27]){
                                                                            case "NP":
                                                                                switch (testData[26]){
                                                                                    case "NP":
                                                                                        switch (testData[7]){
                                                                                            case "true":
                                                                                                return "OBL";
                                                                                            case "false":
                                                                                                return "IOBJ";
                                                                                        }
                                                                                        break;
                                                                                    case "ADJP":
                                                                                        return "NMOD";
                                                                                    case "ADVP":
                                                                                        return "OBJ";
                                                                                    case "PP":
                                                                                        return "OBJ";
                                                                                    case "S":
                                                                                        return "OBL";
                                                                                }
                                                                                break;
                                                                            case "ADVP":
                                                                                return "IOBJ";
                                                                            case "PP":
                                                                                return "NMOD";
                                                                            case "S":
                                                                                return "NMOD";
                                                                            case "ADJP":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                    case "ADVP":
                                                                        return "OBL";
                                                                    case "NUM":
                                                                        return "NMOD";
                                                                    case "S":
                                                                        return "OBJ";
                                                                    case "PP":
                                                                        return "OBL";
                                                                    case "CONJP":
                                                                        return "OBL";
                                                                    case "ADJP":
                                                                        return "ADVCL";
                                                                }
                                                                break;
                                                            case "false":
                                                                switch (testData[27]){
                                                                    case "NP":
                                                                        switch (testData[28]){
                                                                            case "VP":
                                                                                switch (testData[2]){
                                                                                    case "false":
                                                                                        return "NMOD";
                                                                                    case "true":
                                                                                        switch (testData[9]){
                                                                                            case "VERB":
                                                                                                return "OBL";
                                                                                            case "NOUN":
                                                                                                return "NMOD";
                                                                                            case "ADJ":
                                                                                                return "OBL";
                                                                                            case "NUM":
                                                                                                return "NMOD";
                                                                                        }
                                                                                        break;
                                                                                }
                                                                                break;
                                                                            case "null":
                                                                                return "NMOD";
                                                                            case "NP":
                                                                                return "NMOD";
                                                                            case "NOMP":
                                                                                return "AMOD";
                                                                            case "ADVP":
                                                                                return "OBL";
                                                                            case "NUM":
                                                                                switch (testData[2]){
                                                                                    case "false":
                                                                                        return "IOBJ";
                                                                                    case "true":
                                                                                        return "NMOD";
                                                                                }
                                                                                break;
                                                                            case ",":
                                                                                return "OBL";
                                                                            case "CONJP":
                                                                                return "NMOD";
                                                                            case "ADJP":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                    case "VP":
                                                                        switch (testData[26]){
                                                                            case "S":
                                                                                return "CCOMP";
                                                                            case "NP":
                                                                                return "OBL";
                                                                            case "ADVP":
                                                                                return "NMOD";
                                                                            case "PP":
                                                                                switch (testData[2]){
                                                                                    case "true":
                                                                                        return "NMOD";
                                                                                    case "false":
                                                                                        return "OBL";
                                                                                }
                                                                                break;
                                                                        }
                                                                        break;
                                                                    case "ADVP":
                                                                        return "NMOD";
                                                                    case "CONJP":
                                                                        return "OBL";
                                                                    case "DP":
                                                                        return "COMPOUND";
                                                                    case ",":
                                                                        switch (testData[26]){
                                                                            case "PP":
                                                                                return "OBL";
                                                                            case "ADVP":
                                                                                return "NSUBJ";
                                                                            case "NP":
                                                                                switch (testData[28]){
                                                                                    case "ADVP":
                                                                                        return "OBL";
                                                                                    case "VP":
                                                                                        return "NMOD";
                                                                                    case "NP":
                                                                                        return "NMOD";
                                                                                    case "PP":
                                                                                        return "OBL";
                                                                                }
                                                                                break;
                                                                        }
                                                                        break;
                                                                    case "ADJP":
                                                                        return "NMOD";
                                                                    case "NOMP":
                                                                        return "NMOD";
                                                                    case "PP":
                                                                        return "OBL";
                                                                    case "``":
                                                                        return "OBL";
                                                                    case "S":
                                                                        return "OBL";
                                                                    case ":":
                                                                        return "OBL";
                                                                }
                                                                break;
                                                        }
                                                        break;
                                                    case "true":
                                                        switch (testData[26]){
                                                            case "NP":
                                                                switch (testData[27]){
                                                                    case "PP":
                                                                        return "OBL";
                                                                    case "VP":
                                                                        switch (testData[7]){
                                                                            case "false":
                                                                                switch (testData[9]){
                                                                                    case "NOUN":
                                                                                        return "OBJ";
                                                                                    case "VERB":
                                                                                        return "OBJ";
                                                                                    case "ADJ":
                                                                                        return "NSUBJ";
                                                                                }
                                                                                break;
                                                                            case "true":
                                                                                switch (testData[9]){
                                                                                    case "VERB":
                                                                                        return "OBJ";
                                                                                    case "NOUN":
                                                                                        return "NMOD";
                                                                                }
                                                                                break;
                                                                        }
                                                                        break;
                                                                    case ",":
                                                                        return "NMOD";
                                                                    case "NP":
                                                                        switch (testData[28]){
                                                                            case "VP":
                                                                                switch (testData[8]){
                                                                                    case "ADJ":
                                                                                        return "NMOD";
                                                                                    case "VERB":
                                                                                        return "OBJ";
                                                                                    case "NOUN":
                                                                                        return "NMOD";
                                                                                }
                                                                                break;
                                                                            case "NP":
                                                                                return "NMOD";
                                                                            case "null":
                                                                                switch (testData[11]){
                                                                                    case "false":
                                                                                        switch (testData[9]){
                                                                                            case "NOUN":
                                                                                                return "NMOD";
                                                                                            case "VERB":
                                                                                                switch (testData[13]){
                                                                                                    case "true":
                                                                                                        return "OBJ";
                                                                                                    case "false":
                                                                                                        return "NMOD";
                                                                                                }
                                                                                                break;
                                                                                        }
                                                                                        break;
                                                                                    case "true":
                                                                                        return "COMPOUND";
                                                                                }
                                                                                break;
                                                                            case "ADVP":
                                                                                return "NMOD";
                                                                            case "ADJP":
                                                                                return "OBL";
                                                                            case "S":
                                                                                return "PARATAXIS";
                                                                            case "PP":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                    case "ADJP":
                                                                        return "NSUBJ";
                                                                    case "ADVP":
                                                                        return "OBJ";
                                                                    case "NUM":
                                                                        return "NMOD";
                                                                    case "S":
                                                                        return "NMOD";
                                                                    case "QP":
                                                                        return "COMPOUND";
                                                                    case "NOMP":
                                                                        return "NMOD";
                                                                }
                                                                break;
                                                            case "ADVP":
                                                                return "NMOD";
                                                            case "S":
                                                                switch (testData[28]){
                                                                    case "null":
                                                                        switch (testData[7]){
                                                                            case "false":
                                                                                return "CCOMP";
                                                                            case "true":
                                                                                return "ACL";
                                                                        }
                                                                        break;
                                                                    case "VP":
                                                                        return "OBJ";
                                                                    case "NP":
                                                                        return "OBJ";
                                                                    case "ADVP":
                                                                        return "NMOD";
                                                                }
                                                                break;
                                                            case "ADJP":
                                                                return "OBJ";
                                                            case "PP":
                                                                switch (testData[27]){
                                                                    case "NP":
                                                                        switch (testData[28]){
                                                                            case "VP":
                                                                                switch (testData[9]){
                                                                                    case "VERB":
                                                                                        switch (testData[7]){
                                                                                            case "false":
                                                                                                return "OBJ";
                                                                                            case "true":
                                                                                                return "OBL";
                                                                                        }
                                                                                        break;
                                                                                    case "ADJ":
                                                                                        return "NMOD";
                                                                                    case "NOUN":
                                                                                        return "NMOD";
                                                                                }
                                                                                break;
                                                                            case "NP":
                                                                                return "NMOD";
                                                                            case "null":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                    case "ADVP":
                                                                        return "NMOD";
                                                                    case "NUM":
                                                                        return "NMOD";
                                                                    case "VP":
                                                                        switch (testData[7]){
                                                                            case "false":
                                                                                return "OBL";
                                                                            case "true":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                    case "PP":
                                                                        return "NMOD";
                                                                    case ",":
                                                                        return "NMOD";
                                                                    case "ADJP":
                                                                        return "NMOD";
                                                                    case "WP":
                                                                        return "NMOD";
                                                                }
                                                                break;
                                                            case "''":
                                                                return "CCOMP";
                                                            case "VP":
                                                                return "NSUBJ";
                                                            case "NOMP":
                                                                return "VOCATIVE";
                                                            case "NUM":
                                                                return "OBJ";
                                                        }
                                                        break;
                                                }
                                                break;
                                            case "true":
                                                switch (testData[26]){
                                                    case "NP":
                                                        return "OBJ";
                                                    case "ADVP":
                                                        return "OBJ";
                                                    case "PP":
                                                        switch (testData[9]){
                                                            case "VERB":
                                                                switch (testData[27]){
                                                                    case "VP":
                                                                        return "OBL";
                                                                    case "NP":
                                                                        return "OBJ";
                                                                    case ",":
                                                                        return "OBL";
                                                                }
                                                                break;
                                                            case "ADJ":
                                                                return "NSUBJ";
                                                        }
                                                        break;
                                                    case "ADJP":
                                                        return "OBJ";
                                                    case "S":
                                                        return "CCOMP";
                                                    case "DP":
                                                        return "OBJ";
                                                }
                                                break;
                                        }
                                        break;
                                    case "ADJ":
                                        switch (testData[27]){
                                            case "VP":
                                                return "OBJ";
                                            case "ADJP":
                                                return "NMOD";
                                            case "NP":
                                                return "OBL";
                                            case "PP":
                                                return "OBJ";
                                            case "ADVP":
                                                switch (testData[6]){
                                                    case "false":
                                                        return "NMOD";
                                                    case "true":
                                                        return "OBJ";
                                                }
                                                break;
                                        }
                                        break;
                                    case "VERB":
                                        switch (testData[26]){
                                            case "S":
                                                return "CCOMP";
                                            case "NP":
                                                switch (testData[27]){
                                                    case "VP":
                                                        switch (testData[5]){
                                                            case "true":
                                                                return "OBJ";
                                                            case "false":
                                                                switch (testData[6]){
                                                                    case "true":
                                                                        return "OBJ";
                                                                    case "false":
                                                                        switch (testData[3]){
                                                                            case "true":
                                                                                return "CCOMP";
                                                                            case "false":
                                                                                switch (testData[2]){
                                                                                    case "false":
                                                                                        return "OBL";
                                                                                    case "true":
                                                                                        return "OBJ";
                                                                                }
                                                                                break;
                                                                        }
                                                                        break;
                                                                }
                                                                break;
                                                        }
                                                        break;
                                                    case "ADVP":
                                                        return "OBJ";
                                                    case "NP":
                                                        switch (testData[28]){
                                                            case "VP":
                                                                switch (testData[5]){
                                                                    case "false":
                                                                        switch (testData[6]){
                                                                            case "false":
                                                                                return "OBL";
                                                                            case "true":
                                                                                return "CCOMP";
                                                                        }
                                                                        break;
                                                                    case "true":
                                                                        switch (testData[9]){
                                                                            case "VERB":
                                                                                return "OBJ";
                                                                            case "NOUN":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                }
                                                                break;
                                                            case "NP":
                                                                return "OBJ";
                                                            case "null":
                                                                switch (testData[5]){
                                                                    case "true":
                                                                        switch (testData[13]){
                                                                            case "false":
                                                                                return "NMOD";
                                                                            case "true":
                                                                                return "XCOMP";
                                                                        }
                                                                        break;
                                                                    case "false":
                                                                        switch (testData[3]){
                                                                            case "true":
                                                                                return "NMOD";
                                                                            case "false":
                                                                                return "OBJ";
                                                                        }
                                                                        break;
                                                                }
                                                                break;
                                                            case "NUM":
                                                                return "OBL";
                                                        }
                                                        break;
                                                    case "S":
                                                        return "CCOMP";
                                                    case "PP":
                                                        switch (testData[28]){
                                                            case "NP":
                                                                switch (testData[3]){
                                                                    case "false":
                                                                        return "OBL";
                                                                    case "true":
                                                                        return "NMOD";
                                                                }
                                                                break;
                                                            case "VP":
                                                                return "ADVCL";
                                                            case "ADVP":
                                                                return "NMOD";
                                                        }
                                                        break;
                                                    case "ADJP":
                                                        return "IOBJ";
                                                    case "WP":
                                                        return "CCOMP";
                                                    case "CONJP":
                                                        return "CONJ";
                                                }
                                                break;
                                            case "PP":
                                                return "OBL";
                                            case "ADVP":
                                                switch (testData[27]){
                                                    case "NP":
                                                        return "OBJ";
                                                    case "ADVP":
                                                        return "NMOD";
                                                    case "ADJP":
                                                        return "XCOMP";
                                                    case "PP":
                                                        return "NMOD";
                                                    case "VP":
                                                        return "NMOD";
                                                    case "S":
                                                        return "OBJ";
                                                    case ",":
                                                        return "CCOMP";
                                                }
                                                break;
                                            case "VP":
                                                return "CONJ";
                                            case "ADJP":
                                                return "ADVCL";
                                            case "``":
                                                return "OBJ";
                                        }
                                        break;
                                    case "NUM":
                                        switch (testData[6]){
                                            case "false":
                                                switch (testData[27]){
                                                    case "ADVP":
                                                        return "NMOD";
                                                    case "NP":
                                                        switch (testData[26]){
                                                            case "NP":
                                                                switch (testData[28]){
                                                                    case "VP":
                                                                        switch (testData[3]){
                                                                            case "true":
                                                                                return "OBL";
                                                                            case "false":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                    case "NUM":
                                                                        switch (testData[8]){
                                                                            case "NUM":
                                                                                return "OBL";
                                                                            case "VERB":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                    case "null":
                                                                        return "OBL";
                                                                    case "ADVP":
                                                                        return "NMOD";
                                                                    case "NP":
                                                                        return "OBL";
                                                                }
                                                                break;
                                                            case "PP":
                                                                return "NMOD";
                                                            case "ADVP":
                                                                return "OBL";
                                                            case "NUM":
                                                                return "OBL";
                                                        }
                                                        break;
                                                    case "VP":
                                                        return "OBL";
                                                    case "NUM":
                                                        return "NMOD";
                                                    case ",":
                                                        return "OBL";
                                                    case "PP":
                                                        return "OBL";
                                                    case "S":
                                                        return "OBL";
                                                }
                                                break;
                                            case "true":
                                                return "OBJ";
                                        }
                                        break;
                                    case "TIME":
                                        return "NMOD";
                                }
                                break;
                            case "true":
                                switch (testData[6]){
                                    case "false":
                                        switch (testData[27]){
                                            case "VP":
                                                switch (testData[5]){
                                                    case "true":
                                                        return "COMPOUND";
                                                    case "false":
                                                        switch (testData[1]){
                                                            case "NOUN":
                                                                return "COMPOUND";
                                                            case "ADJ":
                                                                return "COMPOUND";
                                                            case "VERB":
                                                                return "IOBJ";
                                                        }
                                                        break;
                                                }
                                                break;
                                            case "NP":
                                                return "COMPOUND";
                                            case "PP":
                                                return "NMOD";
                                            case "WP":
                                                return "NMOD";
                                        }
                                        break;
                                    case "true":
                                        return "OBJ";
                                }
                                break;
                            case "null":
                                switch (testData[26]){
                                    case "NP":
                                        switch (testData[28]){
                                            case "null":
                                                switch (testData[3]){
                                                    case "false":
                                                        switch (testData[8]){
                                                            case "VERB":
                                                                return "OBJ";
                                                            case "NOUN":
                                                                return "NMOD";
                                                        }
                                                        break;
                                                    case "true":
                                                        return "COMPOUND";
                                                }
                                                break;
                                            case "NP":
                                                return "NMOD";
                                            case "VP":
                                                return "NMOD";
                                            case "PP":
                                                return "OBJ";
                                        }
                                        break;
                                    case "NEG":
                                        return "COMPOUND";
                                    case "ADVP":
                                        return "NMOD";
                                    case "PP":
                                        return "OBL";
                                    case "S":
                                        return "CCOMP";
                                }
                                break;
                        }
                        break;
                    case "NOMP":
                        switch (testData[26]){
                            case "NP":
                                switch (testData[17]){
                                    case "VERB":
                                        switch (testData[24]){
                                            case "false":
                                                return "OBJ";
                                            case "true":
                                                return "COMPOUND";
                                        }
                                        break;
                                    case "ADJ":
                                        switch (testData[5]){
                                            case "false":
                                                switch (testData[6]){
                                                    case "false":
                                                        switch (testData[3]){
                                                            case "false":
                                                                return "OBL";
                                                            case "true":
                                                                return "OBJ";
                                                        }
                                                        break;
                                                    case "true":
                                                        return "NSUBJ";
                                                }
                                                break;
                                            case "true":
                                                switch (testData[1]){
                                                    case "NOUN":
                                                        return "NSUBJ";
                                                    case "VERB":
                                                        return "XCOMP";
                                                }
                                                break;
                                        }
                                        break;
                                    case "NOUN":
                                        switch (testData[3]){
                                            case "false":
                                                return "NMOD";
                                            case "true":
                                                return "OBJ";
                                        }
                                        break;
                                    case "ADV":
                                        return "NMOD";
                                    case "NUM":
                                        return "NMOD";
                                    case "POSTP":
                                        return "OBJ";
                                    case "PRON":
                                        return "NMOD";
                                }
                                break;
                            case "PP":
                                return "NMOD";
                            case "ADJP":
                                return "NMOD";
                            case "NUM":
                                return "NMOD";
                            case "ADVP":
                                return "NMOD";
                            case "DP":
                                return "NMOD";
                            case ",":
                                return "NMOD";
                            case "S":
                                return "IOBJ";
                            case "NOMP":
                                return "CONJ";
                            case "CONJP":
                                return "IOBJ";
                            case "\"":
                                return "NMOD";
                            case "VP":
                                return "XCOMP";
                        }
                        break;
                    case "ADJP":
                        switch (testData[24]){
                            case "true":
                                return "COMPOUND";
                            case "false":
                                switch (testData[9]){
                                    case "VERB":
                                        switch (testData[6]){
                                            case "false":
                                                switch (testData[16]){
                                                    case "ADJ":
                                                        switch (testData[5]){
                                                            case "false":
                                                                return "OBL";
                                                            case "true":
                                                                switch (testData[26]){
                                                                    case "NP":
                                                                        switch (testData[1]){
                                                                            case "NOUN":
                                                                                switch (testData[27]){
                                                                                    case "ADJP":
                                                                                        switch (testData[13]){
                                                                                            case "false":
                                                                                                return "OBJ";
                                                                                            case "true":
                                                                                                return "NMOD";
                                                                                        }
                                                                                        break;
                                                                                    case "NP":
                                                                                        return "NMOD";
                                                                                    case "NUM":
                                                                                        return "NMOD";
                                                                                }
                                                                                break;
                                                                            case "VERB":
                                                                                return "OBJ";
                                                                        }
                                                                        break;
                                                                    case "ADJP":
                                                                        return "OBJ";
                                                                    case "PP":
                                                                        return "OBL";
                                                                }
                                                                break;
                                                        }
                                                        break;
                                                    case "NOUN":
                                                        return "NSUBJ";
                                                    case "VERB":
                                                        return "NMOD";
                                                    case "ADV":
                                                        return "AMOD";
                                                }
                                                break;
                                            case "true":
                                                switch (testData[26]){
                                                    case "NP":
                                                        return "OBJ";
                                                    case "ADVP":
                                                        return "OBJ";
                                                    case "ADJP":
                                                        return "OBJ";
                                                    case "PP":
                                                        switch (testData[27]){
                                                            case "NP":
                                                                return "OBJ";
                                                            case "ADJP":
                                                                return "OBL";
                                                        }
                                                        break;
                                                }
                                                break;
                                        }
                                        break;
                                    case "ADJ":
                                        switch (testData[3]){
                                            case "false":
                                                switch (testData[26]){
                                                    case "NP":
                                                        switch (testData[2]){
                                                            case "false":
                                                                switch (testData[7]){
                                                                    case "false":
                                                                        switch (testData[27]){
                                                                            case "PP":
                                                                                return "OBL";
                                                                            case "ADJP":
                                                                                switch (testData[6]){
                                                                                    case "true":
                                                                                        return "OBJ";
                                                                                    case "false":
                                                                                        return "NMOD";
                                                                                }
                                                                                break;
                                                                            case "NP":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                    case "true":
                                                                        switch (testData[5]){
                                                                            case "false":
                                                                                return "IOBJ";
                                                                            case "true":
                                                                                return "NMOD";
                                                                        }
                                                                        break;
                                                                }
                                                                break;
                                                            case "true":
                                                                return "NMOD";
                                                        }
                                                        break;
                                                    case "NUM":
                                                        return "NMOD";
                                                    case "PP":
                                                        return "NMOD";
                                                    case "ADJP":
                                                        return "NMOD";
                                                    case "ADVP":
                                                        return "NMOD";
                                                }
                                                break;
                                            case "true":
                                                return "OBL";
                                        }
                                        break;
                                    case "NOUN":
                                        return "NMOD";
                                    case "NUM":
                                        return "NMOD";
                                    case "ADV":
                                        switch (testData[3]){
                                            case "true":
                                                return "COMPOUND";
                                            case "false":
                                                return "AUX";
                                        }
                                        break;
                                    case "POSTP":
                                        return "NMOD";
                                }
                                break;
                            case "null":
                                return "OBJ";
                        }
                        break;
                    case "ADVP":
                        switch (testData[17]){
                            case "ADV":
                                return "NMOD";
                            case "NOUN":
                                return "NMOD";
                            case "VERB":
                                switch (testData[24]){
                                    case "false":
                                        switch (testData[27]){
                                            case "ADVP":
                                                switch (testData[1]){
                                                    case "NUM":
                                                        return "OBL";
                                                    case "NOUN":
                                                        switch (testData[5]){
                                                            case "true":
                                                                switch (testData[8]){
                                                                    case "ADV":
                                                                        switch (testData[7]){
                                                                            case "false":
                                                                                switch (testData[28]){
                                                                                    case "null":
                                                                                        return "NMOD";
                                                                                    case "ADVP":
                                                                                        return "NSUBJ";
                                                                                }
                                                                                break;
                                                                            case "true":
                                                                                return "OBJ";
                                                                        }
                                                                        break;
                                                                    case "NOUN":
                                                                        return "NSUBJ";
                                                                    case "ADJ":
                                                                        return "OBJ";
                                                                }
                                                                break;
                                                            case "false":
                                                                switch (testData[6]){
                                                                    case "false":
                                                                        return "NMOD";
                                                                    case "true":
                                                                        return "OBJ";
                                                                }
                                                                break;
                                                        }
                                                        break;
                                                    case "VERB":
                                                        return "OBJ";
                                                }
                                                break;
                                            case "VP":
                                                return "NMOD";
                                            case "NP":
                                                return "NMOD";
                                        }
                                        break;
                                    case "true":
                                        switch (testData[6]){
                                            case "false":
                                                return "COMPOUND";
                                            case "true":
                                                return "OBJ";
                                        }
                                        break;
                                }
                                break;
                            case "TIME":
                                return "NMOD";
                            case "ADJ":
                                return "NMOD";
                            case "NUM":
                                return "NMOD";
                        }
                        break;
                    case "PP":
                        switch (testData[27]){
                            case ",":
                                return "NMOD";
                            case "NP":
                                return "NMOD";
                            case "PP":
                                return "CASE";
                            case "NUM":
                                return "NMOD";
                            case "CONJP":
                                return "CONJ";
                            case "VP":
                                return "NSUBJ";
                            case "ADJP":
                                return "NMOD";
                        }
                        break;
                    case "NUM":
                        return "NMOD";
                    case "INTJ":
                        return "NMOD";
                }
                break;
            case "CONJ":
                switch (testData[28]){
                    case "null":
                        switch (testData[26]){
                            case "ADJP":
                                return "FIXED";
                            case "NP":
                                return "CASE";
                            case "CONJP":
                                switch (testData[25]){
                                    case "S":
                                        return "DISCOURSE";
                                    case "NOMP":
                                        return "CC";
                                    case "VP":
                                        return "ADVMOD";
                                    case "ADVP":
                                        return "DISCOURSE";
                                    case "NP":
                                        return "CC";
                                    case "CONJP":
                                        return "NMOD";
                                }
                                break;
                            case "NOMP":
                                return "AUX";
                            case "ADVP":
                                return "CASE";
                            case "VP":
                                return "MARK";
                            case "WP":
                                return "OBJ";
                            case "S":
                                return "CCOMP";
                        }
                        break;
                    case "NOMP":
                        return "CC";
                    case ":":
                        return "CC";
                    case ",":
                        return "CC";
                    case "VP":
                        switch (testData[26]){
                            case "CONJP":
                                return "CC";
                            case "NP":
                                return "CASE";
                            case "VP":
                                return "CC";
                            case "NOMP":
                                return "CC";
                            case "\"":
                                return "CC";
                            case "ADJP":
                                return "CC";
                            case "ADVP":
                                return "CC";
                        }
                        break;
                    case ".":
                        return "CC";
                    case "ADVP":
                        return "CC";
                    case "NP":
                        return "CC";
                    case "ADJP":
                        switch (testData[26]){
                            case "NP":
                                return "CC";
                            case "ADJP":
                                return "CC";
                            case "NUM":
                                return "CC";
                            case "NOMP":
                                return "DISCOURSE";
                            case "CONJP":
                                return "ADVMOD";
                            case "ADVP":
                                return "CC";
                        }
                        break;
                    case "CONJP":
                        switch (testData[26]){
                            case "DP":
                                switch (testData[16]){
                                    case "VERB":
                                        return "CC";
                                    case "NOUN":
                                        return "MARK";
                                    case "ADJ":
                                        return "MARK";
                                }
                                break;
                            case "PP":
                                return "CASE";
                            case "NP":
                                return "MARK";
                            case "CONJP":
                                return "CC";
                            case "S":
                                return "CC";
                            case "VP":
                                return "CC";
                            case "ADJP":
                                return "ADVMOD";
                            case "NOMP":
                                return "CC";
                        }
                        break;
                    case "NUM":
                        return "CC";
                    case "S":
                        return "CC";
                    case "PP":
                        return "CC";
                    case "NEG":
                        return "AUX";
                    case "INTJ":
                        return "DISCOURSE";
                }
                break;
            case "ADV":
                switch (testData[25]){
                    case "S":
                        return "ADVMOD";
                    case "VP":
                        switch (testData[1]){
                            case "ADV":
                                return "ADVMOD";
                            case "ADJ":
                                return "ADVMOD";
                            case "VERB":
                                return "ADVCL";
                            case "NOUN":
                                return "ADVMOD";
                        }
                        break;
                    case "ADVP":
                        switch (testData[24]){
                            case "false":
                                return "ADVMOD";
                            case "true":
                                switch (testData[9]){
                                    case "NOUN":
                                        return "ADVMOD";
                                    case "ADV":
                                        return "COMPOUND";
                                    case "VERB":
                                        return "COMPOUND";
                                    case "ADJ":
                                        return "ADVMOD";
                                }
                                break;
                            case "null":
                                return "CASE";
                        }
                        break;
                    case "ADJP":
                        switch (testData[1]){
                            case "ADV":
                                switch (testData[26]){
                                    case "NP":
                                        return "ADVMOD";
                                    case "ADVP":
                                        return "ADVMOD";
                                    case "ADJP":
                                        return "AMOD";
                                    case "\"":
                                        return "AMOD";
                                    case "PP":
                                        return "ADVMOD";
                                }
                                break;
                            case "VERB":
                                return "ADVCL";
                            case "ADJ":
                                return "ADVMOD";
                        }
                        break;
                    case "NP":
                        return "ADVMOD";
                    case "NOMP":
                        return "ADVMOD";
                    case "PP":
                        switch (testData[26]){
                            case "NP":
                                return "CASE";
                            case "ADVP":
                                return "ADVMOD";
                            case "S":
                                return "CASE";
                            case "WP":
                                return "ADVMOD";
                        }
                        break;
                    case "NUM":
                        return "ADVMOD";
                }
                break;
            case "ADJ":
                switch (testData[27]){
                    case "CONJP":
                        return "CONJ";
                    case "NP":
                        switch (testData[1]){
                            case "ADJ":
                                switch (testData[26]){
                                    case "ADJP":
                                        return "AMOD";
                                    case "NP":
                                        switch (testData[28]){
                                            case "null":
                                                return "NMOD";
                                            case "NP":
                                                return "NMOD";
                                            case ":":
                                                return "NMOD";
                                            case "VP":
                                                return "COMPOUND";
                                            case "ADJP":
                                                return "AMOD";
                                        }
                                        break;
                                    case "CONJP":
                                        return "NSUBJ";
                                    case "PP":
                                        return "ADVMOD";
                                    case "ADVP":
                                        return "ADVMOD";
                                    case "DP":
                                        switch (testData[16]){
                                            case "VERB":
                                                return "NMOD";
                                            case "PRON":
                                                return "DET";
                                            case "NOUN":
                                                return "AMOD";
                                        }
                                        break;
                                    case "NUM":
                                        return "AMOD";
                                    case "INTJ":
                                        return "DISCOURSE";
                                    case "S":
                                        return "ADVMOD";
                                    case "\"":
                                        return "NMOD";
                                }
                                break;
                            case "VERB":
                                switch (testData[26]){
                                    case "ADJP":
                                        switch (testData[25]){
                                            case "NP":
                                                switch (testData[12]){
                                                    case "false":
                                                        switch (testData[8]){
                                                            case "NOUN":
                                                                switch (testData[13]){
                                                                    case "false":
                                                                        return "ACL";
                                                                    case "true":
                                                                        switch (testData[15]){
                                                                            case "false":
                                                                                switch (testData[14]){
                                                                                    case "false":
                                                                                        switch (testData[5]){
                                                                                            case "false":
                                                                                                switch (testData[10]){
                                                                                                    case "false":
                                                                                                        switch (testData[24]){
                                                                                                            case "false":
                                                                                                                switch (testData[11]){
                                                                                                                    case "false":
                                                                                                                        switch (testData[28]){
                                                                                                                            case "null":
                                                                                                                                return "ACL";
                                                                                                                            case "NP":
                                                                                                                                return "ACL";
                                                                                                                            case ".":
                                                                                                                                return "AMOD";
                                                                                                                        }
                                                                                                                        break;
                                                                                                                    case "true":
                                                                                                                        return "ACL";
                                                                                                                }
                                                                                                                break;
                                                                                                            case "true":
                                                                                                                return "AMOD";
                                                                                                        }
                                                                                                        break;
                                                                                                    case "true":
                                                                                                        return "AMOD";
                                                                                                }
                                                                                                break;
                                                                                            case "true":
                                                                                                return "AMOD";
                                                                                        }
                                                                                        break;
                                                                                    case "true":
                                                                                        return "AMOD";
                                                                                }
                                                                                break;
                                                                            case "true":
                                                                                return "ACL";
                                                                        }
                                                                        break;
                                                                }
                                                                break;
                                                            case "ADJ":
                                                                return "AMOD";
                                                            case "VERB":
                                                                return "ACL";
                                                        }
                                                        break;
                                                    case "true":
                                                        return "ACL";
                                                }
                                                break;
                                            case "PP":
                                                switch (testData[28]){
                                                    case "PP":
                                                        return "ACL";
                                                    case "null":
                                                        return "AMOD";
                                                }
                                                break;
                                            case "VP":
                                                return "ACL";
                                            case "ADVP":
                                                return "AMOD";
                                            case "S":
                                                return "ACL";
                                            case "ADJP":
                                                return "AMOD";
                                        }
                                        break;
                                    case "ADVP":
                                        return "ACL";
                                    case "PP":
                                        return "OBL";
                                    case "S":
                                        return "ACL";
                                    case "NP":
                                        return "NMOD";
                                    case "CONJP":
                                        return "ADVMOD";
                                }
                                break;
                            case "NOUN":
                                return "AMOD";
                            case "NUM":
                                return "AMOD";
                        }
                        break;
                    case "ADJP":
                        return "AMOD";
                    case "VP":
                        switch (testData[26]){
                            case "ADVP":
                                return "ADVMOD";
                            case "ADJP":
                                switch (testData[1]){
                                    case "ADJ":
                                        switch (testData[24]){
                                            case "false":
                                                return "ADVMOD";
                                            case "true":
                                                return "COMPOUND";
                                            case "null":
                                                return "COMPOUND";
                                        }
                                        break;
                                    case "NOUN":
                                        return "ADVMOD";
                                    case "VERB":
                                        return "ADVCL";
                                }
                                break;
                            case "PP":
                                return "OBL";
                            case "NP":
                                switch (testData[25]){
                                    case "S":
                                        return "NSUBJ";
                                    case "VP":
                                        switch (testData[24]){
                                            case "false":
                                                return "OBJ";
                                            case "true":
                                                return "COMPOUND";
                                        }
                                        break;
                                }
                                break;
                            case "S":
                                switch (testData[1]){
                                    case "VERB":
                                        return "ADVCL";
                                    case "NOUN":
                                        return "ADVMOD";
                                    case "ADJ":
                                        return "CCOMP";
                                }
                                break;
                            case "VP":
                                return "ADVMOD";
                        }
                        break;
                    case "ADVP":
                        switch (testData[9]){
                            case "VERB":
                                switch (testData[28]){
                                    case "VP":
                                        return "ADVMOD";
                                    case "null":
                                        return "ADVMOD";
                                    case "ADVP":
                                        return "ADVMOD";
                                    case "NP":
                                        switch (testData[14]){
                                            case "false":
                                                return "ADVMOD";
                                            case "true":
                                                return "AMOD";
                                        }
                                        break;
                                }
                                break;
                            case "NOUN":
                                return "AMOD";
                            case "ADV":
                                return "ADVMOD";
                            case "ADJ":
                                return "COMPOUND";
                        }
                        break;
                    case "NOMP":
                        switch (testData[1]){
                            case "VERB":
                                return "ACL";
                            case "ADJ":
                                switch (testData[9]){
                                    case "VERB":
                                        return "ADVMOD";
                                    case "NOUN":
                                        return "AMOD";
                                    case "ADJ":
                                        return "NSUBJ";
                                    case "ADV":
                                        return "AUX";
                                    case "PRON":
                                        return "DISCOURSE";
                                    case "NUM":
                                        return "ADVMOD";
                                }
                                break;
                            case "NOUN":
                                return "AMOD";
                        }
                        break;
                    case "DP":
                        switch (testData[25]){
                            case "NP":
                                return "AMOD";
                            case "NOMP":
                                return "AMOD";
                            case "VP":
                                return "AMOD";
                            case "DP":
                                return "DET";
                            case "ADVP":
                                return "AMOD";
                            case "ADJP":
                                return "AMOD";
                            case "PP":
                                return "AMOD";
                            case "S":
                                return "AMOD";
                        }
                        break;
                    case "NUM":
                        return "AMOD";
                    case ",":
                        switch (testData[26]){
                            case "ADJP":
                                return "AMOD";
                            case "NP":
                                switch (testData[28]){
                                    case "ADJP":
                                        switch (testData[8]){
                                            case "VERB":
                                                return "ADVMOD";
                                            case "NOUN":
                                                return "AMOD";
                                        }
                                        break;
                                    case "NP":
                                        return "NMOD";
                                    case "ADVP":
                                        return "ADVMOD";
                                    case "NOMP":
                                        return "ADVMOD";
                                    case "CONJP":
                                        return "ADVMOD";
                                    case "S":
                                        return "PARATAXIS";
                                    case "VP":
                                        return "NSUBJ";
                                    case "PP":
                                        return "ADVCL";
                                }
                                break;
                            case "S":
                                return "PARATAXIS";
                            case "ADVP":
                                return "ADVMOD";
                            case "INTJ":
                                return "PARATAXIS";
                            case "PP":
                                switch (testData[28]){
                                    case "NP":
                                        return "ADVMOD";
                                    case "VP":
                                        return "OBL";
                                }
                                break;
                            case "CONJP":
                                return "DISCOURSE";
                            case "NOMP":
                                return "PARATAXIS";
                        }
                        break;
                    case "\"":
                        return "AMOD";
                    case "S":
                        switch (testData[25]){
                            case "S":
                                return "CCOMP";
                            case "VP":
                                return "ADVMOD";
                            case "NP":
                                return "ACL";
                        }
                        break;
                    case "PP":
                        return "ADVMOD";
                    case "NEG":
                        return "AUX";
                    case ":":
                        return "PARATAXIS";
                    case "``":
                        return "AMOD";
                    case "''":
                        return "AMOD";
                }
                break;
            case "NUM":
                switch (testData[9]){
                    case "NUM":
                        switch (testData[28]){
                            case "NUM":
                                return "CONJ";
                            case "null":
                                return "NUMMOD";
                            case "NP":
                                switch (testData[23]){
                                    case "true":
                                        return "COMPOUND";
                                    case "false":
                                        switch (testData[21]){
                                            case "false":
                                                return "NUMMOD";
                                            case "true":
                                                return "COMPOUND";
                                        }
                                        break;
                                }
                                break;
                            case "VP":
                                switch (testData[26]){
                                    case "NUM":
                                        return "NUMMOD";
                                    case "NP":
                                        return "NMOD";
                                }
                                break;
                            case "ADJP":
                                return "NUMMOD";
                            case "NOMP":
                                return "NUMMOD";
                            case ",":
                                return "NUMMOD";
                        }
                        break;
                    case "NOUN":
                        return "NUMMOD";
                    case "PUNC":
                        return "NUMMOD";
                    case "VERB":
                        return "NUMMOD";
                    case "ADJ":
                        switch (testData[10]){
                            case "false":
                                switch (testData[27]){
                                    case "NP":
                                        return "NUMMOD";
                                    case "NOMP":
                                        switch (testData[8]){
                                            case "VERB":
                                                return "ADVMOD";
                                            case "NOUN":
                                                return "NUMMOD";
                                            case "ADJ":
                                                return "NUMMOD";
                                        }
                                        break;
                                    case "ADJP":
                                        return "NUMMOD";
                                    case "NUM":
                                        return "NUMMOD";
                                    case "ADVP":
                                        return "NUMMOD";
                                    case ",":
                                        return "NUMMOD";
                                }
                                break;
                            case "true":
                                return "NMOD";
                        }
                        break;
                    case "PERCENT":
                        return "GOESWITH";
                    case "POSTP":
                        return "NUMMOD";
                    case "ADV":
                        return "NUMMOD";
                    case "INTERJ":
                        return "COMPOUND";
                    case "DET":
                        return "NUMMOD";
                }
                break;
            case "VERB":
                switch (testData[27]){
                    case "S":
                        switch (testData[28]){
                            case "null":
                                return "PARATAXIS";
                            case "''":
                                return "CCOMP";
                            case ",":
                                return "CCOMP";
                            case "VP":
                                return "CCOMP";
                            case "\"":
                                return "CCOMP";
                            case "NP":
                                return "PARATAXIS";
                            case ":":
                                return "PARATAXIS";
                            case "CONJP":
                                return "PARATAXIS";
                            case "``":
                                return "CCOMP";
                        }
                        break;
                    case ",":
                        switch (testData[26]){
                            case "NP":
                                return "CCOMP";
                            case "VP":
                                return "PARATAXIS";
                            case "S":
                                switch (testData[28]){
                                    case "NP":
                                        switch (testData[1]){
                                            case "VERB":
                                                return "ADVCL";
                                            case "PRON":
                                                return "ACL";
                                        }
                                        break;
                                    case "VP":
                                        return "CCOMP";
                                    case "CONJP":
                                        return "DEP";
                                    case "S":
                                        return "PARATAXIS";
                                    case "ADVP":
                                        return "PARATAXIS";
                                    case "``":
                                        return "ADVCL";
                                }
                                break;
                            case "ADVP":
                                return "ADVCL";
                            case "NOMP":
                                return "PARATAXIS";
                            case "CONJP":
                                return "PARATAXIS";
                            case "INTJ":
                                return "PARATAXIS";
                            case "ADJP":
                                return "AMOD";
                        }
                        break;
                    case "CONJP":
                        return "CONJ";
                    case "VP":
                        return "CCOMP";
                    case "NP":
                        switch (testData[26]){
                            case "ADJP":
                                return "ACL";
                            case "ADVP":
                                return "ADVCL";
                            case "NP":
                                return "NMOD";
                            case "PP":
                                return "NMOD";
                            case "S":
                                return "ADVCL";
                            case "DP":
                                return "NMOD";
                            case "CONJP":
                                return "CCOMP";
                            case "\"":
                                return "PARATAXIS";
                        }
                        break;
                    case "ADJP":
                        return "NMOD";
                    case "NOMP":
                        switch (testData[28]){
                            case "NEG":
                                return "AUX";
                            case "null":
                                return "NMOD";
                            case "\"":
                                return "CCOMP";
                            case ".":
                                return "CSUBJ";
                            case "S":
                                return "CONJ";
                            case ",":
                                return "CONJ";
                            case "CONJP":
                                return "ACL";
                        }
                        break;
                    case "NEG":
                        return "AUX";
                    case ":":
                        return "PARATAXIS";
                    case "PP":
                        switch (testData[25]){
                            case "PP":
                                return "COP";
                            case "S":
                                return "ADVMOD";
                            case "NOMP":
                                return "AUX";
                            case "VP":
                                switch (testData[5]){
                                    case "false":
                                        return "CCOMP";
                                    case "true":
                                        return "ADVCL";
                                }
                                break;
                        }
                        break;
                    case "DP":
                        return "AMOD";
                    case "\"":
                        return "CCOMP";
                    case "ADVP":
                        return "ADVCL";
                    case "``":
                        return "CCOMP";
                    case ".":
                        return "CCOMP";
                }
                break;
            case "PRON":
                switch (testData[25]){
                    case "S":
                        return "NSUBJ";
                    case "VP":
                        switch (testData[6]){
                            case "false":
                                switch (testData[27]){
                                    case "VP":
                                        return "OBJ";
                                    case "WP":
                                        return "OBJ";
                                    case "PP":
                                        return "OBL";
                                    case "NP":
                                        return "IOBJ";
                                    case "S":
                                        switch (testData[2]){
                                            case "true":
                                                return "NSUBJ";
                                            case "false":
                                                return "IOBJ";
                                        }
                                        break;
                                }
                                break;
                            case "true":
                                switch (testData[14]){
                                    case "false":
                                        return "OBJ";
                                    case "true":
                                        return "NMOD";
                                }
                                break;
                        }
                        break;
                    case "NP":
                        switch (testData[26]){
                            case "NP":
                                switch (testData[9]){
                                    case "NOUN":
                                        return "NMOD";
                                    case "VERB":
                                        switch (testData[6]){
                                            case "true":
                                                switch (testData[10]){
                                                    case "false":
                                                        return "OBJ";
                                                    case "true":
                                                        return "NMOD";
                                                }
                                                break;
                                            case "false":
                                                return "NMOD";
                                        }
                                        break;
                                    case "NUM":
                                        return "NMOD";
                                    case "PRON":
                                        return "NMOD";
                                    case "ADJ":
                                        return "NMOD";
                                    case "ADV":
                                        return "NMOD";
                                }
                                break;
                            case "ADJP":
                                return "AMOD";
                            case "WP":
                                return "ADVMOD";
                            case "DP":
                                return "NMOD";
                            case "PP":
                                return "AMOD";
                            case "ADVP":
                                return "NMOD";
                        }
                        break;
                    case "NOMP":
                        switch (testData[9]){
                            case "ADJ":
                                switch (testData[26]){
                                    case "NP":
                                        return "NMOD";
                                    case "PP":
                                        return "ADVMOD";
                                    case "ADVP":
                                        return "COMPOUND";
                                }
                                break;
                            case "PRON":
                                return "NMOD";
                            case "NOUN":
                                switch (testData[3]){
                                    case "false":
                                        return "NMOD";
                                    case "true":
                                        switch (testData[8]){
                                            case "VERB":
                                                return "OBJ";
                                            case "NOUN":
                                                return "OBL";
                                        }
                                        break;
                                }
                                break;
                            case "VERB":
                                return "OBJ";
                            case "POSTP":
                                return "DEP";
                            case "DET":
                                return "NSUBJ";
                        }
                        break;
                    case "ADVP":
                        return "NMOD";
                    case "PP":
                        return "ADVMOD";
                    case "ADJP":
                        switch (testData[8]){
                            case "ADJ":
                                switch (testData[3]){
                                    case "true":
                                        return "OBL";
                                    case "false":
                                        switch (testData[5]){
                                            case "true":
                                                return "NMOD";
                                            case "false":
                                                return "OBJ";
                                        }
                                        break;
                                }
                                break;
                            case "VERB":
                                return "OBJ";
                            case "NOUN":
                                return "COMPOUND";
                        }
                        break;
                    case "WP":
                        return "ADVMOD";
                    case "INTJ":
                        return "NMOD";
                }
                break;
            case "POSTP":
                switch (testData[26]){
                    case "NP":
                        return "CASE";
                    case "S":
                        return "CASE";
                    case "ADVP":
                        return "CASE";
                    case "VP":
                        return "CASE";
                    case "ADJP":
                        switch (testData[25]){
                            case "PP":
                                return "CASE";
                            case "NP":
                                return "AMOD";
                            case "ADVP":
                                return "FIXED";
                            case "ADJP":
                                return "AMOD";
                            case "VP":
                                return "CASE";
                            case "DP":
                                return "FIXED";
                        }
                        break;
                    case "NOMP":
                        return "CASE";
                    case "PP":
                        return "ADVMOD";
                    case "DP":
                        return "CASE";
                    case ":":
                        return "CASE";
                    case "\"":
                        return "CASE";
                    case "CONJP":
                        return "ADVMOD";
                    case "WP":
                        return "DET";
                    case "NUM":
                        return "CASE";
                    case "``":
                        return "CASE";
                }
                break;
            case "DET":
                switch (testData[25]){
                    case "NOMP":
                        return "DET";
                    case "NP":
                        return "DET";
                    case "S":
                        return "NSUBJ";
                    case "ADVP":
                        return "DET";
                    case "PP":
                        return "DET";
                    case "ADJP":
                        return "DET";
                    case "VP":
                        return "DET";
                    case "CONJP":
                        return "DET";
                    case "NUM":
                        return "DET";
                    case "DP":
                        return "DET";
                    case "INTJ":
                        return "DET";
                }
                break;
            case "PERCENT":
                switch (testData[9]){
                    case "NUM":
                        return "NMOD";
                    case "NOUN":
                        return "NUMMOD";
                    case "VERB":
                        switch (testData[26]){
                            case "NUM":
                                return "NUMMOD";
                            case "NP":
                                switch (testData[28]){
                                    case "NP":
                                        return "ADVMOD";
                                    case "null":
                                        return "ADVMOD";
                                    case "NUM":
                                        return "NUMMOD";
                                    case "VP":
                                        return "ADVMOD";
                                }
                                break;
                        }
                        break;
                    case "ADV":
                        return "NUMMOD";
                    case "ADJ":
                        switch (testData[8]){
                            case "ADJ":
                                return "NUMMOD";
                            case "NOUN":
                                return "NMOD";
                        }
                        break;
                }
                break;
            case "PUNC":
                return "PUNCT";
            case "INTERJ":
                return "DISCOURSE";
            case "QUES":
                return "AUX";
            case "DUP":
                return "NMOD";
            case "TIME":
                return "NUMMOD";
            case "RANGE":
                return "NUMMOD";
        }
        return "DEP";
    }

    private String testKnn(String[] testData, String pathName, int length1, int length2) {
        CounterHashMap<String> counts = new CounterHashMap<>();
        String[][] trainData = new String[length1][length2];
        Scanner input = new Scanner(FileUtils.getInputStream("ConsToDep/" + pathName + ".txt"));
        for (int i = 0; i < length1; i++){
            String[] items = input.nextLine().split(" ");
            for (int j = 0; j < length2; j++){
                trainData[i][j] = items[j];
            }
        }
        input.close();
        int minDistance = length2 - 1;
        for (int i = 0; i < length1; i++){
            int count = 0;
            for (int j = 0; j < length2 - 1; j++){
                if (!testData[j].equals(trainData[i][j])){
                    count++;
                }
            }
            if (count < minDistance){
                minDistance = count;
            }
        }
        for (int i = 0; i < length1; i++){
            int count = 0;
            for (int j = 0; j < length2 - 1; j++){
                if (!testData[j].equals(trainData[i][j])){
                    count++;
                }
            }
            if (count == minDistance){
                counts.put(trainData[i][length2 - 1]);
            }
        }
        return counts.max();
    }

    private ArrayList<SimpleEntry<Integer, Integer>> findList(int length, String classInfo) {
        ArrayList<SimpleEntry<Integer, Integer>> list = new ArrayList<>();
        for (String[] array : dataList) {
            for (int j = 0; j < array.length; j++) {
                if (array[0].equals(Integer.toString(length)) && array[1].equals(classInfo)) {
                    for (int k = 2; k < array.length; k += 2) {
                        list.add(new SimpleEntry<>(Integer.parseInt(array[k]), Integer.parseInt(array[k + 1])));
                    }
                    return list;
                }
            }
        }
        return null;
    }

    private boolean contains(int i, ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> list) {
        for (AbstractMap.SimpleEntry<Integer, Integer> entry : list) {
            if (entry.getKey() == i) {
                return true;
            }
        }
        return false;
    }

    private int findHeadIndex(ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> list, int first, int last) {
        int index = -1;
        for (int i = 0; i <= Math.abs(last - first); i++) {
            if (!contains(i, list)) {
                index = i;
                break;
            }
        }
        return index + first;
    }

    private void addHeadToDecisions(ArrayList<Decision> decisions, int index) {
        for (int i = 0; i < decisions.size(); i++) {
            if (i == 0) {
                if (decisions.get(i).getNo() > index) {
                    decisions.add(0, new Decision(-1, 0, null));
                    break;
                }
            }
            if (i + 1 < decisions.size()) {
                if (decisions.get(i).getNo() < index && decisions.get(i + 1).getNo() > index) {
                    decisions.add(i + 1, new Decision(-1, 0, null));
                    break;
                }
            }
            if (i + 1 == decisions.size()) {
                if (decisions.get(i).getNo() < index) {
                    decisions.add(i + 1, new Decision(-1, 0, null));
                    break;
                }
            }
        }
    }

    @Override
    public ArrayList<Decision> makeDecisions(int firstIndex, int lastIndex, ArrayList<WordNodePair> wordNodePairList, ParseNodeDrawable node) {
        String[] testData = new String[lastIndex + 1 - firstIndex];
        String classInfo;
        ArrayList<SimpleEntry<Integer, Integer>> list = new ArrayList<>();
        ArrayList<Decision> decisions = new ArrayList<>();
        for (int i = 0; i < testData.length; i++) {
            testData[i] = wordNodePairList.get(firstIndex + i).getWord().getParse().getPos();
        }
        switch (lastIndex + 1 - firstIndex) {
            case 2:
                classInfo = testKnn(testData, "2", 22940, 3);
                list = findList(2, classInfo);
                break;
            case 3:
                classInfo = testKnn(testData, "3", 8062, 4);
                list = findList(3, classInfo);
                break;
            case 4:
                classInfo = testKnn(testData, "4", 1667, 5);
                list = findList(4, classInfo);
                break;
            case 5:
                classInfo = testKnn(testData, "5", 598, 6);
                list = findList(5, classInfo);
                break;
            case 6:
                classInfo = testKnn(testData, "6", 167, 7);
                list = findList(6, classInfo);
                break;
            case 7:
                classInfo = testKnn(testData, "7", 60, 8);
                list = findList(7, classInfo);
                break;
            default:
                break;
        }
        int headIndex = findHeadIndex(list, firstIndex, lastIndex);
        for (int i = 0; i < Objects.requireNonNull(list).size(); i++) {
            String[] posDatas = new String[29];
            AnnotatedWord fromWord = wordNodePairList.get(firstIndex + list.get(i).getKey()).getWord();
            AnnotatedWord toWord = wordNodePairList.get(firstIndex + list.get(i).getValue()).getWord();
            AnnotatedWord headWord = wordNodePairList.get(headIndex).getWord();
            posDatas[0] = fromWord.getParse().getPos();
            posDatas[1] = fromWord.getParse().getRootPos();
            posDatas[2] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.ABLATIVE));
            posDatas[3] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.DATIVE));
            posDatas[4] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.GENITIVE));
            posDatas[5] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.NOMINATIVE));
            posDatas[6] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE));
            posDatas[7] = Boolean.toString(fromWord.getParse().containsTag(MorphologicalTag.PROPERNOUN));
            posDatas[8] = toWord.getParse().getPos();
            posDatas[9] = toWord.getParse().getRootPos();
            posDatas[10] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.ABLATIVE));
            posDatas[11] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.DATIVE));
            posDatas[12] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.GENITIVE));
            posDatas[13] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.NOMINATIVE));
            posDatas[14] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE));
            posDatas[15] = Boolean.toString(toWord.getParse().containsTag(MorphologicalTag.PROPERNOUN));
            posDatas[16] = headWord.getParse().getPos();
            posDatas[17] = headWord.getParse().getRootPos();
            posDatas[18] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.ABLATIVE));
            posDatas[19] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.DATIVE));
            posDatas[20] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.GENITIVE));
            posDatas[21] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.NOMINATIVE));
            posDatas[22] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.ACCUSATIVE));
            posDatas[23] = Boolean.toString(headWord.getParse().containsTag(MorphologicalTag.PROPERNOUN));
            if (fromWord.getSemantic() == null || headWord.getSemantic() == null) {
                posDatas[24] = "null";
            } else {
                posDatas[24] = Boolean.toString(fromWord.getSemantic().equals(headWord.getSemantic()));
            }
            posDatas[25] = node.getData().getName();
            String firstChild = "null", secondChild = "null", thirdChild = "null";
            if (node.numberOfChildren() > 0) {
                firstChild = node.getChild(0).getData().getName();
            }
            if (node.numberOfChildren() > 1) {
                secondChild = node.getChild(1).getData().getName();
            }
            if (node.numberOfChildren() > 2) {
                thirdChild = node.getChild(2).getData().getName();
            }
            posDatas[26] = firstChild;
            posDatas[27] = secondChild;
            posDatas[28] = thirdChild;
            decisions.add(new Decision(firstIndex + list.get(i).getKey(), list.get(i).getValue() - list.get(i).getKey(), testC45(posDatas)));
        }
        addHeadToDecisions(decisions, headIndex);
        return decisions;
    }
}
