package br.com.unipds;

import java.nio.file.Path;

public interface GeneratorEbook {
    void generate(Ebook ebook, Path outputFile);
}
