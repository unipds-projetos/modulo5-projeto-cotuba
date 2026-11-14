package br.com.unipds;

import java.nio.file.Path;

public interface ReaderPropertiesEbook {
    EbookProperties read(Path directoryMd);
}
