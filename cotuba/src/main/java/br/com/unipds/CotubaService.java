package br.com.unipds;

import br.com.unipds.ebook.Ebook;
import br.com.unipds.ebook.EbookBuilder;
import br.com.unipds.ebook.chapter.Chapter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CotubaService {

    private final RenderMarkdown renderMarkdown;
    private final ReaderPropertiesEbook readerPropertiesEbook;
    private final MarkdownRepository markdownRepository;
    private final Instance<GeneratorEbook> generatorEbooks;

    @Inject
    public CotubaService(RenderMarkdown renderMarkdown, ReaderPropertiesEbook readerPropertiesEbook, MarkdownRepositoryDirectory markdownRepository, @Any Instance<GeneratorEbook> generatorEbooks) {
        this.renderMarkdown = renderMarkdown;
        this.readerPropertiesEbook = readerPropertiesEbook;
        this.markdownRepository = markdownRepository;
        this.generatorEbooks = generatorEbooks;
    }

    public void executar(CotubaParams optionsCLI) {
        List<Markdown> markdowns = markdownRepository.get(optionsCLI.directoryMd());
        List<Chapter> chapters = renderMarkdown.render(markdowns);

        EbookProperties ebookProperties = readerPropertiesEbook.read(optionsCLI.directoryMd());

        Ebook ebook = EbookBuilder.builder()
                        .chapters(chapters)
                        .format(optionsCLI.fileFormat())
                        .outputFile(optionsCLI.outputFile())
                        .title(ebookProperties.title())
                        .author(ebookProperties.author())
                        .build();

        GeneratorEbook generatorEbook = generatorEbooks.select(EbookFormatFilter.of(optionsCLI.fileFormat())).get();
        generatorEbook.generate(ebook);
    }

}
