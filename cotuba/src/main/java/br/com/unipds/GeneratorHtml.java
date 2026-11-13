package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookFormat;
import br.com.unipds.ebook.chapter.Chapter;
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
    public void generate(Ebook ebook) {
         Path fileOutput = ebook.outputFile();
        try {
            Path directoryHtml = Files.createDirectory(fileOutput);

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
            throw new IllegalStateException("Erro ao criar Ebook HTML: " + fileOutput, e);
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
