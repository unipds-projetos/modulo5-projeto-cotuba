package br.com.unipds.cotuba.html;

import br.com.unipds.cotuba.support.EbookFormatQualifier;
import br.com.unipds.cotuba.domain.Chapter;
import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.domain.EbookFormat;
import br.com.unipds.cotuba.ports.out.GeneratorEbook;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@EbookFormatQualifier(EbookFormat.HTML)
@ApplicationScoped
public class GeneratorHtml implements GeneratorEbook {

    @Override
    public void generate(Ebook ebook, Path outputFile) {
        try {
            Path directoryHtml = Files.createDirectory(outputFile);

            int i = 1;
            Map<Chapter, Path> fileHtmlChapter = new LinkedHashMap<>();
            for (Chapter chapter : ebook.chapters()) {
                String nameFileHtml = getNameFileHtml(i, chapter);
                Path fileHtml = directoryHtml.resolve(nameFileHtml);

                fileHtmlChapter.put(chapter, fileHtml);

                writeFileHtml(chapter, fileHtml);
                i++;
            }

            writeSummary(ebook, directoryHtml, fileHtmlChapter);

        } catch (IOException e) {
            throw new IllegalStateException("Erro ao criar Ebook HTML: " + outputFile, e);
        }
    }

    private void writeFileHtml(Chapter chapter, Path fileHtml) {
        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                </head>
                <body>
                    %s
                </body>
                </html>
                """.formatted(chapter.title(), chapter.html());

        try {
            Files.writeString(fileHtml, html, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gerar arquivo HTML ", e);
        }
    }

    private void writeSummary(Ebook ebook, Path directoryHtml, Map<Chapter, Path> fileHtmlChapter) {
        String itemsSummaryHtml = ebook.chapters().stream().map(chapter ->
            """
                    <li>
                        <a href="%s">%s</a>
                    </li>
                    """.formatted(fileHtmlChapter.get(chapter).getFileName(), chapter.title())
        ).collect(Collectors.joining());

        String summaryHtml = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                </head>
                <body>
                    <h1>%s</h1>
                    <h2>Por: %s</h2>
                    <h3>Sumário:</h3>
                    <ul>
                        %s
                    </ul>
                </body>
                </html>
                """.formatted(ebook.title(), ebook.title(), ebook.author(), itemsSummaryHtml);
        Path fileIndexHtml = directoryHtml.resolve("index.html");
        try {
            Files.writeString(fileIndexHtml, summaryHtml);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gerar arquivo Index HTML ", e);
        }
    }

    private String getNameFileHtml(int i, Chapter chapter) {
        String cleanTitle = chapter.title().toLowerCase().replaceAll("\\W", "");
        return "%02d-%s.html".formatted(i, cleanTitle);
    }

}
