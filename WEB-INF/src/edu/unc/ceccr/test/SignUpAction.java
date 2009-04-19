package edu.unc.ceccr.test;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.formbean.LoginFormBean;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;

public class SignUpAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); // return value

		LoginFormBean loginBean = (LoginFormBean) form;

		if (checkUsernameTaken(loginBean.getLoginName())) {
			createDB(loginBean.getLoginName());
			forward = mapping.findForward("success");
		} else {
			forward = mapping.findForward("failure");
		}
		return forward;
	}

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

	public static void createDB(String username) throws HibernateException,
			ClassNotFoundException, SQLException {
		User user = new User();
		user.setUserName(username);

		Session session = HibernateUtil.getSession();
		Transaction tran = session.beginTransaction();
		tran.begin();
		session.save(user);

		java.sql.Connection conn = DriverManager.getConnection(
				"jdbc:mysql://ceccr.cs.unc.edu/mysql", "xuh", "xuh");
		conn.setAutoCommit(false);
		java.sql.Statement stmt = conn.createStatement();
		try {
			stmt.execute("create database " + user.getUserName());
			stmt
					.execute("CREATE TABLE  "
							+ user.getUserName()
							+ ".`model` (`id` int(10) unsigned NOT NULL auto_increment,  " +
									"`nnn` float default '0',  " +
									"`q_squared` float default '0',  " +
									"`r_squared` float default '0',  " +
									"`n` float default '0',  " +
									"`b01` float default '0',  " +
									"`b02` float default '0',  " +
									"`b11` float default '0',  " +
									"`b12` float default '0',  " +
									"`r` float default '0',  " +
									"`sl_squared` float default '0',  " +
									"`F1` float default '0',  " +
									"`s2_squared` float default '0',  " +
									"`F2` float default '0',  " +
									"`k1` float default '0',  " +
									"`k2` float default '0',  " +
									"`r01_squared` float default '0',  " +
									"`r02_squared` float default '0',  " +
									"`s01_squared` float default '0',  " +
									"`s02_squared` float default '0',  " +
									"`F01` float default '0',  " +
									"`F02` float default '0',  " +
									"`r451_squared` float default '0',  " +
									"`r452_squared` float default '0',  " +
									"`st45` float default '0',  " +
									"`trainingAcc` float default '0',  " +
									"`normTestAcc` float default '0',  " +
									"`testAcc` float default '0',  " +
									"`normTrainingAcc` float default '0',  " +
									"`knnType` varchar(45) NOT NULL default '',  " +
									"`file` varchar(45) default '',  " +
									"`predictor_id` int(10) unsigned default '0',  " +
									"PRIMARY KEY  (`id`),  KEY `FK_model_1` (`predictor_id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			stmt
					.execute("CREATE TABLE "
							+ user.getUserName()
							+ ".`predictor` (  `predictor_id` int(10) unsigned NOT NULL auto_increment,  `name` varchar(45) NOT NULL default '',  `ACTFileName` varchar(45) default NULL,  `SDFileName` varchar(45) default NULL,  PRIMARY KEY  (`predictor_id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			tran.commit();
			conn.commit();
		} catch (Exception e) {
			tran.rollback();
			conn.rollback();
		}
		stmt.close();
		conn.close();
		session.close();
	}
}
