package br.com.unipds.statistics;

import java.util.Map;
import java.util.TreeMap;

public class WordCounter {

    private final Map<String, Integer> wordList = new TreeMap<>();

    public void add(String word) {
        Integer count = wordList.get(word);

        if (count == null) {
            wordList.put(word, 1);
            return;
        }

        wordList.put(word, count + 1);
    }

    public Iterable<Map.Entry<String, Integer>> getEntrySet() {
        return wordList.entrySet();
    }

}
