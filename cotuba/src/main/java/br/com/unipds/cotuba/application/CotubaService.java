package br.com.unipds.cotuba.application;

import br.com.unipds.cotuba.addapters.out.md.MarkdownRepositoryDirectory;
import br.com.unipds.cotuba.domain.Chapter;
import br.com.unipds.cotuba.domain.Ebook;
import br.com.unipds.cotuba.domain.EbookBuilder;
import br.com.unipds.cotuba.domain.Markdown;
import br.com.unipds.cotuba.dto.CotubaParams;
import br.com.unipds.cotuba.dto.EbookProperties;
import br.com.unipds.cotuba.plugin.CotubaPluginAfterGenerate;
import br.com.unipds.cotuba.ports.in.CotubaUseCase;
import br.com.unipds.cotuba.ports.out.GeneratorEbook;
import br.com.unipds.cotuba.ports.out.MarkdownRepository;
import br.com.unipds.cotuba.ports.out.ReaderPropertiesEbook;
import br.com.unipds.cotuba.ports.out.RenderMarkdown;
import br.com.unipds.cotuba.support.EbookFormatFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jmolecules.ddd.annotation.Service;

import java.util.List;
import java.util.ServiceLoader;

@Service
@ApplicationScoped
public class CotubaService implements CotubaUseCase {

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

    @Override
    public void executar(CotubaParams optionsCLI) {
        List<Markdown> markdowns = markdownRepository.get(optionsCLI.directoryMd());
        List<Chapter> chapters = renderMarkdown.render(markdowns);

        EbookProperties ebookProperties = readerPropertiesEbook.read(optionsCLI.directoryMd());

        Ebook ebook = EbookBuilder.builder()
                        .chapters(chapters)
                        .format(optionsCLI.fileFormat())
                        .title(ebookProperties.title())
                        .author(ebookProperties.author())
                        .build();

        GeneratorEbook generatorEbook = generatorEbooks.select(EbookFormatFilter.of(optionsCLI.fileFormat())).get();
        generatorEbook.generate(ebook, optionsCLI.outputFile());

        for (CotubaPluginAfterGenerate plugin : ServiceLoader.load(CotubaPluginAfterGenerate.class)) {
            plugin.afterGenerate(ebook);
        }

    }

}
