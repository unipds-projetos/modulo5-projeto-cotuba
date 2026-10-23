# Software Design & System Design: 4 - Introdução a Banco de Dados na Perspectiva de System Design

## 1. Dados sobre a Aula

*   **Título Alternativo:** Fundamentos de Persistência Distribuída: Bancos Relacionais, NoSQL, Teorema CAP e Estratégias de Dados (ETL/ELT).
*   **Tópico Central:** Como mapear o armazenamento de dados com as necessidades do negócio, explorando os diferentes sabores de bancos NoSQL (Key-Value, Wide-Column, Document, Graph), topologias de replicação e particionamento (Sharding).
*   **Desafio Arquitetural:** Escalar o estado da aplicação de forma distribuída. Como balancear Consistência, Disponibilidade e Latência (Teoremas CAP e PACELC) e escolher o motor de persistência adequado para evitar gargalos de banco de dados e problemas estruturais no processamento de dados (Data Lakes vs. Data Warehouses).



## 2. Passo a Passo Cronológico

**[00:00 - 05:07] O Papel do Banco de Dados na Arquitetura**
*   **Conceito:** O banco de dados é a evolução tecnológica da necessidade humana de reter conhecimento. Em arquiteturas *stateless* modernas, **o banco de dados é o único lugar onde o estado reside**.
*   **Decisão Arquitetural:** O banco de dados não é apenas infraestrutura, é um vetor de descobertas de negócios. (Exemplo prático: a análise de consumo de cartões *Black* que gerou a criação de uma *Winery* focada no nicho). O arquiteto deve desenhar esquemas que permitam a extração rápida de inteligência.

**[05:07 - 12:56] Persistência Poliglota e Trade-offs Relacional vs. NoSQL**
*   **Banco Relacional (SQL):** Maduros, seguem padronizações estritas e possuem alta escalabilidade **Vertical** (Scale Up).
*   **Bancos NoSQL:** Focados em escalabilidade **Horizontal** (Scale Out) e distribuídos por natureza.
*   **Persistência Poliglota (Trade-off):**
    *   *Vantagem:* Usar a ferramenta certa para o problema certo. Adaptar o banco ao cenário (ex: Grafo para recomendação, Relacional para transações financeiras).
    *   *Desvantagem:* Carga cognitiva alta na equipe. Curva de aprendizado, falta de padronização universal (ao contrário do SQL) e complexidade no gerenciamento de múltiplos protocolos e *drivers*.

**[12:56 - 16:35] Arquitetura Key-Value (Ex: Redis)**
*   *Estrutura:* Um mapa (Dicionário). Uma chave que aponta para um único valor (String, JSON, Blob).
*   *Trade-off (Cache vs. DB):* Bancos em memória (Redis/Memcached) sacrificam a **Durabilidade** em favor de extrema baixa **Latência**. Redis salva "snapshots" em disco, mas falhas entre ciclos resultam em perda de dados não persistidos.
*   **Interpretação de Diagrama (Topologia Master-Slave/Leader-Follower):**
    *   O cliente escreve no nó *Master* (Líder).
    *   O Master replica os dados de forma assíncrona ou síncrona para os nós *Slaves* (Replicas).
    *   *Decisão:* Se o Master cai, ocorre uma eleição para promover um Slave. Durante os milissegundos da eleição, o sistema pode perder dados (se assíncrono) ou indisponibilizar a escrita.

**[16:35 - 19:23] Arquitetura Wide-Column (Ex: Apache Cassandra)**
*   *Estrutura:* Chave da linha (*Row-key*) apontando para agrupamentos de colunas. Diferente do Key-Value, permite consultas parciais em partes dos dados.
*   **Interpretação de Diagrama (Topologia Masterless / Ring):**
    *   O diagrama mostra um Anel (Ring) distribuído por dois *Data Centers* (DC1 e DC2). Não há um servidor Master.
    *   O cliente faz a requisição para qualquer nó, que atua como **Nó Coordenador**.
    *   *Trade-off Arquitetural:* Altíssima disponibilidade e tolerância a falhas. Perfeito para *writes* pesados (logs, time-series). A desvantagem é a consistência eventual e a dificuldade em usar índices secundários de alta cardinalidade.

