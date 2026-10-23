# Software Design & System Design: 10 - Design Patterns - Visão Prática

## 1. Dados sobre a Aula

*   **Título Alternativo:** Padrões de Projeto Guiados por Frameworks: Singleton, Strategy, Observer e Decorator na Prática.
*   **Tópico Central:** Implementação moderna dos Design Patterns Clássicos (GoF) utilizando recursos de Inversão de Controle (IoC) e Injeção de Dependência (foco em especificações Jakarta EE / CDI, plenamente aplicáveis ao ecossistema Spring).
*   **Problema de Design:** Como eliminar lógicas procedurais (blocos `if/else` gigantescos), reduzir o acoplamento estrutural na notificação de múltiplos componentes e adicionar comportamentos adicionais a uma classe sem violar o encapsulamento ou abusar da herança.



## 2. Passo a Passo Cronológico

**[00:00 - 02:13] O Fim do Singleton Manual (IoC Containers)**
*   **Conceito de Design:** No mundo real de aplicações corporativas, não implementamos o padrão Singleton gerindo construtores privados e instâncias estáticas manuais. Nós delegamos o ciclo de vida do objeto para o *Container* (Spring, CDI, Micronaut).
*   **Implementação de Código Java:**
```java
import jakarta.enterprise.context.ApplicationScoped;

// Ao utilizar @ApplicationScoped (ou @Singleton), o framework garante
// que apenas UMA instância desta classe existirá em toda a aplicação.
@ApplicationScoped
public class Payment {
    public String processPayment(String paymentDetails) {
        return "Payment processed for: " + paymentDetails;
    }
}
```
*   **Explicação da Lógica de Design:** Deixar o container gerenciar o ciclo de vida remove *boilerplate code*, facilita o uso de *mocks* em testes unitários e evita vazamento de memória e problemas clássicos de concorrência em Singletons manuais.

**[02:14 - 13:43] Refatorando Complexidade Acidental: Padrão Strategy com Qualifiers**
*   **Conceito de Design:** O professor apresenta um anti-pattern bizarro: um serviço de pagamento com múltiplos `if (paypal) ... else if (creditCard) ...`. Isso quebra o Princípio de Responsabilidade Única e o Princípio Aberto/Fechado. A solução é o Padrão **Strategy**, instanciado dinamicamente através de Anotações/Filtros.
*   **Implementação de Código Java (A Refatoração):**

*Passo 1: Extrair o Contrato (Interface)*
```java
public interface PaymentService {
    String processPayment(String paymentDetails);
}
```

*Passo 2: Criar o Enum e a Anotação de Qualificação (O Filtro)*
```java
public enum PaymentType {
    PAYPAL, CREDIT_CARD
}

import jakarta.inject.Qualifier;
import java.lang.annotation.*;

// Esta anotação cria um metadado para o framework saber qual classe injetar
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface PaymentFilter {
    PaymentType type();
}
```

*Passo 3: As Implementações (Fechadas para modificação)*
```java
@ApplicationScoped
@PaymentFilter(type = PaymentType.PAYPAL)
public class PaypalPaymentService implements PaymentService {
    @Override
    public String processPayment(String paymentDetails) {
        return "Paypal payment processed";
    }
}

@ApplicationScoped
@PaymentFilter(type = PaymentType.CREDIT_CARD)
public class CreditCardPaymentService implements PaymentService {
    @Override
    public String processPayment(String paymentDetails) {
        return "Credit Card payment processed";
    }
}
```

*Passo 4: A Injeção Dinâmica*
```java
// O Container permite buscar dinamicamente a implementação baseada no Enum/Filtro, 
// eliminando completamente os IFs da regra de negócio.
PaymentService service = container.select(
    PaymentService.class, 
    PaymentFilter.Literal.of(PaymentType.CREDIT_CARD)
).get();

service.processPayment("Order #12345");
```
*   **Explicação da Lógica de Design:** Transformamos uma lógica imperativa (ifs) em declarativa. Se amanhã a empresa aceitar *Pix*, basta criar `PixPaymentService`, anotar com `@PaymentFilter(type = PaymentType.PIX)` e o sistema funcionará sem quebrar o código antigo. Pura aplicação de **Open/Closed Principle**.

**[13:44 - 19:28] Desacoplamento de Domínios: Padrão Observer (Event-Driven)**
*   **Conceito de Design:** O professor exibe um `NewsService` que precisava enviar notícias para um `Newspaper`, `Magazine` e `SocialMedia`. Injetar essas três classes diretamente no serviço cria forte acoplamento (a classe base precisa ser alterada toda vez que um novo canal nasce). A solução é o padrão **Observer** implementado via barramento de eventos local.
*   **Implementação de Código Java:**
```java
// 1. O Produtor do Evento (Desacoplado de quem o consome)
@ApplicationScoped
public class NewsService {
    
    // Injeta o disparador de eventos do framework
    @Inject
    private Event<String> newsEvent; 

    public void publishNews(String news) {
        // Dispara o evento e não se importa quem vai ler
        newsEvent.fire(news); 
    }
}

// 2. Os Observadores (Listeners)
@ApplicationScoped
public class Newspaper {
    // A anotação @Observes avisa o framework para invocar este método 
    // sempre que um Event<String> for disparado.
    public void printNews(@Observes String news) {
        System.out.println("Printing Newspaper: " + news);
    }
}

@ApplicationScoped
public class SocialMedia {
    public void postToSocialMedia(@Observes String news) {
        System.out.println("Social Media: " + news);
    }
}
```
*   **Explicação da Lógica de Design:** Este padrão inverte a dependência e viabiliza a escalabilidade de times. O time do `NewsService` apenas publica a novidade. O time responsável por Redes Sociais apenas escuta. Um não quebra o build do outro.

