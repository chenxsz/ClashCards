package com.clashcards.data;

import com.clashcards.core.Carta;
import com.clashcards.core.Raridade;
import com.clashcards.core.TipoDaCarta;

import java.io.*;
import java.util.ArrayList;

public class GerenciadorCSV {

    private static final String NOME_ARQUIVO = "cartas.csv";
    private ArrayList<Carta> cartasEmMemoria;

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

            return true; // Sucesso

        } catch (IOException e) {
            System.err.println("Erro ao salvar a carta no arquivo CSV!");
            e.printStackTrace();
            // Se deu erro ao salvar no arquivo, remove da memória também
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
}