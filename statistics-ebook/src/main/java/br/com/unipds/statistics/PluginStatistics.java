package br.com.unipds.statistics;

import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.plugin.CotubaPlugin;
import br.com.unipds.cotuba.domain.Chapter;

public class PluginStatistics implements CotubaPlugin {

    @Override
    public String afterRender(String s) {
        return "";
    }

    @Override
    public void afterGenerate(Ebook ebook) {
        for (Chapter chapter : ebook.chapters()) {
            String html = chapter.html();
        }
    }

}
