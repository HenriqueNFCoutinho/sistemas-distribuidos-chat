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
        final String tema;
        final int acertos;
        final int total;
        final long ordem;

        Resultado(
            String nome, String tema,
            int acertos, int total, long ordem
        ) {
            this.nome = nome;
            this.tema = tema;
            this.acertos = acertos;
            this.total = total;
            this.ordem = ordem;
        }
    }

    private final Map<String, Resultado> melhores = new HashMap<>();
    private long proximaOrdem;

    public synchronized boolean registrar(
        String nome, String tema, int acertos, int total
    ) {
        if (nome == null || nome.isBlank()
            || tema == null || tema.isBlank()
            || total <= 0 || acertos < 0 || acertos > total) {
            throw new IllegalArgumentException("Resultado inválido.");
        }

        String chave = nome.trim().toLowerCase(Locale.ROOT);
        Resultado anterior = melhores.get(chave);

        // Uma pontuação igual ou inferior não substitui a anterior.
        if (anterior != null
            && (long) acertos * anterior.total
                <= (long) anterior.acertos * total) {
            return false;
        }

        melhores.put(
            chave,
            new Resultado(
                nome.trim(), tema, acertos, total, proximaOrdem++
            )
        );

        return true;
    }

    public synchronized String podio() {
        if (melhores.isEmpty()) {
            return "Nenhum quiz foi concluído ainda.";
        }

        List<Resultado> ordenados =
            new ArrayList<>(melhores.values());

        Comparator<Resultado> comparador = (a, b) ->
            Long.compare(
                (long) b.acertos * a.total,
                (long) a.acertos * b.total
            );

        ordenados.sort(
            comparador.thenComparingLong(resultado -> resultado.ordem)
        );

        StringBuilder texto =
            new StringBuilder("========== PÓDIO ==========\n");

        for (int i = 0; i < Math.min(3, ordenados.size()); i++) {
            Resultado resultado = ordenados.get(i);

            texto.append(i + 1).append("º lugar: ")
                .append(resultado.nome)
                .append(" - ").append(resultado.acertos)
                .append("/").append(resultado.total)
                .append(" - ").append(resultado.tema)
                .append("\n");
        }

        texto.append(
            "Desempate: quem alcançou o resultado primeiro."
        );

        return texto.toString();
    }
}