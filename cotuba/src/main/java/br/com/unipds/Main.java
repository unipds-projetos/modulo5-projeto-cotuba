package br.com.unipds;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import br.com.unipds.cli.ReadInputCLI;
import br.com.unipds.services.GenerateEpub;
import br.com.unipds.services.GeneratePDF;
import br.com.unipds.services.RenderMarkdown;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import nl.siegmann.epublib.domain.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.properties.AreaBreakType;

import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

public class Main {

    void main(String[] args) {
        int exitCode = executar(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    boolean verbose = true;

    int executar(String[] args) {
        try {
            ReadInputCLI readInputCLI = new ReadInputCLI();
            readInputCLI.read(args);

            verbose = readInputCLI.isVerbose();

            RenderMarkdown renderMarkdown = new RenderMarkdown();
            List<String> htmls = renderMarkdown.render(readInputCLI.getDirectoryMD());

            if ("pdf".equals(readInputCLI.getFileFormat())) {
                GeneratePDF pdf = new GeneratePDF();
                pdf.generate(htmls, readInputCLI.getOutputFile());

            } else if ("epub".equals(readInputCLI.getFileFormat())) {
                GenerateEpub epub = new GenerateEpub();
                epub.generate(htmls, readInputCLI.getOutputFile());

            } else {
                throw new IllegalArgumentException("Formato do ebook inválido: " + readInputCLI.getFileFormat());
            }
            return 0;

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            if (verbose) {
                System.err.println();
                ex.printStackTrace();
            }
            return 1;
        }
    }

}