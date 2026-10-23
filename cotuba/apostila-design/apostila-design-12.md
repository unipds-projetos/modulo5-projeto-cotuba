# Software Design & System Design: 12 - Conhecendo o JMolecules

## 1. Dados sobre a Aula

*   **Título Alternativo:** Materializando o DDD no Código: Governança Arquitetural com jMolecules e ArchUnit.
*   **Tópico Central:** Implementação de padrões do **DDD Tático** (`Entity`, `Value Object`, `Aggregate Root`, `Repository`, `Service`) de forma agnóstica a frameworks de infraestrutura (como Spring ou Hibernate) utilizando a biblioteca **jMolecules**, e a validação contínua dessas regras utilizando o **ArchUnit**.
*   **Problema de Design:** Como evitar a degradação arquitetural e o "Furacão Tático". O problema resolvido é a falta de rastreabilidade do design no código-fonte. Sem metadados claros, os desenvolvedores quebram as regras do DDD (ex: Entidades sem identidade, Repositórios acoplados a banco de dados em vez de coleções de domínio) e a arquitetura entra em colapso silenciosamente.



## 2. Passo a Passo Cronológico

**[00:00 - 01:50] Setup e o Conceito de Governança como Código**
*   **Conceito de Design:** O Arquiteto não deve depender apenas de documentação (PDFs, wikis) para garantir que a arquitetura seja seguida. Utilizando `jMolecules` (para anotar as intenções arquiteturais) e `ArchUnit` (para testar essas intenções no CI/CD), criamos uma **Função de Adequação (*Fitness Function*)**. O código se auto-valida.
*   **Implementação de Código Java (A Regra de Ouro):**
```java
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;

// Classe que garante que as regras do DDD nunca sejam violadas
@AnalyzeClasses(packages = "expert.os.examples")
public class ArchitectureValidationTest {

    @ArchTest
    ArchRule dddRules = JMoleculesDddRules.all(); // Valida Entities, Aggregates, Value Objects...

    @ArchTest
    ArchRule layerRules = JMoleculesArchitectureRules.ensureLayering(); // Valida isolamento de camadas
}
```

**[01:51 - 03:35] Modelando uma Entity e Protegendo a Identidade**
*   **Conceito de Design:** Uma `Entity` no DDD não é uma tabela de banco de dados (isso seria `@jakarta.persistence.Entity`). Uma Entidade no domínio é um objeto cujo ciclo de vida possui continuidade e é distinguido pela sua **Identidade**, não pelos seus atributos. Se uma classe é anotada como `@Entity`, o ArchUnit exigirá que ela tenha um atributo marcado com `@Identity`.
*   **Implementação de Código Java:**
```java
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
public class Card {
    
    // O jMolecules/ArchUnit forçará a existência de um identificador.
    // Sem ele, o build falha.
    @Identity 
    private String id;
    
    private String number;
    private String holderName;
    
    // ... construtores e métodos de negócio
}
```

**[03:36 - 05:33] Value Objects: O Fim da Obsessão por Primitivos**
*   **Conceito de Design:** Um `ValueObject` mede, quantifica ou descreve uma coisa no domínio. Ele **não possui identidade** e é imutável. Se dois Value Objects têm os mesmos atributos, eles são a mesma coisa. O professor demonstra de forma muito inteligente que **Enums** em Java são casos de uso perfeitos para Value Objects simples.
*   **Implementação de Código Java:**
```java
import org.jmolecules.ddd.annotation.ValueObject;

// Enums são imutáveis por natureza e comparados por valor, 
// encaixando-se perfeitamente no conceito de Value Object do DDD.
@ValueObject
public enum CreditCardType {
    VISA,
    MASTERCARD
}
```

**[05:34 - 06:50] Aggregate Root: A Fronteira de Consistência**
*   **Conceito de Design:** O `Aggregate Root` (Raiz de Agregação) é uma Entidade especial. Ela atua como um portão de entrada (Gateway) para um grupo de objetos relacionados. O domínio externo nunca deve manipular um objeto interno diretamente; toda orquestração de regras de negócio passa pelo Aggregate Root.
*   **Implementação de Código Java:**
```java
import org.jmolecules.ddd.annotation.AggregateRoot;
import java.util.List;

// Order orquestra seus Products. Outras partes do sistema não 
// devem alterar os products sem passar pelas regras de negócio da Order.
@AggregateRoot
public class Order {
    
    @Identity
    private String id;
    
    private List<Product> products;
    
    // ... métodos de delegação e proteção de invariantes
}
```

