package br.com.unipds;

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
            List<String> htmls = renderMarkdown.render(optionsCLI.directoryMd());

            if ("pdf".equals(optionsCLI.fileFormat())) {
                GeneratePDF pdf = new GeneratePDF();
                pdf.generate(htmls, optionsCLI.outputFile());

            } else if ("epub".equals(optionsCLI.fileFormat())) {
                GenerateEpub epub = new GenerateEpub();
                epub.generate(htmls, optionsCLI.outputFile());

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