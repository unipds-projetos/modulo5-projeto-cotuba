# Software Design & System Design: 9 - Design Patterns

## 1. Dados sobre a Aula

*   **Título Alternativo:** A Anatomia dos Design Patterns: Resolvendo Probleos Recorrentes com Princípios de OO.
*   **Tópico Central:** Visão estrutural dos Padrões de Projeto (Design Patterns) do Gang of Four (GoF) – Criacionais, Estruturais e Comportamentais – e como eles emergem dos princípios da Orientação a Objetos.
*   **Problema de Design:** Como gerenciar a criação complexa de objetos garantindo imutabilidade (Builder), como remover ramificações condicionais infinitas (`if/else`) mudando comportamento em tempo de execução (Strategy), e como evitar o vazamento de estado global e alto acoplamento (o dilema do Singleton).



## 2. Passo a Passo Cronológico

**[00:00 - 04:54] A "Teoria Musical" do Software Design e Princípios OO**
*   **Conceito de Design:** O professor ressalta que Padrões de Projeto não devem ser decorados de forma cega. Eles são "templates" que emergem naturalmente quando você aplica princípios sólidos de Orientação a Objetos. Se você focar em **Encapsular o que varia**, **Favorecer Composição sobre Herança** e **Programar para Interfaces**, os padrões nascerão no seu código.

**[06:25 - 10:08] Singleton Pattern: O Dilema da Instância Única**
*   **Conceito de Design:** Garante que uma classe tenha apenas uma instância e provê um ponto de acesso global a ela. O professor levanta a discussão clássica: *Singleton é um Anti-Pattern?* Ele pode quebrar a testabilidade, esconder dependências e gerar estado global problemático. No entanto, é vital para recursos caros, como Pools de Conexão.
*   **Decisão Arquitetural e Implementação (Java):** Hoje, não implementamos o Singleton manualmente (com construtor privado e método `getInstance()` com `synchronized`). Delegamos isso para o motor de Injeção de Dependências (CDI ou Spring).
```java
// BEST PRACTICE: Delegando o Singleton para o Container (Inversion of Control)
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped // No Spring seria @Component / @Service
public class DatabaseConnectionPool {
    
    // O Container garante que apenas UMA instância viva na aplicação inteira
    public Connection getConnection() {
        // lógica de pool de conexões
        return new Connection();
    }
}
```
*   **Explicação da Lógica:** Usar anotações de escopo (`@ApplicationScoped`) resolve os problemas clássicos do Singleton manual. Mantém a classe altamente testável (você pode instanciá-la num teste unitário com um simples `new`) e evita o uso de dependências globais escondidas.

**[10:09 - 11:03] Factory Pattern: Desacoplando a Construção**
*   **Conceito de Design:** Delega a responsabilidade da criação de objetos (o `new`) para uma classe ou método de fábrica. Isso desacopla a regra de instanciação da lógica de negócio.
*   **Implementação de Código Java:**
```java
public interface PaymentProcessor {
    void process(MonetaryAmount amount);
}

// A classe de negócio não sabe QUAIS implementações existem, apenas usa a Fábrica
public class PaymentFactory {
    
    public static PaymentProcessor createProcessor(String method) {
        return switch (method) {
            case "CREDIT_CARD" -> new CreditCardProcessor();
            case "PIX" -> new PixProcessor();
            default -> throw new IllegalArgumentException("Unknown method");
        };
    }
}
```

**[11:04 - 12:20] Strategy Pattern: Comportamento Dinâmico sem Ifs**
*   **Conceito de Design:** Permite variar o comportamento de um algoritmo em tempo de execução sem modificar a classe que o utiliza. É a cura definitiva para o código cheio de blocos `if/else` ou `switch`. Ele atende perfeitamente ao princípio **Open/Closed**.
*   **Implementação de Código Java (via Interfaces e Polimorfismo):**
```java
// 1. A interface Strategy
public interface ShippingStrategy {
    MonetaryAmount calculate(Order order);
}

// 2. As implementações concretas (Fechadas para modificação)
public class SedexShipping implements ShippingStrategy {
    public MonetaryAmount calculate(Order order) { /* lógica sedex */ }
}

// 3. O Contexto (O Serviço)
public class CheckoutService {
    // O comportamento é injetado dinamicamente
    public MonetaryAmount calculateTotal(Order order, ShippingStrategy shippingMethod) {
        return shippingMethod.calculate(order);
    }
}
```

