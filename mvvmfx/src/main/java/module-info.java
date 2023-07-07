open module mvvmfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.slf4j;
    requires eu.lestard.doc.annotations;
    requires net.jodah.typetools;
    exports de.saxsys.mvvmfx;
    exports de.saxsys.mvvmfx.internal;
    exports de.saxsys.mvvmfx.utils.notifications;
    exports de.saxsys.mvvmfx.utils.viewlist;
    exports de.saxsys.mvvmfx.utils.commands;
    exports de.saxsys.mvvmfx.utils.itemlist;
    exports de.saxsys.mvvmfx.utils.mapping;

}