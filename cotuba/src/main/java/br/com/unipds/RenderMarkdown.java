package br.com.unipds;

import br.com.unipds.ebook.chapter.Chapter;

import java.util.List;

public interface RenderMarkdown {
    void render(List<Chapter> chapters);
}
