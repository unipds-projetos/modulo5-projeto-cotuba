# Software Design & System Design: 5 - Comunicação entre Componentes (System Design)

## 1. Dados sobre a Aula

*   **Título Alternativo:** Topologias de Comunicação Distribuída: REST, gRPC, Mensageria Assíncrona e Tolerância a Falhas.
*   **Tópico Central:** Como desenhar a comunicação entre componentes de software (internos e externos) escolhendo os protocolos corretos (HTTP/REST vs. gRPC), estratégias de desacoplamento (Event-Driven/Message Brokers) e garantindo robustez na interface de integração.
*   **Desafio Arquitetural:** Como integrar sistemas heterogêneos evitando que a latência e a indisponibilidade de um serviço de terceiros (ex: Gateway de Pagamento) causem esgotamento de recursos (Thread Starvation) e falhas em cascata no sistema principal.



## 2. Passo a Passo Cronológico

**[00:00 - 06:48] Comunicação Síncrona e o Padrão REST**
*   **Conceito:** A comunicação é o oxigênio dos microsserviços. Os componentes precisam conversar entre si (B2B ou entre domínios internos, como *Estoque* e *Pagamento*).
*   **A "Glória do REST" (Richardson Maturity Model):** O professor aborda os níveis de maturidade da API, desde o uso básico de RPC sobre HTTP (Level 0) até o uso correto de recursos (Level 1), verbos HTTP (Level 2: GET, POST, PUT, DELETE) e HATEOAS (Level 3: Hypermedia como controle de estado).
*   **Trade-off (REST vs. Outros Protocolos):**
    *   *Vantagens:* Altamente interoperável, *text-based* (JSON/XML, legível para humanos), amplamente adotado e possui ecossistema de documentação maduro (OpenAPI/Swagger). É o padrão *De Facto* para APIs públicas e integrações B2B.
    *   *Desvantagens:* Maior overhead de payload (JSON é mais pesado que binário), comunicação baseada em requisição/resposta simples (bloqueante).

**[06:49 - 09:47] Caso Prático REST: Integração de Pagamento (Webhooks)**
*   **Interpretação de Diagrama (Síncrono com Callback):** O slide mostra a comunicação B2B (Business to Business).
    *   `[Cliente (Web/App)] -> POST /checkout -> [Backend System]`.
    *   `[Backend System] -> POST /payments -> [Payment Provider (Stripe/External)]`.
    *   O Provedor retorna um *status* de "Confirmado/Processando".
    *   *Posteriormente*, o provedor de pagamento faz uma chamada de volta para o sistema original: `[Payment Provider] -> POST /webhook (REST) -> [Backend System]`, atualizando o status final do pedido no `[PostgreSQL Database]`.
*   **Decisão Arquitetural:** Em processos externos que demoram, o REST é usado para enviar a intenção de processamento, e um *Webhook* (também REST) é a estratégia padrão para receber a resposta assíncrona do parceiro de negócio, evitando manter a conexão HTTP aberta por tempo indeterminado.

**[09:48 - 13:56] gRPC: Comunicação Remota de Alta Performance e Arquitetura Híbrida**
*   **Conceito:** Desenvolvido pelo Google, roda sobre HTTP/2, usa *protocol buffers* (Protobuf) e permite fluxos de dados bidirecionais (Multiplexing).
*   **Trade-off (gRPC vs. REST):**
    *   *Vantagens:* Performance extrema (payload binário gera menos tráfego de rede e deserialização mais rápida), contratos estritos gerados por código (`.proto`), suporta Streaming bidirecional.
    *   *Desvantagens:* Payload não legível para humanos (dificulta debug direto no browser), exige balanceadores de carga compatíveis com HTTP/2, curva de aprendizado maior.
*   **Interpretação de Diagrama (O Padrão Híbrido REST + gRPC):**
    *   `[Cliente/Browser] -> (REST/HTTP) -> [API Gateway / Java Backend]`. (Fronteira Externa).
    *   `[Java Backend] -> (gRPC) -> [Stock Service] -> [Inventory DB]`. (Fronteira Interna).
