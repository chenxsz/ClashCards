package com.clashcards.paineis;

import com.clashcards.data.GerenciadorCSV;
import com.clashcards.definicoes.Carta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PainelDeDeck {

    private final GerenciadorCSV gerenciador;
    private final List<Carta> deckAtual;

    private HBox containerSlotsDeck;
    private FlowPane galeriaColecao;
    private TextField txtNomeDeck;
    private Label lblStatusDeck;
    private ListView<String> listaDecksSalvos;

    public PainelDeDeck(GerenciadorCSV gerenciador) {
        this.gerenciador = gerenciador;
        this.deckAtual = new ArrayList<>();
    }

    public BorderPane getPainel() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        VBox areaDeck = new VBox(10);
        areaDeck.setPadding(new Insets(0, 0, 20, 0));

        Label lblTituloDeck = new Label("Seu Deck de Batalha");
        lblTituloDeck.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        containerSlotsDeck = new HBox(10);
        containerSlotsDeck.setAlignment(Pos.CENTER_LEFT);
        containerSlotsDeck.setPrefHeight(160);
        containerSlotsDeck.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 10; -fx-padding: 10;");

        HBox controlesDeck = new HBox(10);
        controlesDeck.setAlignment(Pos.CENTER_LEFT);

        txtNomeDeck = new TextField();
        txtNomeDeck.setPromptText("Nome do Deck");

        Button btnSalvar = new Button("Salvar Deck");
        btnSalvar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSalvar.setOnAction(e -> salvarDeck());

        Button btnLimpar = new Button("Limpar");
        btnLimpar.setOnAction(e -> limparDeck());

        lblStatusDeck = new Label("0/8 Cartas");
        lblStatusDeck.setFont(Font.font("System", FontWeight.BOLD, 14));

        controlesDeck.getChildren().addAll(new Label("Nome:"), txtNomeDeck, btnSalvar, btnLimpar, new Separator(), lblStatusDeck);

        areaDeck.getChildren().addAll(lblTituloDeck, containerSlotsDeck, controlesDeck);
        root.setTop(areaDeck);

        VBox areaDecksSalvos = new VBox(10);
        areaDecksSalvos.setPadding(new Insets(10, 0, 10, 0));
        Label lblTituloDecksSalvos = new Label("Decks Salvos");
        lblTituloDecksSalvos.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        listaDecksSalvos = new ListView<>();

        listaDecksSalvos.setPrefHeight(150);
        listaDecksSalvos.setMaxHeight(200);

        listaDecksSalvos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                carregarDeckParaEdicao(newValue);
            }
        });

        areaDecksSalvos.getChildren().addAll(lblTituloDecksSalvos, listaDecksSalvos);

        VBox areaColecao = new VBox(10);
        Label lblTituloColecao = new Label("Sua Coleção (Clique para adicionar)");
        lblTituloColecao.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        galeriaColecao = new FlowPane();
        ScrollPane scrollPane = new ScrollPane(galeriaColecao);

        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        areaColecao.getChildren().addAll(lblTituloColecao, scrollPane);

        VBox centro = new VBox(15);
        centro.getChildren().addAll(areaDecksSalvos, areaColecao);

        VBox.setVgrow(areaColecao, Priority.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        areaColecao.setMinHeight(350);

        root.setCenter(centro);

        atualizarVisualDeck();
        carregarColecao();
        carregarDecksSalvosNaUI();

        return root;
    }

    public void carregarDecksSalvosNaUI() {
        List<String> nomesDecks = gerenciador.carregarDecksSalvos().keySet().stream().toList();

        listaDecksSalvos.getItems().clear();
        if (nomesDecks.isEmpty()) {
            listaDecksSalvos.getItems().add("Nenhum deck salvo.");
        } else {
            listaDecksSalvos.getItems().addAll(nomesDecks);
        }
    }

    private void carregarDeckParaEdicao(String nomeDeck) {
        deckAtual.clear();

        Map<String, List<String>> todosOsDecks = gerenciador.carregarDecksSalvos();
        List<String> nomesDasCartas = todosOsDecks.get(nomeDeck);

        if (nomesDasCartas == null || nomesDasCartas.size() != 8) {
            mostrarAlertaErro("Erro", "Falha ao carregar o Deck ou o arquivo está incompleto.");
            return;
        }

        for (String nomeCarta : nomesDasCartas) {
            Carta carta = buscarCartaPorNome(nomeCarta);
            if (carta != null) {
                deckAtual.add(carta);
            } else {
                System.err.println("Carta '" + nomeCarta + "' não encontrada na coleção principal.");
            }
        }

        txtNomeDeck.setText(nomeDeck);
        atualizarVisualDeck();
    }

    private Carta buscarCartaPorNome(String nomeCarta) {
        for (Carta c : gerenciador.getCartasEmMemoria()) {
            if (c.getNome().equals(nomeCarta)) {
                return c;
            }
        }
        return null;
    }

    public void recarregarDados() {
        atualizarVisualDeck();
        carregarColecao();
        carregarDecksSalvosNaUI();
    }

    public void carregarColecao() {
        galeriaColecao.getChildren().clear();
        ArrayList<Carta> todasCartas = gerenciador.getCartasEmMemoria();

        if (todasCartas.isEmpty()) {
            galeriaColecao.getChildren().add(new Label("Nenhuma carta cadastrada. Vá em Cadastro."));
            return;
        }

        for (Carta c : todasCartas) {
            Button btnCarta = criarVisualCarta(c, false);
            galeriaColecao.getChildren().add(btnCarta);
        }
    }

    private void adicionarAoDeck(Carta c) {
        if (deckAtual.size() >= 8) {
            mostrarAlertaErro("Deck Cheio", "Você só pode ter 8 cartas no deck.");
            return;
        }
        for (Carta cartaNoDeck : deckAtual) {
            if (cartaNoDeck.getNome().equals(c.getNome())) {
                mostrarAlertaErro("Duplicada", "Esta carta já está no deck.");
                return;
            }
        }

        deckAtual.add(c);
        atualizarVisualDeck();
    }

    private void removerDoDeck(Carta c) {
        deckAtual.remove(c);
        atualizarVisualDeck();
    }

    private void limparDeck() {
        deckAtual.clear();
        atualizarVisualDeck();
    }

    private void atualizarVisualDeck() {
        containerSlotsDeck.getChildren().clear();
        lblStatusDeck.setText(deckAtual.size() + "/8 Cartas");

        for (Carta c : deckAtual) {
            Button btnCarta = criarVisualCarta(c, true);
            containerSlotsDeck.getChildren().add(btnCarta);
        }

        for (int i = deckAtual.size(); i < 8; i++) {
            VBox slotVazio = new VBox();
            slotVazio.setPrefSize(100, 140);
            slotVazio.setStyle("-fx-border-color: #aaaaaa; -fx-border-style: dashed; -fx-background-color: #f4f4f4;");
            slotVazio.setAlignment(Pos.CENTER);
            slotVazio.getChildren().add(new Label("Vazio"));
            containerSlotsDeck.getChildren().add(slotVazio);
        }
    }

    private void salvarDeck() {
        String nome = txtNomeDeck.getText().trim();
        if (nome.isEmpty()) {
            mostrarAlertaErro("Nome inválido", "Dê um nome para o seu deck.");
            return;
        }

        if (deckAtual.size() != 8) {
            mostrarAlertaErro("Deck Incompleto", "O deck precisa ter exatamente 8 cartas para ser salvo.");
            return;
        }

        boolean salvou = gerenciador.salvarDeck(nome, deckAtual);

        if (salvou) {
            limparDeck();
            carregarDecksSalvosNaUI();
            mostrarAlertaInfo("Sucesso", "Deck '" + nome + "' salvo com sucesso!");
        } else {
            mostrarAlertaErro("Erro", "Erro ao salvar deck. Verifique o console.");
        }
    }

    private Button criarVisualCarta(Carta c, boolean isNoDeck) {
        Button btn = new Button();
        btn.setPrefSize(100, 140);
        btn.setPadding(Insets.EMPTY);

        BorderPane layoutCarta = new BorderPane();

        Label lblElixir = new Label(String.valueOf(c.getElixir()));
        lblElixir.setStyle("-fx-background-color: #FF00FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50;");
        lblElixir.setPadding(new Insets(2, 6, 2, 6));
        BorderPane.setAlignment(lblElixir, Pos.TOP_LEFT);
        BorderPane topContainer = new BorderPane();
        topContainer.setLeft(lblElixir);
        topContainer.setPadding(new Insets(5));

        ImageView imgView = new ImageView();
        String caminhoAbsoluto = c.getImagem();

        if (caminhoAbsoluto != null && !caminhoAbsoluto.isEmpty() && !caminhoAbsoluto.contains("Nenhuma imagem selecionada")) {

            try {
                File arquivoLocal = new File(caminhoAbsoluto);

                if (!arquivoLocal.exists()) {
                    System.err.println("ERRO: Arquivo não encontrado. Caminho procurado: " + arquivoLocal.getAbsolutePath());
                    layoutCarta.setCenter(new Label("Arquivo Não Existe."));
                } else {
                    String uriPath = arquivoLocal.toURI().toString();
                    Image img = new Image(uriPath, 80, 80, true, true);

                    if (img.isError()) {
                        System.err.println("ERRO INTERNO NA IMAGEM. Causa: " + img.exceptionProperty().get().getMessage());
                        layoutCarta.setCenter(new Label("Erro de Leitura."));
                    } else {
                        imgView.setImage(img);
                        layoutCarta.setCenter(imgView);
                    }
                }
            } catch (Exception e) {
                System.err.println("EXCEÇÃO FATAL AO CARREGAR: " + e.getMessage());
                layoutCarta.setCenter(new Label("Erro fatal."));
            }
        } else {
            layoutCarta.setCenter(new Label("(Sem img)"));
        }

        VBox infoBase = new VBox(2);
        infoBase.setAlignment(Pos.CENTER);
        infoBase.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
        Label lblNome = new Label(c.getNome());
        lblNome.setTextFill(Color.WHITE);
        lblNome.setFont(Font.font("Arial", FontWeight.BOLD, 10));

        Label lblNivel = new Label("Nv. " + c.getNivel());
        lblNivel.setTextFill(Color.LIGHTGRAY);
        lblNivel.setFont(Font.font("Arial", 9));

        infoBase.getChildren().addAll(lblNome, lblNivel);
        layoutCarta.setBottom(infoBase);

        layoutCarta.setTop(topContainer);

        btn.setGraphic(layoutCarta);

        String corBorda = switch (c.getRaridade()) {
            case COMUM -> "gray";
            case RARA -> "orange";
            case ÉPICA -> "purple";
            case LENDÁRIA -> "cyan";
            default -> "black";
        };

        btn.setStyle("-fx-border-color: " + corBorda + "; -fx-border-width: 2; -fx-background-color: white;");

        btn.setOnAction(e -> {
            if (isNoDeck) {
                removerDoDeck(c);
            } else {
                adicionarAoDeck(c);
            }
        });

        return btn;
    }

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
}