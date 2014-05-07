package edu.unc.ceccr.persistence;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    private static Logger logger = Logger.getLogger(HibernateUtil.class.getName());

    static {
        Configuration config = new Configuration()
                .addAnnotatedClass(Job.class)
                .addAnnotatedClass(JobStats.class)
                .addAnnotatedClass(KnnModel.class)
                .addAnnotatedClass(Predictor.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Prediction.class)
                .addAnnotatedClass(PredictionValue.class)
                .addAnnotatedClass(ExternalValidation.class)
                .addAnnotatedClass(DataSet.class)
                .addAnnotatedClass(SoftwareLink.class)
                .addAnnotatedClass(Descriptors.class)
                .addAnnotatedClass(DescriptorGenerator.class)
                .addAnnotatedClass(KnnParameters.class)
                .addAnnotatedClass(KnnPlusParameters.class)
                .addAnnotatedClass(KnnPlusModel.class)
                .addAnnotatedClass(SvmParameters.class)
                .addAnnotatedClass(SvmModel.class)
                .addAnnotatedClass(RandomForestParameters.class)
                .addAnnotatedClass(RandomForestTree.class)
                .addAnnotatedClass(RandomForestGrove.class)
                .configure();

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(config.getProperties()).build();
        sessionFactory = config.buildSessionFactory(serviceRegistry);
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }
}

