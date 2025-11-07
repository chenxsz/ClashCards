package com.clashcards.paineis;

import com.clashcards.definicoes.Carta;
import com.clashcards.definicoes.Raridade;
import com.clashcards.definicoes.TipoDaCarta;

import com.clashcards.data.GerenciadorCSV;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class PainelDeCadastro {
    private TextField nome = new TextField();
    private TextField nivel = new TextField();
    private TextField elixir = new TextField();

    private ComboBox<TipoDaCarta> tipo = new ComboBox<>();
    private ComboBox<Raridade> raridade = new ComboBox<>();

    private TextField vida = new TextField();
    private TextField dano = new TextField();
    private TextField danosPorSegundo = new TextField();
    private TextField alvos = new TextField();
    private TextField alcance = new TextField();
    private TextField velocidade = new TextField();
    private TextField velocidadeImpacto = new TextField();

    private Button salvar = new Button("Salvar");
    private Button imagem = new Button("Carregar imagem");
    private Label caminhoDaImagem = new Label("Nenhuma imagem selecionada");
    private GerenciadorCSV gerenciador;
    private  PainelDeColecao colecao;

    PainelDeCadastro painelCadastro;

    private boolean estaEmModoEdicao = false;
    private Carta cartaSendoEditada = null;
    private String textoOriginalBotao;

    public PainelDeCadastro(GerenciadorCSV gerenciador, PainelDeColecao colecao) {
        this.gerenciador = gerenciador;
        this.colecao = colecao;

        textoOriginalBotao = salvar.getText();
    }

    //organizar tudo em linha e coluna - como uma planilha
    public VBox getPainel() {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));

        formGrid.add(new Label("Nome:"), 0, 0);
        formGrid.add(nome, 1, 0);

        formGrid.add(new Label("Nível:"), 0, 1);
        formGrid.add(nivel, 1, 1);

        formGrid.add(new Label("Custo Elixir:"), 2, 1);
        formGrid.add(elixir, 3, 1);

        formGrid.add(new Label("Tipo:"), 0, 2);
        tipo.getItems().setAll(TipoDaCarta.values());
        formGrid.add(tipo, 1, 2);

        formGrid.add(new Label("Raridade:"), 2, 2);
        raridade.getItems().setAll(Raridade.values());
        formGrid.add(raridade, 3, 2);

        formGrid.add(new Label("Pontos de Vida:"), 0, 3);
        formGrid.add(vida, 1, 3);

        formGrid.add(new Label("Dano:"), 2, 3);
        formGrid.add(dano, 3, 3);

        formGrid.add(new Label("DPS:"), 0, 4);
        formGrid.add(danosPorSegundo, 1, 4);

        formGrid.add(new Label("Alvos:"), 2, 4);
        formGrid.add(alvos, 3, 4);

        formGrid.add(new Label("Alcance:"), 0, 5);
        formGrid.add(alcance, 1, 5);

        formGrid.add(new Label("Velocidade:"), 2, 5);
        formGrid.add(velocidade, 3, 5);

        formGrid.add(new Label("Vel. Impacto:"), 0, 6);
        formGrid.add(velocidadeImpacto, 1, 6);

        formGrid.add(imagem, 0, 7);
        formGrid.add(caminhoDaImagem, 1, 7);

        imagem.setOnAction(e -> selecionarImagem());
        salvar.setOnAction(e -> salvarCarta());

        VBox painelPrincipal = new VBox(formGrid, salvar);
        painelPrincipal.setPadding(new Insets(10));
        painelPrincipal.setSpacing(10);

        return painelPrincipal;
    }

    private void selecionarImagem() {
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Selecionar Imagem da Carta");
        seletor.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        java.io.File arquivoSelecionado = seletor.showOpenDialog(null);

        if (arquivoSelecionado != null) {
            String caminho = arquivoSelecionado.toURI().toString();
            caminhoDaImagem.setText(caminho);
            System.out.println("Imagem selecionada: " + caminho);
        }
    }

    private Carta construirCartaAposEdicao() {
        try {
            String nome = this.nome.getText().trim();
            int nivel = Integer.parseInt(this.nivel.getText().trim());
            int elixir = Integer.parseInt(this.elixir.getText().trim());
            TipoDaCarta tipo = this.tipo.getValue();
            Raridade raridade = this.raridade.getValue();
            String imagem = caminhoDaImagem.getText();
            int dano = Integer.parseInt(this.dano.getText().trim());
            double dps = Double.parseDouble(this.danosPorSegundo.getText().trim());
            int vida = Integer.parseInt(this.vida.getText().trim());

            if (nome.isEmpty() || tipo == null || raridade == null) {
                mostrarAlertaErro("Os campos obrigatórios (Nome, Tipo, Raridade) não podem estar vazios!");
                return null;
            }

            return new Carta(nome, nivel, tipo, raridade, imagem, elixir, vida,
                    dano, dps,
                    this.alvos.getText().trim(),
                    this.alcance.getText().trim(),
                    this.velocidade.getText().trim(),
                    this.velocidadeImpacto.getText().trim());

        } catch (NumberFormatException e) {
            mostrarAlertaErro("Erro de Formato: Campos numéricos (Nível, Elixir, Vida, etc.) devem conter apenas números.");
            return null;
        } catch (Exception e) {
            mostrarAlertaErro("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void tratarEdicao() {
        Carta novaCarta = construirCartaAposEdicao();

        if (novaCarta == null) {
            return;
        }

        boolean sucessoRemocao = gerenciador.removerCarta(cartaSendoEditada);
        boolean sucessoAdicao = gerenciador.salvarNovaCarta(novaCarta);

        if (sucessoRemocao && sucessoAdicao) {
            limparCampos();
            nome.setDisable(false);
            salvar.setText(textoOriginalBotao);
            this.estaEmModoEdicao = false;
            this.cartaSendoEditada = null;

            colecao.carregarDados();
            mostrarAlertaInfo("Edição Concluída", "A carta '" + novaCarta.getNome() + "' foi salva com sucesso!");
        } else {
            mostrarAlertaErro("Houve um problema ao salvar as alterações. Tente novamente.");
        }
    }

    private void salvarCarta() {
        if (estaEmModoEdicao) {
            tratarEdicao();
            return;
        }

        System.out.println("Salvando carta...");

        try {
            String nome = this.nome.getText().trim();
            int nivel = Integer.parseInt(this.nivel.getText().trim());
            int elixir = Integer.parseInt(this.elixir.getText().trim());
            TipoDaCarta tipo = this.tipo.getValue();
            Raridade raridade = this.raridade.getValue();
            String imagem = caminhoDaImagem.getText();
            int dano = Integer.parseInt(this.dano.getText().trim());
            double dps = Double.parseDouble(this.danosPorSegundo.getText().trim());
            int vida = Integer.parseInt(this.vida.getText().trim());
            String alcance = this.alcance.getText().trim();
            String velocidade = this.velocidade.getText().trim();
            String velImpacto = this.velocidadeImpacto.getText().trim();

            if (nome.isEmpty() || tipo == null || raridade == null || imagem.equals("Nenhuma imagem selecionada.")) {
                mostrarAlertaErro("Campos obrigatórios (Nome, Tipo, Raridade, Imagem) não podem estar vazios!");
                return;
            }

            Carta novaCarta = new Carta(nome, nivel, tipo, raridade, imagem, elixir, vida,
                    dano, dps,
                    this.alvos.getText().trim(),
                    this.alcance.getText().trim(),
                    this.velocidade.getText().trim(),
                    this.velocidadeImpacto.getText().trim());
            
            boolean salvou = gerenciador.salvarNovaCarta(novaCarta);

            if (salvou) {
                mostrarAlertaInfo("Sucesso!", "Carta salva com sucesso no arquivo 'cartas.svc'.");
                limparCampos();

                if (colecao != null) {
                    colecao.carregarDados();
                }
            } else {
                mostrarAlertaErro("Uma carta com este nome já existe!");
            }

        } catch (NumberFormatException e) {
            mostrarAlertaErro("Erro de Formato: Campos numéricos (Nível, Elixir, Vida, etc.) devem conter apenas números.");
        } catch (Exception e) {
            mostrarAlertaErro("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        nome.clear();
        nivel.clear();
        elixir.clear();
        tipo.setValue(null);
        raridade.setValue(null);
        vida.clear();
        dano.clear();
        danosPorSegundo.clear();
        alvos.clear();
        alcance.clear();
        velocidade.clear();
        velocidadeImpacto.clear();
        caminhoDaImagem.setText("Nenhuma imagem selecionada.");
    }

    public void carregarDadosParaEdicao(Carta carta) {
        nome.setText(carta.getNome());
        nivel.setText(String.valueOf(carta.getNivel()));
        elixir.setText(String.valueOf(carta.getElixir()));
        tipo.setValue(carta.getTipo());
        raridade.setValue(carta.getRaridade());
        vida.setText(String.valueOf(carta.getVida()));
        dano.setText(String.valueOf(carta.getDano()));
        danosPorSegundo.setText(String.valueOf(carta.getDanoPorSegundo()));
        alvos.setText(carta.getAlvos());
        alcance.setText(carta.getAlcance());
        velocidade.setText(carta.getVelocidade());
        velocidadeImpacto.setText(String.valueOf(carta.getVelocidadeDeImpacto()));

        nome.setDisable(true);

        this.cartaSendoEditada = carta;
        this.estaEmModoEdicao = true;
        salvar.setText("Salvar Edição");

        mostrarAlertaInfo("Modo Edição de carta", "Edite as propriedades e clique em 'Salvar Edição'.");
    }


    private void mostrarAlertaErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro no Cadastro");
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
