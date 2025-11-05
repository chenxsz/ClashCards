package com.clashcards.paineis;

import com.clashcards.definicoes.Carta;
import com.clashcards.data.GerenciadorCSV;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class PainelDeColecao {
    private GerenciadorCSV gerenciador;
    private TableView<Carta> cartas;

    public PainelDeColecao(GerenciadorCSV gerenciador) {
        this.gerenciador = gerenciador;
        this.cartas = new TableView<>();

        colunasTabela();
        carregarDados();
    }

    private void colunasTabela() {
        TableColumn<Carta, String> nome = new TableColumn<>("Nome");
        nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        nome.setPrefWidth(150);

        TableColumn<Carta, Integer> custo = new TableColumn<>("Elixir");
        custo.setCellValueFactory(new PropertyValueFactory<>("custo de elixir"));
        custo.setPrefWidth(80);

        TableColumn<Carta, String> tipo = new TableColumn<>("Tipo");
        tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        tipo.setPrefWidth(100);

        TableColumn<Carta, String> raridade = new TableColumn<>("Raridade");
        raridade.setCellValueFactory(new PropertyValueFactory<>("raridade"));
        raridade.setPrefWidth(100);

        TableColumn<Carta, Integer> vida = new TableColumn<>("Vida");
        vida.setCellValueFactory(new PropertyValueFactory<>("vida"));
        vida.setPrefWidth(80);

        TableColumn<Carta, Integer> dano = new TableColumn<>("Dano");
        dano.setCellValueFactory(new PropertyValueFactory<>("dano"));
        dano.setPrefWidth(80);

        cartas.getColumns().addAll(nome, custo, tipo, raridade, vida, dano);

        cartas.setPlaceholder(new Label("Nenhuma carta cadastrada. Cadastre em 'Cartas - Cadastro'."));
    }

    public void carregarDados() {
        ArrayList<Carta> lista = gerenciador.getCartasEmMemoria();

        cartas.getItems().clear();
        cartas.getItems().addAll(lista);
        System.out.println("Coleção atualizada: " + lista.size() + "cartas.");
    }

    public VBox getPainel() {
        VBox painelPrincipal = new VBox(cartas);
        painelPrincipal.setSpacing(5);

        VBox.setVgrow(cartas, javafx.scene.layout.Priority.ALWAYS);

        return painelPrincipal;
    }
}
