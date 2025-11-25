module org.example.javafxhibernate {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;

    opens org.example.javafxhibernate to javafx.fxml;
    opens org.example.Entities to org.hibernate.orm.core;
    opens org.example.DAO to org.hibernate.orm.core;
    opens org.example.Util to org.hibernate.orm.core;
    
    exports org.example.javafxhibernate;
    exports org.example.Entities;
    exports org.example.DAO;
    exports org.example.Util;
}