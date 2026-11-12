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

        if (EbookFormat.PDF.equals(ebook.getFormat())) {
            GeneratePDF pdf = new GeneratePDF();
            pdf.generate(ebook);

        } else if (EbookFormat.EPUB.equals(ebook.getFormat())) {
            GenerateEpub epub = new GenerateEpub();
            epub.generate(ebook);
        }

    }

}
