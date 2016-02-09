package edu.unc.ceccr.chembench.persistence;

import edu.unc.ceccr.chembench.global.Constants;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());

    static {
        final String DATABASENAME = Constants.CECCR_DATABASE_NAME;
        final String URL;
        if (Constants.DATABASE_URL.endsWith(DATABASENAME)) {
            URL = Constants.DATABASE_URL;
        } else {
            URL = Constants.DATABASE_URL + DATABASENAME;
        }

        Configuration configuration = new Configuration().setProperty("hibernate.connection.url", URL)
                .setProperty("hibernate.connection.username", Constants.DATABASE_USERNAME)
                .setProperty("hibernate.connection.password", Constants.CECCR_DATABASE_PASSWORD)
                .setProperty("hibernate.connection.driver_class", Constants.DATABASE_DRIVER)
                .addAnnotatedClass(Job.class).addAnnotatedClass(JobStats.class).addAnnotatedClass(KnnModel.class)
                .addAnnotatedClass(Predictor.class).addAnnotatedClass(User.class).addAnnotatedClass(Prediction.class)
                .addAnnotatedClass(PredictionValue.class).addAnnotatedClass(ExternalValidation.class)
                .addAnnotatedClass(Dataset.class).addAnnotatedClass(SoftwareLink.class)
                .addAnnotatedClass(Descriptors.class).addAnnotatedClass(DescriptorGenerator.class)
                .addAnnotatedClass(KnnParameters.class).addAnnotatedClass(KnnPlusParameters.class)
                .addAnnotatedClass(KnnPlusModel.class).addAnnotatedClass(SvmParameters.class)
                .addAnnotatedClass(SvmModel.class).addAnnotatedClass(RandomForestParameters.class)
                .addAnnotatedClass(RandomForestTree.class).addAnnotatedClass(RandomForestGrove.class);
        ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }
}
