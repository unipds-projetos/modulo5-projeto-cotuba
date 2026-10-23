#Arquitetura Cotubify

O cotubify é uma plataforma de auto publicação e venda de ebooks.

O sistema permite autores técnicos e profissionais de TI publicarem seus livros e venderem seus livros,
no formato markdowns para geração automatizada de ebooks. nos formatos pdf e epub.
Permitindo também que os leitores acessem uma loja online para navegação e download das obras.
Painel de vendas dos autores
Os leitores recebem recibos e notificações via e-mail.

Integração com o github
Integração com o stripe para pagementos
Integração com o mailchimp para envio de emails

#Arquitetura Cotubify
## Diagrama de contexto (C4 Model)
'mermaid
    C4Context
        title Cotubify, uma plataforma de autopublicação de books
        Person(autor, "Autor", "Escreve livros em markedown, configura repositórios git com esses livros, acompanha o painel de livros e solicitar eventuais saques")
        Person(leitor, "Leitor", "Navega no catálago de ebooks, comprar, fazer downmoad de pdfs e receber notificações por e-mail")
        System(cotubify, "Cotubify", "Gerenciar venda de ebooks, geração e venda de ebooks em PDF ou EPUB.")

        Rel(autor, cotubify, "Configura conta, publica livros, verifica painel de vendas e solicita saques.")
        Rel(leitor, cotubify, "Naveha na loja, faz compras e navega no site")
    
        Rel(cotubify, git, "Clona o repositório para obter código fonte do livro")
        Rel(cotubify, pagamento, "Envia a recebe cobranças financeiras")
        Rel(cotubify, email, "Envia e-mails")
        System_Ext(git, "Provedor Git externo", "Armazena o código fonte")
        System_Ext(pagamento, "Gateway de pagamentos", "Processamento de pagamento - PIx ou Cartão de Crédito")
        System_Ext(email, "Sistema de e-mail externo", "Enviar recibos, notificações e avisos.")



## Diagram de Containers
'mermaid
    C4Container
        title Diagrama de Container - Cotubify
        Person(autor, "Autor", "Escreve livros em markedown, configura repositórios git com esses livros, acompanha o painel de livros e solicitar eventuais saques")
        Person(leitor, "Leitor", "Navega no catálago de ebooks, comprar, fazer downmoad de pdfs e receber notificações por e-mail")


        System_Boundary(cotubify, "Cotubify", "Gerenciar venda de ebooks, geração e venda de ebooks em PDF ou EPUB.")
        {   Container(api,"API Principal", "Java, SpringBoot","Regras de negócio, catálogo de vendas, segurançam, financeiro")
            Container(webap,"Frontend","","UI para os autores e leitores")
            
            Container(gerador,"Serviço de geração de ebook","Java, Spring Boot","Serviço que é o Motor da transformação de livros Markdown para pdf e epub")
    
            ContainerDb(db,"BD Relacional","PostgreSQL (AWS RDS)","Armazenar dados financeiros dos usuários, catálogos.")
    
            ContainerDb(storage,"Armezenamento de arquivos","Object Storage (AWS S3)","Capas dos livros, PDF, EBUP binários")
        }
        
        Rel(autor,webap,"","HTTPS")
        Rel(leitor,webap,"","HTTPS")
        
        Rel(webap,api,"","HTTPS/JSON")
        Rel(webap,storage,"Faz download dos ebooks e imagens","HTTPS")
        
        Rel(api,db,"Transaciona com o banco de dados","JDBC")
        Rel(api,pagamento,"Processa pagamentos","TTP/JSON")
        Rel(api,storage,"Salva capa dos livros e gera URLs de download","S3 API")
        Rel(api,email,"Envia dados de avisos, notificações e recibos","HTTPS/JSON")
        Rel(api,gerador,"Solicitar geração de arquivos pesados","HTTPS/JSON")
    
        Rel(gerador,git,"Clonar repositório do livro","SSH")
        Rel(gerador,storage,"Faz upload de pdf/epubs gerados","SSH")
    
        System_Ext(git, "Provedor Git externo", "Armazena o código fonte")
        System_Ext(pagamento, "Gateway de pagamentos", "Stripe - Processamento de pagamento - PIx ou Cartão de Crédito")
        System_Ext(email, "Sistema de e-mail externo", "Enviar recibos, notificações e avisos.")