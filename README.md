# FitLife — Aula 15, Unidade 4

Atividade prática: implementação e validação do sistema FitLife em Java. Projeto de console com orientação a objetos, validações, exceções e testes JUnit 5.

## Executar

Requisitos: **JDK 17 ou superior** e **Maven 3.9 ou superior** para compilar/testar. A primeira compilação precisa de internet para obter as dependências. O JAR pronto precisa apenas de Java 17 ou superior. Java 8 não executa este projeto.

Extraia o ZIP e abra um terminal na pasta `FitLife_Aula15`.

```sh
mvn clean package
java -jar target/fitlife-1.0.0.jar
```

Para executar o arquivo já compilado entregue na raiz:

```sh
java -jar fitlife-1.0.0.jar
```

Demonstração automática, com exemplos válidos e erros tratados:

```sh
java -jar fitlife-1.0.0.jar --demo
```

No Windows, também é possível abrir `executar.bat` ou `demonstrar.bat`. Os scripts procuram Java 17+ em `JAVA_HOME`, no IntelliJ IDEA, nas instalações Java e no PATH, ignorando versões antigas. A seleção vale apenas para o FitLife e não modifica o Java padrão do Windows. Se houver erro de versão, configure um JDK 17+ e confirme com `java -version`. No terminal Windows, use `chcp 65001` para visualizar UTF-8.

### IntelliJ IDEA, Eclipse ou VS Code

Importe/abra `pom.xml` como projeto Maven, selecione um JDK 17+ e aguarde as dependências. Execute `app.Main`. Para a demonstração, acrescente `--demo` aos argumentos. Os testes ficam em `src/test/java/fitlife/FitLifeTest.java`.

## Uso do menu

O menu inicia vazio, para permitir realizar os cadastros da atividade.

1. Opção 1: cadastre nome, e-mail e idade do cliente. O primeiro ID será 1.
2. Opção 2: cadastre o plano Mensal ou Premium. O primeiro ID será 1.
3. Opção 3: informe o ID do cliente e o ID do plano para associá-los.
4. Opção 4: informe ID do cliente, nome, descrição e duração do treino em minutos.
5. Opção 5: informe ID do cliente e competência no formato `2026-10` para simular a mensalidade.
6. Opções 6, 7 e 8: consulte clientes, planos e treinos. Opção 0: encerre.

Erros de preenchimento são exibidos no console e devolvem o usuário ao menu. O fim da entrada também encerra a aplicação de forma controlada.

## Requisitos da imagem e implementação

| Requisito da Aula 15 | Implementação |
| --- | --- |
| Cadastrar clientes | `Cliente` e `AcademiaService.cadastrarCliente` |
| Cadastrar planos Mensal e Premium | `PlanoMensal`, `PlanoPremium` e `cadastrarPlano` |
| Associar plano ao cliente | `AcademiaService.associarPlano` e `Cliente.setPlano` |
| Cadastrar treinos para cliente | `Treino`, `cadastrarTreino` e `Cliente.adicionarTreino` |
| Processar mensalidade | Interface `ProcessadorPagamento` e `PagamentoService` |
| Validar e tratar exceções | Validações nos objetos, `IllegalArgumentException`, `RegraNegocioException` e tratamento no menu |
| Exibir no console | Menu interativo e argumento `--demo` |
| Testar regras | JUnit 5: 30 casos executados com sucesso |

### Regras e decisões

- Nome obrigatório e não vazio; espaços nas extremidades são removidos. Não se exige sobrenome.
- E-mail obrigatório, com validação sintática simplificada (ex.: `ana@email.com`). Não é verificada a existência da caixa postal; endereços internacionais fora desse formato não são aceitos.
- Idade inteira mínima de 15 anos; o limite é inclusivo. Construtor e setters aplicam as mesmas regras.
- Mensal custa **R$ 100,00**, conforme o exemplo da imagem. Premium custa **R$ 150,00**, valor didático adotado porque a imagem não define seu preço ou benefícios adicionais.
- Valores monetários usam `BigDecimal`, evitando imprecisão de ponto flutuante. Essa é uma adaptação do `double` ilustrado na imagem.
- Cada cliente tem um plano por vez; uma nova associação substitui o plano anterior. Planos de mesmo nome não podem ser cadastrados novamente.
- Treino tem nome, descrição e duração positiva. Um cliente pode ter vários treinos, mesmo antes de contratar um plano.
- Pagamento exige cliente, plano e competência. O recibo guarda nome, plano, competência e valor da operação.
- Uma mensalidade por cliente e competência, durante a execução da mesma instância do serviço. Trocar nome ou plano não permite pagar novamente aquele mês. Outro mês pode ser processado.
- Cadastros de clientes representam pessoas distintas por ID; não há regra de unicidade de e-mail.
- Pagamento é **simulado**, sem conexão bancária. Dados ficam em memória e são perdidos ao sair. Não há banco, autenticação ou persistência de cobranças.

