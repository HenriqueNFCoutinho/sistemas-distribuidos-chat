package Servidor;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ConexaoCliente implements Runnable {

    private final Socket socket;

    public ConexaoCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            Socket conexao = socket;
            DataInputStream entrada =
                new DataInputStream(conexao.getInputStream())
        ) {
            while (true) {
                String mensagem = entrada.readUTF();

                System.out.println(
                    "[" + Thread.currentThread().getName()
                    + "] recebeu: " + mensagem
                );
            }
        } catch (EOFException e) {
            // O cliente encerrou a conexão.
        } catch (IOException e) {
            System.out.println("Erro com cliente: " + e.getMessage());
        } finally {
            System.out.println(
                "Cliente desconectou: " + socket.getRemoteSocketAddress()
            );
        }
    }
}