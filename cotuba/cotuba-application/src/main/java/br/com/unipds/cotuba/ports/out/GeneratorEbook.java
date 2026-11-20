package br.com.unipds.cotuba.ports.out;

import br.com.unipds.cotuba.domain.Ebook;

import java.nio.file.Path;

public interface GeneratorEbook {
    void generate(Ebook ebook, Path outputFile);
}