## Orientação a objetos

- **Classes e objetos:** cliente, plano, treino e pagamento modelam o domínio.
- **Encapsulamento:** atributos privados, setters validados e listas protegidas contra alterações externas.
- **Herança:** planos Mensal e Premium estendem a classe abstrata `Plano`.
- **Polimorfismo:** o serviço chama `calcularMensalidade()` pela referência `Plano`, executando a implementação do tipo concreto.
- **Interface:** `ProcessadorPagamento` define o contrato usado pelo console; `PagamentoService` o implementa.
- **Relacionamentos:** cliente referencia um plano e vários treinos; serviço de pagamentos registra recibos por cliente e mês.
- **Exceções:** dados inválidos lançam `IllegalArgumentException`; situações de negócio lançam `RegraNegocioException`. O console apresenta mensagens sem interromper a sessão.

## Aproveitamento do projeto anterior

Foi localizado e lido `FitLife_Academia.zip` na pasta Downloads do usuário. Esta entrega evolui sua organização em `app`, `model` e `service`, o `AcademiaService` com coleções em memória, consultas por ID e o menu com `Scanner`.

O antigo `Aluno` foi adaptado para `Cliente`, incluindo idade e validações. O antigo plano concreto foi substituído pela hierarquia abstrata exigida. Para o escopo desta aula, o vínculo de `Matricula` foi simplificado para a associação direta cliente-plano ilustrada na imagem. CPF, telefone, cancelamento de matrícula e planos trimestral/anual do projeto anterior não fazem parte desta versão. Não há migração automática de dados. O arquivo original permanece intacto.

## Estrutura

```text
FitLife_Aula15/
  pom.xml
  README.md
  executar.bat
  demonstrar.bat
  fitlife-1.0.0.jar
  src/main/java/
    app/Main.java
    model/Cliente.java
    model/Plano.java
    model/PlanoMensal.java
    model/PlanoPremium.java
    model/Treino.java
    model/Pagamento.java
    model/Validacao.java
    service/AcademiaService.java
    service/ProcessadorPagamento.java
    service/PagamentoService.java
    service/RegraNegocioException.java
  src/test/java/fitlife/FitLifeTest.java
  docs/
    EXEMPLO_SAIDA.txt
    RESULTADO_JUNIT.txt
    TESTE_CONSOLE.txt
```

## Testes e evidências

```sh
mvn test
```

Foram executados **30 testes, 0 falhas, 0 erros e 0 ignorados**. Incluem valores nulos/vazios, e-mails inválidos, idade abaixo de 15 e no limite, setters, planos, IDs inexistentes, associação e troca de plano, cadastro de treinos, proteção das listas, pagamento sem plano, duplicidade, mês seguinte, preservação do recibo e fluxo integrado.

Validação realizada com Maven 3.9.9 e JDK 25.0.4, compilando com `--release 17`. O bytecode é destinado ao Java 17; a execução nesta máquina foi verificada no JDK 25. O fluxo de console também foi executado com entradas válidas, número inválido, competência inválida e pagamento duplicado. Foi conferida a saída UTF-8, incluindo acentos.

Evidências textuais estão em `docs/RESULTADO_JUNIT.txt`, `docs/TESTE_CONSOLE.txt` e `docs/EXEMPLO_SAIDA.txt`. O JAR da raiz corresponde ao código entregue; após editar o código, use o novo JAR gerado em `target/`.

## Exemplo da saída real

```textFITLIFE — Demonstração Aula 15 / Unidade 4
1 - Mensal | R$ 100,00
2 - Premium | R$ 150,00
1 - João Silva | joao@email.com | 20 anos | Plano: Mensal | Treinos: 1
2 - Maria Souza | maria@email.com | 15 anos | Plano: Premium | Treinos: 1
Treinos de João:
1 - Treino A | Musculação de membros superiores | 45 min
Pagamento realizado com sucesso! Cliente: João Silva | Plano: Mensal | Competência: 2026-10 | Valor: R$ 100,00
Pagamento realizado com sucesso! Cliente: Maria Souza | Plano: Premium | Competência: 2026-10 | Valor: R$ 150,00
Validação esperada: Idade mínima é 15 anos.
Validação esperada: Mensalidade já paga nesta competência.
```

## Entrega acadêmica

O código, os testes, as instruções e a saída de execução estão incluídos neste pacote. A imagem menciona repositório do grupo e histórico de commits: esta entrega é local, sem publicação no GitHub nem criação de histórico artificial. Para obter um print de execução, execute `demonstrar.bat` e capture a janela do terminal.