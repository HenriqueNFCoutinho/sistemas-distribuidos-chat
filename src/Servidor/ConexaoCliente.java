package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConexaoCliente implements Runnable {

    private final Socket socket;

    public ConexaoCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {

            String linha;
            while ((linha = entrada.readLine()) != null) {
                System.out.println("[" + Thread.currentThread().getName() + "] recebeu: " + linha);
            }
        } catch (IOException e) {
            System.out.println("Erro com cliente: " + e.getMessage());
        }
        System.out.println("Cliente desconectou: " + socket.getRemoteSocketAddress());
    }
}
