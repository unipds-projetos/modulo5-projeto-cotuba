package br.com.unipds;

import br.com.unipds.ebook.chapter.Chapter;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GenerateEpub {

    public void generate(List<Chapter> chapters, Path outputFile) {
        try {
            var epub = new Book();

            //TODO: definir título e autor para o livro
            epub.getMetadata().addTitle("Livro");
            epub.getMetadata().addAuthor(new Author("Autor"));

            for (int i = 0; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);

                String titleChapter = chapter.getTitle();
                String htmlChapter = chapter.getHtml();

                String epubHtml = """
                          <html xmlns="http://www.w3.org/1999/xhtml">
                            <head>
                              <title>%s</title>
                            </head>
                            <body>
                              %s
                            </body>
                          </html>
                        """.formatted(titleChapter, htmlChapter);

                Resource chapterResouce = new Resource(epubHtml.getBytes(), MediatypeService.XHTML);

                epub.addSection(
                        titleChapter,
                        chapterResouce
                );

                if (i == 0) {
                    epub.getGuide().addReference(new GuideReference(chapterResouce, "text", "Start Reading"));
                }

            }

            EpubWriter epubWriter = new EpubWriter();
            epubWriter.write(epub, Files.newOutputStream(outputFile));

            System.out.println("Arquivo gerado com sucesso: " + outputFile);

        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar EPUB: " + outputFile.toAbsolutePath(), ex);
        }
    }

}
