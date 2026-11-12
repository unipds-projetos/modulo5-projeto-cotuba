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
    private final MarkdownRepository markdownRepository;

    @Inject
    public CotubaService(RenderMarkdown renderMarkdown, ReaderPropertiesEbook readerPropertiesEbook, MarkdownRepository markdownRepository) {
        this.renderMarkdown = renderMarkdown;
        this.readerPropertiesEbook = readerPropertiesEbook;
        this.markdownRepository = markdownRepository;
    }

    public void executar(OptionsCLIDTO optionsCLI) {
        List<Chapter> chapters = markdownRepository.get(optionsCLI.directoryMd());
        renderMarkdown.render(chapters);

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
