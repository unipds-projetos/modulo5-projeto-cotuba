package br.com.unipds;

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

            if (EbookFormat.PDF.equals(optionsCLI.fileFormat())) {
                GeneratePDF pdf = new GeneratePDF();
                pdf.generate(chapters, optionsCLI.outputFile());

            } else if (EbookFormat.EPUB.equals(optionsCLI.fileFormat())) {
                GenerateEpub epub = new GenerateEpub();
                epub.generate(chapters, optionsCLI.outputFile());

            } else {
                throw new IllegalArgumentException("Formato do ebook inválido: " + optionsCLI.fileFormat());
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