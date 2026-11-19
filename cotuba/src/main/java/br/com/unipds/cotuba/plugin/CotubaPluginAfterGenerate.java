package br.com.unipds.cotuba.plugin;

import br.com.unipds.cotuba.domain.Ebook;

public interface CotubaPluginAfterGenerate {

    void afterGenerate(Ebook ebook);

}
