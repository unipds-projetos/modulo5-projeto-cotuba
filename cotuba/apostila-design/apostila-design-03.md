# Software Design & System Design: 3 - System Design e Documentação

## 1. Dados sobre a Aula

*   **Título Alternativo:** A Engenharia da Documentação: C4 Model, ADRs e a Cultura de *Documentation as Code*.
*   **Tópico Central:** Como escalar o conhecimento técnico de sistemas distribuídos utilizando artefatos de documentação rastreáveis e versionáveis (C4 Model, UML, ADR, Markdown, AsciiDoc, Mermaid e Structurizr).
*   **Desafio Arquitetural:** Resolver a "escalabilidade humana" e a continuidade de negócio. Como garantir que dezenas de engenheiros em fuso horários diferentes compreendam as fronteiras do sistema, o fluxo de dados e o *porquê* das decisões técnicas passadas, sem depender de reuniões síncronas ou ferramentas manuais não versionadas.



## 2. Passo a Passo Cronológico

**[00:00 - 03:55] O Propósito da Documentação em System Design**
*   **Conceito:** A documentação não é um fim em si mesma, mas uma ferramenta de alinhamento e escalabilidade. Em arquiteturas distribuídas, a documentação mitiga a necessidade de comunicação síncrona (reuniões).
*   **Motivadores (Bullet Points do Slide):**
    *   Planejamento e Alinhamento.
    *   Escalabilidade (Foco humano, não técnico: *Onboarding/Offboarding* rápidos).
    *   Rastreabilidade de decisões (*Traceability*).
    *   Refatoração e Modernização.
    *   Continuidade de Negócio (Reduzir o fator "Lotação de Ônibus" / *Bus Factor*).

**[03:55 - 07:43] Tipos de Documentação: UML (Unified Modeling Language)**
*   **Conceito:** A linguagem clássica, porém deve ser usada com pragmatismo em System Design.
*   **Diagramas Úteis para Arquitetura:**
    *   *Component Diagram:* Mostra módulos e dependências.
    *   *Sequence Diagram:* **Altamente utilizado.** Define integrações, *caminho feliz* e chamadas assíncronas ao longo do tempo.
    *   *Activity / Flow Diagram:* Mostra o fluxo de dados e condicionais.
*   **Trade-off Arquitetural (Class Diagrams):** O professor desaconselha o uso de Diagramas de Classe para System Design. Eles são detalhados demais e entram no escopo tático (Software Design). A exceção são cenários de *Outsourcing* (fábricas de software), onde a especificação precisa ser rígida para times que apenas codificam sem entender o negócio.

**[07:43 - 10:07] Tipos de Documentação: O Modelo C4 (C4 Model)**
*   **Conceito Crítico:** O professor usa a analogia do **Google Maps**. O C4 Model permite dar *Zoom In* e *Zoom Out* na arquitetura, variando do nível de negócios até o nível de código.
*   **Níveis de Abstração:**
    1.  **Context (Nível 1):** O sistema em relação ao mundo externo (usuários, sistemas de terceiros). Visão macro para executivos.
    2.  **Container (Nível 2):** As unidades "deployáveis" (APIs, Bancos de Dados, Filas Kafka, SPAs). Visão para Arquitetos.
    3.  **Component (Nível 3):** Divisão lógica interna de um container (ex: Controladores, Serviços, Repositórios).
    4.  **Code (Nível 4 - Opcional):** Raramente utilizado. A recomendação do criador (Simon Brown) e do professor é: **deixe o código falar por si mesmo**.
*   **Decisão Arquitetural:** Em System Design, o foco absoluto deve estar nos Níveis 1 (Context) e 2 (Container).

**[10:07 - 11:10] ADR - Architecture Decision Record**
*   **Conceito:** Artefato de texto curto que documenta *decisões de design de software que importam*.
*   **Responsabilidade:** Documentar o **Porquê**. O código diz o *como*, a ADR diz o porquê (contexto, trade-offs analisados, prós e contras da decisão aceita).
*   **Evolução Sistêmica:** Permite que o time rastreie quando uma decisão se tornou obsoleta (ex: Migração de Monolito para Microsserviços, ou troca de Java 21 por Java 25).

**[11:10 - 14:18] A Regra de Ouro: Documentation as Code (Docs-as-Code)**
*   **Conceito Crítico:** Tratar a documentação com o mesmo rigor, ciclo de vida e ferramentas usadas no código-fonte.
*   **Trade-off (Ferramentas Visuais vs. Docs-as-Code):**
    *   *Draw.io / Miro / Visio:* **Desvantagem:** São difíceis de versionar no Git, não permitem Code Review/Pull Requests e ficam rapidamente defasados em relação à implementação real.
    *   *Docs-as-Code:* **Vantagem:** Arquivos de texto renderizados. Permitem controle de versão (Git), revisões via Pull Request (integração natural ao fluxo do dev) e integração com CI/CD.

