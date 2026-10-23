package br.com.unipds.cotuba.adapters.out.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import br.com.unipds.cotuba.dto.PropriedadesEbook;
import br.com.unipds.cotuba.ports.out.LeitorPropriedadesEbook;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LeitorPropriedadesEbookArquivo implements LeitorPropriedadesEbook {

    private static String validarPropriedades(String valor, String propriedade) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Propriedade inválida:" + propriedade);
        }
        return valor;
    }

    @Override
    public PropriedadesEbook ler(Path diretorioMD) {
        Path arquivoProperties = diretorioMD.resolve("ebook.properties");

        if (!Files.exists(arquivoProperties)) {
            throw new IllegalStateException(("Arquivo ebook.properties não encontrado em " + diretorioMD));
        }
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(arquivoProperties)) {
            properties.load(inputStream);
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao ler arquivo ebook.properties", ex);
        }
        String propriedadeTitulo = "cotuba.ebook.titulo";

        String titulo = properties.getProperty(propriedadeTitulo);
        validarPropriedades(titulo, propriedadeTitulo);
        String propriedadeAutor = "cotuba.ebook.autor";

        String autor = properties.getProperty(propriedadeAutor);
        validarPropriedades(autor, propriedadeAutor);
        return new PropriedadesEbook(titulo, autor);
    }

}
