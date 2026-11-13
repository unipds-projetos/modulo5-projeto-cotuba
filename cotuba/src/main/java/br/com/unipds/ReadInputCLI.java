package br.com.unipds;

import br.com.unipds.ebook.EbookFormat;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Locale;

@ApplicationScoped
public class ReadInputCLI {

    public CotubaParams read(String[] args) {
        var options = new Options();

        try {
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
            CommandLine cmd = cmdParser.parse(options, args);

            String nameDirectoryMD = cmd.getOptionValue("dir");

            Path directoryMD;
            if (nameDirectoryMD != null) {
                directoryMD = Paths.get(nameDirectoryMD);
                if (!Files.isDirectory(directoryMD)) {
                    throw new IllegalArgumentException(nameDirectoryMD + " não é um diretório.");
                }
            } else {
                directoryMD = Paths.get("");
            }

            EbookFormat ebookFormat;
            String fileFormatEbook = cmd.getOptionValue("format");

            try {
                if (fileFormatEbook != null) {
                    ebookFormat = EbookFormat.valueOf(fileFormatEbook.toUpperCase());
                } else {
                    ebookFormat = EbookFormat.PDF;
                }
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Formato do Ebook inválido: ." + fileFormatEbook.toLowerCase());
            }

            Path outputEbook;
            try {
                String nameOutputFile = cmd.getOptionValue("output");
                if (nameOutputFile != null) {
                    outputEbook = Paths.get(nameOutputFile);
                } else {
                    outputEbook = Paths.get("book." + ebookFormat.name().toLowerCase());
                }
                if (Files.isDirectory(outputEbook)) {
                    // deleta arquivos do diretório recursivamente
                    Files.walk(outputEbook).sorted(Comparator.reverseOrder())
                            .map(Path::toFile).forEach(File::delete);
                } else {
                    Files.deleteIfExists(outputEbook);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            boolean verbose = cmd.hasOption("verbose");

            return new CotubaParams(directoryMD, outputEbook, ebookFormat, verbose);

        } catch (ParseException e) {
            System.err.println(e.getMessage());

            HelpFormatter help = new HelpFormatter();
            help.printHelp("cotuba", options);

            throw new IllegalStateException();
        }
    }

}
