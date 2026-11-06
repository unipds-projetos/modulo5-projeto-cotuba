package br.com.unipds.cli;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ReadInputCLI {

    private CommandLine cmd;

    public void read(String[] args) {
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

        try {
            cmd = cmdParser.parse(options, args);

        } catch (ParseException e) {
            System.err.println(e.getMessage());

            HelpFormatter help = new HelpFormatter();
            help.printHelp("cotuba", options);

            throw new IllegalStateException();
        }

    }

    public Path getDirectoryMD() {
        String nameDirectoryMD = cmd.getOptionValue("dir");

        if (nameDirectoryMD != null) {
            Path directoryMD = Paths.get(nameDirectoryMD);
            if (!Files.isDirectory(directoryMD)) {
                throw new IllegalArgumentException(nameDirectoryMD + " não é um diretório.");
            }

            return directoryMD;
        }

        return Paths.get("");
    }

    public String getFormat() {
        String nameFormatEbook = cmd.getOptionValue("format");

        if (nameFormatEbook != null) {
            return nameFormatEbook.toLowerCase();
        }

        return "pdf";
    }

    public Path getOutputFile() throws IOException {
        String nameOutputFileEbook = cmd.getOptionValue("output");
        Path outputFile;

        if (nameOutputFileEbook != null) {
            outputFile = Paths.get(nameOutputFileEbook);
        } else {
            outputFile = Paths.get("book." + getFormat().toLowerCase());
        }

        if (Files.isDirectory(outputFile)) {
            // deleta arquivos do diretório recursivamente
            Files.walk(outputFile).sorted(Comparator.reverseOrder())
                    .map(Path::toFile).forEach(File::delete);
        } else {
            Files.deleteIfExists(outputFile);
        }

        return outputFile;
    }

    public boolean isVerbose() {
        return cmd.hasOption("verbose");
    }

}
