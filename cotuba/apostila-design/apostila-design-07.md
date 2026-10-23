# Software Design & System Design: 7 - Visão Geral de Práticas de Código

## 1. Dados sobre a Aula

*   **Título Alternativo:** Fundamentos de Code Design: Princípios Essenciais (KISS, YAGNI, DRY), Acoplamento e Proteção de Invariantes.
*   **Tópico Central:** Princípios fundamentais de escrita de código Orientado a Objetos que antecedem os Padrões de Projeto. Foco na eliminação de "Code Smells" (Primitive Obsession, God Class) e na transição de um Modelo Anêmico para um Modelo de Domínio Rico (Rich Domain Model).
*   **Problema de Design:** Como evitar a dívida técnica gerada por *Overengineering* (Complexidade Acidental), vazamento de escopo (*Train Wrecks*), acoplamento forte por herança indevida e quebra de encapsulamento do estado interno dos objetos.



## 2. Passo a Passo Cronológico

**[01:21 - 03:45] A "Teoria Musical" do Software Design**
*   **Conceito de Design:** Antes de decorar Padrões de Projeto (Design Patterns) de forma cega, é preciso entender os **Princípios**. O professor usa a analogia musical: aprender os princípios (escala, notas) permite que você entenda o *porquê* de um acorde (Design Pattern) existir. Decorar padrões sem entender princípios leva ao *Overengineering*.

