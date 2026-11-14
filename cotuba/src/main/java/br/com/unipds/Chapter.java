package br.com.unipds;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
@RecordBuilder
public record Chapter(
        @Identity
        String title,
        Markdown markdown,
        String html
) {
}
