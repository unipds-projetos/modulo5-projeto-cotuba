package br.com.unipds;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

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
        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            ReadInputCLI readInputCLI = container.select(ReadInputCLI.class).get();
            CotubaParams optionsCLI = readInputCLI.read(args);

            verbose = optionsCLI.verbose();

            CotubaService cotubaService = container.select(CotubaService.class).get();
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