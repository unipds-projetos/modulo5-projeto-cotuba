package br.com.unipds.cotuba.plugin;

import br.com.unipds.cotuba.domain.Ebook;

public interface CotubaPlugin {
    String aposRenderizacao(String html);

    void aposGeracao(Ebook ebook);

}
