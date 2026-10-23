# Software Design & System Design: 1 - Introdução

## 1. Dados sobre a Aula

*   **Título Alternativo:** A Evolução da Complexidade: Fronteiras entre System Design, Arquitetura e Software Design.
*   **Tópico Central:** Fundamentação teórica das disciplinas de engenharia de software, mapeando como as limitações históricas (hardware, redes, times) moldaram os padrões arquiteturais que usamos hoje, e a distinção clara entre os papéis de Design e Arquitetura.
*   **Desafio Arquitetural:** Gerenciar a **complexidade** sistêmica e organizacional. Evitar a *Complexidade Acidental* (overengineering) e alinhar a tecnologia puramente como uma estratégia de negócio.



## 2. Passo a Passo Cronológico

**[00:00 - 02:46] Introdução e Definição de Escopo**
*   **Conceito:** O professor estabelece a metáfora do corpo humano. *System Design* é a visão do todo (o sistema digestivo e a comunicação entre os órgãos). *Software Design* é a visão microscópica (a célula, o código, as classes).
*   **Decisão Arquitetural / Lógica:** Engenheiros em início de carreira focam no "Como" (Frameworks, sintaxe Java). Engenheiros Staff/Arquitetos focam no "Porquê" (Business, trade-offs, design do sistema).

**[05:19 - 14:06] A Trend Histórica: As 5 Eras da Complexidade de Software**
O professor detalha como os gargalos arquiteturais evoluíram ao longo das décadas, moldando os sistemas atuais:
*   **1. Era do Hardware (Mainframes):** O hardware era caro e limitadíssimo.
    *   *Trade-off Histórico:* Armazenamento custava milhares de dólares por Megabyte. Portanto, aplicava-se regras de Normalização de Banco de Dados de forma agressiva para evitar qualquer duplicação de dados, sacrificando CPU para economizar disco.
*   **2. Lei de Moore (Concorrência):** O poder de processamento dobrou, surgiram processadores multicore.
    *   *Trade-off Histórico:* O software precisou ser adaptado para lidar com concorrência e paralelismo (Threads), o que introduziu problemas de concorrência, mas habilitou o modelo Cliente-Servidor (PCs conectados).
*   **3. Era Distribuída (Integrações):** O fim do isolamento.
    *   *Decisão Arquitetural:* Aplicações deixam de ser um monolito rodando em uma máquina e passam a se comunicar via rede com APIs de terceiros (Stripe, PayPal, Bancos). *Problema introduzido:* Latência de rede, falhas de comunicação e a necessidade de resiliência (onde hoje usamos padrões como *Circuit Breaker*).
*   **4. Escala de Times (Team Scale):** Times globais e distribuídos.
    *   *Conceito:* É aqui que a **Lei de Conway** entra em ação. Monólitos organizacionais geram monólitos de software. A arquitetura de microsserviços ganha força não apenas por tecnologia, mas para permitir que times independentes (Índia, EUA, Europa) façam deploy sem bloquear uns aos outros.
*   **5. Complexidade Acidental (O Paradoxo da Escolha):** O gargalo atual.
    *   *Conceito:* Temos tantas opções (Cloud, Kubernetes, Kafka, Redis) que criamos complexidade onde não precisa. Como o exemplo citado: passar 40 minutos configurando a Clean Architecture (Ports & Adapters) para um sistema que é apenas um CRUD básico de 1 tabela.

**[14:06 - 19:11] A Evolução de Layers (Camadas Lógicas) e Tiers (Camadas Físicas)**
*   **Tradução de Diagrama:** O professor ilustra a separação física ao longo do tempo.
    *   *Anos 60/70 (1-Tier):* UI, Lógica e DB tudo na mesma máquina (Mainframe). Sem isolamento.
    *   *Anos 80/90 (2-Tier):* O Cliente (App Desktop) se comunica diretamente com o DB remoto.
    *   *Anos 90/2010 (3-Tier):* Introdução do Application Server. (Navegador -> App Server -> Database). Permite aplicar Design Patterns clássicos (MVC).
    *   *2010 - Atual (N-Tier):* Cloud, Microservices, API Gateways. Alta distribuição física, exigindo estratégias maduras de *Software Architecture*.

