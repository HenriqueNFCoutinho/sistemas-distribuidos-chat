# Chat em Java — Sistemas Distribuídos

Aplicação de chat em terminal desenvolvida em Java, utilizando sockets TCP, streams de entrada/saída e threads para atender vários clientes simultaneamente. Também inclui um quiz por temas e um placar de resultados.

## Requisitos

- JDK 17 ou superior instalado.
- Terminal (CMD ou PowerShell no Windows).
- Porta TCP `3000` disponível no computador que executará o servidor.

Confira a instalação do Java com:

```bat
java -version
javac -version
```

## Como compilar no Windows

Abra o PowerShell na pasta principal do projeto, que contém a pasta `src`, e execute:

```powershell
javac -encoding UTF-8 -d out src/Main.java src/servidor/*.java src/clientes/*.java
```

O comando cria a pasta `out`, onde ficam os arquivos compilados.

## Como executar

### 1. Iniciar o servidor

Na pasta principal do projeto, execute:

```bat
java -cp out Main
```

Deixe essa janela aberta enquanto houver clientes conectados. O servidor escuta na porta TCP `3000`.

### 2. Iniciar um cliente

Abra outro CMD na mesma pasta e execute:

```bat
java -cp out clientes.Cliente
```

Para testar várias conexões no mesmo computador, abra mais terminais e execute o comando do cliente em cada um.

### Conectar computadores diferentes na mesma rede

No arquivo `src/clientes/Cliente.java`, altere o endereço `localhost` para o endereço IP do computador onde o servidor está rodando. Por exemplo:

```java
new Socket("192.168.1.100", 3000);
```

Use o IP real do computador servidor. Os computadores precisam ter conectividade entre si, e o Firewall do Windows pode precisar permitir conexões TCP de entrada na porta `3000`. Não exponha essa porta diretamente à internet sem configurar segurança adequada.

## Funcionalidades

- Chat de texto entre clientes conectados.
- Tratamento de conexões individuais com threads.
- Verificação para impedir nomes de usuário repetidos (sem diferenciar maiúsculas e minúsculas).
- Quiz de múltipla escolha com os temas Geografia, História, Ciências e Programação.
- Perguntas apresentadas em ordem aleatória dentro do tema selecionado.
- Registro do melhor resultado de cada jogador por tema durante a execução do servidor.
- Consulta ao pódio de um tema ou de todos os temas.

## Comandos do chat

| Comando | Função |
|---|---|
| `/ajuda` | Exibe os comandos disponíveis e suas funções. |
| `/quiz` | Inicia um quiz e mostra o menu de temas. |
| `/podio` | Abre o menu para consultar o pódio de um tema ou de todos. |
| `/cancelar` | Cancela o quiz ou a consulta ao pódio em andamento e retorna ao chat. |
| `/sair` | Encerra a conexão do cliente com o servidor. |

Comandos devem ser digitados no início da mensagem. Se um comando não for reconhecido, o cliente recebe uma orientação para usar `/ajuda`.

Durante um quiz, responda com a letra da alternativa (A, B, C ou D). Também é possível usar `/cancelar` para desistir do quiz.
Durante o quiz, o jogador não recebe as mensagens do chat.
Essas mensagens não são guardadas para depois.

## Como funciona o placar

O placar mantém o melhor resultado de cada jogador em cada tema. A comparação é feita pela proporção de acertos, e o resultado anterior é mantido quando a nova tentativa não é melhor. Em caso de empate, fica à frente quem alcançou o resultado primeiro.

**Importante:** os resultados ficam apenas na memória do servidor e são perdidos quando o processo é encerrado. O projeto não salva o placar em arquivo ou banco de dados.

## Estrutura principal

```text
src/
├── Main.java
├── clientes/
│   └── Cliente.java
└── servidor/
    ├── BancoDePerguntas.java
    ├── ConexaoCliente.java
    ├── Pergunta.java
    ├── Placar.java
    └── Servidor.java
```

Todas as classes do servidor ficam no diretório `src/servidor` e usam o pacote `servidor`.

## Integrantes

- Henrique Nobre
- Arthur Casemiro
- Manoel Franklin

## Solução de problemas

- **`javac` não é reconhecido:** instale um JDK e configure o `PATH` do Windows.
- **`Address already in use`:** a porta `3000` já está sendo usada por outro processo; encerre o processo ou altere a constante `PORTA` em `Servidor.java` e use a mesma porta no cliente.
- **Cliente não conecta:** confirme que o servidor foi iniciado, que o IP está correto e que o firewall permite a conexão.
- **Caracteres acentuados incorretos:** compile com `-encoding UTF-8`, como mostrado acima.
