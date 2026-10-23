package br.com.unipds.cotuba.adapters.in.cli;

import br.com.unipds.cotuba.dto.ParametrosCotuba;
import jakarta.enterprise.inject.se.SeContainer;
import br.com.unipds.cotuba.ports.in.CotubaUseCase;
import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(String[] args) {
        int exitCode = new Main().executar(args);

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    public int executar(String[] args) {


        boolean modoVerboso = true;

        try(SeContainer container = SeContainerInitializer.newInstance().initialize()){
            LeitorOpcoesCLI leitorOpcoesCLI =container.select(LeitorOpcoesCLI.class).get();
            ParametrosCotuba parametrosCotuba = leitorOpcoesCLI.ler(args);

            modoVerboso = leitorOpcoesCLI.isModoVerboso();
            CotubaUseCase cotubaUseCase = container.select(CotubaUseCase.class).get();
            cotubaUseCase.executar(parametrosCotuba);
            System.out.println("Arquivo gerado com sucesso: " + parametrosCotuba.arquivoSaida());
            return 0;

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            if (modoVerboso) {
                System.err.println();
                ex.printStackTrace(System.err);
            }
            return 1;
        }
    }
}