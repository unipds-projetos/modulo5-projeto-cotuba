package br.com.unipds.cotuba.ports.out;

import br.com.unipds.cotuba.dto.EbookProperties;

import java.nio.file.Path;

public interface ReaderPropertiesEbook {
    EbookProperties read(Path directoryMd);
}
