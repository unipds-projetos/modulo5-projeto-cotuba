package br.com.unipds;

public class Main {

    void main(String[] args) {
        int exitCode = executar(args);
        if (exitCode != 0) {
            System.out.println("Exit Code: " + exitCode);
            System.exit(exitCode);
        }
    }

    boolean verbose = true;

    int executar(String[] args) {
        try {
            ReadInputCLI readInputCLI = new ReadInputCLI();
            OptionsCLIDTO optionsCLI = readInputCLI.read(args);

            verbose = optionsCLI.verbose();

            CotubaService cotubaService = new CotubaService();
            cotubaService.executar(optionsCLI);

            System.out.println("Arquivo gerado com sucesso: " + optionsCLI.outputFile());

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