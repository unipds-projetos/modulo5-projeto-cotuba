# ADR 001: Geração de Ebooks via Mensageria Assincrona

## Status

Aprovada.

## Contexto

O Cotubify trouxe um aumento massivo de trafego.

Quando o autor publica a API Principal faz uma chamada HTTP sincrona para o Serviço Gerador de Ebooks e fica aguardando a geração de PDF e EPUB.
Como esse processo é intensivo em CPU e teve em média 20 segundos, as Threads da API Principal ficam bloqueadas.
No serviço Gerador o uso de CPU e memória durante a geração de Ebook é intensa.

Lentidão de Timeouts na plataforma.

## Opções Consideradas

1. **Manter sincrono e ter escalabilidade horizontal**
    - Adicionar mais servidores -> Apenas adia o gargalo, Threads continuariam a ser desperdiçadas aguardando o processamento dos Ebooks.

2. **Mensageria com Fila Assincrona**
    - A API Principal atua como um Publisher de um comando de geração de Ebooks (`GeracaoEbookCommand`) para uma fila no nosso Message Broker.
    - A API Principal responde para o Front-End de forma imediata (`HTTP 202 Accepted`).
    - O Serviço gerador passa a atuar como um Consumer / Worker, puxando as filas das mensagens no próprio ritmo.

## Decisão

Decidimos pela opção 2 (Mensageria com Fila Assincrona)

Será implementado um Message Broker na nossa infraestrutura.

Message Broker escolhido será o AWS SQS, alinhado com o favorecimento da plataforma AWS.

## Consequências

### Positivas

- Alta disponibilidade de vazão: A API Principal não fica mais bloqueada respondendo em milissegundos.
- Libera imediatamente para atender outras requisições.
- Resiliencia em picos de carga -> Se 1.000 autores publicarem ao mesmo tempo, as mensagens irão para as filas. O Gerador i´ra consumindo aos poucos, sem ficar sobrecarregado.
- Escalabilidade Horizontal -> Se a fila aumentar demais, podemos alocar mais instâncias do servidor Gerador.

### Negativas

- Complexidade de infra: provisionar, manter e configurar o Message Broker.
- Mudança na UX -> Experiência do usuário passa a ser assincrona ("Em processamento"). Impacto será mínimo.
