import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.AbstractMap.SimpleEntry;

public class CompareOutputs1 {

    private static int handle(AnnotatedWord word) {
        if (word.getUniversalDependency() == null) {
            return Integer.MAX_VALUE;
        }
        return word.getUniversalDependency().to();
    }

    private static String handleForString(AnnotatedWord word) {
        if (word.getUniversalDependency() == null) {
            return "";
        }
        return word.getUniversalDependency().toString();
    }

    private static void print(HashMap<String, HashMap<String, Integer>> tagMap) {
        ArrayList<String> keySet = new ArrayList<>(tagMap.keySet());
        for (int i = 0; i < 12; i++) {
            if (i != 10) {
                System.out.print(" ");
            } else {
                System.out.print("|");
            }
        }
        for (int i = 0; i < keySet.size(); i++) {
            System.out.print(keySet.get(i));
            for (int j = 0; j < 10 - keySet.get(i).length(); j++) {
                System.out.print(" ");
            }
            System.out.print("| ");
        }
        for (int i = 0; i < keySet.size(); i++) {
            System.out.println();
            System.out.print(keySet.get(i));
            for (int j = 0; j < 10 - keySet.get(i).length(); j++) {
                System.out.print(" ");
            }
            System.out.print("| ");
            for (int j = 0; j < keySet.size(); j++) {
                if (tagMap.get(keySet.get(i)).containsKey(keySet.get(j))) {
                    System.out.print(tagMap.get(keySet.get(i)).get(keySet.get(j)));
                    for (int k = 0; k < 10 - Integer.toString(tagMap.get(keySet.get(i)).get(keySet.get(j))).length(); k++) {
                        System.out.print(" ");
                    }
                    System.out.print("| ");
                } else {
                    System.out.print("0");
                    for (int k = 0; k < 9; k++) {
                        System.out.print(" ");
                    }
                    System.out.print("| ");
                }
            }
        }
    }

    private static void fillMap(AnnotatedSentence annotatedSentence1, AnnotatedSentence annotatedSentence2, HashMap<Integer, Integer> map, HashMap<Integer, SimpleEntry<Integer, Integer>[]> wordCountMap, HashMap<String, HashMap<String, Integer>> tagMap, HashMap<Integer, SimpleEntry<Integer, Integer>[]> dependencyMap) {
        if (!wordCountMap.containsKey(annotatedSentence1.wordCount())) {
            SimpleEntry<Integer, Integer>[] array = new SimpleEntry[3];
            array[0] = new SimpleEntry<>(0, 0);
            array[1] = new SimpleEntry<>(0, 0);
            array[2] = new SimpleEntry<>(0, 0);
            wordCountMap.put(annotatedSentence1.wordCount(), array);
        }
        for (int i = 0; i < annotatedSentence1.wordCount(); i++) {
            AnnotatedWord word1 = (AnnotatedWord) annotatedSentence1.getWord(i);
            AnnotatedWord word2 = (AnnotatedWord) annotatedSentence2.getWord(i);
            if (!tagMap.containsKey(word1.getUniversalDependency().toString())) {
                tagMap.put(word1.getUniversalDependency().toString(), new HashMap<>());
            }
            if (!dependencyMap.containsKey(-(word1.getUniversalDependency().to() - (i + 1)))) {
                SimpleEntry<Integer, Integer>[] a = new SimpleEntry[3];
                a[0] = new SimpleEntry<>(0, 0);
                a[1] = new SimpleEntry<>(0, 0);
                a[2] = new SimpleEntry<>(0, 0);
                dependencyMap.put(-(word1.getUniversalDependency().to() - (i + 1)), a);
            }
            if (word2.getUniversalDependency() != null) {
                if (!tagMap.get(word1.getUniversalDependency().toString()).containsKey(word2.getUniversalDependency().toString())) {
                    tagMap.get(word1.getUniversalDependency().toString()).put(word2.getUniversalDependency().toString(), 0);
                }
                tagMap.get(word1.getUniversalDependency().toString()).put(word2.getUniversalDependency().toString(), tagMap.get(word1.getUniversalDependency().toString()).get(word2.getUniversalDependency().toString()) + 1);
            }
            map.put(0, map.get(0) + 1);
            for (int j = 0; j < 3; j++) {
                dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[j] = new SimpleEntry<>(dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[j].getKey() + 1, dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[j].getValue());
                wordCountMap.get(annotatedSentence1.wordCount())[j] = new SimpleEntry<>(wordCountMap.get(annotatedSentence1.wordCount())[j].getKey() + 1, wordCountMap.get(annotatedSentence1.wordCount())[j].getValue());
            }
            if (word1.getUniversalDependency().toString().equals(handleForString(word2)) && word1.getUniversalDependency().to() == handle(word2)) {
                dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[0] = new SimpleEntry<>(dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[0].getKey(), dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[0].getValue() + 1);
                wordCountMap.get(annotatedSentence1.wordCount())[0] = new SimpleEntry<>(wordCountMap.get(annotatedSentence1.wordCount())[0].getKey(), wordCountMap.get(annotatedSentence1.wordCount())[0].getValue() + 1);
                map.put(1, map.get(1) + 1);
            } else if (word1.getUniversalDependency().to() == handle(word2)) {
                dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[1] = new SimpleEntry<>(dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[1].getKey(), dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[1].getValue() + 1);
                wordCountMap.get(annotatedSentence1.wordCount())[1] = new SimpleEntry<>(wordCountMap.get(annotatedSentence1.wordCount())[1].getKey(), wordCountMap.get(annotatedSentence1.wordCount())[1].getValue() + 1);
                map.put(2, map.get(2) + 1);
            } else if (word1.getUniversalDependency().toString().equals(handleForString(word2))) {
                dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[2] = new SimpleEntry<>(dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[2].getKey(), dependencyMap.get(-(word1.getUniversalDependency().to() - (i + 1)))[2].getValue() + 1);
                wordCountMap.get(annotatedSentence1.wordCount())[2] = new SimpleEntry<>(wordCountMap.get(annotatedSentence1.wordCount())[2].getKey(), wordCountMap.get(annotatedSentence1.wordCount())[2].getValue() + 1);
                map.put(3, map.get(3) + 1);
            }
        }
    }

