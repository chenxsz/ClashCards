package com.clashcards.paineis;

import com.clashcards.definicoes.Carta;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CardComponent extends VBox {
    public CardComponent(Carta carta) {
        try {
            String cssPath = getClass().getResource("/css/estilos.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Erro ao carregar CSS: " + e.getMessage());
        }

        this.getStylesheets().add("card-base");

        String raridadeClass = "raridade-" + carta.getRaridade().toString().toLowerCase();
        this.getStylesheets().add(raridadeClass);

        Label elixirLabel = new Label(String.valueOf(carta.getElixir()));
        elixirLabel.getStyleClass().add("elixir-label");

        Label nomeLabel = new Label(carta.getNome());
        nomeLabel.getStyleClass().add("nome-label");
        nomeLabel.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        imageView.getStyleClass().add("ilustração-carta");

        try {
            Image ilustracao = new Image("file:" + carta.getImagem());
            imageView.setImage(ilustracao);
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem para " + carta.getNome() + ": " + e.getMessage());
        }

        this.getChildren().addAll(elixirLabel, imageView, nomeLabel);

        this.setSpacing(5);
        this.setAlignment(Pos.TOP_CENTER);
    }
}
