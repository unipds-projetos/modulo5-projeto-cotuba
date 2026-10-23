# Software Design & System Design: 6 - Exemplos e Visão de Geral de Cases de System Design

## 1. Dados sobre a Aula

*   **Título Alternativo:** System Design na Prática: Desconstruindo E-commerces, Sistemas de Reserva e Redes Sociais.
*   **Tópico Central:** A transição da teoria para a prática utilizando o **C4 Model** (Contexto e Containers) e Diagramas de Fluxo para documentar e resolver problemas reais de arquitetura. Foco em ingestão assíncrona, desacoplamento e isolamento de domínios.
*   **Desafio Arquitetural:** Como traduzir requisitos de negócio em topologias técnicas viáveis. Balancear a flexibilidade de ingestão de dados com a resiliência do processamento, e definir limites claros entre o que é o "Core" do negócio e o que deve ser delegado a terceiros (Buy vs. Build).



## 2. Passo a Passo Cronológico

**[00:00 - 01:32] Introdução: A Prática do System Design**
*   **Conceito:** A teoria fornece as ferramentas, mas System Design só se consolida com a prática. O livro não ensina o "C4 Model" profundamente, mas recomenda-se que o primeiro passo para qualquer arquitetura seja **compreender o problema de negócio** e, em seguida, desenhar o contexto macro (Nível 1 do C4).
*   **Decisão Arquitetural:** Iniciar o design de soluções sempre pelo "Caminho Feliz" e pelo fluxo de negócio antes de escolher bancos de dados ou frameworks.

**[01:33 - 10:16] Cenário 1: Multi-Channel Product Updates (E-commerce Broadcast)**
*   **Contexto de Negócio:** Um sistema que atua como ponte/agregador entre usuários e plataformas de e-commerce (ex: buscar o iPhone 16 mais barato entre concorrentes).
*   **Visão de Contexto (C4 Nível 1):** O sistema se posiciona entre os *Vendors* (Fornecedores de dados), *Internal Operators* (Operação de negócio) e *Marketplace Platforms* (Amazon, etc).
*   **Interpretação do Diagrama de Containers (C4 Nível 2):**
    *   `[Vendor/Client]` -> `[Ingestion Service (REST/FTP/Email)]`
    *   `[Ingestion Service]` -> Publica mensagem -> `[Message Queue (RabbitMQ)]`
    *   `[Queue]` -> Consumida por -> `[Processing Service (c/ motor de regras Drools)]`
    *   `[Processing Service]` salva dados brutos no -> `[Data Lake (Raw Data)]`
    *   `[Processing Service]` salva dados limpos no -> `[Data Warehouse (Normalized DB)]`
    *   `[Warehouse]` alimenta o -> `[Cache (Redis)]` (Para leitura rápida)
    *   `[Processing Service]` aciona -> `[Notifiers/Async Sync]` -> Envia `[Webhooks]` para parceiros.
*   **Trade-offs e Decisões Arquiteturais no Cenário 1:**
    *   *Trade-off de Ingestão (REST vs FTP/Email):* A arquitetura aceita formatos legados (FTP) porque, no mundo real, o tamanho e a maturidade tecnológica dos parceiros B2B variam. **Vantagem:** Maior adoção de parceiros. **Desvantagem:** Maior complexidade na camada de *Ingestion/ETL*.
    *   *Trade-off de Persistência (Raw Data vs Normalized):* O professor destaca a persistência dupla. Salvar o *Raw Data* (Data Lake). **Vantagem:** Permite auditoria, reprocessamento em caso de falhas e consumo futuro por times de Data Science. **Desvantagem:** Custo extra de armazenamento.
    *   *Tolerância a Falhas (Mensageria):* O uso do RabbitMQ entre a ingestão e o processamento é justificado para evitar gargalos. Se o banco de dados cair, o sistema continua ingerindo requisições (elas ficam na fila), evitando que o parceiro receba erros `500 Internal Server Error`.

**[10:17 - 12:37] Cenário 2: Reservation System (Clone de Uber/Booking)**
*   **Contexto de Negócio:** Sistema que exige alta disponibilidade, concorrência, pagamentos, geolocalização e notificações.
*   **Interpretação do Diagrama de Contexto:**
    *   `[Customer]` -> `[Reservation System (Core)]`
    *   `[Reservation System]` se comunica com -> `[Payment Platform (Stripe)]`, `[Notification Service]`, `[Map/Routing Service]`.
*   **Decisão Arquitetural / Justificativa (Domain-Driven Design):** O professor enfatiza o isolamento do *Core Business*. Construa apenas o sistema de reservas (seu diferencial). Autenticação, Mapas e Pagamentos são "Generic Subdomains".
    *   *Trade-off:* Assinar contratos com terceiros (Stripe, Google Maps). **Vantagem:** Tira a complexidade absurda (PCI Compliance, Roteamento) de dentro de casa. Reduz tempo de *Time to Market*. **Desvantagem:** Cria dependência de fornecedores externos e custos fixos por transação.