**[16:33 - 19:12] KISS & YAGNI: O Combate ao Overengineering**
*   **Conceito de Design:** 
    *   **KISS (Keep It Simple, Stupid):** A simplicidade é o último grau da sofisticação. Evite abstrações precoces. O professor destaca o anti-pattern de criar uma `Interface` quando só existirá uma única implementação. Use uma classe simples e refatore para interface *apenas quando necessário*.
    *   **YAGNI (You Aren't Gonna Need It):** Otimização prematura é a raiz de todo o mal. Não adicione código hoje para uma *feature* que "talvez" seja necessária no futuro.

**[19:13 - 21:13] DRY (Don't Repeat Yourself) - Reuso de Conhecimento**
*   **Conceito de Design:** DRY não é apenas sobre não copiar e colar linhas de código, mas sobre **não duplicar o conhecimento/regra de negócio**.
*   **Implementação de Código Java (O Anti-Pattern):**
```java
// Anti-Pattern: Lógica de negócio (cálculo de 10%) espalhada
class CheckoutService {
    BigDecimal calculateFinalPrice(BigDecimal price) {
        // A regra de negócio vaza para o serviço
        return price.subtract(price.multiply(BigDecimal.valueOf(0.10))); 
    }
}

class PromotionService {
    BigDecimal promoPrice(BigDecimal price) {
        // Duplicação do conhecimento do que é o desconto
        return price.subtract(price.multiply(BigDecimal.valueOf(0.10))); 
    }
}
```

**[21:14 - 25:09] Make a Type (DDD: Evitando Primitive Obsession com Value Objects)**
*   **Conceito de Design:** Em DDD, chamamos isso de **Value Object** e combate ao *Primitive Obsession*. Se um valor primitivo (`double`, `BigDecimal`, `String`) tem comportamento, regras de validação ou significado para o domínio, ele deve virar um Tipo (Classe).
*   **Implementação de Código Java (A Refatoração para Domínio Rico):**
```java
// DDD Concept: Value Object representando um conceito do Domínio
public class Discount {
    public static final Discount NONE = new Discount("No Discount", 0.0);
    
    private final String reason;
    private final double rate;

    // Construtor, validações...

    // O Comportamento (cálculo) pertence ao próprio tipo! (Rich Model)
    public MonetaryAmount applyTo(MonetaryAmount price) {
        return price.multiply(1.0 - rate);
    }
}

// O Serviço agora apenas orquestra, não calcula (Alta Coesão)
class CheckoutService {
    public MonetaryAmount calculateFinalPrice(MonetaryAmount price, Discount discount) {
        return discount.applyTo(price);
    }
}
```
*   **Explicação da Lógica:** Tiramos o cálculo da camada anêmica de Serviço e encapsulamos no objeto `Discount`. Isso garante que qualquer lugar do sistema que precise aplicar um desconto use a mesma regra, centralizada e testável.

**[25:10 - 26:01] Composition Over Inheritance (Composição sobre Herança)**
*   **Conceito de Design:** Herança deve ser usada apenas para relações de "É UM" (*Is-a*), nunca apenas para reutilizar código.
*   **Implementação de Código Java:**
```java
// Anti-Pattern Bizarro (Herança por conveniência)
class Sea {
    boolean hasSalt() { return true; }
}

class Cake extends Sea { 
    // Errado! Um bolo NÃO É um mar. Quebra o Princípio de Liskov (SOLID).
}

// Refatoração (Composição - "TEM UM")
class Cake {
    private Salt salt = new Salt(); // Bolo TEM sal
}
```

**[26:02 - 27:49] Law of Demeter (Princípio do Menor Conhecimento)**
*   **Conceito de Design:** Um objeto só deve conversar com seus "amigos diretos", não com "estranhos". 
*   **Implementação de Código Java (Evitando Train Wrecks):**
```java
// Anti-Pattern: Train Wreck (Acoplamento profundo)
// O Order precisa saber a estrutura interna de Customer e Address. Se Address mudar, Order quebra.
String zip = order.getCustomer().getAddress().getZipCode();

// Refatoração Correta (Delegação)
// Order expõe apenas o que é necessário.
String zip = order.getZipCode(); 
```

**[27:50 - 29:12] Coesão vs Acoplamento (God Class)**
*   **Conceito de Design:** Alta coesão (foco em uma única tarefa clara) e baixo acoplamento (saber o mínimo possível sobre outras classes).
*   **O Anti-Pattern (God Service Class):** Uma classe `UserService` que faz registro (lógica de usuário), envia e-mail (infraestrutura), gera PDF (relatório) e loga no sistema. Ela fere o **Single Responsibility Principle (SRP)** do SOLID.

**[29:13 - 31:00] Encapsulamento (DDD: Protegendo os Invariantes do Aggregate Root)**
*   **Conceito de Design:** No DDD, uma Entidade Raiz (*Aggregate Root*) deve proteger seu estado interno (Invariantes). Vazamento de referências de memória permite que outras classes alterem o estado sem passar pelas regras de negócio.
*   **Implementação de Código Java (CRÍTICO):**
```java
// Exemplo clássico de Aggregate Root no DDD
public class Order {
    private final List<Item> items = new ArrayList<>();

    // Comportamento rico: Apenas a Order sabe como adicionar um item (regras de limite, preço, etc)
    public void addItem(Item item) {
        // Validações de negócio, regras de pricing...
        items.add(item);
    }

    // PROTEÇÃO DE INVARIANTE: 
    // Se retornássemos a lista original, qualquer um faria order.getItems().add(...) burlando o método addItem().
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }
}
```
*   **Explicação da Lógica:** O professor usa `Collections.unmodifiableList` (ou `List.copyOf` nas versões mais recentes do Java). Isso blinda a classe `Order`. Nenhuma outra classe consegue manipular a lista de itens diretamente, forçando o uso do método de negócio `addItem()`.



## 3. Principais Conceitos

*   **Value Object (Objeto de Valor):** Um pequeno objeto que representa um conceito descritivo do domínio sem identidade conceitual (ex: `Discount`, `Money`, `ZipCode`). Eles encapsulam primitivos (*Primitive Obsession*) e suas lógicas de formatação/cálculo. Devem ser **imutáveis**.
*   **Rich Domain Model (Modelo de Domínio Rico):** Abordagem onde a lógica de negócios, validações e comportamentos residem diretamente nas Entidades e Value Objects, em oposição ao *Modelo Anêmico*, onde entidades são apenas sacos de *getters/setters* e a lógica fica em "Services".
*   **Aggregate Root (Raiz de Agregação):** (Exemplificado pela classe `Order`). É a principal entidade que controla o acesso e garante a consistência (invariantes) dos objetos filhos (como `Item`). Acesso a coleções internas deve ser sempre em modo *read-only*.
*   **God Class (Classe Deus):** Um *Code Smell* onde uma classe centraliza excessiva responsabilidade (fere a Coesão). Fica enorme, difícil de ler, testar e manter.



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Pitfalls)
1.  **Primitive Obsession (Obsessão por Primitivos):** Usar `BigDecimal` para representar Preço e Desconto em todo o sistema. Isso espalha a lógica matemática (como na aula) e impede a evolução do código. Crie os tipos `MonetaryAmount` e `Discount`.
2.  **Train Wreck (Acoplamento em Cadeia):** Encadeamentos como `a.getB().getC().doSomething()`. Isso torna o código extremamente frágil. Se a estrutura de `B` mudar, `A` quebra, mesmo que não devesse se importar com a estrutura de `B`. Aplique a **Lei de Demeter** delegando métodos.
3.  **Vazamento de Referência de Coleção:** Retornar listas internas diretamente via *Getters*. Um desenvolvedor desavisado pode modificar a lista de fora da entidade, pulando as regras de negócio de inclusão.

### 🛡️ Princípios de Orientação a Objetos
*   **SRP (Single Responsibility Principle):** Mencionado no combate à *God Class* (`UserService`). Cada classe deve ter apenas uma razão para mudar.
*   **LSP (Liskov Substitution Principle):** Abordado indiretamente no erro absurdo do `Cake extends Sea`. Subtipos devem ser substituíveis por seus tipos base. Um bolo não pode substituir um mar.
*   **Encapsulamento / Information Hiding:** Não é apenas colocar atributos como `private`. É esconder os detalhes de *como* o objeto trabalha internamente, forçando o mundo externo a interagir apenas através de métodos de negócio com intenção clara (`applyTo()`, `addItem()`).