package br.com.unipds.statistics;

import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.plugin.CotubaPlugin;
import br.com.unipds.cotuba.domain.Chapter;
import org.jsoup.Jsoup;

import java.util.Map;

public class PluginStatistics implements CotubaPlugin {

    @Override
    public String afterRender(String s) {
        return "";
    }

    @Override
    public void afterGenerate(Ebook ebook) {
        WordCounter wordCounter = new WordCounter();

        for (Chapter chapter : ebook.chapters()) {
            String html = chapter.html();

            String chapterText = Jsoup.parseBodyFragment(html).text().toLowerCase();
            chapterText = chapterText.replaceAll("\\p{Punct}", "");
            String[] words = chapterText.split("\\s+");

            for (String word : words) {
                wordCounter.add(word);
            }
        }

        for (Map.Entry<String, Integer> count : wordCounter.entrySet()) {
            String word = count.getKey();
            Integer value = count.getValue();

            System.out.printf("'%s': '%d'%n", word, value);
        }
    }

}