*   **Decisão Arquitetural:** O professor defende fortemente este padrão. Use **REST** para o mundo exterior (onde a flexibilidade e adoção são necessárias) e **gRPC** para a comunicação interna do seu cluster de microsserviços (onde a performance, baixa latência e contratos rígidos são cruciais).

**[13:57 - 22:07] Mensageria Assíncrona (Queues & Message Brokers)**
*   **Conceito:** Como lidar com sistemas que operam em velocidades diferentes e evitar a indisponibilidade. Ferramentas citadas: Kafka, RabbitMQ.
*   **Trade-off (Comunicação Síncrona vs. Assíncrona):**
    *   *Vantagens (Assíncrono):* Desacoplamento extremo (Loose coupling). Permite que o produtor e o consumidor escalem independentemente. Alta tolerância a falhas (se o consumidor cair, a mensagem fica retida no Broker).
    *   *Desvantagens (Assíncrono):* Aumenta drasticamente a complexidade operacional, dificulta o rastreamento (tracing) de requisições e a resolução de erros exige tratamento de retentativas e DLQs (Dead-Letter Queues).
*   **Interpretação de Diagrama (Desacoplando Webhooks com Mensageria):**
    *   O professor apresenta um fluxo maduro para alta carga de pagamentos:
    *   `[Payment Provider] -> POST /webhook -> [Webhook Service (REST)]`.
    *   `[Webhook Service]` salva o payload cru em um `[Raw DB (Data Lake)]` para auditoria e publica o evento `payment.received` em uma `[Message Queue (Kafka)]`. Responde HTTP 200 para o Provider imediatamente.
    *   `[Payment System]` consome a fila no seu próprio tempo e processa a lógica pesada de negócio no banco principal.
*   **Decisão Arquitetural:** Nunca processe lógica pesada diretamente no endpoint que recebe o Webhook de um terceiro. Se o seu banco estiver lento, a API de Webhook dará *Timeout*, e o parceiro bloqueará sua aplicação. Receba, armazene, publique em uma fila e devolva "200 OK". Processe depois.

**[22:08 - 26:07] Design de Interfaces Robustas (Contract-Driven)**
*   **Princípios Críticos apresentados nos slides:**
    *   **Versionamento:** Sempre versione APIs externas (`/v1/`, `/v2/`). Sistemas mudam e você não pode quebrar a integração de clientes legados.
    *   **Idempotência:** A capacidade de processar a mesma requisição múltiplas vezes sem alterar o estado final além da primeira vez. **Decisão:** Essencial em pagamentos. Se houver falha de rede e o cliente enviar o "Pagar" duas vezes, o sistema deve reconhecer a chave de idempotência (Header) e cobrar apenas uma vez.
    *   **Clear Contracts & Observability:** Usar OpenAPI (Swagger) ou `.proto` como a "fonte da verdade". Implementar logs e *Distributed Tracing* (OpenTelemetry).

**[26:08 - Fim] Alta Disponibilidade, Tolerância a Falhas e Circuit Breakers**
*   **Conceito:** Arquiteturas distribuídas caem. O objetivo do System Design é evitar que caiam de forma não graciosa.
*   **Circuit Breaker (Disjuntor):** Padrão de resiliência que previne falhas em cascata.
    *   *Closed (Verde):* Requisições fluem normalmente.
    *   *Open (Vermelho):* Falhas atingiram o *threshold* (ex: banco de dados lento). O Circuit Breaker "abre" e bloqueia novas requisições na origem para não sobrecarregar ainda mais o recurso doente. Retorna um erro imediato ou um *Fallback* (ex: dado em cache).
    *   *Half-Open (Amarelo):* Após um *timeout*, permite passar algumas requisições testes para checar se o serviço se recuperou.
