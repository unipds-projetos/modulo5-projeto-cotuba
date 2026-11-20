package br.com.unipds.cotuba.md;

import br.com.unipds.cotuba.domain.Chapter;
import br.com.unipds.cotuba.domain.ChapterBuilder;
import br.com.unipds.cotuba.domain.Markdown;
import br.com.unipds.cotuba.plugin.CotubaPluginAfterRender;
import br.com.unipds.cotuba.ports.out.RenderMarkdown;
import jakarta.enterprise.context.ApplicationScoped;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;
import java.util.ServiceLoader;

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
                throw new IllegalStateException("Erro ao fazer parse do arquivo " + markdown.fileName(), ex);
            }

            try {
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                String html = renderer.render(document);

                for (CotubaPluginAfterRender plugin : ServiceLoader.load(CotubaPluginAfterRender.class)) {
                    String processedHtml = plugin.afterRender(html);

                    if (processedHtml != null && !processedHtml.isBlank()) {
                        html = processedHtml;
                    }
                }

                chapterBuilder.html(html);

            } catch (Exception ex) {
                throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + markdown.fileName(), ex);
            }
            return chapterBuilder.build();
        }).toList();
    }
}