**[19:11 - 26:30] O Ciclo da Engenharia de Software (O "Porquê")**
Sistemas de software são a **estratégia de negócio**, não a implementação de TI. O fluxo correto de design é:
1.  **Complexity Mapping:** Entender o domínio (DDD estratégico se encaixa aqui), inputs e outputs.
2.  **System Planning:** Quais partes farão o que? (Responsabilidades macro).
3.  **Software Architecture:** Escolha de padrões estruturais (Event-Driven, Microsserviços), definição de fronteiras (Bounded Contexts) e interações.
4.  **Software Design:** Tradução tática. Divisão em classes, objetos e padrões de projeto (Strategy, Factory, etc).

**[26:31 - Final] System Design vs. Arquitetura vs. Software Design (A Matriz Definitiva)**
O professor resume os três níveis estruturais:
*   **System Design (O Planejador):** Foco no negócio, escala, restrições. Perguntas: *O que o sistema faz? Em qual escala?* (Stakeholders: CTO, Arquitetos, SREs).
*   **Software Architecture (O Estrategista):** Foco nas estruturas, componentes, limites (Boundaries). Perguntas: *Quais são as partes principais e como elas conversam?* (Ex: Usar Kafka vs RabbitMQ).
*   **Software Design (O Tático):** Foco na implementação, padrões de código, Clean Code. Perguntas: *Como cada parte funciona internamente?* (Stakeholders: Desenvolvedores, Tech Leads).



## 3. Principais Conceitos

*   **Complexidade Acidental:** Complexidade introduzida pelas ferramentas, linguagens ou arquiteturas escolhidas pelos desenvolvedores, que não é inerente ao problema de negócio em si (Complexidade Essencial).
*   **Paradoxo da Escolha:** Situação moderna onde a vasta quantidade de ferramentas (AWS, GCP, frameworks) atrasa a tomada de decisão e gera ansiedade arquitetural.
*   **Lei de Conway:** "*As organizações que projetam sistemas são restritas a produzir designs que são cópias das estruturas de comunicação dessas organizações.*" Se a empresa tem 3 equipes de backend isoladas, o sistema fatalmente terá 3 serviços isolados.
*   **Tier vs Layer:**
    *   **Tier:** Camada física (Ex: Um servidor EC2 rodando o banco de dados e outro servidor rodando a aplicação Spring Boot).
    *   **Layer:** Camada lógica no código (Ex: `Controller`, `Service`, `Repository` dentro do mesmo pacote Java).
*   **Hexagonal Architecture (Ports and Adapters):** Padrão de *Software Architecture* mencionado como exemplo. Isola a lógica de negócio (Core) de integrações externas (Bancos, APIs) usando interfaces (Portas) e implementações (Adaptadores). **Trade-off:** Traz alto isolamento e testabilidade, mas introduz complexidade acidental se usado em domínios anêmicos (CRUDs simples).



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Anti-patterns & Pitfalls)
1.  **Descolamento do Negócio:** Achar que Arquitetura é apenas sobre colocar Kubernetes e Kafka. Arquitetura é sobre viabilizar o negócio de forma econômica. Uma infraestrutura cara que não se paga é uma arquitetura falha.
2.  **"Resume Driven Development" (Overengineering):** Escolher a arquitetura da moda (ex: Microsserviços para tudo) apenas para colocar no currículo, incorrendo em pesada complexidade acidental para problemas que um Monolito bem modularizado resolveria.
3.  **Ignorar as Pessoas (Conway's Law):** Tentar impor uma arquitetura altamente distribuída e assíncrona em uma empresa onde as equipes têm uma comunicação falha e hierarquia rígida. A arquitetura vai falhar.
4.  **Não entender a diferença entre Tática e Estratégia:** Tentar resolver problemas de escala de dados (System Design) aplicando padrões de código (Software Design - ex: enchendo de `Design Patterns`), ao invés de repensar a topologia do sistema.

### 💡 Princípios Fundamentais (Regras de Ouro)
*   **Sistemas de Software são a Estratégia de Negócio:** Você não está escrevendo código; você está mapeando e automatizando a complexidade do mundo real.
*   **Abrace os Trade-offs:** Toda decisão arquitetural (como usar um Banco Relacional vs NoSQL, ou Monolito vs Microsserviços) possui ganhos e perdas. O bom Arquiteto justifica o "Porquê" escolheu o caminho A e assumiu as dores do caminho B.
*   **O Espectro do Design:** O design é fluido. Uma decisão de Software Design (ex: separar um módulo grande em pacotes menores) se feita em grande escala, torna-se uma decisão de Arquitetura de Software (extrair esse pacote para um serviço independente). Identifique em qual parte do espectro você está atuando.