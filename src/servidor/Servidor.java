package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static final int PORTA = 3000;

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor iniciado na porta " + PORTA);
            System.out.println("Aguardando clientes...");

            while (true) {
                Socket cliente = servidor.accept();

                System.out.println(
                    "Cliente conectado: " + cliente.getRemoteSocketAddress()
                );

                ConexaoCliente conexao = new ConexaoCliente(cliente);

                Thread thread = new Thread(conexao);
                thread.start();
            }

        } catch (IOException erro) {
            System.err.println("Erro no servidor: " + erro.getMessage());
        }
    }
}