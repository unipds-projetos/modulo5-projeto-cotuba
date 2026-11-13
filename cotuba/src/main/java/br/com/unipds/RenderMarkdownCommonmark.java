package br.com.unipds;

import br.com.unipds.ebook.chapter.Chapter;
import br.com.unipds.ebook.chapter.ChapterBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class RenderMarkdownCommonmark implements RenderMarkdown {

    @Override
    public List<Chapter> render(List<Markdown> markdowns) {
        return markdowns.stream().map(markdown -> {

            ChapterBuilder chapterBuilder = ChapterBuilder.builder();
            chapterBuilder.markdown(markdown);

            Parser parser = Parser.builder().build();
            Node document = null;

            try {
                document = parser.parse(markdown.content());
                document.accept(new AbstractVisitor() {
                    @Override
                    public void visit(Heading heading) {
                        if (heading.getLevel() == 1) {
                            String chapterTitle = ((Text) heading.getFirstChild()).getLiteral();
                            chapterBuilder.title(chapterTitle);

                        } else if (heading.getLevel() == 2) {
                            // seção
                        } else if (heading.getLevel() == 3) {
                            // título
                        }
                    }

                });

            } catch (Exception ex) {
                throw new IllegalStateException("Erro ao fazer parse do arquivo " + markdown.file(), ex);
            }

            try {
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                String html = renderer.render(document);

                chapterBuilder.html(html);

            } catch (Exception ex) {
                throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + markdown.file(), ex);
            }
            return chapterBuilder.build();
        }).toList();
    }
}
