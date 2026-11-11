package br.com.unipds;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ReadInputCLI {

    private Path directoryMD;
    private String fileFormat;
    private Path outputFile;
    private boolean verbose;

    public OptionsCLI read(String[] args) {
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

            if (nameDirectoryMD != null) {
                directoryMD = Paths.get(nameDirectoryMD);
                if (!Files.isDirectory(directoryMD)) {
                    throw new IllegalArgumentException(nameDirectoryMD + " não é um diretório.");
                }
            } else {
                directoryMD = Paths.get("");
            }

            String fileFormatEbook = cmd.getOptionValue("format");

            if (fileFormatEbook != null) {
                fileFormat = fileFormatEbook.toLowerCase();
            } else {
                fileFormat = "pdf";
            }

            try {
                String nameOutputFile = cmd.getOptionValue("output");
                if (nameOutputFile != null) {
                    outputFile = Paths.get(nameOutputFile);
                } else {
                    outputFile = Paths.get("book." + fileFormat.toLowerCase());
                }
                if (Files.isDirectory(outputFile)) {
                    // deleta arquivos do diretório recursivamente
                    Files.walk(outputFile).sorted(Comparator.reverseOrder())
                            .map(Path::toFile).forEach(File::delete);
                } else {
                    Files.deleteIfExists(outputFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            verbose = cmd.hasOption("verbose");

            return new OptionsCLI(directoryMD, outputFile, fileFormat, verbose);

        } catch (ParseException e) {
            System.err.println(e.getMessage());

            HelpFormatter help = new HelpFormatter();
            help.printHelp("cotuba", options);

            throw new IllegalStateException();
        }
    }

}
