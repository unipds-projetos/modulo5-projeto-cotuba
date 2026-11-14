package br.com.unipds.ebook;

import br.com.unipds.ebook.chapter.Chapter;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.nio.file.Path;
import java.util.List;

@AggregateRoot
@RecordBuilder
public record Ebook(
        @Identity
        String title,
        String author,
        EbookFormat format,
        List<Chapter> chapters
) {
}
