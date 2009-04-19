package edu.unc.ceccr.test;

import java.sql.SQLException;
import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;

public class NewDBTest {

	public static boolean checkUsernameTaken(String username) throws HibernateException,
			ClassNotFoundException, SQLException {
		User user = new User();
		user.setUserName(username);

		Session session = HibernateUtil.getSession();

		if (session.createCriteria(User.class).add(
				Expression.eq("userName", username)).list().isEmpty())
			return true;
		else
			return false;

	}
	
	public static boolean queryUsers(String username){
		boolean querySucess = false;
		try{
			Session session = HibernateUtil.getSession();
			if (!(session.createCriteria(User.class).add(
					Expression.eq("userName", username)).list().isEmpty())){
				querySucess = true;
			}

		}catch(Exception e){
			Utility.writeToDebug(e);
		}
		return querySucess;
	}


	public static void queryPredictors(String file, String username){
		ArrayList <Predictor> predictors = null;
		try{
			Session sess = HibernateUtil.getSession();
			/*
			predictors = (ArrayList) sess.createCriteria(Predictor.class)
			.add(Expression.eq("SDFileName", file))
			.add(Expression.eq("ACTFileName", username))
			.list();
			*/
			predictors = (ArrayList)sess.createCriteria(Predictor.class)
			.add(Expression.eq("username", "julia")).list();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(predictors.isEmpty())
			System.out.println("no predictors found");
		for(Predictor predictor: predictors){
			System.out.println(predictor.toString());
		}

	}
}
