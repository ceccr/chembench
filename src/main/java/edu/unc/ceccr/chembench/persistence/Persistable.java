package edu.unc.ceccr.chembench.persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class Persistable {
    public void save() {
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(this);
        tx.commit();
        session.close();
    }
}
