package clientes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        try (
            Socket conexao = new Socket("localhost", 3000);
            DataOutputStream saida =
                new DataOutputStream(conexao.getOutputStream());
            Scanner teclado = new Scanner(System.in)
        ) {
            System.out.println("Conectado ao servidor!");
            System.out.println("Digite /sair para encerrar.");

            while (teclado.hasNextLine()) {
                String mensagem = teclado.nextLine();

                if (mensagem.equalsIgnoreCase("/sair")) {
                    break;
                }

                saida.writeUTF(mensagem);
                saida.flush();
            }

        } catch (IOException erro) {
            System.err.println("Erro no cliente: " + erro.getMessage());
        }
    }
}