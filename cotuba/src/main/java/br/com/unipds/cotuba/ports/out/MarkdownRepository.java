package br.com.unipds.cotuba.ports.out;

import br.com.unipds.cotuba.domain.Markdown;
import org.jmolecules.ddd.annotation.Repository;

import java.nio.file.Path;
import java.util.List;

@Repository
public interface MarkdownRepository {
    List<Markdown> get(Path directoryMd);
}
