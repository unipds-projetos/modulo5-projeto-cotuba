package br.com.unipds.cotuba.adapters.in.cli;

import br.com.unipds.cotuba.domain.FormatoEbook;
import br.com.unipds.cotuba.dto.ParametrosCotuba;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@ApplicationScoped
public class LeitorOpcoesCLI {

    private boolean modoVerboso = false;

    public ParametrosCotuba ler(String[] args){
        var options = new Options();

        var opcaoDeDiretorioDosMD = new Option("d", "dir", true,
                "Diretório que contém os arquivos md. Default: diretório atual.");
        options.addOption(opcaoDeDiretorioDosMD);

        var opcaoDeFormatoDoEbook = new Option("f", "format", true,
                "Formato de saída do ebook. Pode ser: pdf ou epub. Default: pdf");
        options.addOption(opcaoDeFormatoDoEbook);

        var opcaoDeArquivoDeSaida = new Option("o", "output", true,
                "Arquivo de saída do ebook. Default: book.{formato}.");
        options.addOption(opcaoDeArquivoDeSaida);

        var opcaoModoVerboso = new Option("v", "verbose", false,
                "Habilita modo verboso.");
        options.addOption(opcaoModoVerboso);

        CommandLineParser cmdParser = new DefaultParser();
        var ajuda = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = cmdParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            ajuda.printHelp("cotuba", options);
            throw new IllegalStateException(e);
        }


        try {

            Path diretorioDosMD;
            FormatoEbook formato;
            Path arquivoDeSaida;

            String nomeDoDiretorioDosMD = cmd.getOptionValue("dir");

            if (nomeDoDiretorioDosMD != null) {
                diretorioDosMD = Paths.get(nomeDoDiretorioDosMD);
                if (!Files.isDirectory(diretorioDosMD)) {
                    throw new IllegalArgumentException(nomeDoDiretorioDosMD + " não é um diretório.");
                }
            } else {
                diretorioDosMD= Paths.get("apostila-design");
            }

            String nomeDoFormatoDoEbook = cmd.getOptionValue("format");

            if (nomeDoFormatoDoEbook != null) {
                try {
                    formato = FormatoEbook.valueOf(nomeDoFormatoDoEbook.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Formato do ebook inválido: "+nomeDoFormatoDoEbook.toUpperCase());
                }
            } else {
                formato = FormatoEbook.PDF;
            }

            String nomeDoArquivoDeSaidaDoEbook = cmd.getOptionValue("output");
            if (nomeDoArquivoDeSaidaDoEbook != null) {
                arquivoDeSaida = Paths.get(nomeDoArquivoDeSaidaDoEbook);
            } else {
                arquivoDeSaida = Paths.get("book." + formato.name().toLowerCase());
            }
            if (Files.isDirectory(arquivoDeSaida)) {
                // deleta arquivos do diretório recursivamente
                Files.walk(arquivoDeSaida).sorted(Comparator.reverseOrder())
                        .map(Path::toFile).forEach(File::delete);
            } else {
                Files.deleteIfExists(arquivoDeSaida);
            }

            this.modoVerboso = cmd.hasOption("verbose");

            return new ParametrosCotuba(diretorioDosMD,formato,arquivoDeSaida,this.modoVerboso);

        }catch (Exception ex){
             throw new IllegalStateException(ex);
        }
    }


    public boolean isModoVerboso() {
        return modoVerboso;
    }

    public void setModoVerboso(boolean modoVerboso) {
        this.modoVerboso = modoVerboso;
    }
}
