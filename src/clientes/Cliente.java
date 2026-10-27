package clientes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Cliente {

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println(
                "Uso: java -cp out clientes.Cliente [endereço] [porta]"
            );
            return;
        }

        String endereco = args.length > 0 ? args[0] : "localhost";
        int porta = 3000;

        if (args.length == 2) {
            try {
                porta = Integer.parseInt(args[1]);

                if (porta < 1 || porta > 65535) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException erro) {
                System.err.println(
                    "Porta inválida. Informe um número entre 1 e 65535."
                );
                return;
            }
        }

        AtomicBoolean encerrando = new AtomicBoolean(false);

        try (
            Socket conexao = new Socket(endereco, porta);
            DataOutputStream saida =
                new DataOutputStream(conexao.getOutputStream());
            DataInputStream entrada =
                new DataInputStream(conexao.getInputStream())
        ) {
            System.out.println("Conectado ao servidor!");
            System.out.println("Digite /sair para encerrar.");

            Thread teclado = new Thread(() -> {
                Scanner leitor = new Scanner(System.in);

                try {
                    while (!encerrando.get() && leitor.hasNextLine()) {
                        String mensagem = leitor.nextLine();

                        if (mensagem.trim().equalsIgnoreCase("/sair")
                            || encerrando.get()) {
                            break;
                        }

                        if (mensagem.length() > 1000) {
                            System.out.println(
                                "Mensagem muito longa. "
                                + "Use no máximo 1000 caracteres."
                            );
                            continue;
                        }

                        saida.writeUTF(mensagem);
                        saida.flush();
                    }

                } catch (IOException erro) {
                    if (!encerrando.get()) {
                        System.out.println(
                            "Não foi possível enviar: conexão encerrada."
                        );
                    }

                } finally {
                    encerrando.set(true);

                    try {
                        conexao.close();
                    } catch (IOException erro) {
                        System.err.println(
                            "Erro ao fechar conexão: " + erro.getMessage()
                        );
                    }
                }
            }, "teclado-cliente");

            teclado.setDaemon(true);
            teclado.start();

            try {
                while (!encerrando.get()) {
                    System.out.println();
                    System.out.println(entrada.readUTF());
                }

            } catch (IOException erro) {
                if (!encerrando.get()) {
                    System.out.println(
                        "Conexão com o servidor encerrada."
                    );
                }

            } finally {
                encerrando.set(true);
            }

        } catch (IOException erro) {
            System.err.println(
                "Erro no cliente: " + erro.getMessage()
            );
        }
    }
}