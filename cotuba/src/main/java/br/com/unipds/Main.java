package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookFormat;
import br.com.unipds.ebook.chapter.Chapter;

import java.util.List;

public class Main {

    void main(String[] args) {
        int exitCode = executar(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    boolean verbose = true;

    int executar(String[] args) {
        try {
            ReadInputCLI readInputCLI = new ReadInputCLI();
            OptionsCLI optionsCLI = readInputCLI.read(args);

            verbose = optionsCLI.verbose();

            RenderMarkdown renderMarkdown = new RenderMarkdown();
            List<Chapter> chapters = renderMarkdown.render(optionsCLI.directoryMd());

            Ebook ebook = new Ebook();
            ebook.setTitle("Teste");
            ebook.setAuthor("Teste Autor");
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

            return 0;

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            if (verbose) {
                System.err.println();
                ex.printStackTrace();
            }
            return 1;
        }
    }

}