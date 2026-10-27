package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

public class Servidor {

    public static final int PORTA = 3000;

    private static final int MAX_CONEXOES = 50;
    private final Semaphore vagas = new Semaphore(MAX_CONEXOES);

    private final Placar placar = new Placar();

    private final List<ConexaoCliente> clientes =
        new CopyOnWriteArrayList<>();

    public Placar getPlacar() {
        return placar;
    }

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor iniciado na porta " + PORTA);

            while (true) {
                Socket socket = servidor.accept();

                if (!vagas.tryAcquire()) {
                    socket.close();

                    System.out.println(
                        "Conexão recusada: limite de clientes atingido."
                    );

                    continue;
                }

                try {
                    ConexaoCliente cliente =
                        new ConexaoCliente(socket, this);

                    new Thread(() -> {
                        try {
                            cliente.run();
                        } finally {
                            vagas.release();
                        }
                    }, "atendimento-cliente").start();

                } catch (RuntimeException | Error erro) {
                    vagas.release();
                    socket.close();
                    throw erro;
                }
            }

        } catch (IOException erro) {
            System.err.println(
                "Erro no servidor: " + erro.getMessage()
            );
        }
    }

    public synchronized boolean registrarCliente(
        ConexaoCliente cliente, String nome
    ) {
        for (ConexaoCliente conectado : clientes) {
            if (conectado.getNome().equalsIgnoreCase(nome)) {
                return false;
            }
        }

        cliente.definirNome(nome);
        clientes.add(cliente);
        return true;
    }

    public synchronized void removerCliente(ConexaoCliente cliente) {
        clientes.remove(cliente);
    }

    public void distribuirMensagem(
        String mensagem, ConexaoCliente remetente
    ) {
        for (ConexaoCliente cliente : clientes) {
            if (cliente != remetente && !cliente.estaEmQuiz()) {
                cliente.enviarMensagem(mensagem);
            }
        }
    }
}