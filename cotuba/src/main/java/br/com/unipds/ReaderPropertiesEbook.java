package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookBuilder;

import java.nio.file.Path;

public interface ReaderPropertiesEbook {
    void read(Path directoryMd, EbookBuilder ebookBuilder);
}
