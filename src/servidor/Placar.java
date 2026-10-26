package servidor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Placar {

    private static class Resultado {
        final String nome;
        final int acertos;
        final int total;
        final long ordem;

        Resultado(String nome, int acertos, int total, long ordem) {
            this.nome = nome;
            this.acertos = acertos;
            this.total = total;
            this.ordem = ordem;
        }
    }

    private final Map<String, Map<String, Resultado>> resultados =
        new HashMap<>();

    private long proximaOrdem;

    public synchronized boolean registrar(
        String nome, String tema, int acertos, int total
    ) {
        if (nome == null || nome.isBlank()
            || tema == null || tema.isBlank()
            || total <= 0 || acertos < 0 || acertos > total) {
            throw new IllegalArgumentException("Resultado inválido.");
        }

        Map<String, Resultado> jogadores =
            resultados.computeIfAbsent(
                tema, chave -> new HashMap<>()
            );

        String chaveNome = nome.trim().toLowerCase(Locale.ROOT);
        Resultado anterior = jogadores.get(chaveNome);

        if (anterior != null
            && (long) acertos * anterior.total
                <= (long) anterior.acertos * total) {
            return false;
        }

        jogadores.put(
            chaveNome,
            new Resultado(
                nome.trim(), acertos, total, proximaOrdem++
            )
        );

        return true;
    }

    public synchronized String podio(String tema) {
        Map<String, Resultado> jogadores = resultados.get(tema);

        if (jogadores == null || jogadores.isEmpty()) {
            return "PÓDIO — " + tema
                + "\nNenhum quiz concluído neste tema.";
        }

        List<Resultado> ordenados =
            new ArrayList<>(jogadores.values());

        Comparator<Resultado> comparador = (a, b) ->
            Long.compare(
                (long) b.acertos * a.total,
                (long) a.acertos * b.total
            );

        ordenados.sort(
            comparador.thenComparingLong(resultado -> resultado.ordem)
        );

        StringBuilder texto = new StringBuilder(
            "========== PÓDIO — " + tema + " ==========\n"
        );

        for (int i = 0; i < Math.min(3, ordenados.size()); i++) {
            Resultado resultado = ordenados.get(i);

            texto.append(i + 1).append("º lugar: ")
                .append(resultado.nome)
                .append(" - ").append(resultado.acertos)
                .append("/").append(resultado.total)
                .append("\n");
        }

        texto.append(
            "Desempate: quem alcançou o resultado primeiro."
        );

        return texto.toString();
    }

    public synchronized String podio() {
        StringBuilder texto = new StringBuilder();

        for (String tema : BancoDePerguntas.temas()) {
            texto.append(podio(tema)).append("\n\n");
        }

        return texto.toString();
    }
}