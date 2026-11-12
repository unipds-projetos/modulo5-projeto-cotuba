package br.com.unipds;

import br.com.unipds.ebook.chapter.Chapter;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Path;
import java.util.List;

public class RenderMarkdownCommonmark implements RenderMarkdown {

    @Override
    public List<Chapter> render(Path directoryMd) {

        MarkdownRepository markdownRepository = new MarkdownRepository();
        List<Chapter> chapters = markdownRepository.get(directoryMd);

        return chapters.stream().map(chapter -> {
            Parser parser = Parser.builder().build();
            Node document = null;

            Path fileMd = chapter.getFileMarkdown();

            try {;
                document = parser.parse(chapter.getMarkdown());
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
                throw new IllegalStateException("Erro ao fazer parse do arquivo " + fileMd, ex);
            }

            try {
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                String html = renderer.render(document);

                chapter.setHtml(html);
                return chapter;

            } catch (Exception ex) {
                throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + fileMd, ex);
            }

        }).toList();
    }
}
