# Software Design & System Design: 8 - Visão Geral das Práticas de Código na Prática

## 1. Dados sobre a Aula

*   **Título Alternativo:** Código de Alta Coesão: Combatendo Complexidade Acidental, Protegendo Invariantes e Package by Feature.
*   **Tópico Central:** Princípios essenciais de Clean Code e fundamentos de DDD aplicados ao Java. Foco na eliminação de interfaces prematuras (KISS/YAGNI), proteção do estado interno (Encapsulamento de Aggregate Roots), coesão arquitetural através de pacotes (Package by Feature) e aplicação do Padrão Strategy utilizando Enums.
*   **Problema de Design:** Como evitar a complexidade acidental (Overengineering), o vazamento de regras de negócio através de coleções mutáveis, o acoplamento estrutural gerado pela organização de pacotes por camadas físicas (Layers) e o uso excessivo de condicionais lógicas.



## 2. Passo a Passo Cronológico

**[01:15 - 06:00] KISS & YAGNI: O Anti-Pattern da Interface "Impl"**
*   **Conceito de Design:** É um vício comum criar uma `Interface` (ex: `PaymentService`) e uma única implementação atrelada a ela (`PaymentServiceImpl`) baseando-se na premissa do "vai que no futuro eu precise". Isso gera complexidade acidental. No Clean Code, o sufixo `Impl` é considerado um *Code Smell* por não ter significado no domínio. Inicie com classes concretas simples e extraia interfaces apenas quando houver polimorfismo real ou necessidade estrita de contrato arquitetural (Ports & Adapters).
*   **Implementação de Código Java (Refatoração para Simplicidade):**
```java
// ANTI-PATTERN: Overengineering precoce
// public interface PaymentService { void process(String details); }
// public class PaymentServiceImpl implements PaymentService { ... }

// BEST PRACTICE: KISS (Keep It Simple, Stupid)
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped // Anotação CDI (equivalente ao @Service/@Component do Spring)
public class PaymentService {
    
    public void process(String paymentDetails) {
        System.out.println("Processing payment: " + paymentDetails);
    }
}
```
*   **Explicação da Lógica:** Os frameworks modernos (CDI, Spring) conseguem criar *proxies* transacionais e injetar dependências diretamente em classes concretas. Não há necessidade técnica de interfaces genéricas.

**[06:00 - 11:20] Encapsulamento e DDD: Protegendo Invariantes de um Aggregate Root**
*   **Conceito de Design:** Um dos erros mais destrutivos na Orientação a Objetos é expor o estado interno (Invariantes) de uma entidade raiz (*Aggregate Root*) através de *Getters* que retornam coleções mutáveis. Se uma classe externa pode dar um `.clear()` ou `.add()` na lista de membros de um `Team`, a classe `Team` perdeu o controle sobre suas próprias regras de negócio.
*   **Implementação de Código Java (A Blindagem da Classe):**
```java
public class Team {
    private List<String> members = new ArrayList<>();

    // 1. Construtor e inicialização correta...

    // 2. Comportamento Rico (Intenção clara no domínio)
    public void addMember(String member) {
        // Regra de negócio e validação centralizada na entidade
        Objects.requireNonNull(member, "Member cannot be null");
        this.members.add(member);
    }

    // PROTEÇÃO DE INVARIANTE (CRÍTICO EM DDD)
    public List<String> getMembers() {
        // Retorna uma cópia read-only. Modificações externas lançarão UnsupportedOperationException
        return Collections.unmodifiableList(this.members); 
    }
}
```
*   **Explicação da Lógica:** Ao retornar `Collections.unmodifiableList()`, transformamos o `Team` em um verdadeiro *Aggregate Root*. Qualquer classe que precise adicionar um membro é forçada a invocar `team.addMember()`, passando obrigatoriamente pela validação de negócio (o `Objects.requireNonNull`).

**[11:20 - 17:35] Primitive Obsession e o Perigo dos Default Setters**
*   **Conceito de Design:** Em Entidades gerenciadas por ORM (JPA/Hibernate), o `id` é gerado pelo banco de dados. Nunca se deve criar um `setId()` público, pois isso sugere que a aplicação pode manipular a identidade da entidade, o que fere o ciclo de vida do dado. O professor também alerta para o uso excessivo de `String` para e-mails e CPFs, incentivando a criação de *Value Objects* (`Email`, `Cpf`).
*   **Implementação de Código Java:**
```java
public class Player {
    // ID sem Setter! Apenas Getter.
    private Long id; 
    
    // Propriedade imutável no ciclo de vida deste objeto
    private final String name;

    public Player(String name) {
        this.name = name;
    }
    
    public Long getId() { return id; }
    public String getName() { return name; }
}
```

