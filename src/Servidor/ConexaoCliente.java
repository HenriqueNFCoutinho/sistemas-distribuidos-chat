package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ConexaoCliente implements Runnable {

    private final Socket socket;
    private final Servidor servidor;
    private final String nome;
    private DataOutputStream saida;

    public ConexaoCliente(Socket socket, Servidor servidor, String nome) {
        this.socket = socket;
        this.servidor = servidor;
        this.nome = nome;
    }

    @Override
    public void run() {
        try (Socket conexao = socket;
             DataInputStream entrada = new DataInputStream(conexao.getInputStream())) {

            saida = new DataOutputStream(conexao.getOutputStream());

            servidor.adicionar(this);
            enviar("Bem-vindo ao chat! Você é o " + nome + ". Digite /sair para encerrar.");

            while (true) {
                String mensagem = entrada.readUTF().trim();

                if (mensagem.isEmpty()) {
                    continue;
                }
                if (mensagem.equalsIgnoreCase("/sair")) {
                    break;
                }

                System.out.println(nome + ": " + mensagem);
                servidor.transmitir(nome + ": " + mensagem, this);
            }
        } catch (EOFException e) {
        } catch (IOException e) {
            System.out.println("Conexão com " + nome + " perdida: " + e.getMessage());
        } finally {
            servidor.remover(this);
        }
    }

    public synchronized void enviar(String mensagem) {
        if (saida == null) {
            return;
        }
        try {
            saida.writeUTF(mensagem);
            saida.flush();
        } catch (IOException e) {
        }
    }

    public String getNome() {
        return nome;
    }
}