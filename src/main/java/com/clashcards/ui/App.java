package com.clashcards.ui;

import com.clashcards.data.GerenciadorCSV;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private GerenciadorCSV gerenciadorCSV = new GerenciadorCSV();

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Clash Cards - Gerenciador de Decks");
        TabPane painelDeAbas = new TabPane();

        PainelDeCadastro cadastro = new PainelDeCadastro(gerenciadorCSV);
        VBox formulario = cadastro.getPainel();

        Tab abaDeCartas = new Tab("Cartas - Cadastro");
        Tab abaDeColecao = new Tab("Coleção");
        Tab abaDeDecks = new Tab("Decks");

        abaDeCartas.setContent(formulario);
        abaDeColecao.setContent(new BorderPane(new Label("VISUALIZAR todas as cartas salvas.")));
        abaDeDecks.setContent(new BorderPane(new Label("CRIAR/EDITAR decks.")));

        abaDeCartas.setClosable(false);
        abaDeColecao.setClosable(false);
        abaDeDecks.setClosable(false);

        painelDeAbas.getTabs().addAll(abaDeCartas, abaDeColecao, abaDeDecks);

        Scene cenaPrincipal = new Scene(painelDeAbas, 800, 600);
        primaryStage.setScene(cenaPrincipal);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
