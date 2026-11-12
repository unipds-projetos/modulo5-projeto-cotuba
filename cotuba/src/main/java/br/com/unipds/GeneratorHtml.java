package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookFormat;
import br.com.unipds.ebook.chapter.Chapter;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@EbookFormatQualifier(EbookFormat.HTML)
@ApplicationScoped
public class GeneratorHtml implements GeneratorEbook {

    @Override
    public void generate(Ebook ebook) {
         Path fileOutput = ebook.getOutputFile();
        try {
            Path directoryHtml = Files.createDirectory(fileOutput);

            int i = 1;
            for (Chapter chapter : ebook.getChapters()) {
                String nameFileHtml = getNameFileHtml(i, chapter);
                System.out.println(nameFileHtml);
                i++;
            }

        } catch (IOException e) {
            throw new IllegalStateException("Erro ao criar diretório: " + fileOutput, e);
        }
    }

    private String getNameFileHtml(int i, Chapter chapter) {
        String cleanTitle = chapter.getTitle().toLowerCase().replaceAll("\\W", "");
        return "%02d-%s.html".formatted(i, cleanTitle);
    }

}
