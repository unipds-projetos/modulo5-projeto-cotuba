package br.com.unipds.estatisticas;

import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.domain.Capitulo;
import br.com.unipds.cotuba.plugin.CotubaPlugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class PluginEstatisticas implements CotubaPlugin {
    @Override
    public String aposRenderizacao(String s) {
        return "";
    }

    @Override
    public void aposGeracao(Ebook ebook) {
        ContadorPalavras contadorPalavras = new ContadorPalavras();

        for(Capitulo capitulo : ebook.capitulos()){
            String html = capitulo.html();
            Document elements = Jsoup.parseBodyFragment(html);
            String texto = elements.text().toLowerCase();
            texto = texto.replaceAll("\\p{Punct}", "");//punct --> pontuação, removida
            String[] palavras = texto.split("\\s+");
            Arrays.stream(palavras).forEach(contadorPalavras::adicionarPalavra);
            for(Map.Entry<String, Integer> entry : contadorPalavras.entrySet()){
                String palavra = entry.getKey();
                Integer contagem = entry.getValue();
                System.out.printf("'%s':'%d' \n",palavra,contagem);
            }
        }
        for(ContadorPalavras.ContemPalavra contemPalavra : contadorPalavras){
            System.out.println(contemPalavra.palavra() + " " + contemPalavra.ocorrencias());
        }
    }
}
