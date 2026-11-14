package br.com.unipds;

import br.com.unipds.ebook.Ebook;

import java.nio.file.Path;

public interface GeneratorEbook {
    void generate(Ebook ebook, Path outputFile);
}
