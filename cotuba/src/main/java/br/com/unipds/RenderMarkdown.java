package br.com.unipds;

import java.util.List;

public interface RenderMarkdown {
    List<Chapter> render(List<Markdown> markdowns);
}
