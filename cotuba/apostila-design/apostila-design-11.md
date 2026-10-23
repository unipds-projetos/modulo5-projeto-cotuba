# Software Design & System Design: 11 - Visão Geral Sobre DDD

## 1. Dados sobre a Aula

*   **Título Alternativo:** Desmistificando o DDD: Do Domain Storytelling à Governança com JMolecules.
*   **Tópico Central:** Fundamentos do Domain-Driven Design (DDD), divididos entre a visão **Estratégica** (Subdomínios, Contextos Delimitados, Linguagem Ubíqua) e a visão **Tática** (Padrões de Código), culminando na automação da validação arquitetural.
*   **Problema de Design:** Como evitar o "Furacão Tático" (*Tactical Tornado*) — o erro fatal de aplicar padrões de código complexos (como Repositories e Aggregates) sem antes mapear e compreender os limites reais do domínio do negócio e sua linguagem. Além disso, como garantir que a arquitetura não se degrade com o tempo.



## 2. Passo a Passo Cronológico

**[03:59 - 07:18] O Maior Mal-Entendido da Indústria: O que NÃO é DDD**
*   **Conceito de Design:** O professor inicia quebrando mitos. DDD **não é** um framework, não é focado apenas em Java, não exige microsserviços por padrão e **não é apenas código**. O fato de você usar anotações do Spring Data/JPA (como `@Entity` e `@Repository`) não significa que você está fazendo DDD. DDD é uma metodologia para transferir o conhecimento do negócio para dentro do design do software. A principal recomendação de literatura moderna citada é *Learning Domain-Driven Design* (Vlad Khononov).

**[08:36 - 13:13] DDD Estratégico: Mapeamento de Domínios e Subdomínios**
*   **Conceito de Design:** Antes de escrever código, o Arquiteto deve fatiar o problema. Existem três tipos de subdomínios:
    *   **Core Domain:** O coração da empresa. É único, complexo e gera vantagem competitiva. (Ex: O algoritmo de roteamento do Uber). *É aqui que seus melhores engenheiros devem atuar e onde o DDD tático brilha.*
    *   **Generic Subdomain:** Processos comuns a qualquer empresa, alta complexidade, mas nenhuma diferenciação de negócio. (Ex: Autenticação, Pagamentos, Faturamento). *A regra é: Compre pronto (Off-the-shelf) ou integre APIs (Auth0, Stripe).*
    *   **Supporting Subdomain:** Apoia o negócio, mas é simples e não traz vantagem competitiva. (Ex: Um sistema interno de cadastro de crachás). *Crie internamente usando abordagens simples (CRUD clássico, RAD).*

**[13:14 - 15:40] Linguagem Ubíqua e Bounded Contexts (Contextos Delimitados)**
*   **Conceito de Design:** A **Linguagem Ubíqua** é um vocabulário comum e rigoroso criado entre Desenvolvedores e Especialistas de Domínio (Domain Experts). A mesma palavra muda de significado dependendo do **Bounded Context**.
    *   *Exemplo Prático:* A entidade `Customer` no contexto de "Vendas" (Sales) tem atributos como *Pipeline, Opportunity*. A mesma entidade `Customer` no contexto de "Suporte" tem atributos como *Tickets, Defect*. Tentar unificar tudo em uma única tabela gigantesca `Customer` no banco de dados gera acoplamento destrutivo (*God Class*). Separe-os fisicamente ou logicamente.

**[21:08 - 25:10] DDD Tático e o Risco do "Tactical Tornado"**
*   **Conceito de Design:** O DDD Tático foca na implementação: **Entities, Value Objects, Aggregates, Repositories, Factories e Events**. O professor alerta através de um gráfico crucial: focar apenas na tática (padrões de código) sem fazer o design estratégico gera um falso progresso rápido no início, mas com o tempo, o código se torna um "Furacão Tático" — insustentável, pois não reflete as fronteiras reais do negócio.

**[25:46 - 26:54] Governança Arquitetural como Código (JMolecules & ArchUnit)**
*   **Conceito de Design:** Como garantir que o time de desenvolvimento não quebre as regras do DDD e da Arquitetura (como uma camada de domínio acessando o banco de dados)? A resposta é validar a arquitetura em tempo de CI/CD utilizando testes de unidade com **ArchUnit** em conjunto com a biblioteca **jMolecules** (que fornece abstrações arquiteturais puras para Java).
*   **Implementação de Código Java (CRÍTICO - Governança):**

```java
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;

// 1. A classe de teste varre todos os pacotes do projeto
@AnalyzeClasses(packages = "expert.os.examples")
public class IntegrationSampleTest {

    // 2. Valida as regras Táticas do DDD
    @ArchTest
    private ArchRule dddRules = JMoleculesDddRules.all();

    // 3. Valida as regras de Camadas Arquiteturais (Onion, Layered, Clean)
    @ArchTest
    private ArchRule layering = JMoleculesArchitectureRules.ensureLayering();
}
```