**[14:18 - 20:05] Markdown vs. AsciiDoc (A Escolha da Linguagem)**
*   **Markdown:** 
    *   *Pros:* Curva de aprendizado quase nula, suportado nativamente pelo GitHub/GitLab. Excelente para *Readmes* e ADRs.
    *   *Cons:* Falta semântica rica para documentos complexos. Necessita de plugins para diagramação.
*   **AsciiDoc:**
    *   *Pros:* Extensível, modular e semântico. Permite a quebra de documentação em múltiplos arquivos (includes). Suporta diagramas nativamente. **Decisão Arquitetural:** É a escolha padrão para especificações longas e complexas (ex: Especificações do Jakarta EE).
    *   *Cons:* Curva de aprendizado moderada; requer conversores pesados (ex: gerar PDF).

**[24:24 - 34:28] Visualizando Arquitetura com Diagramação em Texto**
*   **PlantUML:** Usa sintaxe própria. Excelente para estruturar classes e componentes rapidamente via texto.
*   **Mermaid.js (Altamente Recomendado):**
    *   *Justificativa:* Suporte nativo nas principais plataformas Git (GitHub, GitLab).
    *   *Interpretação de Diagrama (Sequence - Representação de Requisão Java):* O professor exibe a construção em texto de um fluxo clássico N-Tier:
        `[Cliente] -> [API Gateway/Controller] -> [Service/Lógica de Negócio] -> [Cache Redis] (Opcional) -> [Repository/DAO] -> [Banco de Dados]`.
        O fluxo reverso representa o retorno da resposta (*Response*).
    *   *Interpretação de Diagrama (State - Ciclo do Cartão de Crédito):* Um fluxo de estado onde o cartão vai de `Criado` -> `Ativo` -> `Suspenso` ou `Cancelado`.

**[34:28 - Fim] Structurizr e a Implementação do C4 Model**
*   **Conceito:** Uma ferramenta baseada em uma DSL (Domain Specific Language) escrita especificamente para instanciar o C4 Model como código.
*   *Interpretação de Diagrama (Contexto Bancário - C4 Nível 1):*
    `Customer (Usuário) -> Bank System (Sistema Interno) -> Payment Processor (Gateway de Pagamento Externo como Stripe)`.
*   *Vantagem:* A partir de um modelo único de código, o Structurizr gera as visões de Contexto, Container e Componente dinamicamente, mantendo tudo sincronizado.



## 3. Principais Conceitos

*   **Documentation as Code (GitOps para Docs):** Filosofia onde a documentação é escrita em arquivos de marcação de texto plano (Markdown, AsciiDoc) e armazenada no repositório Git junto com o código-fonte, acompanhando o mesmo ciclo de CI/CD e Pull Requests.
*   **C4 Model:** Padrão arquitetural criado por Simon Brown para modelar a topologia de sistemas através de 4 níveis hierárquicos de abstração (Context, Container, Component, Code).
*   **ADR (Architecture Decision Record):** Log imutável que registra uma escolha arquitetural em um momento específico do tempo, documentando o contexto, as restrições da época e as consequências (prós e contras) da escolha.
*   **AsciiDoc:** Linguagem de marcação de texto avançada, superior ao Markdown para a geração de livros técnicos, especificações e documentações complexas, permitindo *includes* e macros nativas.
*   **Mermaid / PlantUML:** Ferramentas/Linguagens que convertem scripts de texto em diagramas UML ou gráficos de fluxo em tempo de renderização.



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Anti-patterns)
1.  **"Draw.io Architecture":** Desenhar diagramas arquiteturais em plataformas visuais fora do controle de versão (Git). Isso gera o anti-pattern *Outdated Documentation*, onde o diagrama não reflete a topologia atual em produção.
2.  **Excesso de Detalhamento (O Anti-Pattern do Microgerenciamento):** Tentar documentar lógicas de classes e métodos minuciosos em diagramas arquiteturais. *Regra de ouro:* Se o nível 4 do C4 Model (Código) está sendo exigido, o código possui "Bad Smells" e está ilegível. O código deve ser a verdade final.
3.  **Monólitos de Documentação:** Ter repositórios com documentação separada do repositório da aplicação. Quando o desenvolvedor altera um serviço de pagamento (ex: adicionando integração com Stripe), a documentação no repositório isolado é esquecida. A documentação deve viver *ao lado* da base de código que a origina.

### 💡 Princípios Fundamentais (Regras de Ouro)
*   **Single Source of Truth:** O Git é a única fonte de verdade para a aplicação e para o design da arquitetura. Se não está no repositório, não existe.
*   **A Documentação deve ter o perfil de um mapa (C4 Model):** Diferentes *stakeholders* exigem níveis de detalhe diferentes. Um CTO não quer ver a ordem de execução do Banco de Dados; ele quer ver o C4 Model Nível 1 (Sistema Bancário se conectando à API do Bacen).
*   **Documente Trade-offs com ADRs:** Toda arquitetura evolui. O que é um "padrão defasado" hoje (como usar um banco relacional gigantesco) foi uma decisão correta há 5 anos atrás. A ADR protege o time atual de cometer os mesmos erros do passado sem entender o contexto.