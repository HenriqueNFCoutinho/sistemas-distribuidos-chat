package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {

    public static final int PORTA = 3000;

    private final List<ConexaoCliente> clientes =
        new CopyOnWriteArrayList<>();

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor iniciado na porta " + PORTA);

            while (true) {
                Socket socket = servidor.accept();

                ConexaoCliente cliente =
                    new ConexaoCliente(socket, this);

                new Thread(cliente).start();
            }

        } catch (IOException erro) {
            System.err.println("Erro no servidor: " + erro.getMessage());
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