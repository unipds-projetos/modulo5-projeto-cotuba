package br.com.unipds.cotuba.adapters.in.cli;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MainIntegrationTest {

    @TempDir
    Path diretorioDosMd;

    private Path arquivoMd;

    // Utilitários para capturar o System.err nativamente
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() throws Exception {
        // Redireciona a saída de erro para conseguirmos validar nos testes
        System.setErr(new PrintStream(errContent));

        // Cria o arquivo de teste
        arquivoMd = diretorioDosMd.resolve("01-introducao.md");
        Files.writeString(arquivoMd, "# Capítulo Teste\n\nEste é um conteúdo de um arquivo Markdown.");

        Path arquivoProperties = diretorioDosMd.resolve("ebook.properties");
        Files.writeString(arquivoProperties, """
                cotuba.ebook.titulo=Apostila de Design
                cotuba.ebook.autor=UNIPDS
                """);

    }

    @AfterEach
    void tearDown() {
        // Restaura a saída padrão de erro do Java
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Deve gerar o arquivo PDF corretamente e conter o texto do Markdown.")
    void deveGerarPdfComSucesso() throws Exception {
        Path arquivoSaida = diretorioDosMd.resolve("saida.pdf");

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "pdf",
                "-o", arquivoSaida.toString()
        });

        assertThat(exitCode).isEqualTo(0);
        assertThat(arquivoSaida).exists().isRegularFile();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(arquivoSaida.toFile()))) {
            String textoDaPagina = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
            assertThat(textoDaPagina)
                    .contains("Capítulo Teste")
                    .contains("Este é um conteúdo de um arquivo Markdown.");
        }
    }

    @Test
    @DisplayName("Deve gerar o arquivo EPUB corretamente e conter o HTML renderizado")
    void deveGerarEpubComSucesso() throws Exception {
        Path arquivoSaida = diretorioDosMd.resolve("saida.epub");

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "epub",
                "-o", arquivoSaida.toString()
        });

        assertThat(exitCode).isEqualTo(0);
        assertThat(arquivoSaida).exists().isRegularFile();

        EpubReader epubReader = new EpubReader();
        Book epubLido = epubReader.readEpub(Files.newInputStream(arquivoSaida));

        byte[] dadosDoHtml = epubLido.getSpine().getResource(0).getData();
        String htmlDoCapitulo = new String(dadosDoHtml);

        assertThat(htmlDoCapitulo)
                .contains("<h1>Capítulo Teste</h1>")
                .contains("<p>Este é um conteúdo de um arquivo Markdown.</p>");
    }

    @Test
    @DisplayName("Deve retornar status 1 e exibir erro caso o formato seja inválido")
    void deveFalharEEncerrarQuandoFormatoEhInvalido() {
        Path arquivoSaida = diretorioDosMd.resolve("saida.mobi");

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "mobi",
                "-o", arquivoSaida.toString()
        });

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Formato do ebook inválido: MOBI");
        assertThat(arquivoSaida).doesNotExist();
    }

    @Test
    @DisplayName("Deve retornar status 1 e exibir erro caso diretório não tenha arquivos .md")
    void deveFalharEEncerrarQuandoNaoHaArquivosMd() throws Exception {
        Files.deleteIfExists(arquivoMd); // Deleta o arquivo para simular diretório vazio
        Path arquivoSaida = diretorioDosMd.resolve("saida.pdf");

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "pdf",
                "-o", arquivoSaida.toString(),
                "-v"
        });

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Não foram encontrados capítulos");
    }

    @Test
    @DisplayName("Deve acionar a ajuda do CLI e encerrar em caso de argumento desconhecido")
    void deveAcionarAjudaEEncerrarAoPassarArgumentoInvalido() {
        int exitCode = new Main().executar(new String[]{
                "-x"
        });

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Unrecognized option: -x");
    }
}