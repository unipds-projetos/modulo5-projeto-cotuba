package br.com.unipds.cotuba.dto;

import br.com.unipds.cotuba.domain.FormatoEbook;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.nio.file.Path;

@RecordBuilder
public record ParametrosCotuba(Path diretorioMD, FormatoEbook formato, Path arquivoSaida, boolean modoVerboso) {

    public ParametrosCotuba(Path diretorioMD, FormatoEbook formato, Path arquivoSaida) {
        this(diretorioMD, formato, arquivoSaida, false);
    }
}
