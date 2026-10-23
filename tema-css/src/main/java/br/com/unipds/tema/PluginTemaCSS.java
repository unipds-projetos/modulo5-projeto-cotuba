package br.com.unipds.tema;

import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.plugin.CotubaPlugin;

public class PluginTemaCSS implements CotubaPlugin {

    @Override
    public String aposRenderizacao(String html) {
        return """
                <style>
                    h1 {
                        border-bottom: 1px solid #ccc;
                        font-size: 2.5em;
                        font-weight: bold;
                        font-variant: small-caps;
                    }
                    h2{
                        border-left: 5px solid #ccc;
                        padding-left: 10px;
                        border-bottom: 1px solid #ccc;
                    }
                </style>
                %s
                """.formatted(html);
    }

    @Override
    public void aposGeracao(Ebook ebook) {

    }
}
