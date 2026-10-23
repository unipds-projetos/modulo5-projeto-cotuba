package br.com.unipds.cotuba.ports.out;

import br.com.unipds.cotuba.dto.PropriedadesEbook;

import java.nio.file.Path;

public interface LeitorPropriedadesEbook {
    PropriedadesEbook ler(Path diretorioMD);
}