**[06:51 - 09:55] Repository vs. DAO (Data Access Object)**
*   **Conceito de Design:** Esta é uma das confusões mais clássicas da engenharia de software. 
    *   **DAO (Padrão de Infraestrutura):** Abstrai operações puras de banco de dados (`insert`, `update`, `delete`). É focado em tabelas.
    *   **Repository (Padrão de Domínio - DDD):** Emula uma **coleção de objetos em memória** (`save`, `findById`, `findAll`). O domínio não sabe e não se importa se os dados vão para o MySQL, Kafka ou um arquivo de texto.
*   **Implementação de Código Java:**
```java
import org.jmolecules.ddd.annotation.Repository;

// Interface puramente orientada ao domínio. Não vaza JDBC, JPA ou SQL.
@Repository
public interface CreditCardRepository {
    
    // Lida com o objeto de domínio (Card), não com a tabela do banco
    void save(Card card);
    
    Card findById(String id);
}
```
*   **Explicação da Lógica:** O jMolecules permite expressar que essa interface é um Repositório do Domínio. A implementação real (`CreditCardRepositoryImpl` usando Spring Data ou Hibernate) residirá em uma camada externa (Infrastructure Layer), mantendo o *Core Domain* livre de acoplamento tecnológico.

**[09:56 - 11:30] Domain Services e Camadas Arquiteturais**
*   **Conceito de Design:** Quando uma operação de negócio não pertence naturalmente a nenhuma Entidade ou Value Object (pois orquestra múltiplos agregados), extraímos isso para um `Domain Service`.
*   Além disso, o jMolecules oferece anotações puras para mapear estilos arquiteturais baseados em camadas (Clean Architecture, Onion, Hexagonal) como `@ApplicationLayer`, `@DomainLayer` e `@InfrastructureLayer`.



## 3. Principais Conceitos

*   **jMolecules:** Um projeto inovador para a comunidade Java. É um conjunto de anotações e interfaces puras (sem dependências pesadas) que permite aos desenvolvedores expressar conceitos arquiteturais (DDD, Onion Architecture) diretamente no código, servindo como documentação viva.
*   **ArchUnit:** Biblioteca de testes em Java focada na verificação de arquitetura. Permite escrever testes unitários que falham se regras de design de software (ex: "A camada de Domínio não pode importar o pacote `java.sql`") forem violadas.
*   **Entity (Entidade Tática):** Objeto de domínio focado na rastreabilidade e continuidade. Duas entidades são iguais se, e somente se, seus IDs forem iguais, independentemente do resto de seus atributos.
*   **Value Object:** Objeto imutável distinguido apenas pelo estado de seus atributos. Ideal para representar Moedas (Money), Tipos sanguíneos, CEPs ou, como na aula, Tipos de Cartão (`CreditCardType`).
*   **Repository vs DAO:** Repository lida com objetos inteiros e ricos (Aggregates) da perspectiva do Domínio ("Adicione este Cartão à minha coleção"). DAO lida com a granularidade da persistência do banco de dados relacional ("Faça um INSERT na tabela X").



## 4. Resumo para Fixação

### 🚨 Armadilhas Comuns (Pitfalls)
1.  **Framework-Driven Development disfarçado de DDD:** O maior erro é achar que anotar uma classe com `@javax.persistence.Entity` e herdar de `JpaRepository` do Spring Data significa estar fazendo DDD. Essas anotações pertencem à camada de **Infraestrutura**, não de Domínio. O `jMolecules` resolve isso entregando anotações puramente conceituais.
2.  **Entidades sem Identidade Clara:** Criar classes de domínio e esquecer de definir rigorosamente o que a torna única. O ArchUnit, junto com o jMolecules, trava o *build* apontando exatamente qual classe esqueceu o `@Identity`.
3.  **Vazamento de Detalhes de Persistência no Repositório:** Construir Repositórios no Domínio recebendo lógicas de banco (ex: `salvarComTransacao(Connection conn, Card card)`). O Repositório do domínio só deve conhecer a linguagem do negócio.

### 🛡️ Princípios e Orientação a Objetos
*   **Separation of Concerns (Separação de Preocupações):** A aula demonstra a clara distinção entre as intenções do domínio (o que o sistema deve fazer) e as implementações técnicas (como o banco de dados vai salvar isso).
*   **Automation of Design Rules (Fitness Functions):** O princípio mais moderno de arquitetura ágil demonstrado na aula: regras de arquitetura não devem viver na boca do "Tech Lead", mas automatizadas no pipeline do Git (via ArchUnit + jMolecules) para escalar a segurança arquitetural da equipe.