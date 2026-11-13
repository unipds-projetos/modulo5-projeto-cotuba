package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookBuilder;

import java.nio.file.Path;

public interface ReaderPropertiesEbook {
    EbookProperties read(Path directoryMd);
}
