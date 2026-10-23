package br.com.unipds.cotuba.ports.out;

import br.com.unipds.cotuba.domain.Capitulo;
import br.com.unipds.cotuba.domain.Markdown;

import java.util.List;


public interface RenderizadorMarkdown {
        List<Capitulo> renderizar(List<Markdown> markdowns);

}

