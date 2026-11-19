package br.com.unipds.theme;

import br.com.unipds.cotuba.plugin.CotubaPluginAfterRender;

public class PluginThemeCSS implements CotubaPluginAfterRender {

    @Override
    public String afterRender(String html) {
            return """
                <style>
                   h1 {
                    border-bottom: 1px dashed black;
                    font-size: 3em;
                    font-weight: bold;
                    font-variant-caps: small-caps;
                   }
                   h2 {
                    border-left: 1px solid black;
                    padding-left: 5px;
                    border-bottom: 1px solid black;
                   }
                </style>
                %s
                """.formatted(html);
    }

}
