package br.com.unipds.cotuba.addapters.out.md;

import br.com.unipds.cotuba.domain.Markdown;
import br.com.unipds.cotuba.ports.out.MarkdownRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class MarkdownRepositoryDirectory implements MarkdownRepository {

    public List<Markdown> get(Path directoryMd) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");
        try (Stream<Path> streamMDs = Files.list(directoryMd)) {
            List<Path> filesMd = streamMDs
                    .filter(matcher::matches)
                    .sorted()
                    .toList();

            if (filesMd.isEmpty()) {
                    throw new IllegalStateException("Não foram encontrados capítulos (arquivos .md) no diretório: " + directoryMd.toAbsolutePath());
            }

            return filesMd.stream().map(fileMd -> {
                try {
                    String content = Files.readString(fileMd);
                    return new Markdown(fileMd.getFileName().toString(), content);

                } catch (IOException e) {
                    throw new IllegalStateException("Erro ao ler arquivo: " + fileMd);
                }
            }).toList();

        } catch (IOException e) {
            throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + directoryMd.toAbsolutePath());
        }
    }
}
