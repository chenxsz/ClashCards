package com.clashcards.core;

public class Carta {
    private String nome;
    private int nivel;
    private TipoDaCarta tipo;
    private Raridade raridade;
    private String imagem;
    private int elixir;
    private int vida;
    private int dano;
    private double danoPorSegundo;
    private String alvos;
    private String alcance;
    private String velocidade;
    private String velocidadeDeImpacto;

    public Carta(String nome, int nivel, TipoDaCarta tipo, Raridade raridade, String imagem, int elixir, int vida, int dano,
                 double danoPorSegundo, String alvos, String alcance, String velocidade, String velocidadeDeImpacto) {
        this.nome = nome;
        this.nivel= nivel;
        this.tipo = tipo;
        this.raridade = raridade;
        this.imagem = imagem;
        this.elixir = elixir;
        this.vida = vida;
        this.dano = dano;
        this.danoPorSegundo = danoPorSegundo;
        this.alvos = alvos;
        this.alcance= alcance;
        this.velocidade = velocidade;
        this.velocidadeDeImpacto = velocidadeDeImpacto;
    }

    public String getNome() {
        return nome;
    }

    public int getNivel() {
        return nivel;
    }

    public TipoDaCarta getTipo() {
        return tipo;
    }

    public Raridade getRaridade() {
        return raridade;
    }

    public String getImagem() {
        return imagem;
    }

    public int getElixir() {
        return elixir;
    }

    public int getVida() {
        return vida;
    }

    public int getDano() {
        return dano;
    }

    public double getDanoPorSegundo() {
        return danoPorSegundo;
    }

    public String getAlcance() {
        return alcance;
    }

    public String getAlvos() {
        return alvos;
    }

    public String getVelocidade() {
        return velocidade;
    }

    public String getVelocidadeDeImpacto() {
        return velocidadeDeImpacto;
    }

    //evitar duplicações no CSV
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Carta carta = (Carta) obj;
        return nome.equalsIgnoreCase(carta.nome);
    }

    public int hashCode() {
        return nome.toLowerCase().hashCode();
    }
}
