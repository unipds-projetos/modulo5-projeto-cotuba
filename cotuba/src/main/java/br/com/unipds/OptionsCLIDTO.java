package br.com.unipds;

import br.com.unipds.ebook.EbookFormat;

import java.nio.file.Path;

public record OptionsCLIDTO(Path directoryMd, Path outputFile, EbookFormat fileFormat, boolean verbose) {
}
