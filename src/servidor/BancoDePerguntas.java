package servidor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BancoDePerguntas {

    private static final Map<String, List<Pergunta>> TEMAS = new LinkedHashMap<>();

    static {
        TEMAS.put("Geografia", List.of(
            new Pergunta("Qual é a capital da Austrália?", 2,
                "Sydney", "Melbourne", "Canberra", "Perth"),
            new Pergunta("Qual é o maior oceano do planeta?", 0,
                "Pacífico", "Atlântico", "Índico", "Ártico"),
            new Pergunta("Qual é o rio mais extenso inteiramente brasileiro?", 0,
                "São Francisco", "Amazonas", "Paraná", "Tocantins"),
            new Pergunta("Em qual continente fica o Egito?", 3,
                "Ásia", "Europa", "Oceania", "África"),
            new Pergunta("Quantos estados tem o Brasil (sem contar o DF)?", 2,
                "24", "25", "26", "27"),
            new Pergunta("Qual é a capital do estado da Paraíba?", 1,
                "Campina Grande", "João Pessoa", "Patos", "Sousa"),
            new Pergunta("Qual linha imaginária divide a Terra em hemisférios Norte e Sul?", 0,
                "Linha do Equador", "Trópico de Capricórnio", "Meridiano de Greenwich", "Trópico de Câncer")
        ));

        TEMAS.put("História", List.of(
            new Pergunta("Em que ano o Brasil foi 'descoberto' pelos portugueses?", 1,
                "1492", "1500", "1822", "1889"),
            new Pergunta("Quem proclamou a independência do Brasil?", 0,
                "Dom Pedro I", "Dom Pedro II", "Tiradentes", "Deodoro da Fonseca"),
            new Pergunta("Em que ano terminou a Segunda Guerra Mundial?", 2,
                "1918", "1939", "1945", "1964"),
            new Pergunta("Qual civilização construiu Machu Picchu?", 3,
                "Maias", "Astecas", "Olmecas", "Incas"),
            new Pergunta("Em que ano caiu o Muro de Berlim?", 1,
                "1985", "1989", "1991", "2001"),
            new Pergunta("Quem foi o primeiro presidente do Brasil?", 3,
                "Getúlio Vargas", "Juscelino Kubitschek", "Floriano Peixoto", "Deodoro da Fonseca"),
            new Pergunta("Qual povo antigo construiu as pirâmides de Gizé?", 2,
                "Romanos", "Persas", "Egípcios", "Fenícios")
        ));

        TEMAS.put("Ciências", List.of(
            new Pergunta("Qual é a fórmula química da água?", 0,
                "H2O", "CO2", "O2", "NaCl"),
            new Pergunta("Qual planeta é conhecido como Planeta Vermelho?", 2,
                "Vênus", "Júpiter", "Marte", "Saturno"),
            new Pergunta("Qual órgão bombeia o sangue pelo corpo humano?", 1,
                "Pulmão", "Coração", "Fígado", "Rim"),
            new Pergunta("Qual é a velocidade aproximada da luz no vácuo?", 3,
                "300 km/s", "3.000 km/s", "30.000 km/s", "300.000 km/s"),
            new Pergunta("Qual gás as plantas absorvem na fotossíntese?", 1,
                "Oxigênio", "Gás carbônico", "Nitrogênio", "Hélio"),
            new Pergunta("Qual é o planeta mais próximo do Sol?", 0,
                "Mercúrio", "Vênus", "Terra", "Marte"),
            new Pergunta("Qual parte da célula contém a maior parte do material genético?", 2,
                "Membrana", "Citoplasma", "Núcleo", "Ribossomo")
        ));

        TEMAS.put("Programação", List.of(
            new Pergunta("Em Java, qual palavra-chave cria uma subclasse?", 2,
                "implements", "super", "extends", "inherits"),
            new Pergunta("Qual classe Java é usada pelo servidor para aceitar conexões TCP?", 0,
                "ServerSocket", "Socket", "DatagramSocket", "HttpClient"),
            new Pergunta("Qual interface uma classe implementa para rodar em uma Thread?", 1,
                "Callable", "Runnable", "Executor", "Thread"),
            new Pergunta("O que significa a sigla TCP?", 3,
                "Text Control Protocol", "Transfer Code Process",
                "Total Connection Port", "Transmission Control Protocol"),
            new Pergunta("Qual palavra-chave Java evita que duas threads executem um método ao mesmo tempo?", 2,
                "volatile", "static", "synchronized", "final"),
            new Pergunta("Qual método é o ponto de entrada tradicional de um programa Java?", 1,
                "start()", "main()", "run()", "init()"),
            new Pergunta("Qual estrutura permite repetir um bloco enquanto uma condição for verdadeira?", 3,
                "class", "import", "package", "while")
        ));
    }

    public static List<String> temas() {
        return new ArrayList<>(TEMAS.keySet());
    }

    public static List<Pergunta> perguntasDo(String tema) {
        List<Pergunta> perguntas = new ArrayList<>(TEMAS.get(tema));
        Collections.shuffle(perguntas);
        return perguntas;
    }
}
