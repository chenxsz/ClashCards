module ClashCards {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.clashcards.paineis to javafx.fxml;
    opens com.clashcards.definicoes to javafx.fxml;

    exports com.clashcards.paineis;
    exports com.clashcards.definicoes;
}