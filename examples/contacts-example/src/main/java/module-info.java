module contacts.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;
    requires java.desktop;
    opens de.saxsys.mvvmfx.examples.contacts.ui to javafx.fxml,javafx.graphics;
}