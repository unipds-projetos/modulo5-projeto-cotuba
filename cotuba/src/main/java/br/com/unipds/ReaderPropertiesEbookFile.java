package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookBuilder;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@ApplicationScoped
public class ReaderPropertiesEbookFile implements ReaderPropertiesEbook {

    @Override
    public EbookProperties read(Path directoryMd) {

        Path fileProperties = directoryMd.resolve("ebook.properties");

        if (!Files.exists(fileProperties)) {
            throw new IllegalStateException("Arquivo ebook.properties não encontrado em: " + directoryMd);
        }

        Properties properties = new Properties();

        try (Reader reader = Files.newBufferedReader(fileProperties, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler arquivo " + fileProperties);
        }

        String titlePropertie = "cotuba.ebook.titulo";
        validatePropertie(titlePropertie);
        String title = properties.getProperty(titlePropertie);

        String authorPropertie = "cotuba.ebook.autor";
        validatePropertie(authorPropertie);
        String author = properties.getProperty(authorPropertie);

        return new EbookProperties(title, author);
    }

    private void validatePropertie(String propertie) {
        if (propertie == null || propertie.isBlank()) {
            throw new IllegalArgumentException("Propriedade " + propertie + " Inválida!");
        }
    }

}
