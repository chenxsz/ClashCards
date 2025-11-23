package com.clashcards.paineis;

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

        PainelDeColecao colecao = new PainelDeColecao(gerenciadorCSV, null);
        VBox colecaoTabela = colecao.getPainel();

        PainelDeCadastro cadastro = new PainelDeCadastro(gerenciadorCSV, colecao);
        VBox formulario = cadastro.getPainel();

        colecao.setPainelCadastro(cadastro);

        PainelDeDeck deckManager = new PainelDeDeck(gerenciadorCSV);
        BorderPane visualDoDeck = deckManager.getPainel();


        Tab abaDeCartas = new Tab("Cartas - Cadastro");
        abaDeCartas.setContent(formulario);

        Tab abaDeColecao = new Tab("Coleção");
        abaDeColecao.setContent(colecaoTabela);

        Tab abaDeDecks = new Tab("Decks");
        abaDeDecks.setContent(visualDoDeck);

        abaDeCartas.setClosable(false);
        abaDeColecao.setClosable(false);
        abaDeDecks.setClosable(false);

        painelDeAbas.getTabs().addAll(abaDeCartas, abaDeColecao, abaDeDecks);

        Scene cenaPrincipal = new Scene(painelDeAbas, 900, 600);
        java.net.URL cssUrl = getClass().getResource("/estilos.css");

        if (cssUrl != null) {
            cenaPrincipal.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("⚠️ ATENÇÃO: Arquivo 'estilos.css' não foi encontrado. Verifique a pasta.");
        }
        primaryStage.setScene(cenaPrincipal);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