**[19:24 - 22:23] Arquitetura de Documentos (Ex: MongoDB)**
*   *Estrutura:* Objetos ricos em formato JSON/BSON, suportando matrizes e sub-documentos nativamente.
*   *Vantagem Arquitetural:* Agrupa dados em um formato atômico. Evita o custo de realizar *JOINs* complexos como no modelo relacional, favorecendo leituras muito rápidas.
*   **Interpretação de Diagrama (MongoDB Cluster com Sharding):**
    *   Cliente -> *Router* (mongos) -> *Shards* (A, B, C).
    *   O Router direciona a escrita/leitura para a partição correta com base na *Shard Key* (ex: alfabeto, ID geográfico).

**[22:23 - 25:54] Arquitetura de Grafos (Ex: Neo4j) e Matriz de Escala/Flexibilidade**
*   *Estrutura:* Foco não na entidade, mas no **relacionamento** entre elas (Nós e Arestas, com direções e propriedades).
*   *Caso de Uso:* Motores de recomendação, Detecção de Fraudes. (Consultas do tipo: "Amigos de amigos que compraram o produto X em São Paulo").
*   **Matriz Crítica (Escalabilidade vs. Flexibilidade):**
    *   *Key-Value:* Máxima escalabilidade horizontal, mínima flexibilidade de *query* (só busca por ID exato).
    *   *Grafo:* Máxima flexibilidade de consultas relacionais profundas, dificílimo de escalar horizontalmente (fragmentar um grafo na rede quebra a eficiência do relacionamento).

**[25:54 - 28:52] Teoremas CAP e PACELC**
*   **Teorema CAP:** Em um sistema distribuído, você deve escolher duas de três garantias: Consistência (C), Disponibilidade (A), Tolerância a Partição (P).
    *   *Nota do Arquiteto:* Como falhas de rede (P) são inevitáveis na nuvem, a escolha real é sempre entre **CP** (Consistência: falha a requisição se a rede cair, ex: Bancos Relacionais clássicos/MongoDB configurado estrito) ou **AP** (Disponibilidade: retorna dados desatualizados se a rede cair, ex: Cassandra/Redis).
*   **Teorema PACELC (Extensão do CAP):**
    *   Se Particionado (**P**), escolha Disponibilidade (**A**) ou Consistência (**C**).
    *   **E**lse (em operação normal, sem falhas de rede), escolha Latência (**L**) ou Consistência (**C**). (Ex: Redis prioriza Latência; bancos SQL pesados priorizam Consistência).

**[28:53 - 31:10] Data Processing: ETL vs. ELT e Warehouse vs. Lake**
*   **Data Warehouse + ETL (Extract, Transform, Load):**
    *   Os dados são transformados *antes* de chegar ao repositório final. Dados limpos, estruturados. Ideal para usuários de negócio e relatórios BI executivos.
*   **Data Lake + ELT (Extract, Load, Transform):**
    *   Os dados brutos (*raw data*, JSON, imagens, logs do Kafka) são jogados em um repositório barato (ex: AWS S3). A transformação ocorre *on-demand*.
    *   *Trade-off:* Extremamente escalável e barato, ideal para cientistas de dados e Machine Learning. O risco é virar um "Pântano de Dados" (*Data Swamp*) se não houver governança.

**[31:10 - 34:46] Sharding (Particionamento de Dados)**
*   *Conceito:* Quebrar o banco de dados horizontalmente. Se a tabela tem usuários de A-Z, o Servidor 1 guarda A-J, o Servidor 2 guarda K-T, e assim por diante.
*   *Trade-offs Críticos:*
    *   **Pros:** Melhora o *throughput* de gravação/leitura infinitamente.
    *   **Cons:** Realizar cópias de segurança (Backups) em bancos *sharded* é um pesadelo logístico. Como sincronizar o ponto no tempo de 6 máquinas distintas? Dificulta integrações analíticas.

