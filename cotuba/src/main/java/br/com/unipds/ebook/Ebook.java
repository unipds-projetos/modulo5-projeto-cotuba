package br.com.unipds.ebook;

import br.com.unipds.ebook.chapter.Chapter;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.nio.file.Path;
import java.util.List;

@RecordBuilder
public record Ebook(
        String title,
        String author,
        EbookFormat format,
        List<Chapter> chapters,
        Path outputFile
) {
}
