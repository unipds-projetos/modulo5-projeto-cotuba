package br.com.unipds;

import java.nio.file.Path;

public record OptionsCLI(Path directoryMd, Path outputFile, String fileFormat, boolean verbose) {
}
