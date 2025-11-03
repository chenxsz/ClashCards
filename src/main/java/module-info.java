module ClashCards {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.clashcards.ui to javafx.fxml;
    opens com.clashcards.core to javafx.fxml;

    exports com.clashcards.ui;
    exports com.clashcards.core;
}