package br.com.unipds.statistics;

import br.com.unipds.statistics.dto.CountWord;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class WordCounter implements Iterable<CountWord> {

    private final Map<String, Integer> wordList = new TreeMap<>();

    public void add(String word) {
        Integer count = wordList.get(word);

        if (count == null) {
            wordList.put(word, 1);
            return;
        }

        wordList.put(word, count + 1);
    }

    @Override
    public Iterator<CountWord> iterator() {

        Iterator<Map.Entry<String, Integer>> iterator = wordList.entrySet().iterator();

        return new Iterator<CountWord>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public CountWord next() {
                Map.Entry<String, Integer> entry = iterator.next();
                return new CountWord(entry.getKey(), entry.getValue());
            }
        };

    }

}
