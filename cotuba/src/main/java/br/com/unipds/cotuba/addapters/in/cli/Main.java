package br.com.unipds.cotuba.addapters.in.cli;

import br.com.unipds.cotuba.dto.CotubaParams;
import br.com.unipds.cotuba.ports.in.CotubaUseCase;
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

    public int executar(String[] args) {
        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            ReadInputCLI readInputCLI = container.select(ReadInputCLI.class).get();
            CotubaParams optionsCLI = readInputCLI.read(args);

            verbose = optionsCLI.verbose();

            CotubaUseCase cotubaUseCase = container.select(CotubaUseCase.class).get();
            cotubaUseCase.executar(optionsCLI);

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