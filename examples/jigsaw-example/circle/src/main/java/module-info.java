
module de.saxsys.mvvmfx.examples.jigsaw.circle {

    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires de.saxsys.mvvmfx;
    requires de.saxsys.mvvmfx;

    exports de.saxsys.mvvmfx.examples.jigsaw.circle;

    opens de.saxsys.mvvmfx.examples.jigsaw.circle to de.saxsys.mvvmfx, javafx.fxml;
}