    public static void main(String[]args) {
        AnnotatedCorpus annotatedCorpus1 = new AnnotatedCorpus(new File("Turkish-Phrase2"));
        AnnotatedCorpus annotatedCorpus2 = new AnnotatedCorpus(new File("Turkish-Phrase3"));
        HashMap<Integer, Integer> map = new HashMap<>();
        HashMap<Integer, SimpleEntry<Integer, Integer>[]> wordCountMap = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> tagMap = new HashMap<>();
        HashMap<Integer, SimpleEntry<Integer, Integer>[]> dependencyMap = new HashMap<>();
        map.put(0, 0);
        map.put(1, 0);
        map.put(2, 0);
        map.put(3, 0);
        int i = 0, j = 0;
        while (i < annotatedCorpus1.sentenceCount() || j < annotatedCorpus2.sentenceCount()) {
            AnnotatedSentence annotatedSentence1 = (AnnotatedSentence) annotatedCorpus1.getSentence(i);
            AnnotatedSentence annotatedSentence2 = (AnnotatedSentence) annotatedCorpus2.getSentence(j);
            if (annotatedSentence1.getFileName().equals(annotatedSentence2.getFileName())) {
                String first = annotatedSentence1.toWords();
                String second = annotatedSentence2.toWords();
                if (first.toLowerCase(new Locale("tr")).equals(second.toLowerCase(new Locale("tr")))) {
                    fillMap(annotatedSentence1, annotatedSentence2, map, wordCountMap, tagMap, dependencyMap);
                } else {
                    System.out.println(annotatedSentence1.getFileName() + " can't done." + "\t" + first + "\t" + second);
                }
                i++;
                j++;
            } else if (annotatedSentence1.getFileName().compareTo(annotatedSentence2.getFileName()) > 0) {
                j++;
            } else {
                i++;
            }
        }
        System.out.println("Total Word Count: " + map.get(0) + " LAS: " + map.get(1) + " UAS: " + (map.get(1) + map.get(2)) + " LA: " + (map.get(1) + map.get(3)));
        System.out.println("LAS %" + map.get(1) * 100.00 / map.get(0));
        System.out.println("UAS %" + (map.get(1) + map.get(2)) * 100.00 / map.get(0));
        System.out.println("LA %" + (map.get(1) + map.get(3)) * 100.00 / map.get(0));
        for (Integer key : wordCountMap.keySet()) {
            System.out.println("Sentence Length: " + key + " Total Word Count: " + wordCountMap.get(key)[0].getKey() + " LAS: " + wordCountMap.get(key)[0].getValue() + " UAS: " + (wordCountMap.get(key)[0].getValue() + wordCountMap.get(key)[1].getValue()) + " LA: " + (wordCountMap.get(key)[0].getValue() + wordCountMap.get(key)[2].getValue()));
            System.out.println("LAS %" + wordCountMap.get(key)[0].getValue() * 100.00 / wordCountMap.get(key)[0].getKey());
            System.out.println("UAS %" + (wordCountMap.get(key)[0].getValue() + wordCountMap.get(key)[1].getValue()) * 100.00 / wordCountMap.get(key)[0].getKey());
            System.out.println("LA %" + (wordCountMap.get(key)[0].getValue() + wordCountMap.get(key)[2].getValue()) * 100.00 / wordCountMap.get(key)[0].getKey());
        }
        for (Integer key : dependencyMap.keySet()) {
            System.out.println("Relation Length: " + key + " Total Word Count: " + dependencyMap.get(key)[0].getKey() + " LAS: " + dependencyMap.get(key)[0].getValue() + " UAS: " + dependencyMap.get(key)[1].getValue() + " LA: " + dependencyMap.get(key)[2].getValue());
            System.out.println("LAS %" + dependencyMap.get(key)[0].getValue() * 100.00 / dependencyMap.get(key)[0].getKey());
            System.out.println("UAS %" + (dependencyMap.get(key)[0].getValue() + dependencyMap.get(key)[1].getValue()) * 100.00 / dependencyMap.get(key)[0].getKey());
            System.out.println("LA %" + (dependencyMap.get(key)[0].getValue() + dependencyMap.get(key)[2].getValue()) * 100.00 / dependencyMap.get(key)[0].getKey());
        }
        print(tagMap);
    }
}