**[12:56 - 16:04] Builder Pattern: Objetos Complexos e Imutabilidade**
*   **Conceito de Design:** Utilizado quando uma classe precisa de muitos parâmetros na criação (o anti-pattern do *Telescoping Constructor*). O Builder não apenas limpa a instanciação, mas em DDD, é a ferramenta perfeita para garantir a **Imutabilidade** de um *Value Object* ou *Aggregate Root*.
*   **Implementação de Código Java:**
```java
public class UserAccount {
    // Campos finais garantem que, uma vez criado, o objeto é imutável
    private final String username;
    private final String email;
    private final String role; // opcional

    // Construtor privado: Ninguém dá 'new' diretamente na entidade
    private UserAccount(Builder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.role = builder.role;
    }

    public static class Builder {
        private String username;
        private String email;
        private String role = "GUEST"; // Default

        public Builder username(String username) {
            this.username = username; return this;
        }
        public Builder email(String email) {
            this.email = email; return this;
        }
        public Builder role(String role) {
            this.role = role; return this;
        }

        public UserAccount build() {
            // Validações de negócio ocorrem AQUI, antes da criação real
            Objects.requireNonNull(username);
            Objects.requireNonNull(email);
            return new UserAccount(this);
        }
    }
}

// Uso fluente:
// UserAccount user = new UserAccount.Builder().username("otavio").email("o@os.com").build();
```

**[17:17 - 19:28] Padrões Estruturais: Decorator e Proxy**
*   **Decorator:** Adiciona responsabilidades a um objeto dinamicamente sem alterar seu código-fonte, englobando-o em um "Wrapper". Aplica o princípio de *Composição sobre Herança*.
*   **Proxy:** Um representante (ou substituto) que controla o acesso ao objeto real. O professor destaca que frameworks modernos (Hibernate para Lazy Loading, Spring/CDI para transações via `@Transactional`) baseiam-se pesadamente na geração de Proxies em tempo de execução (via CGLIB ou Byte Buddy).



## 3. Principais Conceitos

*   **GoF (Gang of Four):** Referência aos autores do livro "Design Patterns" de 1994, que catalogaram 23 soluções padronizadas. Dividem-se em: **Criacionais** (lidam com a instanciação), **Estruturais** (lidam com a composição de classes) e **Comportamentais** (lidam com a comunicação e fluxo de controle).
*   **Telescoping Constructor:** Um *Code Smell* onde uma classe possui múltiplos construtores encadeados (`Construtor(A)`, `Construtor(A, B)`, `Construtor(A, B, C)`). Resolvido com o padrão **Builder**.
*   **Lazy Loading:** Conceito onde a inicialização ou a busca de um recurso caro (como dados do banco) é adiada até o momento em que é estritamente necessária. Geralmente implementado através do padrão **Proxy**.
*   **Inversion of Control (IoC):** Princípio em que o fluxo de controle de um sistema é invertido: em vez do seu código instanciar as dependências (`new Servico()`), um *Container* (como Spring ou Jakarta EE) as cria e as injeta para você.



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Pitfalls)
1.  **O Abuso do Singleton Manual:** Criar classes com `public static MeuServico getInstance()` cria dependências fortíssimas no código, dificultando imensamente a criação de *Mocks* durante os testes de unidade. **Solução:** Use Injeção de Dependência.
2.  **Lógica Procedural Presa em Regras de Negócio:** Ter métodos inchados com múltiplos `if (tipo == A) { ... } else if (tipo == B) { ... }`. Toda vez que um novo tipo nasce, você precisa modificar a classe principal, arriscando quebrar o que já funcionava. **Solução:** Padrão Strategy.
3.  **Falta de Caching e Gargalos em Lotes (Memory Leaks):** O professor contou uma anedota de um processo de *Batch* que recriava recursos caros via SPI sem fazer cache, estourando a memória (`OutOfMemoryError`). O uso de Padrões Criacionais (Singleton ou Cache Proxies) é vital para preservar a *Performance* em loops pesados.

### 🛡️ Princípios de Orientação a Objetos
*   **Open/Closed Principle (Princípio Aberto/Fechado do SOLID):** O Padrão **Strategy** é a personificação deste princípio. Você pode adicionar novas estratégias de frete ou pagamento apenas criando novas classes (aberto para extensão), sem tocar na classe de `Checkout` existente (fechada para modificação).
*   **Favor Composition Over Inheritance:** Mencionada fortemente ao lado dos padrões estruturais como o **Decorator**. Estender classes base para obter reaproveitamento de código gera o anti-pattern do Acoplamento Forte (Fragile Base Class). Decoradores encapsulam instâncias ("TEM UM") no lugar de herdá-las ("É UM").
*   **Encapsulate What Varies:** O Princípio basilar por trás do **Factory Pattern**. Se a forma de criar um objeto muda com base em regras dinâmicas, retire essa lógica da sua classe de negócio e isole-a em uma fábrica especializada.