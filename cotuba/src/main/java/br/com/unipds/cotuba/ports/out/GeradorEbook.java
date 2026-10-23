package br.com.unipds.cotuba.ports.out;

import br.com.unipds.cotuba.domain.Ebook;

import java.nio.file.Path;

public interface GeradorEbook {
    void gerar(Ebook ebook, Path arquivoDeSaida);
}
