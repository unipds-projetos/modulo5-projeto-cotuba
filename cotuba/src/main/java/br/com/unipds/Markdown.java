package br.com.unipds;

import java.nio.file.Path;

public record Markdown(
        String content,
        Path file
) {
}
