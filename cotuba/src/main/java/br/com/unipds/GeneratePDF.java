package br.com.unipds;

import br.com.unipds.ebook.chapter.Chapter;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.properties.AreaBreakType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GeneratePDF {

    public void generate(List<Chapter> htmls, Path outputFile) {
        try (var writer = new PdfWriter(Files.newOutputStream(outputFile));
             var pdf = new PdfDocument(writer);
             var pdfDocument = new Document(pdf)) {

            //TODO: definir título e autor para o livro
            pdf.getDocumentInfo().setTitle("Livro");
            pdf.getDocumentInfo().setAuthor("Autor");

            htmls.forEach(chapter -> {
                List<IElement> convertToElements = HtmlConverter.convertToElements(chapter.getHtml());

                if (pdf.getNumberOfPages() == 0) {
                    pdf.addNewPage();
                }
                PdfOutline rootOutline = pdf.getOutlines(false);
                if (rootOutline == null) {
                    pdf.initializeOutlines();
                    rootOutline = pdf.getOutlines(false);
                }

                PdfOutline chapterOutline = rootOutline.addOutline(chapter.getTitle());
                chapterOutline.addDestination(PdfExplicitDestination.createFit(pdf.getLastPage()));

                for (IElement element : convertToElements) {
                    pdfDocument.add((IBlockElement) element);
                }
                // TODO: não adicionar página depois do último capítulo
                pdfDocument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            });

            System.out.println("Arquivo gerado com sucesso: " + outputFile);

        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar PDF: " + outputFile.toAbsolutePath(), ex);
        }
    }

}
