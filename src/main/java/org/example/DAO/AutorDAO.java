package org.example.DAO;

import org.example.Entities.Autor;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AutorDAO {

    public Autor create(Autor autor) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(autor);
        tx.commit();
        session.close();
        return autor;
    }

    public Autor update(Autor autor) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(autor);
        tx.commit();
        session.close();
        return autor;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Autor autor = session.get(Autor.class, id);
        if (autor != null) {
            session.delete(autor);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public List<Autor> findByNombre(String nombre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Autor> autores = session.createQuery("from Autor where nombre = :nombre", Autor.class)
                .setParameter("nombre", nombre)
                .list();
        session.close();
        return autores;
    }

    public List<Autor> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Autor> autores = session.createQuery("from Autor", Autor.class).list();
        session.close();
        return autores;
    }

    public Autor findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Autor autor = session.get(Autor.class, id);
        session.close();
        return autor;
    }
}