**[12:38 - 15:36] Cenário 3: Social Media (Rede Social Corporativa/Twitter Clone)**
*   **Contexto de Negócio:** Aplicação com altíssimo volume de leituras (Feeds) em relação a gravações (Posts).
*   **Interpretação do Diagrama de Contexto:**
    *   `[User]` -> `[Social Platform]` -> `[Media Storage (S3/Blob)]` & `[Analytics Platform]`.
*   **Desafio Arquitetural:** A "Inundação de Informações". Como o sistema define o que o usuário deve ver primeiro no Feed?
*   **Decisão Arquitetural:** Introdução de camadas de Machine Learning (ML) ou algoritmos de ranqueamento fora do fluxo síncrono. O feed precisa ser pré-computado ou usar motores de busca indexados (o professor citou o uso do *Elasticsearch* no Google Cloud para essa finalidade) para garantir baixíssima latência na leitura.

**[15:37 - Fim] Dicas Finais para Entrevistas**
*   Em entrevistas, use lousas visuais, abstraia a complexidade desnecessária e sempre comece pelo contexto de negócio (C1) antes de descer para a infraestrutura (C2).



## 3. Principais Conceitos

*   **Message Queue (Fila de Mensageria - Ex: RabbitMQ / Kafka):** Componente que implementa comunicação assíncrona. Atua como um *buffer* entre serviços que produzem dados em alta velocidade e serviços que consomem esses dados em uma velocidade menor, nivelando picos de carga (Load Leveling).
*   **Drools (Rule Engine):** Um Sistema de Gerenciamento de Regras de Negócio (BRMS). Usado para separar lógicas de decisão complexas e mutáveis (ex: regras de precificação, descontos) do código-fonte da aplicação, permitindo atualizações dinâmicas.
*   **Data Lake (Raw Data Storage):** Repositório focado em armazenar um vasto volume de dados em seu formato bruto (JSON, XML, logs) sem esquemas rígidos. Altamente escalável e barato (Ex: AWS S3).
*   **Data Warehouse (Normalized Storage):** Repositório relacional ou colunar focado em dados já limpos, processados e normalizados, otimizado para consultas rápidas e geração de relatórios de BI (Business Intelligence).
*   **Webhook:** Padrão arquitetural onde um sistema notifica outro de forma reativa através de uma chamada HTTP POST. É o padrão da indústria para integrações assíncronas B2B (ex: "Aise-me quando o pagamento for aprovado").



## 4. Resumo para Fixação

### 📊 Design de Casos Práticos Resumidos
*   **Sistema de E-commerce (Price Aggregator):**
    *   *Foco:* Ingestão massiva e normalização.
    *   *Solução:* Padrão *Pipes and Filters* (Ingestão -> Fila -> Motor de Regras -> Data Lake/DB -> Cache -> Webhook).
*   **Sistema de Reservas (Uber/Airbnb):**
    *   *Foco:* Transacionalidade, geolocalização e parcerias.
    *   *Solução:* Integrações pesadas via API externas para comodities (Pagamentos/Mapas) e isolamento do motor de cálculo de reservas em infraestrutura proprietária de alta disponibilidade.
*   **Sistema de Rede Social (Feed):**
    *   *Foco:* Baixa latência de leitura (*Read-Heavy*).
    *   *Solução:* Bancos NoSQL/Motores de busca (Elasticsearch) para o Feed pré-computado, armazenamento de objetos em nuvem (S3) para mídias, e filas assíncronas para processamento de Machine Learning.

### 🚨 Armadilhas Comuns (Anti-patterns)
1.  **Construir o que se deve Comprar (Not Invented Here Syndrome):** Tentar arquitetar do zero o processamento de cartões de crédito ou servidores de envio de e-mail ao invés de plugar APIs maduras (Stripe, SendGrid). Em System Design, saber o que *não* construir é tão importante quanto saber construir.
2.  **Ignorar a Tolerância a Falhas na Ingestão (Backpressure):** Desenhar uma API de ingestão que grava direto no banco de dados de forma síncrona. Se houver um pico de tráfego (Black Friday), o banco cai, a API cai e os dados dos parceiros são perdidos. *Solução: Use sempre um Message Broker na borda de ingestão.*
3.  **Mergulhar Fundo Demais no C4 Model (Code Level):** Durante entrevistas ou reuniões executivas, tentar explicar a arquitetura desenhando Diagramas de Classe (C4 Nível 3 ou 4). Mantenha a discussão em Componentes Deployáveis (APIs, Bancos, Filas - C4 Nível 2).

### 💡 Princípios Fundamentais (Regras de Ouro)
*   **Guarde o Raw Data (Sempre):** Em processamento de dados críticos, se o seu algoritmo de normalização falhar ou corromper dados devido a um *bug*, ter o payload cru original salvo permite que você realize o "Replay" das mensagens e corrija a base de dados histórica.
*   **O Diagrama é um Mapa de Conversação:** Um diagrama de System Design não é um artefato estático; ele serve para guiar a conversa sobre restrições. Pergunte ao entrevistador/stakeholder: *"Qual é a latência esperada aqui?"*, *"Quantos eventos por segundo prevemos nesta fila?"*.
