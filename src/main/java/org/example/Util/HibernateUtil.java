package org.example.Util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    public static SessionFactory getSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure();
        return configuration.buildSessionFactory();
    }
}
/*En resumen, la SessionFactory es la fábrica, y la Session es el producto.

SessionFactory (Fábrica): Única por aplicación.

Método clave: openSession() o getCurrentSession().

Session (Conexión): Se abre para una unidad de trabajo o una transacción específica y es de vida corta.

Método clave: save(), get(), update(), delete(), etc. (Operaciones CRUD).*/