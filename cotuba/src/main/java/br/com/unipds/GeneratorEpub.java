package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookFormat;
import br.com.unipds.ebook.chapter.Chapter;
import jakarta.enterprise.context.ApplicationScoped;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@EbookFormatQualifier(EbookFormat.EPUB)
@ApplicationScoped
public class GeneratorEpub implements GeneratorEbook {

    @Override
    public void generate(Ebook ebook) {
        List<Chapter> chapters = ebook.chapters();
        Path outputFile = ebook.outputFile();

        try {
            var epub = new Book();

            epub.getMetadata().addTitle(ebook.title());
            epub.getMetadata().addAuthor(
                    new Author(ebook.author())
            );

            for (int i = 0; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);

                String titleChapter = chapter.title();
                String htmlChapter = chapter.html();

                try {
                    StringWriter stringWriter = new StringWriter();
                    XMLStreamWriter writer = XMLOutputFactory.newInstance()
                            .createXMLStreamWriter(stringWriter);

                    writer.writeStartElement("html");
                    writer.writeDefaultNamespace("http://www.w3.org/1999/xhtml");

                    writer.writeStartElement("head");
                    writer.writeStartElement("title");
                    writer.writeCharacters(titleChapter);
                    writer.writeEndElement();
                    writer.writeEndDocument();

                    writer.writeStartElement("body");
                    writer.writeCharacters("");
                    writer.flush();

                    stringWriter.write(htmlChapter);

                    writer.writeEndElement();
                    writer.writeEndDocument();

                    writer.close();

                    Resource resourceChapter = new Resource(stringWriter.toString().getBytes(), MediatypeService.XHTML);
                    epub.addSection(titleChapter, resourceChapter);

                } catch (XMLStreamException e) {
                    throw new IllegalStateException("Erro ao gerar capitulo do ebook " + titleChapter, e);
                }
            }

            EpubWriter epubWriter = new EpubWriter();
            epubWriter.write(epub, Files.newOutputStream(outputFile));

        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar EPUB: " + outputFile.toAbsolutePath(), ex);
        }
    }

}
