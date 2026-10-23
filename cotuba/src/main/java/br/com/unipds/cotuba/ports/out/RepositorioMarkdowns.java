package br.com.unipds.cotuba.ports.out;

import br.com.unipds.cotuba.domain.Markdown;

import java.nio.file.Path;
import java.util.List;


public interface RepositorioMarkdowns {
    List<Markdown> buscar(Path diretorioMD);
}
