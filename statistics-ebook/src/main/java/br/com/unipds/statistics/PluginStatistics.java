package br.com.unipds.statistics;

import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.plugin.CotubaPlugin;
import br.com.unipds.cotuba.domain.Chapter;
import org.jsoup.Jsoup;

public class PluginStatistics implements CotubaPlugin {

    @Override
    public String afterRender(String s) {
        return "";
    }

    @Override
    public void afterGenerate(Ebook ebook) {

        System.out.println("renderizando essa porcaria");

        for (Chapter chapter : ebook.chapters()) {
            String html = chapter.html();

            System.out.println("Teste for chapter");

            System.out.println(html);

            String chapterText = Jsoup.parseBodyFragment(html).text();
            String[] words = chapterText.split("\\s+");

            for (String word : words) {
                System.out.println("Teste");
                System.out.println(word);
            }

        }
    }

}