*   **Estratégias de Failover:** O professor lista abordagens para manter sistemas vivos: Redundância (Múltiplas réplicas sem SPOF), Health Checks (Liveness/Readiness probes via Kubernetes) e Auto-healing.



## 3. Principais Conceitos

*   **REST (Representational State Transfer):** Estilo arquitetural para sistemas distribuídos que utiliza HTTP para comunicação. Focado em recursos (entidades) e operações padronizadas (verbos HTTP).
*   **gRPC:** Framework RPC (Remote Procedure Call) de alta performance que usa HTTP/2 e buffers de protocolo. Ideal para microserviços poliglota que exigem comunicação veloz e bidirecional.
*   **Message Broker (Ex: Kafka, RabbitMQ):** Intermediário arquitetural que recebe, armazena temporariamente e distribui mensagens entre serviços. Viabiliza a comunicação assíncrona e o processamento reativo (Event-Driven).
*   **Webhook:** Uma "API reversa" ou callback HTTP. É uma forma de um sistema notificar outro sobre a ocorrência de um evento em tempo quase real (Push), evitando que o cliente precise ficar checando ativamente (Polling).
*   **Idempotency Key (Chave de Idempotência):** Um identificador único gerado pelo cliente e enviado no cabeçalho da requisição. O backend o utiliza para garantir que uma transação retentada devido a falhas de rede não seja processada em duplicidade.
*   **Circuit Breaker:** Design pattern de estabilidade. Evita que uma aplicação tente continuamente executar uma operação que tem grandes chances de falhar, preservando recursos do servidor (CPU/Threads) e permitindo que o serviço defeituoso se recupere.



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Anti-patterns & Pitfalls)
1.  **Distributed Monolith (O Pior dos Dois Mundos):** Criar dezenas de microsserviços, mas fazer com que eles se comuniquem exclusivamente de forma **Síncrona** via REST. Se o Serviço A chama o B, que chama o C, e o C cai, toda a cadeia falha e as requisições geram *timeout*. Isso cria um "Monolito Distribuído", onde você tem os custos da nuvem, mas a fragilidade do monolito. Solução: Inserir *Message Brokers* e/ou *Circuit Breakers*.
2.  **Ignorar a Idempotência em APIs de Mutação:** Desenhar sistemas transacionais (compra, reservas) sem suporte a idempotência no método `POST`. Em uma internet instável (redes mobile), retentativas automáticas são regra. Sem idempotência, você gerará duplicidade no banco de dados.
3.  **Processamento Síncrono em Webhooks:** Segurar a conexão HTTP aberta de um webhook de terceiros (como a Stripe ou PayPal) enquanto o seu sistema faz verificações complexas em banco de dados e disparo de e-mails. O provedor dará timeout e pode aplicar *rate limits* punitivos no seu sistema. Devolva `200 ACK` rápido e processe via filas.

### 💡 Princípios Fundamentais (Regras de Ouro)
*   **Contratos Flexíveis vs. Rígidos:** Seja conservador no que você envia e liberal no que você recebe no mundo REST (Lei de Postel). Para tráfego interno pesado, adote contratos estritos de "Schema-first" (gRPC/.proto) para evitar erros de tipagem em tempo de execução entre times.
*   **Abrace as falhas (Design for Failure):** Engenheiros seniores assumem que componentes vão falhar. O bom design não evita a falha, mas limita o seu raio de explosão (Blast Radius) utilizando Padrões de Tolerância a Falhas (Retry, Backoff exponencial, Circuit Breaker).
*   **Persista a Intenção, Atue no Evento:** Para lidar com fluxos críticos que envolvem sistemas fora do seu controle (Cartão de Crédito), use o padrão de *Event Sourcing / Outbox Pattern* (mencionado conceitualmente no slide do Raw DB). Salve a requisição crua num Data Lake/DB primeiro, e depois orquestre a complexidade garantindo que os dados não foram perdidos se o processador der erro.