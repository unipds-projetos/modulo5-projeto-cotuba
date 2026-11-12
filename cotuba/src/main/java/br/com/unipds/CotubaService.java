package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookFormat;
import br.com.unipds.ebook.chapter.Chapter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CotubaService {

    private final RenderMarkdown renderMarkdown;
    private final ReaderPropertiesEbook readerPropertiesEbook;

    @Inject
    public CotubaService(RenderMarkdown renderMarkdown, ReaderPropertiesEbook readerPropertiesEbook) {
        this.renderMarkdown = renderMarkdown;
        this.readerPropertiesEbook = readerPropertiesEbook;
    }

    public void executar(OptionsCLIDTO optionsCLI) {
        List<Chapter> chapters = renderMarkdown.render(optionsCLI.directoryMd());

        Ebook ebook = new Ebook();
        readerPropertiesEbook.read(optionsCLI.directoryMd(), ebook);

        ebook.setChapters(chapters);
        ebook.setOutputFile(optionsCLI.outputFile());
        ebook.setFormat(optionsCLI.fileFormat());

        GeneratorEbook generatorEbook = null;

        if (EbookFormat.PDF.equals(ebook.getFormat())) {
            generatorEbook = new GeneratorPdf();

        } else if (EbookFormat.EPUB.equals(ebook.getFormat())) {
            generatorEbook = new GeneratorEpub();
        } else {
            throw new IllegalArgumentException("Formato do Ebook inválido: " + optionsCLI.fileFormat());
        }

        generatorEbook.generate(ebook);

    }

}
