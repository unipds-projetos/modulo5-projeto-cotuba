package br.com.unipds.cotuba.adapters.out.md;

import br.com.unipds.cotuba.domain.Markdown;
import br.com.unipds.cotuba.ports.out.RepositorioMarkdowns;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class RepositorioMarkdownsDiretorio implements RepositorioMarkdowns {

    /*public static Capitulo capitulo(Path arquivoMD) {
        try {
            var capitulo = new Capitulo();
            String markdown = Files.readString(arquivoMD);
            capitulo.setMarkdown(markdown);
            capitulo.setArquivoMarkdown(arquivoMD);
            return capitulo;
        }catch(IOException ex){
            throw new IllegalStateException("Erro ao ler o arquivo " + arquivoMD.toAbsolutePath(), ex);
        }
    }*/

    public List<Markdown> buscar(Path diretorioMD){
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");
        try (Stream<Path> streamMDs = Files.list(diretorioMD)) {
            List<Path> arquivosMD = streamMDs
                    .filter(matcher::matches)
                    .sorted()
                    .toList();
            if (arquivosMD.isEmpty()) {
                throw new IllegalStateException("Não foram encontrados capítulos (arquivos .md) no diretório: " + diretorioMD.toAbsolutePath());
            }

            return arquivosMD.stream()
                    .map(arquivoMD -> {
                        try {
                            String conteudo = Files.readString(arquivoMD);
                            String nome = arquivoMD.getFileName().toString();
                            return new Markdown(nome,conteudo);
                        } catch (IOException ex) {
                            throw new IllegalStateException("Erro ao ler o arquivo: " + arquivoMD.toAbsolutePath(), ex);
                        }
                    })
                    .toList();
        }catch(IOException ex){
            throw new IllegalStateException("Erro tentando encontrar arquivos .md");
        }

    }
}
