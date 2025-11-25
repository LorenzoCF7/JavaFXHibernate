package org.example.DAO;


import org.example.Entities.Socio;
import org.example.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SocioDAO {

    public Socio create(Socio dueno) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(dueno);
        tx.commit();
        session.close();
        return dueno;
    }

    public Socio update(Socio socio) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(socio);
        tx.commit();
        session.close();
        return socio;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Socio socio = session.get(Socio.class, id);
        if (socio != null) {
            session.delete(socio);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public List<Socio> findByNombre(String nombre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Socio> socios = session.createQuery("from Socio where nombre = :nombre", Socio.class)
                .setParameter("nombre", nombre)
                .list();
        session.close();
        return socios;
    }

    public List<Socio> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Socio> socios = session.createQuery("from Socio", Socio.class).list();
        session.close();
        return socios;
    }

    public Socio findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Socio socio = session.get(Socio.class, id);
        session.close();
        return socio;
    }
}
