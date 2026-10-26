package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ConexaoCliente implements Runnable {

    private enum Estado {
        NOME, CHAT, TEMA, RESPOSTA, PODIO
    }

    private final Socket socket;
    private final Servidor servidor;

    private DataOutputStream saida;
    private String nome;

    private volatile Estado estado = Estado.NOME;

    private List<Pergunta> perguntas;
    private int perguntaAtual;
    private String temaAtual;
    private int acertos;

    public ConexaoCliente(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
public void run() {
    try (
        Socket conexao = socket;
        DataInputStream entrada =
            new DataInputStream(conexao.getInputStream());
        DataOutputStream fluxoSaida =
            new DataOutputStream(conexao.getOutputStream())
    ) {
        saida = fluxoSaida;
        enviarMensagem("Digite seu nome:");

        while (true) {
            String mensagem = entrada.readUTF();
            String comando = mensagem.trim();

            if (comando.equalsIgnoreCase("/sair")) {
                break;
            }

            if (comando.equalsIgnoreCase("/cancelar")) {
                if (estaEmQuiz()) {
                    voltarAoChat();
                    enviarMensagem(
                        "Quiz cancelado. Você voltou ao chat."
                    );

                } else if (estado == Estado.PODIO) {
                    estado = Estado.CHAT;
                    enviarMensagem(
                        "Consulta cancelada. Você voltou ao chat."
                    );

                } else {
                    enviarMensagem(
                        "Nenhum quiz ou consulta está em andamento."
                    );
                }

                continue;
            }

            switch (estado) {
                case NOME -> receberNome(comando);
                case CHAT -> tratarChat(mensagem);
                case TEMA -> escolherTema(comando);
                case RESPOSTA -> responder(comando);
                case PODIO -> escolherPodio(comando);
            }
        }

    } catch (EOFException erro) {
        // O cliente encerrou a conexão.
    } catch (IOException erro) {
        System.out.println(
            "Conexão encerrada: " + erro.getMessage()
        );
    } finally {
        servidor.removerCliente(this);

        if (nome != null) {
            servidor.distribuirMensagem(
                "* " + nome + " saiu do chat.", this
            );
        }
    }
}

    private void receberNome(String tentativa) {
        if (tentativa.isBlank() || tentativa.startsWith("/")) {
            enviarMensagem(
                "Nome inválido. Digite um nome sem começar com /:"
            );
            return;
        }

        if (!servidor.registrarCliente(this, tentativa)) {
            enviarMensagem(
                "Nome já está em uso. Digite outro nome:"
            );
            return;
        }

        estado = Estado.CHAT;

        enviarMensagem(
         "\n========== CHAT GERAL ==========\n"
         + "Você entrou como: " + nome + "\n"
         + "Digite uma mensagem e pressione Enter para conversar.\n"
         + "Comandos: /quiz, /podio, /cancelar e /sair.\n"
         + "================================"
);

        servidor.distribuirMensagem(
            "* " + nome + " entrou no chat.", this
        );
    }

    private void tratarChat(String mensagem) {
        if (mensagem.trim().equalsIgnoreCase("/quiz")) {
            estado = Estado.TEMA;

            List<String> temas = BancoDePerguntas.temas();
            StringBuilder menu =
                new StringBuilder("Escolha um tema:");

            for (int i = 0; i < temas.size(); i++) {
                menu.append("\n")
                    .append(i + 1)
                    .append(") ")
                    .append(temas.get(i));
            }

            enviarMensagem(
                menu + "\nDigite /cancelar para voltar ao chat."
            );

        } else if (mensagem.trim().equalsIgnoreCase("/podio")
            || mensagem.trim().equalsIgnoreCase("/podia")) {
            mostrarMenuPodio();

        } else if (mensagem.isBlank()) {
            enviarMensagem("Digite uma mensagem.");

        } else if (mensagem.trim().startsWith("/")) {
            enviarMensagem(
                "Comando desconhecido. Use /quiz, /cancelar ou /sair."
            );

        } else {
            servidor.distribuirMensagem(
                nome + ": " + mensagem, this
            );
        }
    }

    private void escolherTema(String escolha) {
        List<String> temas = BancoDePerguntas.temas();

        try {
            int opcao = Integer.parseInt(escolha);

            if (opcao >= 1 && opcao <= temas.size()) {
                String tema = temas.get(opcao - 1);
                temaAtual = tema;

                perguntas = BancoDePerguntas.perguntasDo(tema);
                perguntaAtual = 0;
                acertos = 0;
                estado = Estado.RESPOSTA;

                enviarPergunta();
                return;
            }

        } catch (NumberFormatException erro) {
            // A escolha precisa ser um número do menu.
        }

        enviarMensagem(
            "Tema inválido. Digite um número de 1 a "
            + temas.size() + " ou /cancelar."
        );
    }

    private void responder(String resposta) {
    Pergunta pergunta = perguntas.get(perguntaAtual);
    int indice = pergunta.interpretarResposta(resposta);

    if (indice < 0) {
        enviarMensagem(
            "Resposta inválida. Digite uma alternativa ou /cancelar."
        );
        return;
    }

    if (pergunta.estaCorreta(indice)) {
        acertos++;
        enviarMensagem("Correto!");
    } else {
        enviarMensagem(
            "Errado! Resposta certa: " + pergunta.respostaCorreta()
        );
    }

    perguntaAtual++;

    if (perguntaAtual < perguntas.size()) {
        enviarPergunta();
    } else {
        // Só registra depois da última resposta válida.
        boolean melhorou = servidor.getPlacar().registrar(
            nome, temaAtual, acertos, perguntas.size()
        );

        enviarMensagem(
            "Fim do quiz! Você acertou " + acertos
            + " de " + perguntas.size()
            + ". Você voltou ao chat."
        );

        enviarMensagem(
            melhorou
                ? "Seu melhor resultado foi registrado!"
                : "Seu resultado anterior foi mantido."
        );

        enviarMensagem(servidor.getPlacar().podio(temaAtual));
        voltarAoChat();
    }
}

    private void enviarPergunta() {
        Pergunta pergunta = perguntas.get(perguntaAtual);

        enviarMensagem(
            pergunta.formatar(perguntaAtual + 1, perguntas.size())
            + "\nDigite /cancelar para voltar ao chat."
        );
    }

    private void voltarAoChat() {
        temaAtual = null;
        perguntas = null;
        perguntaAtual = 0;
        acertos = 0;
        estado = Estado.CHAT;
    }

    public String getNome() {
        return nome;
    }

    public void definirNome(String nome) {
        this.nome = nome;
    }

    public boolean estaEmQuiz() {
        return estado == Estado.TEMA || estado == Estado.RESPOSTA;
    }

    public synchronized void enviarMensagem(String mensagem) {
        try {
            saida.writeUTF(mensagem);
            saida.flush();

        } catch (IOException erro) {
            servidor.removerCliente(this);

            try {
                socket.close();
            } catch (IOException erroFechamento) {
                System.err.println(
                    "Erro ao fechar conexão: "
                    + erroFechamento.getMessage()
                );
            }
        }
    }
    private void mostrarMenuPodio() {
    estado = Estado.PODIO;

    List<String> temas = BancoDePerguntas.temas();

    StringBuilder menu = new StringBuilder(
        "Qual pódio você quer ver?\n0) Todos"
    );

    for (int i = 0; i < temas.size(); i++) {
        menu.append("\n")
            .append(i + 1)
            .append(") ")
            .append(temas.get(i));
    }

    enviarMensagem(
        menu + "\nDigite o número ou /cancelar."
    );
}

private void escolherPodio(String escolha) {
    List<String> temas = BancoDePerguntas.temas();
    int opcao;

    if (escolha.equalsIgnoreCase("Todos")) {
        opcao = 0;
    } else {
        try {
            opcao = Integer.parseInt(escolha);
        } catch (NumberFormatException erro) {
            enviarMensagem(
                "Opção inválida. Digite o número ou /cancelar."
            );
            return;
        }
    }

    if (opcao < 0 || opcao > temas.size()) {
        enviarMensagem(
            "Opção inválida. Digite de 0 a " + temas.size() + "."
        );
        return;
    }

    if (opcao == 0) {
        enviarMensagem(servidor.getPlacar().podio());
    } else {
        String tema = temas.get(opcao - 1);
        enviarMensagem(servidor.getPlacar().podio(tema));
    }

    estado = Estado.CHAT;
    enviarMensagem("Você está no chat novamente.");
}
}