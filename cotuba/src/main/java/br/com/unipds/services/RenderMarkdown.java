package br.com.unipds.services;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

public class RenderMarkdown {

    public List<String> render(Path directoryMD) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");
        try (Stream<Path> streamMDs = Files.list(directoryMD)) {
            List<Path> arquivosMD = streamMDs
                    .filter(matcher::matches)
                    .sorted()
                    .toList();

            if (arquivosMD.isEmpty()) {
                throw new IllegalStateException("Não foram encontrados capítulos (arquivos .md) no diretório: " + directoryMD.toAbsolutePath());
            }

            return arquivosMD.stream().map(arquivoMD -> {
                Parser parser = Parser.builder().build();
                Node document = null;
                try {
                    document = parser.parseReader(Files.newBufferedReader(arquivoMD));
                    document.accept(new AbstractVisitor() {
                        @Override
                        public void visit(Heading heading) {
                            if (heading.getLevel() == 1) {
                                // capítulo
                                String tituloDoCapitulo = ((Text) heading.getFirstChild()).getLiteral();
                                // TODO: usar título do capítulo
                            } else if (heading.getLevel() == 2) {
                                // seção
                            } else if (heading.getLevel() == 3) {
                                // título
                            }
                        }

                    });
                } catch (Exception ex) {
                    throw new IllegalStateException("Erro ao fazer parse do arquivo " + arquivoMD, ex);
                }

                try {
                    HtmlRenderer renderer = HtmlRenderer.builder().build();
                    return renderer.render(document);

                } catch (Exception ex) {
                    throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + arquivoMD, ex);
                }
            }).toList();

        } catch (IOException e) {
            throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + directoryMD.toAbsolutePath(), e);
        }

    }

}
