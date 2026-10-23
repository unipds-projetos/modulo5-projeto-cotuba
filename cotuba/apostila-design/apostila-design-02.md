# Software Design & System Design: 2 - Princípios Básicos de System Design

## 1. Dados sobre a Aula

*   **Título Alternativo:** Fundamentos de System Design: Topologias (Tiers vs Layers), Modelos de Renderização e Vetores de Escalabilidade.
*   **Tópico Central:** A distinção entre fronteiras lógicas (Layers) e físicas (Tiers), a anatomia dos componentes (Frontend/Backend) e as estratégias fundamentais de crescimento sistêmico (Vertical vs. Horizontal) e performance (Latência vs. Throughput).
*   **Desafio Arquitetural:** Como particionar um sistema de forma inteligente, balanceando a facilidade de manutenção (código) com a resiliência e custo operacional (infraestrutura), evitando falhas em cascatas e gargalos de rede.



## 2. Passo a Passo Cronológico

**[00:00 - 02:58] O Mindset do Staff Engineer**
*   **Conceito:** Para dominar System Design, é mandatório dominar os fundamentos da computação (referência a autores como Tanenbaum). O que diferencia um engenheiro júnior de um sênior/staff é a capacidade de revisitar e aplicar esses fundamentos básicos (hardware, redes, concorrência) para resolver problemas complexos de negócio em escala.

**[02:58 - 07:08] Layers vs Tiers (Fronteiras Lógicas vs Físicas)**
*   **Conceito Crítico:** 
    *   **Layers (Camadas Lógicas):** Organizam o software logicamente no código (ex: divisão em pacotes `UI`, `Business`, `DAO`).
    *   **Tiers (Camadas Físicas):** Organizam o software fisicamente na infraestrutura (onde o código é "deployado").
*   **Evolução Histórica (Slide):**
    *   *1970s (1-Tier / Mainframe):* Monolito absoluto. UI, lógica e DB no mesmo hardware.
    *   *1990s (2-Tier):* UI + Lógica no cliente comunicando com um DB remoto.
    *   *2000s-2010s (3-Tier):* Introdução do Application Server. Separação clara entre UI, Business Layer e DB.
    *   *2010s+ (N-Tier / Microservices):* Clean Architecture, Cloud, alta fragmentação de componentes.

**[07:08 - 13:46] Trade-offs: Aumentando Tiers (Camadas Físicas)**
*   **Regra Arquitetural:** *"Mais Tiers significam mais poder, mas também mais complexidade."*
*   **Vantagens (Pros):**
    *   *Separation of concerns:* Facilita escalar partes específicas (ex: escalar apenas o banco de dados sem mexer na aplicação).
    *   *Isolamento de Falhas:* Se uma máquina cai, não derruba a empresa inteira. Traz alta tolerância a falhas.
*   **Desvantagens (Cons):**
    *   *Orquestração complexa:* Exige sincronização de dados (padrões Leader/Follower, replicação).
    *   *Lógica Duplicada:* A validação feita no cliente (Frontend) **deve** obrigatoriamente ser revalidada no servidor (Backend), sob pena de falha de segurança ou consistência.
    *   *Cross-tier communication:* Introduz latência de rede e overhead de serialização/deserialização (ex: transformar Objetos Java para JSON e vice-versa ao trafegar via rede).
*   **Decisão Arquitetural:** Em sistemas altamente distribuídos, desenvolvedores ignoram as falhas de rede. Se um *node* cai (ex: Cassandra ou microsserviço), o sistema deve prever compensações (*Fallbacks*, *Circuit Breakers*). Redes internas falham.

**[13:46 - 22:19] Trade-offs: Aumentando Layers (Camadas Lógicas)**
*   **Vantagens (Pros):**
    *   Código modular, arquitetura mais limpa, testabilidade isolada (facilidade de usar *mocks*). Permite trocar tecnologias (ex: trocar MySQL por MongoDB) sem reescrever a regra de negócios.
*   **Desvantagens (Cons):**
    *   Risco de *Overengineering*. 
    *   Criação de código anêmico ou "Arquitetura Cebola/Matrioska", onde você atravessa 15 camadas apenas para salvar um dado simples no banco.
    *   Adiciona *overhead* para operações simples.

**[22:19 - 26:22] Componentes Core: O Frontend**
*   **Conceito:** *"Onde os humanos encontram o sistema."*
*   **Análise de Modelos (Trade-offs):**
    *   *Client-Side Rendering (CSR):* Lógica roda no browser (React, Angular). **Pro:** Experiência rica e fluida (SPA), tira carga de processamento do servidor. **Con:** SEO é prejudicado, primeiro carregamento (First load) é lento devido ao tamanho do *bundle* JS.
    *   *Server-Side Rendering (SSR):* Renderizado no servidor e enviado como HTML (JSF, Thymeleaf, Next.js). **Pro:** Excelente para SEO, carregamento inicial previsível. **Con:** Maior carga de processamento no servidor (Backend) e maior volume de *network requests*.

**[26:22 - 32:20] Componentes Core: O Backend**
*   **Análise de Diagrama:** O slide mostra o Backend como o "Cérebro". Ele orquestra a comunicação bidirecional entre: `UI` <-> `Backend` <-> (`Databases`, `Messaging Systems/Kafka`, `External APIs`).
*   **Responsabilidade:** É onde a lógica de negócio principal reside e os custos com infraestrutura emergem. Define as fronteiras dos serviços e lida com a orquestração e transações (ACID/Sagas).

