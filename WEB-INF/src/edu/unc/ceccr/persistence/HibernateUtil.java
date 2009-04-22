package edu.unc.ceccr.persistence;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.utilities.Utility;

public class HibernateUtil {
	
	private static final SessionFactory sessionFactory;
	private static String PASSWORD;
	private static String URL;
	private static String USERNAME;
	private static String DATABASENAME;
	
	static {
		sessionFactory = new AnnotationConfiguration()
		.addAnnotatedClass(QueueTask.class)
		.addAnnotatedClass(Model.class)
		.addAnnotatedClass(Predictor.class)
		.addAnnotatedClass(User.class)
		.addAnnotatedClass(PredictionJob.class)
		.addAnnotatedClass(PredictionValue.class)
		.addAnnotatedClass(ExternalValidation.class)
		.addAnnotatedClass(DataSet.class)
		.addAnnotatedClass(AdminSettings.class)
		.addAnnotatedClass(SoftwareExpiration.class)
		.configure().buildSessionFactory();
	}

	public static Session getSession() throws HibernateException,
			ClassNotFoundException, SQLException {

		Utility.writeToDebug("in getSession 1");
		
		
		USERNAME=Constants.DATABASE_USERNAME;
		PASSWORD = Constants.CECCR_DATABASE_PASSWORD;
		URL=Constants.DATABASE_URL;
		DATABASENAME=Constants.CECCR_DATABASE_NAME;

		Utility.writeToDebug("in getSession 2");
		
		try{
			Class.forName(Constants.DATABASE_DRIVER);
			java.sql.Connection con = DriverManager.getConnection(URL + DATABASENAME, USERNAME, PASSWORD);
			Session s = sessionFactory.openSession(con);
			return s;
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		Utility.writeToDebug("in getSession 3");
		
		return null;
	}
}
