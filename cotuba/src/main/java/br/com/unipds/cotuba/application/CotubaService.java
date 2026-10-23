package br.com.unipds.cotuba.application;

import br.com.unipds.cotuba.domain.Capitulo;
import br.com.unipds.cotuba.domain.EbookBuilder;
import br.com.unipds.cotuba.domain.Markdown;
import br.com.unipds.cotuba.dto.ParametrosCotuba;
import br.com.unipds.cotuba.adapters.out.md.RenderizadorMarkdownCommonMark;
import br.com.unipds.cotuba.adapters.out.md.RepositorioMarkdownsDiretorio;
import br.com.unipds.cotuba.plugin.CotubaPlugin;
import br.com.unipds.cotuba.ports.in.CotubaUseCase;
import br.com.unipds.cotuba.ports.out.GeradorEbook;
import br.com.unipds.cotuba.ports.out.LeitorPropriedadesEbook;
import br.com.unipds.cotuba.support.FormatoEbookFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jmolecules.ddd.annotation.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;

@Service
@ApplicationScoped
public class CotubaService implements CotubaUseCase {

    private final RenderizadorMarkdownCommonMark renderizadorMarkdown;
    private final LeitorPropriedadesEbook leitorPropriedadesEbook;
    private final RepositorioMarkdownsDiretorio repositorioMarkdownsDiretorio;
    private final Instance<GeradorEbook> geradororesEbook;

    @Inject
    public CotubaService(RenderizadorMarkdownCommonMark renderizadorMarkdown, LeitorPropriedadesEbook leitorPropriedadesEbook, RepositorioMarkdownsDiretorio repositorioMarkdownsDiretorio,
                         @Any Instance<GeradorEbook> geradororesEbook) {
        this.renderizadorMarkdown = renderizadorMarkdown;
        this.leitorPropriedadesEbook = leitorPropriedadesEbook;
        this.repositorioMarkdownsDiretorio = repositorioMarkdownsDiretorio;
        this.geradororesEbook = geradororesEbook;
    }

    public void executar(ParametrosCotuba par){
        Path diretorioMD = par.diretorioMD();

        List<Markdown> markdowns = repositorioMarkdownsDiretorio.buscar(diretorioMD);

        List<Capitulo> capitulos =  renderizadorMarkdown.renderizar(markdowns);

        var propriedadeEbook = leitorPropriedadesEbook.ler(par.diretorioMD());

        var ebook = EbookBuilder.builder()
                .titulo(propriedadeEbook.titulo())
                .autor(propriedadeEbook.autor())
                .capitulos(capitulos)
                .formato(par.formato())
                .build();


        GeradorEbook geradorEbook = geradororesEbook.select(FormatoEbookFilter.of(ebook.formato())).get();

        geradorEbook.gerar(ebook, par.arquivoSaida());

        for(CotubaPlugin plugin: ServiceLoader.load(CotubaPlugin.class)){
            plugin.aposGeracao(ebook);
        }

    }

}
