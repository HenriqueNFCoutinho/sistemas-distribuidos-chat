import servidor.Servidor;

public class Main {

    public static void main(String[] args) {
        Servidor servidor = new Servidor(12345);
        servidor.iniciar();
    }
}