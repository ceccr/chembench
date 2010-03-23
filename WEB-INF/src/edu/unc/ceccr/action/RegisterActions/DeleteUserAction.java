package edu.unc.ceccr.action.RegisterActions;

import java.io.File;
import java.util.List;
import java.util.Iterator;
import java.sql.SQLException;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import edu.unc.ceccr.utilities.Utility;

public class DeleteUserAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response)
	throws Exception 
	{
		ActionForward forward = new ActionForward(); 
		
		HttpSession session = request.getSession(false);
		
		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {
			String userName=request.getParameter("userName");
			
			try{
				deleteUser(userName);
			}
			catch(Exception e){
				Utility.writeToDebug(e);
				forward = mapping.findForward("failure");	
			}
			forward=mapping.findForward("success");
		}
		return forward;
	}
	
	protected void deleteUser(String userName)throws ClassNotFoundException,SQLException
	{
		Utility.writeToDebug("Deleting user: " + userName);
		List predictionJobs = getUserDatabase(userName, Prediction.class);
		List predictors = getUserDatabase(userName,Predictor.class);
		List datasets = getUserDatabase(userName,DataSet.class);
		List tasks = getUserDatabase(userName,Job.class);;
		
		deleteDatabaseData(predictionJobs);
		deleteDatabaseData(predictors);
		deleteDatabaseData(datasets);
		deleteDatabaseData(tasks);
		
	    try {
	    	deleteUserInfo(userName);
	    	deleteDirectory(userName);
	    }
	    catch(Exception ex){
	    	Utility.writeToDebug(ex);
	    }
	   
	}
	
	protected void deleteDatabaseData(List list)throws ClassNotFoundException,SQLException
	{
		if(list.size()!=0)
		{
			Session session = HibernateUtil.getSession();	
			Iterator it=list.iterator();	
			while(it.hasNext())
			{
				Transaction tx = null;
				try {
					tx = session.beginTransaction();
					session.delete(it.next());
					tx.commit();
				} 
				catch (RuntimeException e) {
						if (tx != null)
							tx.rollback();
						Utility.writeToDebug(e);
				} 
			}
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List getUserDatabase(String userName, Class className)throws ClassNotFoundException,SQLException
	{
		List datasets=null;
		Session s = HibernateUtil.getSession();// query
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			datasets= s.createCriteria(className).add(Expression.eq("userName", userName)).list();
			
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			s.close();
		}
		
		return datasets;
	}
	
	protected void deleteDirectory(String userName)
	{
		File dir=new File(Constants.CECCR_USER_BASE_PATH+userName);
		FileAndDirOperations.deleteDir(dir);
	}
	
	protected void deleteUserInfo(String userName)throws ClassNotFoundException,SQLException
	{
		List user= getUserDatabase(userName,User.class);
		deleteDatabaseData(user);
	}
}