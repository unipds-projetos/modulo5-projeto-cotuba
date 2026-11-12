package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookFormat;
import br.com.unipds.ebook.chapter.Chapter;

import java.util.List;

public class CotubaService {

    public void executar(OptionsCLIDTO optionsCLI) {
        RenderMarkdown renderMarkdownCommonmark = new RenderMarkdownCommonmark();
        List<Chapter> chapters = renderMarkdownCommonmark.render(optionsCLI.directoryMd());

        Ebook ebook = new Ebook();

        ReaderPropertiesEbook readerPropertiesEbookFile = new ReaderPropertiesEbookFile();
        readerPropertiesEbookFile.read(optionsCLI.directoryMd(), ebook);

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