**[17:35 - 24:45] Coesão Arquitetural: Package by Layer vs. Package by Feature**
*   **Conceito de Design:**
    *   *Package by Layer (Anti-pattern estrutural):* Agrupar classes por tipo técnico (`controllers`, `services`, `repositories`). Isso destrói o encapsulamento, forçando com que `UserService` seja `public` para ser acessado por `UserController` em outro pacote.
    *   *Package by Feature (DDD Bounded Context):* Agrupar classes pelo conceito de negócio (`users`, `payment`).
*   **Explicação da Lógica de Design:** Ao adotar o *Package by Feature*, todos os componentes referentes a Usuários ficam no mesmo pacote. Isso permite que `UserRepository` e `UserService` tenham visibilidade `default` (package-private). Apenas o `UserController` ou uma `UserFacade` fica `public`. Isso cria uma fronteira estrita, preparando o terreno perfeitamente caso esse pacote precise ser extraído para um Microsserviço no futuro.

**[24:45 - 27:00] Padrão Strategy Nativo utilizando Enums Avançados**
*   **Conceito de Design:** Em Java, Enums são muito mais do que constantes; são instâncias de classes poderosas. Em vez de criar blocos `if/else` gigantescos ou uma classe `Strategy` separada para cada pequena operação, podemos incorporar o Polimorfismo diretamente no Enum.
*   **Implementação de Código Java:**
```java
// Aplicando o padrão Strategy dentro de um Enum
public enum Operation {
    
    SUM {
        @Override
        public double execute(double a, double b) {
            return a + b;
        }
    },
    SUBTRACT {
        @Override
        public double execute(double a, double b) {
            return a - b;
        }
    }; // ... MULTIPLY, DIVIDE

    // Método abstrato que força cada constante a prover sua própria implementação
    public abstract double execute(double a, double b);
}
```
*   **Explicação da Lógica de Design:** Isso atende perfeitamente ao **Open/Closed Principle (SOLID)**. Se precisarmos adicionar uma nova operação (ex: `POWER`), criamos uma nova constante no Enum e implementamos seu método, sem alterar o código das operações existentes e sem criar uma hierarquia massiva de classes.



## 3. Principais Conceitos

*   **Aggregate Root (Raiz de Agregação):** A entidade principal que atua como porta de entrada para um grafo de objetos associados (ex: `Team` controlando `members`). É responsável por garantir a consistência (invariantes) das mudanças de estado.
*   **Invariantes:** Regras de negócio que devem ser sempre verdadeiras para que o objeto esteja em um estado válido (ex: "O membro do time não pode ser nulo", "O valor não pode ser negativo").
*   **Package by Feature:** Estratégia de modularização onde os pacotes refletem domínios de negócio (`com.app.pagamento`, `com.app.estoque`) em vez de camadas de infraestrutura. Essencial para design modular e extração de microsserviços.
*   **Strategy Pattern (Via Enum):** Padrão comportamental que permite definir uma família de algoritmos, encapsulá-los e torná-los intercambiáveis. No Java, métodos abstratos em Enums são uma forma concisa e *type-safe* de implementar esse padrão.
*   **Primitive Obsession:** Sintoma de design pobre onde se utiliza tipos primitivos (`String`, `double`) para representar conceitos ricos do domínio (como Senha, Moeda, Email).



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Pitfalls)
1.  **A "Doença" do `...Impl`:** Criar a trindade `Interface -> Impl -> DTO` por padrão, sem pensar na real necessidade. Isso apenas polui a base de código e dificulta a navegação na IDE. Siga o princípio **KISS**.
2.  **Vazamento de Coleções (Quebra de Encapsulamento):** Retornar `List`, `Set` ou `Map` diretamente via métodos Getter. O encapsulamento falha e a classe perde o domínio sobre si mesma. Use `Collections.unmodifiableList()` ou `List.copyOf()` (Java 10+).
3.  **Big Decimal vs Moneta API:** Manipular dinheiro diretamente com `BigDecimal` espalha verificações lógicas complexas. Sempre prefira criar ou utilizar bibliotecas consolidadas (como o JSR 354 - Java Money and Currency API / Moneta) para lidar com *Currencies* e operações financeiras.
4.  **Tudo Público (God Visibility):** Quando usamos *Package by Layer*, somos obrigados a colocar a palavra `public` em todas as classes. Isso permite que um dev júnior no pacote `pedidos` injete diretamente um `UserRepository` do pacote `users`, pulando a camada de serviço. *Package by Feature* trava isso usando visibilidade *package-private*.

### 🛡️ Princípios de Orientação a Objetos
*   **Information Hiding / Encapsulamento:** Demonstrado na prática ao impedir que classes externas mutem a lista do `Team`.
*   **Open/Closed Principle (OCP):** Demonstrado no `Operation Enum`. O código está fechado para modificação (a estrutura do Enum não muda), mas aberto para extensão (podemos adicionar novas operações matemáticas).
*   **Alta Coesão e Baixo Acoplamento:** Mover do modelo de camadas (Layer) para o modelo de domínio (Feature) agrupa naturalmente o que muda junto, aumentando a coesão do pacote e reduzindo o acoplamento externo.