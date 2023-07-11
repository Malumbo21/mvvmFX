open module examples.books {
    requires javafx.controls;
    requires javafx.fxml;
    requires mvvmfx.easydi;
    requires eu.lestard.easydi;
    requires eu.lestard.doc.annotations;
    requires mvvmfx;
    requires flatter;
    requires com.theoryinpractive.halbuilder.api;
    requires resteasy.client;
    requires resteasy.core;
    requires org.slf4j;
    requires halbuilder.jaxrs;
    requires javax.ws.rs.api;
    requires jakarta.inject;
    requires advanced.bindings;
    requires net.jodah.typetools;
    requires fontawesomefx;
    exports de.saxsys.mvvmfx.examples.books;
}