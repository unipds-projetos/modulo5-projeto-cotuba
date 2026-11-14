package br.com.unipds;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

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
