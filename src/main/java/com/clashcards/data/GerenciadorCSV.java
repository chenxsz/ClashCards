package com.clashcards.data;

import com.clashcards.definicoes.Carta;
import com.clashcards.definicoes.Raridade;
import com.clashcards.definicoes.TipoDaCarta;

import java.io.*;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorCSV {

    public static final String NOME_ARQUIVO = "cartas.csv";
    public static final String ARQUIVO_DECKS = "decks.csv";
    private ArrayList<Carta> cartasEmMemoria;
// ...

    public GerenciadorCSV() {
        this.cartasEmMemoria = new ArrayList<>();
        carregarCartasDoCSV();
    }


    public boolean salvarNovaCarta(Carta novaCarta) {
        for (Carta c : cartasEmMemoria) {
            if (c.getNome().equalsIgnoreCase(novaCarta.getNome())) {
                System.out.println("Erro: Carta duplicada!");
                return false;
            }
        }

        this.cartasEmMemoria.add(novaCarta);

        try (FileWriter fw = new FileWriter(NOME_ARQUIVO, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw))
        {
            String linhaCSV = formatarParaCSV(novaCarta);
            out.println(linhaCSV);

            return true;

        } catch (IOException e) {
            System.err.println("Erro ao salvar a carta no arquivo CSV!");
            e.printStackTrace();
            //se deu erro ao salvar no arquivo, remove da memória também
            this.cartasEmMemoria.remove(novaCarta);
            return false;
        }
    }

    private void carregarCartasDoCSV() {
        File arquivo = new File(NOME_ARQUIVO);

        if (!arquivo.exists()) {
            System.out.println("Arquivo 'cartas.csv' não encontrado. Será criado no primeiro save.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                Carta carta = parseDoCSV(linha);
                if (carta != null) {
                    this.cartasEmMemoria.add(carta);
                }
            }
            System.out.println("Cartas carregadas: " + this.cartasEmMemoria.size());
        } catch (IOException e) {
            System.err.println("Erro ao carregar cartas!");
            e.printStackTrace();
        }
    }


    private String formatarParaCSV(Carta c) {
        return String.join(";",
                c.getNome(),
                String.valueOf(c.getNivel()),
                String.valueOf(c.getElixir()),
                c.getTipo().name(),
                c.getRaridade().name(),
                c.getImagem(),
                String.valueOf(c.getDano()),
                String.valueOf(c.getDanoPorSegundo()),
                String.valueOf(c.getVida()),
                c.getAlvos(),
                c.getAlcance(),
                c.getVelocidade(),
                c.getVelocidadeDeImpacto()
        );
    }

    private Carta parseDoCSV(String linha) {
        try {
            String[] dados = linha.split(";");

            String nome = dados[0];
            int nivel = Integer.parseInt(dados[1]);
            int custoElixir = Integer.parseInt(dados[2]);
            TipoDaCarta tipo = TipoDaCarta.valueOf(dados[3]);
            Raridade raridade = Raridade.valueOf(dados[4]);
            String imagem = dados[5];
            int dano = Integer.parseInt(dados[6]);
            double dps = Double.parseDouble(dados[7]);
            int vida = Integer.parseInt(dados[8]);
            String alvos = dados[9];
            String alcance = dados[10];
            String velocidade = dados[11];
            String velocidadeImpacto = dados[12];

            return new Carta(nome, nivel, tipo, raridade, imagem, custoElixir, vida,
                    dano, dps, alvos, alcance, velocidade, velocidadeImpacto);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Carta> getCartasEmMemoria() {
        return cartasEmMemoria;
    }

    private boolean reescreverArquivoCSV() {
        try (FileWriter fw = new FileWriter(NOME_ARQUIVO, false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (Carta c : cartasEmMemoria) {
                out.println(formatarParaCSV(c));
            }
            return true;
        } catch (IOException e) {
            System.out.println("Erro ao reescrever o arquivo CSV!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean removerCarta(Carta removerCarta) {
        if (cartasEmMemoria.removeIf(c -> c.getNome().equalsIgnoreCase(removerCarta.getNome()))) {
            if (reescreverArquivoCSV()) {
                return true;
            } else {
                System.out.println("ERRO GRAVE: Falha ao reescrever CSV após a remoção!");
                return true;
            }
        }
        return false;
    }

    private Carta lerDoCSV(String linha) {
        String[] partes = linha.split(";");
        if (partes.length < 12) {
            System.err.println("Erro: Linha CSV incompleta (esperado 12 campos): " + linha);
            return null;
        }

        try {
            String nome = partes[0];
            int nivel = Integer.parseInt(partes[1]);
            int elixir = Integer.parseInt(partes[2]);
            TipoDaCarta tipo = TipoDaCarta.valueOf(partes[3]);
            Raridade raridade = Raridade.valueOf(partes[4]);
            String caminhoImagem = partes[5];
            int vida = Integer.parseInt(partes[6]);
            int dano = Integer.parseInt(partes[7]);
            double dps = Double.parseDouble(partes[8]);
            String alvos = partes[9];
            String alcance = partes[10];
            String velocidade = partes[11];
            String velocidadeImpacto = partes[12];

            return new Carta(nome, nivel, tipo, raridade, caminhoImagem, elixir,
                    vida, dano, dps, alvos, alcance, velocidade, velocidadeImpacto);

        } catch (Exception e) {
            System.err.println("Erro ao converter linha CSV: " + linha + " - Detalhe: " + e.getMessage());
            return null;
        }
    }

    public String getDiretorioBase() {
        String caminhoCompleto = new java.io.File(NOME_ARQUIVO).getAbsolutePath();

        return Paths.get(NOME_ARQUIVO).toAbsolutePath().getParent().toString();
    }

    public boolean salvarDeck(String nomeDeck, List<Carta> cartas) {
        if (cartas.size() != 8) return false; // Deve ser 8

        StringBuilder linhaCSV = new StringBuilder(nomeDeck);
        for (Carta carta : cartas) {
            linhaCSV.append(";").append(carta.getNome());
        }

        try (FileWriter fw = new FileWriter(ARQUIVO_DECKS, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw))
        {
            out.println(linhaCSV.toString());
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar o deck no arquivo CSV!");
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, List<String>> carregarDecksSalvos() {
        Map<String, List<String>> decksSalvos = new HashMap<>();

        File arquivoDecks = new File(ARQUIVO_DECKS);
        if (!arquivoDecks.exists()) {
            System.out.println("Arquivo de Decks não encontrado. Iniciando com 0 decks.");
            return decksSalvos;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_DECKS))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");

                if (partes.length == 9) { // 1 nome do deck + 8 nomes de cartas
                    String nomeDeck = partes[0];
                    List<String> nomesCartas = new ArrayList<>();

                    // Adiciona os 8 nomes de cartas (começando do índice 1)
                    for (int i = 1; i < partes.length; i++) {
                        nomesCartas.add(partes[i]);
                    }
                    decksSalvos.put(nomeDeck, nomesCartas);
                } else {
                    System.err.println("Linha inválida no decks.csv: " + linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de decks: " + e.getMessage());
            e.printStackTrace();
        }

        return decksSalvos;
    }

}