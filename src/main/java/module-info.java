module com.ferreteria.ferreteriaapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires itextpdf;

    exports com.ferreteria.ferreteriaapp.model;
    opens com.ferreteria.ferreteriaapp.model to javafx.base;
    opens com.ferreteria.ferreteriaapp.controller to javafx.fxml;

    opens com.ferreteria.ferreteriaapp to javafx.fxml;
    exports com.ferreteria.ferreteriaapp;
}