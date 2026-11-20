package br.com.unipds.cotuba.ports.out;

import br.com.unipds.cotuba.domain.Chapter;
import br.com.unipds.cotuba.domain.Markdown;

import java.util.List;

public interface RenderMarkdown {
    List<Chapter> render(List<Markdown> markdowns);
}
