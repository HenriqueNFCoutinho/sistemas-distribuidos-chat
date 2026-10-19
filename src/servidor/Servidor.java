package servidor;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static final int PORTA = 3000;

    public void iniciar() {
        try (
            ServerSocket servidor = new ServerSocket(PORTA)
        ) {
            System.out.println("Servidor iniciado na porta " + PORTA);
            System.out.println("Aguardando um cliente...");

            try (
                Socket cliente = servidor.accept();
                DataInputStream entrada =
                    new DataInputStream(cliente.getInputStream())
            ) {
                System.out.println("Cliente conectado!");

                String mensagem = entrada.readUTF();
                System.out.println("Mensagem recebida: " + mensagem);
            }

        } catch (IOException erro) {
            System.err.println("Erro no servidor: " + erro.getMessage());
        }
    }
}