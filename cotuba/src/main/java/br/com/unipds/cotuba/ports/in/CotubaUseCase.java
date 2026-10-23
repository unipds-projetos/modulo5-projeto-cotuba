package br.com.unipds.cotuba.ports.in;

import br.com.unipds.cotuba.dto.ParametrosCotuba;

public interface CotubaUseCase {
    void executar(ParametrosCotuba parametrosCotuba);
}
