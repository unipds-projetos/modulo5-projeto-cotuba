package br.com.unipds;

import br.com.unipds.ebook.Ebook;

import java.nio.file.Path;

public interface ReaderPropertiesEbook {
    void read(Path directoryMd, Ebook ebook);
}
