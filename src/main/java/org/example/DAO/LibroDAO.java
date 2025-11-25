package org.example.DAO;

import org.example.Entities.Libro;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class LibroDAO {

    public List<Libro> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Libro> libros = session.createQuery("from Libro", Libro.class).list();
        session.close();
        return libros;
    }

    public Libro findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Libro libro = session.get(Libro.class, id);
        session.close();
        return libro;
    }

    public List<Libro> findByTitulo(String titulo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Libro> libros = session.createQuery("from Libro where titulo like :titulo", Libro.class)
                .setParameter("titulo", "%" + titulo + "%")
                .list();
        session.close();
        return libros;
    }

    public List<Libro> findByNombre(String nombre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Libro> libros = session.createQuery("from Libro where titulo = :nombre", Libro.class)
                .setParameter("titulo", nombre)
                .list();
        session.close();
        return libros;
    }

    public List<Libro> findLibrosAutorID(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Libro> libros = session.createQuery("from Libro where autor = :id", Libro.class)
                .setParameter("autor_id", id)
                .list();
        session.close();
        return libros;
    }

    public Libro create(Libro libro) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(libro);
        tx.commit();
        session.close();
        return libro;
    }

    public Libro update(Libro libro) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(libro);
        tx.commit();
        session.close();
        return libro;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Libro libro = session.get(Libro.class, id);
        if (libro != null) {
            session.delete(libro);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }
}
