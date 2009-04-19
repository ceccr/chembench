package edu.unc.ceccr.action;


import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.utilities.Utility;


public class DeleteRecordAction extends Action {

	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward();

		HttpSession session = request.getSession(false); 
		if (session == null ){
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}
		else{
			String taskName=request.getParameter("jobName");
			try{
				deleteTask(taskName);
			}catch(SQLException e)
			{Utility.writeToDebug(e);
			forward = mapping.findForward("failure");
			}catch(HibernateException e)
			{
				Utility.writeToDebug(e);
				forward = mapping.findForward("failure");
			}
			
			forward = mapping.findForward("success");
			}
		return forward;
	}
	
	public void deleteTask(String taskName)throws HibernateException,ClassNotFoundException, SQLException
	{
		Queue.QueueTask task=null;
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			task=(QueueTask)s.createCriteria(QueueTask.class).add(Expression.eq("jobName", taskName)).uniqueResult();
			s.delete(task);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			s.close();
		}
	}
	
}