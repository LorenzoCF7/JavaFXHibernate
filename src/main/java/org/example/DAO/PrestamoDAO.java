package org.example.DAO;

import org.example.Entities.Autor;
import org.example.Entities.Prestamo;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PrestamoDAO {

    public Prestamo create(Prestamo prestamo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(prestamo);
        tx.commit();
        session.close();
        return prestamo;
    }



    public List<Prestamo> findByID(Integer id_socio) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Prestamo> prestamos = session.createQuery("from Prestamo where socio = :id_socio", Prestamo.class)
                .setParameter("socio_id", id_socio)
                .list();
        session.close();
        return prestamos;
    }

    public List<Prestamo> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Prestamo> prestamos = session.createQuery("from Prestamo ", Prestamo.class).list();
        session.close();
        return prestamos;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Prestamo prestamo = session.get(Prestamo.class, id);
        if (prestamo != null) {
            session.delete(prestamo);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public Prestamo update(Prestamo prestamo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(prestamo);
        tx.commit();
        session.close();
        return prestamo;
    }

}
