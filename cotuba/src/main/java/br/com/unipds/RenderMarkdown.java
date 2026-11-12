package br.com.unipds;

import br.com.unipds.ebook.chapter.Chapter;
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

    public List<Chapter> render(Path directoryMD) {
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

                Chapter chapter = new Chapter();

                Parser parser = Parser.builder().build();
                Node document = null;
                try {
                    String markdown = Files.readString(arquivoMD);
                    chapter.setMarkdown(markdown);

                    chapter.setFileMarkdown(arquivoMD);

                    document = parser.parse(markdown);
                    document.accept(new AbstractVisitor() {
                        @Override
                        public void visit(Heading heading) {
                            if (heading.getLevel() == 1) {
                                String tituloDoCapitulo = ((Text) heading.getFirstChild()).getLiteral();
                                chapter.setTitle(tituloDoCapitulo);

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
                    String html = renderer.render(document);

                    chapter.setHtml(html);
                    return chapter;

                } catch (Exception ex) {
                    throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + arquivoMD, ex);
                }
            }).toList();

        } catch (IOException e) {
            throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + directoryMD.toAbsolutePath(), e);
        }

    }

}
