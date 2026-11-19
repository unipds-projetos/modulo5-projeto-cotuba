package br.com.unipds.cotuba.plugin;

import br.com.unipds.cotuba.domain.Ebook;

public interface CotubaPlugin {

    String afterRender(String html);
    void afterGenerate(Ebook ebook);

}
