package br.com.unipds.statistics;

import java.util.TreeMap;

public class WordCounter extends TreeMap<String, Integer> {

    public void add(String word) {
        Integer count = super.get(word);

        if (count == null) {
            super.put(word, 1);
            return;
        }

        super.put(word, count + 1);
    }

}