**[36:35 - 37:44] Impedance Mismatch (Incompatibilidade de Mapeamento)**
*   *Conceito:* A diferença estrutural entre o código Orientado a Objetos (Herança, Polimorfismo, Coleções) na aplicação e a tabela bidimensional (Linhas/Colunas) no Banco Relacional. Frameworks ORM (Hibernate) tentam esconder essa dor, gerando custos de processamento (N+1 queries).



## 3. Principais Conceitos

*   **Sharding (Particionamento Horizontal):** Divisão lógica dos dados em múltiplas instâncias físicas de banco de dados para distribuir a carga. Exige uma *Shard Key* para determinar para qual máquina o dado deve ir.
*   **Leader-Follower (Master-Slave):** Padrão de replicação onde apenas um nó (Leader) recebe as operações de escrita, enquanto múltiplos nós (Followers) processam leituras e recebem cópias assíncronas/síncronas dos dados.
*   **Masterless Architecture:** Topologia de banco distribuído (ex: Cassandra, Riak) onde não há líder. Qualquer nó pode receber escrita ou leitura. Usa protocolos de fofoca (*Gossip Protocol*) e Quórum para atingir consistência.
*   **Consistência Eventual:** Padrão comum em sistemas AP (Teorema CAP). Uma vez que um dado é atualizado, demora alguns milissegundos para que todas as réplicas globais reflitam essa mudança. Leituras imediatas podem retornar valores "velhos" (Stale Data).
*   **Teorema PACELC:** Complemento essencial ao CAP. Trata o que acontece em tempos de paz (sem falha de rede), mostrando que o sistema invariavelmente fará o trade-off entre Latência (velocidade de resposta) e Consistência (garantia do dado mais atual).



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Anti-patterns & Pitfalls)
1.  **Focar em Sharding Cedo Demais:** O professor enfatizou a dor operacional de fazer backups distribuídos. Implementar Sharding em um banco relacional deve ser o último recurso de escalabilidade, adotado apenas quando o hardware vertical atingir seu limite físico e o banco não puder ser separado funcionalmente.
2.  **Tratar Cache como Banco de Dados Padrão:** Não entender o nível de durabilidade. Sequestrar o fluxo arquitetural usando Redis como fonte primária da verdade sem entender o mecanismo de persistência dele (AOF/RDB) pode resultar na perda catastrófica de informações críticas se o nó cair.
3.  **A "Bala de Prata" do Relacional ou NoSQL:** Tentar colocar modelagem complexa de relacionamentos profundos dentro de um Banco de Documentos (JSON gigante com dezenas de referências e código do lado da aplicação emulando um JOIN) ou usar um Banco Relacional para tráfego puro de logs de telemetria IoT em alto *throughput*.
4.  **Acreditar que Bancos de Dados Masterless nunca falham:** Eles oferecem altíssima disponibilidade (Cassandra), mas o preço é suportar colisões de estado, relógios não sincronizados (*Clock Skew*) e ler dados fantasma em falhas de partição temporária.

### 💡 Princípios Fundamentais (Regras de Ouro)
*   **Não desenhe a persistência antes de entender a Consulta (Query):** NoSQL como o Wide-Column (Cassandra) ou Document (DynamoDB/MongoDB) são *Query-First*. Você modela as tabelas baseadas exatamente na consulta que a UI/API precisa fazer, sacrificando a normalização e duplicando dados intencionalmente para não precisar processar nada em tempo de leitura.
*   **Aceite a Persistência Poliglota com Limites:** Não crie um zoológico de bancos de dados onde cada microsserviço usa um banco exótico diferente, apenas porque "pode". Limite o portfólio corporativo (ex: 1 Relacional, 1 Cache, 1 Documento, 1 Grafo) para manter a sustentabilidade operacional e de CI/CD.
*   **O Teorema CAP é Lei, PACELC é o Dia-a-Dia:** Na entrevista, mencionar PACELC demonstra extrema maturidade arquitetural, pois mostra que você se preocupa com a **Latência** mesmo quando o sistema não está em crise.