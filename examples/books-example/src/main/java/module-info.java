module examples.books {
    requires javafx.controls;
    requires javafx.fxml;
    requires mvvmfx.easydi;
    requires mvvmfx;

    opens de.saxsys.mvvmfx.examples.books to javafx.fxml;
    exports de.saxsys.mvvmfx.examples.books;
}