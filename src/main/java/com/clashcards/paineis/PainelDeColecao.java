package com.clashcards.paineis;

import com.clashcards.definicoes.Carta;
import com.clashcards.data.GerenciadorCSV;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

public class PainelDeColecao {
    private GerenciadorCSV gerenciador;
    private TableView<Carta> cartas;
    private PainelDeCadastro painelCadastro;

    private Button excluir = new Button("Excluir carta");
    private Button editar = new Button("Editar carta");

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void mostrarAlertaInfo(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    public PainelDeColecao(GerenciadorCSV gerenciador, PainelDeCadastro painelCadastro) {
        this.gerenciador = gerenciador;
        this.painelCadastro = painelCadastro;
        this.cartas = new TableView<>();

        colunasTabela();
        carregarDados();

        excluir.setOnAction(e -> excluirCarta());
        editar.setOnAction(e -> editarCarta());
    }

    public void setPainelCadastro(PainelDeCadastro painelCadastro) {
        this.painelCadastro = painelCadastro;
    }

    private void excluirCarta() {
        Carta selecionada = cartas.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            mostrarAlertaErro("Selecione uma carta", "Por favor, selecione uma carta para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação de exclusão");
        confirmacao.setHeaderText("Tem certeza que deseja excluir a carta " + selecionada.getNome() + "?");
        confirmacao.setContentText("Essa ação é permanente e removerá a carta do arquivo");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean sucesso = gerenciador.removerCarta(selecionada);

            if (sucesso) {
                cartas.getItems().remove(selecionada);
                mostrarAlertaInfo("Sucesso!", "A carta foi excluída com sucesso.");
            } else {
                mostrarAlertaErro("Erro!", "Não foi possível exluir a carta. Tente novamente.");
            }
        }
    }

    private void editarCarta() {
        Carta selecionada = cartas.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            mostrarAlertaErro("Selecione uma carta", "Por favor, selecione uma carta para editar.");
            return;
        }

        painelCadastro.carregarDadosParaEdicao(selecionada);

        mostrarAlertaInfo("Carregamento concluído", "Os dados da carta '" + selecionada.getNome() + "' foram carregados na aba 'Cartas - Cadastro'. Vá até lá para editar.");
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
        System.out.println("Coleção atualizada: " + lista.size() + " cartas.");
    }

    // PainelDeColecao.java

    public VBox getPainel() {
        HBox containerBotoes = new HBox(10, editar, excluir);
        containerBotoes.setPadding(new Insets(10, 0, 0, 0));

        containerBotoes.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(containerBotoes, Priority.ALWAYS);

        VBox painelPrincipal = new VBox(5);
        painelPrincipal.setPadding(new Insets(10));

        painelPrincipal.getChildren().addAll(cartas, containerBotoes);

        VBox.setVgrow(cartas, Priority.ALWAYS);

        painelPrincipal.setMaxWidth(Double.MAX_VALUE);
        painelPrincipal.setMaxHeight(Double.MAX_VALUE);

        return painelPrincipal;
    }
}