**[19:29 - Fim] Adicionando Comportamento Dinâmico: Padrão Decorator**
*   **Conceito de Design:** E se eu quiser adicionar um comportamento (ex: mandar um e-mail para o chefe avisando que uma tarefa começou) ANTES de executar a regra de negócio central, mas sem alterar a classe original e sem criar herança bizarra? Usamos o **Decorator**.
*   **Implementação de Código Java:**
```java
// 1. O Contrato
public interface Worker {
    String task(String taskName);
}

// 2. A implementação original (O Core do negócio)
@ApplicationScoped
public class Programmer implements Worker {
    @Override
    public String task(String taskName) {
        return "Coffee, code, repeat: " + taskName;
    }
}

// 3. O Decorator (Adiciona comportamento ao Programmer)
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;

@Decorator
public class Manager implements Worker {

    @Inject
    @Delegate // O CDI sabe que deve envelopar a chamada real aqui
    private Worker worker;

    @Override
    public String task(String taskName) {
        // Comportamento adicionado ANTES
        System.out.println("Sending an email to the boss about: " + taskName);
        
        // Delega para o Programmer fazer o trabalho real
        String result = worker.task(taskName); 
        
        // Comportamento adicionado DEPOIS
        System.out.println("And now I will take a coffee break.");
        
        return result;
    }
}
```
*   **Explicação da Lógica de Design:** O Decorator atua como um "wrapper" (um envelope). O framework intercepta a chamada destinada ao `Programmer`, passa pelo `Manager` primeiro, e então o `Manager` delega para a classe original. Isso respeita o princípio **Favor Composition Over Inheritance**.



## 3. Principais Conceitos

*   **Strategy Pattern:** Padrão comportamental que define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. Executado na aula removendo `if/else` usando injeção de dependência qualificada.
*   **Observer Pattern / Event-Driven:** Padrão onde um objeto (Subject/Publisher) mantém uma lista de dependentes (Observers) e notifica-os automaticamente de qualquer mudança de estado, garantindo baixo acoplamento entre módulos.
*   **Decorator Pattern:** Padrão estrutural que permite anexar comportamentos adicionais a um objeto dinamicamente (em tempo de execução via *Proxies* do framework), oferecendo uma alternativa flexível à herança para estender funcionalidades.
*   **Qualifier (Filtro):** Metadado (Anotação customizada) em frameworks IoC que permite ao desenvolvedor especificar *qual* implementação de uma interface ele deseja injetar, resolvendo ambiguidades quando existem múltiplas implementações para o mesmo contrato.



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Pitfalls)
1.  **Lógica Procedural Presa em Regras de Negócio (God Class):** Manter métodos inchados com múltiplos `if (tipo == A) ... else if (tipo == B)`. Toda vez que um novo tipo nasce, você precisa modificar a classe principal, arriscando quebrar o que já funcionava. **Solução: Extraia para um padrão Strategy.**
2.  **Acoplamento Forte em Notificações:** Uma classe que precisa executar um processo e depois notificar 5 outros subsistemas sobre isso, tendo os 5 serviços injetados em seu construtor. Isso gera classes difíceis de testar e fáceis de quebrar. **Solução: Eventos e padrão Observer.**
3.  **Abuso de Herança para adicionar pequenos comportamentos:** Criar `ProgrammerComNotificacaoDeChefe extends Programmer` fere a semântica da OO. Herança é "É um". O programador não "É UM" notificador. **Solução: Componha via Decorator.**

### 🛡️ Princípios de Orientação a Objetos
*   **Open/Closed Principle (OCP - SOLID):** Os três padrões mostrados (Strategy, Observer, Decorator) baseiam-se em deixar o código *Fechado para Modificação* (as classes antigas não são mais editadas) e *Aberto para Extensão* (basta criar uma nova classe `Strategy`, um novo método com `@Observes`, ou um novo `@Decorator`).
*   **Single Responsibility Principle (SRP - SOLID):** Ao fatiar o gigantesco `PaymentService` nas implementações específicas (`PaypalPaymentService`, `CreditCardPaymentService`), cada classe passou a ter um único motivo para mudar.
*   **Inversion of Control (IoC):** A prática de não dar `new` manualmente nos objetos, permitindo que a arquitetura escale sob o gerenciamento de metadados do container da aplicação.