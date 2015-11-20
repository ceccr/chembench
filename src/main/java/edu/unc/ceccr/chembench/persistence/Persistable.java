package edu.unc.ceccr.chembench.persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;

public class Persistable {
    public void save() {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            if (session == null) {
                throw new RuntimeException("Received null session");
            }
            tx = session.beginTransaction();
            session.saveOrUpdate(this);
            tx.commit();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to open session", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
