package br.com.unipds.ebook.chapter;

import br.com.unipds.Markdown;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Chapter(
        String title,
        Markdown markdown,
        String html
) {
}
