package edu.unc.ceccr.chembench.persistence;

import edu.unc.ceccr.chembench.global.Constants;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import java.sql.DriverManager;
import java.sql.SQLException;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static String PASSWORD;
    private static String RAW_URL;
    private static String USERNAME;
    private static String DATABASENAME;

    private static Logger logger = Logger.getLogger(HibernateUtil.class.getName());

    static {
        sessionFactory = new AnnotationConfiguration().addAnnotatedClass(Job.class).addAnnotatedClass(JobStats.class)
                .addAnnotatedClass(KnnModel.class).addAnnotatedClass(Predictor.class).addAnnotatedClass(User.class)
                .addAnnotatedClass(Prediction.class).addAnnotatedClass(PredictionValue.class)
                .addAnnotatedClass(ExternalValidation.class).addAnnotatedClass(Dataset.class)
                .addAnnotatedClass(SoftwareLink.class).addAnnotatedClass(Descriptors.class)
                .addAnnotatedClass(DescriptorGenerator.class).addAnnotatedClass(KnnParameters.class)
                .addAnnotatedClass(KnnPlusParameters.class).addAnnotatedClass(KnnPlusModel.class)
                .addAnnotatedClass(SvmParameters.class).addAnnotatedClass(SvmModel.class)
                .addAnnotatedClass(RandomForestParameters.class).addAnnotatedClass(RandomForestTree.class)
                .addAnnotatedClass(RandomForestGrove.class).configure().buildSessionFactory();
    }

    public static Session getSession() throws HibernateException, ClassNotFoundException, SQLException {

        USERNAME = Constants.DATABASE_USERNAME;
        PASSWORD = Constants.CECCR_DATABASE_PASSWORD;
        RAW_URL = Constants.DATABASE_URL;
        DATABASENAME = Constants.CECCR_DATABASE_NAME;
        String URL = RAW_URL.endsWith(DATABASENAME) ? RAW_URL : RAW_URL + DATABASENAME;
        try {
            //IMPORTANT: If you get a "too many connections" error
            //use this debug output to help trace where the wasteful connections are getting made!
            //count++;
            //logger.debug("Making connection number: " + count);
            Class.forName(Constants.DATABASE_DRIVER);
            java.sql.Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Session s = sessionFactory.openSession(con);
            return s;
        } catch (Exception ex) {
            logger.error(ex);
        }

        return null;
    }
}
