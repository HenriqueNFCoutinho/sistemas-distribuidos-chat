package servidor;

public class Pergunta {

    private final String enunciado;
    private final String[] alternativas;
    private final int indiceCorreto;

    public Pergunta(String enunciado, int indiceCorreto, String... alternativas) {
        this.enunciado = enunciado;
        this.indiceCorreto = indiceCorreto;
        this.alternativas = alternativas;
    }

    public String formatar(int numero, int total) {
        StringBuilder texto = new StringBuilder();
        texto.append("\nPergunta ").append(numero).append("/").append(total)
             .append(": ").append(enunciado);

        for (int i = 0; i < alternativas.length; i++) {
            texto.append("\n  ").append(letra(i)).append(") ")
                 .append(alternativas[i]);
        }
        return texto.toString();
    }

    public int interpretarResposta(String resposta) {
        String r = resposta.trim().toUpperCase();
        if (r.length() != 1) {
            return -1;
        }

        char c = r.charAt(0);
        int indice;
        if (c >= 'A' && c <= 'Z') {
            indice = c - 'A';
        } else if (c >= '1' && c <= '9') {
            indice = c - '1';
        } else {
            return -1;
        }
        return indice < alternativas.length ? indice : -1;
    }

    public boolean estaCorreta(int indice) {
        return indice == indiceCorreto;
    }

    public String respostaCorreta() {
        return letra(indiceCorreto) + ") " + alternativas[indiceCorreto];
    }

    public int quantidadeAlternativas() {
        return alternativas.length;
    }

    private static char letra(int indice) {
        return (char) ('A' + indice);
    }
}
