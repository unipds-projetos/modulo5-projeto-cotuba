package br.com.unipds;

import br.com.unipds.ebook.Ebook;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class LeitorPropriedadesEbook {

    public void ler(Path directoryMd, Ebook ebook) {

        Path fileProperties = directoryMd.resolve("ebook.properties");

        if (!Files.exists(fileProperties)) {
            throw new IllegalStateException("Arquivo ebook.properties não encontrado em: " + directoryMd);
        }

        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(fileProperties)) {
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler arquivo " + fileProperties);
        }

        String titlePropertie = "cotuba.ebook.titulo";
        validatePropertie(titlePropertie);
        String title = properties.getProperty(titlePropertie);

        String authorPropertie = "cotuba.ebook.autor";
        validatePropertie(authorPropertie);
        String author = properties.getProperty(authorPropertie);

        ebook.setTitle(title);
        ebook.setAuthor(author);

    }

    private void validatePropertie(String propertie) {
        if (propertie == null || propertie.isBlank()) {
            throw new IllegalArgumentException("Propriedade " + propertie + " Inválida!");
        }
    }

}