*   **Explicação da Lógica de Design:** O código acima não é lógica de negócios, é **Governança Automatizada**. 
    *   Ao utilizar as anotações do `jMolecules` no código fonte da aplicação (ex: `@AggregateRoot`, `@Entity`, `@ValueObject`), o `JMoleculesDddRules.all()` vai garantir via testes que as regras do DDD estão sendo cumpridas (Ex: Um Value Object não pode sofrer mutação; Uma Entidade de fora não pode referenciar diretamente uma Entidade interna de outro Aggregate sem passar pelo Aggregate Root).
    *   O `JMoleculesArchitectureRules.ensureLayering()` garante que a Injeção de Dependências está correta: a camada de *Domain* jamais pode importar pacotes da camada de *Infrastructure* ou *Presentation*. Se um desenvolvedor fizer esse import errado, o teste quebra e o *Pull Request* é bloqueado.

*   *(Nota Adicional: Exemplo prático de como o código de produção se pareceria para que o teste acima funcione)*
```java
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;
import org.jmolecules.ddd.annotation.ValueObject;

@AggregateRoot // Metadado arquitetural (JMolecules)
public class Order {
    
    @Identity
    private OrderId id; // O ID em si é um tipo forte, não apenas um UUID primitivo
    
    private final Currency amount; // Imutável
    
    // ...
}

@ValueObject // Garante semântica de imutabilidade e igualdade baseada em valor
public record Currency(String code, BigDecimal value) {}
```

**[26:55 - 30:06] Estilos Arquiteturais Compatíveis com DDD**
*   **Conceito de Design:** DDD não dita como sua infraestrutura deve ser desenhada. Ele se encaixa no centro (Domain Layer) de várias topologias, como Arquitetura em Camadas (N-Tier), Clean Architecture, Hexagonal (Ports and Adapters) e CQRS.
*   **Decisão do Arquiteto:** O professor adverte sobre o uso prematuro da *Clean Architecture*: ela exige a criação de muitos *Interface Adapters*, gerando classes de *Use Cases* excessivas. Só pague o preço dessa complexidade se o seu *Core Domain* justificar.



## 3. Principais Conceitos

*   **Domain Storytelling:** Técnica colaborativa onde *Domain Experts* narram fluxos de trabalho do negócio enquanto os engenheiros desenham a interação graficamente (usando pictogramas e verbos). É o passo anterior para extrair as Entidades e a Linguagem Ubíqua.
*   **Ubiquitous Language (Linguagem Ubíqua):** O idioma falado de forma estrita no projeto. Termos de negócio devem ser refletidos de forma idêntica no código-fonte (nomes de classes e métodos). O fim da "tradução mental" entre o que o negócio pede e o que o desenvolvedor codifica.
*   **Bounded Context (Contexto Delimitado):** Uma fronteira explícita dentro da qual um modelo de domínio específico é válido e a Linguagem Ubíqua faz sentido. Ajuda a evitar a criação de Entidades "Deus" (God Classes) que tentam resolver tudo para toda a empresa.
*   **Context Mapping:** As estratégias de integração entre diferentes Bounded Contexts. (Ex: *Anti-Corruption Layer* para integrar com sistemas legados sem sujar o seu novo domínio limpo).
*   **jMolecules:** Biblioteca em Java focada puramente em expressar conceitos arquiteturais no código (sem acoplamento com frameworks de runtime como Spring). Permite documentar *Bounded Contexts*, *Aggregates* e *Events* via anotações, habilitando verificações estáticas com o ArchUnit.



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Pitfalls)
1.  **Achar que JPA `@Entity` é a Entidade do DDD:** A anotação `@Entity` do Jakarta Persistence (JPA) é um mapeamento de tabela de banco de dados (Infraestrutura). A Entidade do DDD Tático diz respeito à identidade de negócio e comportamento rico em memória. Misturar os dois gera um modelo altamente acoplado ao modelo relacional.
2.  **O "Furacão Tático" (Tactical Tornado):** Ignorar a fase de Domain Storytelling e mapeamento de Bounded Contexts, pulando direto para a criação de pastas `repository`, `service` e `factory`. Isso resulta em uma arquitetura burocrática, com código anêmico travestido de DDD.
3.  **Tentar aplicar DDD em CRUDs (Supporting Subdomains):** Não aplique o peso tático do DDD (Aggregates, Value Objects) em domínios de suporte que apenas leem e escrevem dados em tela. O DDD é projetado para lidar com complexidade de lógica de negócios (Core Domain). Para sistemas de apoio, o bom e velho *Active Record* ou CRUD resolve mais rápido e mais barato.

### 🛡️ Princípios de Orientação a Objetos
*   **Separation of Concerns (Separação de Responsabilidades):** Evidenciado fortemente na divisão de `Bounded Contexts`. Um mesmo conceito do mundo real (Cliente) é dividido em diferentes classes para atender perfeitamente aos requisitos específicos de vendas, suporte e logística, sem interferir uns nos outros.
*   **Automação da Conformidade (Fitness Functions):** O uso de `ArchUnit` e `jMolecules` demonstra um princípio avançado de engenharia de software contínua: regras de arquitetura e design OO não devem viver apenas em documentos PDF e na cabeça de engenheiros seniores, mas devem ser programadas e validadas automaticamente na esteira de *Continuous Integration* (CI).