**[32:20 - 41:58] Vetores de Escalabilidade: Vertical vs. Horizontal**
*   **Escalabilidade Vertical (Scale Up):** Adicionar mais recursos (CPU, RAM, Disco) a uma mesma máquina.
    *   **Pros:** Nenhuma complexidade distribuída. Sem dores de cabeça com sincronização. Suportado por sistemas legados.
    *   **Cons:** O hardware tem um limite físico inescapável. Gera um **Single Point of Failure (SPOF)**. Requer *downtime* para upgrade.
*   **Escalabilidade Horizontal (Scale Out):** Distribuir a carga adicionando mais máquinas/nós em paralelo.
    *   **Pros:** Escalabilidade quase infinita. Alta tolerância a falhas (remove o SPOF). Ideal para *stateless apps* e microsserviços.
    *   **Cons:** Arquitetura altamente complexa. Requer balanceadores de carga (*Load Balancers*). Exige resolução de consistência de dados e estado de sessão distribuída.

**[41:58 - Fim] Métricas de Performance: Latency vs. Throughput**
*   **Latência (Latency):** O tempo entre a ação e a reação. O tempo que um sistema leva para responder a um *request*. (Medido em milissegundos). Foco na **Velocidade**.
*   **Throughput:** Quanto trabalho o seu sistema consegue processar em um dado intervalo de tempo. (Medido em Requisições por Segundo - RPS, ou Transações por Minuto - TPM). Foco na **Capacidade**.
*   *Nota Arquitetural:* Você pode ter um sistema com excelente throughput (processa 10.000 requisições), mas péssima latência (cada requisição demora 5 segundos para retornar). Otimizar um geralmente afeta o outro.



## 3. Principais Conceitos

*   **Load Balancer (Balanceador de Carga):** Componente vital na *Escalabilidade Horizontal*. Atua como um "guarda de trânsito", recebendo as requisições externas e distribuindo-as de forma equitativa entre as múltiplas instâncias do Backend/API, garantindo que nenhum servidor fique sobrecarregado.
*   **SPOF (Single Point of Failure):** Ponto único de falha. Um componente no sistema que, se parar de funcionar, derruba a aplicação inteira (muito comum na escalabilidade vertical).
*   **Circuit Breaker / Fallback:** Padrões de resiliência necessários ao adotar N-Tiers (Microservices). Se uma chamada de rede para um serviço terciário falhar seguidas vezes, o circuito "abre", retornando um erro rápido ou um dado padrão (fallback) para não esgotar as *threads* do servidor chamador.
*   **Serialização (Serialization):** Processo obrigatório ao separar camadas físicas (Tiers). É a conversão de um objeto em memória (ex: Entidade Java) para um formato trafegável pela rede (ex: JSON/XML) para que o banco de dados ou a interface gráfica possa consumi-lo. Gera custo de latência e CPU.
*   **Stateless Apps:** Aplicações que não guardam estado/sessão do usuário localmente no disco ou memória do servidor. Condição indispensável para escalar horizontalmente de forma eficiente.



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Anti-patterns)
1.  **A Ilusão da Rede Perfeita:** Ao fatiar um sistema fisicamente (N-Tiers / Microservices), desenvolvedores juniores assumem que a rede da nuvem nunca falha e que a latência é zero. Engenheiros Staff sabem que a comunicação entre *nodes* exige *retries*, tolerância a falhas e estratégias assíncronas.
2.  **Confiar Apenas na Validação do Client-Side:** Nunca confie no Frontend. Um erro grotesco é realizar regras de validação pesadas no browser (React) e enviar os dados para o Backend gravar direto no Banco de Dados. Usuários maliciosos podem contornar a UI via API externa. O Backend **sempre** deve revalidar lógicas de negócio cruciais.
3.  **"Resume-Driven Architecture" na Divisão Lógica:** Aplicar Clean Architecture e quebrar um projeto em dezenas de pacotes/camadas (Layers) para um serviço cujo objetivo é ser apenas um CRUD passageiro. Aumenta o tempo de entrega e a barreira de entrada para novos desenvolvedores na equipe sem gerar ROI para a empresa.
4.  **Desalinhamento de Estrutura de Times (Conway's Law):** Tentar adotar uma arquitetura de múltiplos *tiers* de forma "Flat" onde todos aprovam PRs em tudo. Estruturas distribuídas fisicamente exigem times alinhados de forma independente.

### 💡 Princípios Fundamentais (Regras de Ouro)
*   **Justifique o Acoplamento/Desacoplamento:** Só separe o software fisicamente (Tier) se você precisar de: (A) Escalar partes da aplicação de forma diferente, (B) Isolamento de segurança, ou (C) Times independentes.
*   **O Sufixo "Soft":** Software foi feito para ser maleável. Em caso de dúvidas sobre regras muito rígidas ou designs burocráticos, lembre-se que o "contexto é rei". É permitido fundir camadas se o projeto for pequeno e separá-las conforme ele cresce.
*   **Conheça a diferença entre Latência e Throughput:** Na entrevista, demonstre que entende a diferença: Otimizar o tamanho de um *Payload* JSON otimiza a latência. Colocar instâncias de servidor em paralelo atrás de um Load Balancer escala o throughput.