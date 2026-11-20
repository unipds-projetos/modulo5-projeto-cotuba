# ADR 002: Introdução de Cache em Memória para o Catálogo de E-Books

## Status

Aprovada.

## Contexto

O Cotubify cresceu para milhões de leitores ativos. A página inicial da nossa loja, que exibe o catalogo dos "Top 100 E-Books Mais Vendidos", tornou-se o *endpoint* mais acessado de todo o sistema.

Atualmente, para montar essa lista, a API Principal faz uma consulta complexa no Banco de Dados Relacional (envolvendo tabelas de livros, autores, preços e histórico de vendas com múltiplos JOIN`s e agregações). Essa abordagem está levando o uso de CPU do banco de dados a 100% nos horários de pico. A consequência direta é o aumento inaceitável da latência para os leitores e o risco iminente de queda do banco de dados, o que paralisaria tanto as vendas quanto o painel dos autores.

A lista dos "Top 100", por sua natureza, não precisa ter uma consistência forte em tempo real (milissegundo a milissegundo).
Se o livro passar para o 1º lugar, não há problema se os usuários demorarem alguns minutos par aver essa atualização.

## Opções Consideradas

1. **Scale Up do Banco de Dados (Aumentar Máquina):**
    - Comprar um servidor de banco de dados com mais CPU e mais RAM.
    - * *Problema:* É extremamente caro, a escalabilidade vertical tem um limite físico e não resolve o problema raiz de ineficiência (fazer a mesma conta complexa milhões de vezes para obter o mesmo resultado).

2. **Materalized Views no Banco Relacional:**
    - Pré-calcular os resultados no próprio Banco PostgreSQL.
    - * *Problema:* Embora melhore a performance da query, os milhões de acessos ainda bateriam no banco de dados principal (I/O de disco e conexões de rede), competindo com as transações críticas de pagamento.

3. **Introdução de Cache em Momória (Key-Value Store):**
    - Utilizar um banco de dados chave-valor em memória (como Redis ou Memcached) entre a API Principal e o Banco de Dados Relacional. A lista "Top 100" pronta (Ex.: em formato JSON) é armazenada ali com um tempo de expiração (TTL - *Time to Live*).

## Decisão

Decidimos adotar a **Opção 3 (Introdução de Cache em Memória)**.

Escolhemos o AWS Elasticache com Redis como mecanismo de Cache.

A API Principal adotará o padrão *Cache-Aside*.

Ao receber uma requisição para a página inicial, ela primeiro verificará o Cache (Key-Value Store). Se o catalogo estiver lá (*Cache Hit*), ela o retorna imediatamente em milissegundos. Se não estiver (*Cache Miss*), ela vai até o Banco de Dados Relacional, faz a query pesada, salva o resultado no Cache com um TTL (Ex.: 5 minutos) e retorna para o usuário.

## Consequências

### Positivas

- Queda Drástica da Latência
- Alivio do Banco de Dados Principal
- Escalabilidade de Leitura -> escalar as instâncias do Redis.

### Negativas

- Dados desatualizados (Stale Data)
- Complexidade na Infra
