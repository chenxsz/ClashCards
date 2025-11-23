package com.clashcards.paineis;

import com.clashcards.definicoes.Carta;
import com.clashcards.data.GerenciadorCSV;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class PainelDeDeck {

    private final GerenciadorCSV gerenciador;
    private final List<Carta> deckAtual;

    // Componentes Visuais
    private HBox containerSlotsDeck;
    private FlowPane galeriaColecao;
    private TextField txtNomeDeck;
    private Label lblStatusDeck;

    public PainelDeDeck(GerenciadorCSV gerenciador) {
        this.gerenciador = gerenciador;
        this.deckAtual = new ArrayList<>();
    }

    public BorderPane getPainel() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // --- TOPO: ÁREA DO DECK (8 SLOTS) ---
        VBox areaDeck = new VBox(10);
        areaDeck.setPadding(new Insets(0, 0, 20, 0));

        Label lblTituloDeck = new Label("Seu Deck de Batalha");
        lblTituloDeck.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Container horizontal para as 8 cartas
        containerSlotsDeck = new HBox(10);
        containerSlotsDeck.setAlignment(Pos.CENTER_LEFT);
        containerSlotsDeck.setPrefHeight(160);
        containerSlotsDeck.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 10; -fx-padding: 10;");

        // Área de controles do deck (Nome e Botão Salvar)
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

        // --- CENTRO: COLEÇÃO DE CARTAS (GALERIA) ---
        VBox areaColecao = new VBox(10);
        Label lblTituloColecao = new Label("Sua Coleção (Clique para adicionar)");
        lblTituloColecao.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // FlowPane permite que as cartas se organizem em grade automaticamente
        galeriaColecao = new FlowPane();
        galeriaColecao.setHgap(15);
        galeriaColecao.setVgap(15);
        galeriaColecao.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(galeriaColecao);
        scrollPane.setFitToWidth(true); // Faz o scroll se adaptar a largura
        scrollPane.setStyle("-fx-background-color: transparent;");

        areaColecao.getChildren().addAll(lblTituloColecao, scrollPane);
        // Garante que a coleção ocupe o resto do espaço
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.setCenter(areaColecao);

        // Inicializa a visualização
        atualizarVisualDeck();
        carregarColecao();

        return root;
    }

    // --- LÓGICA DE NEGÓCIO ---

    public void carregarColecao() {
        galeriaColecao.getChildren().clear();
        ArrayList<Carta> todasCartas = gerenciador.getCartasEmMemoria();

        if (todasCartas.isEmpty()) {
            galeriaColecao.getChildren().add(new Label("Nenhuma carta cadastrada. Vá em Cadastro."));
            return;
        }

        for (Carta c : todasCartas) {
            // Cria o visual da carta para a galeria (clique adiciona)
            Button btnCarta = criarVisualCarta(c, false);
            galeriaColecao.getChildren().add(btnCarta);
        }
    }

    private void adicionarAoDeck(Carta c) {
        if (deckAtual.size() >= 8) {
            mostrarAlertaErro("Deck Cheio", "Você só pode ter 8 cartas no deck.");
            return;
        }
        // Verifica se a carta já existe no deck (comparando por nome ou objeto)
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

        // 1. Adiciona as cartas que estão no deck
        for (Carta c : deckAtual) {
            Button btnCarta = criarVisualCarta(c, true); // true = clique remove
            containerSlotsDeck.getChildren().add(btnCarta);
        }

        // 2. Preenche o restante com slots vazios "fantasmas" para manter o layout de 8 espaços
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

        // AQUI VOCÊ CHAMA O GERENCIADOR PARA SALVAR O DECK
        // Como não tenho o código de salvar deck no GerenciadorCSV, deixo o exemplo:
        /*
        boolean salvou = gerenciador.salvarDeck(nome, deckAtual);
        if(salvou) {
             mostrarAlertaInfo("Sucesso", "Deck salvo com sucesso!");
        } else {
             mostrarAlertaErro("Erro", "Erro ao salvar deck.");
        }
        */

        // Apenas para feedback visual enquanto você não implementa o método no gerenciador:
        mostrarAlertaInfo("Simulação", "Deck '" + nome + "' com 8 cartas validado! (Implementar lógica no CSV)");
    }

    // --- COMPONENTES VISUAIS AUXILIARES ---

    /**
     * Cria um botão estilizado que parece uma carta do Clash Royale.
     * @param c A carta a ser exibida
     * @param isNoDeck Se true, o clique remove a carta. Se false, adiciona.
     */
    private Button criarVisualCarta(Carta c, boolean isNoDeck) {
        Button btn = new Button();
        btn.setPrefSize(100, 140);
        btn.setPadding(Insets.EMPTY);

        // Layout interno da carta
        BorderPane layoutCarta = new BorderPane();

        // Topo: Custo de Elixir (Gota roxa/rosa)
        Label lblElixir = new Label(String.valueOf(c.getElixir()));
        lblElixir.setStyle("-fx-background-color: #FF00FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50;");
        lblElixir.setPadding(new Insets(2, 6, 2, 6));
        BorderPane.setAlignment(lblElixir, Pos.TOP_LEFT);
        BorderPane topContainer = new BorderPane();
        topContainer.setLeft(lblElixir);
        topContainer.setPadding(new Insets(5));

        // Centro: Imagem
        try {
            // Tenta carregar a imagem se o caminho for válido, senão mostra texto
            if (c.getImagem() != null && !c.getImagem().contains("Nenhuma imagem")) {
                // Note: Em JavaFX, carregar arquivo local geralmente precisa de "file:/" prefixo se for caminho absoluto
                // Aqui estou assumindo que c.getImagem() retorna algo compatível ou URL.
                // Se for caminho de arquivo Windows puro, use: new File(c.getImagem()).toURI().toString()
                Image img = new Image(c.getImagem(), 80, 80, true, true);
                ImageView imgView = new ImageView(img);
                layoutCarta.setCenter(imgView);
            } else {
                layoutCarta.setCenter(new Label("(Sem img)"));
            }
        } catch (Exception e) {
            layoutCarta.setCenter(new Label("Img Erro"));
        }

        // Base: Nome e Nível
        VBox infoBase = new VBox(2);
        infoBase.setAlignment(Pos.CENTER);
        infoBase.setStyle("-fx-background-color: rgba(0,0,0,0.6);"); // Fundo semi-transparente para ler o texto
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

        // Estilização do botão baseada na raridade (Opcional)
        String corBorda = switch (c.getRaridade()) {
            case COMUM -> "gray";
            case RARA -> "orange";
            case ÉPICA -> "purple";
            case LENDÁRIA -> "cyan";
            default -> "black";
        };

        btn.setStyle("-fx-border-color: " + corBorda + "; -fx-border-width: 2; -fx-background-color: white;");

        // Ação do clique
        btn.setOnAction(e -> {
            if (isNoDeck) {
                removerDoDeck(c);
            } else {
                adicionarAoDeck(c);
            }
        });

        return btn;
    }

    // --- ALERTAS (Copiados dos seus outros painéis para consistência) ---

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
