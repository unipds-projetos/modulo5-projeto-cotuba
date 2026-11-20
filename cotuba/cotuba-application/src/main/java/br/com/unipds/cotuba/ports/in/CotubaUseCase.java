package br.com.unipds.cotuba.ports.in;

import br.com.unipds.cotuba.dto.CotubaParams;

public interface CotubaUseCase {
    void executar(CotubaParams optionsCLI);
}
