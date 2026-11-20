# Arquitetura Cotubify

O Cotubify é uma plataforma de auto-publicação e venda de ebooks.

O sistema permite autores técnicos conectarem repositórios Git com arquivos no formato .md para geração automatizada de ebooks nos formatos de PDF e EPUB.
Autores também tem um painel de vendas.

Permite também que os leitores acessem uam loja on-line para navegação, compra e download das obras.
Os leitores recebem recibos e notificações via email.

## Diagrama de Contexto (C4 Model)

```mermaid
    C4Context
        title Cotubify uma plataforma de autopublicação de Ebooks

        Person(autor, "Autor")
        Person(leitor, "Leitor")

        System(cotubify, "Cotubify", "Gerenciar venda de ebooks, geração e publicação de ebooks")
        System(git, "Provedor de Git Externo", "Armazenar o código fonte Mardown e Imagens dos livros")
        System(pagamento, "Gatway de Pagamentos", "Processar pagamento de venda - Pix e Cartão de Crédito")
        System(email, "Sistema de Email externo", "Enviar recibos, notificações e avisos para os usuários")

        Rel(autor, cotubify, "Configura conta, publica livros, verifica painel de vendas, solicita saque")
        Rel(leitor, cotubify, "Navega na loja, faz compras e baixa ebooks")

        Rel(cotubify, git, "Clona repositórios para obter o código fonte do livro")
        Rel(cotubify, pagamento, "Envia e recebe cobranças financeiras")
        Rel(cotubify, email, "Envia emails")
```

## Diagrama de Containers (C4 Model)

```mermaid
    C4Container
        title Diagrama de Conatiner - Cotubify

        Person(autor, "Autor")
        Person(leitor, "Leitor")

        System_Boundary(cotubify, "Cotubify", "Gerenciar venda de ebooks, geração e publicação de ebooks") {
            Container(webapp, "Front-End", "UI dos Autores e Leitores")
            Container(api, "API Principal", "Regras de negócio, catalogo de vendas, segurança, financeiro")

            Container(gerador, "Serviço Gerador de Eooks - Worker", "Java, Spring Boot", "Serviço que transofrma arquivos Markdown para PDF e EPUB")

            ContainerDb(db, "BD Relacional", "PostgreSQL - AWS RDS", "Armazenar dados financeiros dos usuários, catalogo de livros")

            ContainerQueue(broker, "Message Broker", "AWS SQS", "Fila de mensagens assincronas para desacoplar tarefas pesadas.")

            ContainerDb(cache, "Cache em Memória", "Redis - AWS Elasticache", "Armazena o catálogo de livros mais vendidos Top 100 par aleitura ultrarapida.")

            ContainerDb(storage, "Armazenamento de Arquivos", "Object Storage - AWS S3", "Capas dos Livros, PDF, EPUB - binários")
        }

        Rel(autor, webapp, "", "HTTPS")
        Rel(leitor, webapp, "", "HTTPS")

        Rel(webapp, api, "", "HTTPS/JSON")
        Rel(webapp, storage, "Faz download de ebooks e imagens", "HTTPS")

        Rel(api, cache, "Lê/Escreve catálogo em cache - Cache-Aside", "TCP")
        Rel(api, db, "Transacional, queries", "JDBC")
        Rel(api, pagamento, "Processar pagamentos", "HTTP/JSON")
        Rel(api, email, "Envia dados para enviar avisos, notificações e recibos", "HTTP/JSON")
        Rel(api, storage, "Salvar capa dos livros e PDF e gerar URLs de Download", "S3 API")
        Rel(api, broker, "Solicita geração dos Ebooks pesados", "TCP???")

        Rel(gerador, git, "Clona repositorio do livro", "SSH")
        Rel(gerador, storage, "Faz upload de PDF e EPUB gerados", "SSH")
        
        Rel(broker, gerador, "GeracaoEbookCommand - conumido pelo gerador", "TCP???")

        System_Ext(git, "Provedor de Git Externo", "Armazenar o código fonte Mardown e Imagens dos livros")
        System_Ext(pagamento, "Stripe - Gatway de Pagamentos", "Processar pagamento de venda - Pix e Cartão de Crédito")
        System_Ext(email, "AWS SES - Sistema de Email externo", "Enviar recibos, notificações e avisos para os usuários")
```

## Decisões Arquiteturais (ADRs)

- [ADR 001: Geração de Ebooks via Mensageria Assincrona](adr/adr-001-geracao-ebooks-assincrona.md)
- [ADR 002: Introdução de Cache em Memória para o Catálogo de E-Books](adr/adr-002-cache-catalogo.md)