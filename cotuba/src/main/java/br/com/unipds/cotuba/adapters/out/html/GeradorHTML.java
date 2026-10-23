package br.com.unipds.cotuba.adapters.out.html;

import br.com.unipds.cotuba.support.FormatoEbookQualifier;
import br.com.unipds.cotuba.domain.Capitulo;
import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.domain.FormatoEbook;
import br.com.unipds.cotuba.ports.out.GeradorEbook;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@FormatoEbookQualifier(FormatoEbook.HTML)
@ApplicationScoped
public class GeradorHTML implements GeradorEbook {

    public void gerar(Ebook ebook, Path arquivoDeSaida) {
        try {
            Path diretorioHTML = Files.createDirectories(arquivoDeSaida);
            Map<Capitulo,Path> arquivoHtmlDoCapitulo = new LinkedHashMap<>();
            Integer i = 1;
            for(Capitulo capitulo:ebook.capitulos()){
                String nomeArquivoHtml =  obterNomeArquivoHTMLCapitulo(i,capitulo);
                Path arquivoHtml  = diretorioHTML.resolve(nomeArquivoHtml);
                arquivoHtmlDoCapitulo.put(capitulo,arquivoHtml);
                escreveArquivoHtml(capitulo,arquivoHtml);
                i++;
            }
            escreveSumario(ebook,diretorioHTML, arquivoHtmlDoCapitulo);
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao criar ebook HTML:"+arquivoDeSaida,ex);
        }
    }

    private void escreveSumario(Ebook ebook, Path diretorioHTML, Map<Capitulo,Path> arquivoHtmlDoCapitulo) throws IOException {
        String itensSumarioHtml = ebook.capitulos().stream().map((Capitulo capitulo) ->
                    """
                   <li>
                        <a href="%s">%s</a>
                   </li>
                   """.formatted(arquivoHtmlDoCapitulo.get(capitulo) ,capitulo.titulo())
        ).collect(Collectors.joining()); // junta tudo em uma string
        String sumarioHtml = """
                  <DOCTYPE html>
                    <html lang="pt-br">
                        <head>
                            <meta charset="UTF-8">
                            <title>%s</title>
                        </head>
                        <body>
                                <h1>%s</h1>
                                <h2>Por: %2</h2>
                                <h3>Sumário</h3>
                                <ul>
                                
                                </ul>
                        </body>
                    </html>
                    """.formatted(ebook.titulo(),ebook.titulo(),ebook.autor());
        Path arquivoIndex = diretorioHTML.resolve("index.hml");
        Files.writeString(arquivoIndex,sumarioHtml,StandardCharsets.UTF_8);
    }

    private void escreveArquivoHtml(Capitulo capitulo, Path arquivoHtml) throws IOException {
          String html = """
                  <DOCTYPE html>
                    <html lang="pt-br">
                        <head>
                            <meta charset="UTF-8">
                            <title>%s</title>
                        </head>
                        <body>
                         %s
                        </body>
                    </html>
                    """.formatted(capitulo.titulo(),capitulo.html());

          Files.writeString(arquivoHtml,html, StandardCharsets.UTF_8);
    }

    private String obterNomeArquivoHTMLCapitulo(Integer i, Capitulo capitulo) {
       String tituloLimpo =  capitulo.titulo().toLowerCase().replaceAll("\\W","").replaceAll("\\s+","-"); // tudo que não for letra
        return "%02d-%s.html".formatted(i,tituloLimpo);
    }
}
