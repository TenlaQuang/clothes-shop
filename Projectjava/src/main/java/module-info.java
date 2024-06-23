module project.projectjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires mysql.connector.j;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.sql;
    requires java.naming;
    requires itextpdf;
    requires spring.security.crypto;

    exports System.client;
    exports System.Model;
    exports System.Controller;
    exports System.View;
    opens System.Model;
    opens System.Controller to javafx.fxml;


}