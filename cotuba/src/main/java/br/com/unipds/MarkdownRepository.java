package br.com.unipds;

import br.com.unipds.ebook.chapter.Chapter;

import java.nio.file.Path;
import java.util.List;

public interface MarkdownRepository {
    List<Markdown> get(Path directoryMd);
}
