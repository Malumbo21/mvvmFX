module contacts.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;
    requires java.desktop;
    requires de.saxsys.mvvmfx;
    requires jakarta.inject;
    requires slf4j.api;
    requires mvvmfx.cdi;
    requires jakarta.xml.bind;
    requires datafx.core;
    requires datafx.datareader;
    requires jakarta.annotation;
    requires org.controlsfx.controls;
    requires fontawesomefx;
    requires mvvmfx.validation;
    requires advanced.bindings;
    requires cdi.api;
    opens de.saxsys.mvvmfx.examples.contacts to javafx.fxml,javafx.graphics;
}