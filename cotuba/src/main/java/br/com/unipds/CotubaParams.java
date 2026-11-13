package br.com.unipds;

import br.com.unipds.ebook.EbookFormat;

import java.nio.file.Path;

public record CotubaParams(
        Path directoryMd,
        Path outputFile,
        EbookFormat fileFormat,
        boolean verbose
) {
    CotubaParams(Path directoryMd, Path outputFile, EbookFormat fileFormat) {
        this(directoryMd, outputFile, fileFormat, false);
    }
}
