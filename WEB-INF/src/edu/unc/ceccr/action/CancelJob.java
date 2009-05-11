package edu.unc.ceccr.action;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class CancelJob extends Action {

	ActionForward forward;

	ActionMapping mapping;

	Queue queue = Queue.getInstance();
	
	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");
		
		HttpSession session = request.getSession(false); // edited to
		
		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {

			try {

				Long selectedTaskId = Long.parseLong(request.getParameter("id"));
				QueueTask task = getTask(selectedTaskId);
				Utility.writeToDebug("Canceling job with id: " + selectedTaskId + " named " + task.jobName);
				
				//stop the running programs
				//???
				if(queue.runningTask != null && task.jobName.equals( queue.runningTask.jobName) && task.getUserName().equals( queue.runningTask.getUserName())){
					Utility.writeToDebug("Job " + task.jobName + " is currently executing.");
					//check if kNN is running. Kill it if it is.
					/*if(){
						
					}
					else{
						
					}*/
				}
				else{
					Utility.writeToDebug("Job " + task.jobName + " is not executing.");
				}

				//remove associated files
				String BASE=Constants.CECCR_USER_BASE_PATH;
				File file=new File(BASE+task.getUserName()+"/"+task.jobName);
				FileAndDirOperations.deleteDir(file);

				file=new File(BASE+task.getUserName()+"/PREDICTIONS/"+task.jobName);
				FileAndDirOperations.deleteDir(file);

				file=new File(BASE+task.getUserName()+"/PREDICTORS/"+task.jobName);
				FileAndDirOperations.deleteDir(file);
				
				//remove the task. Gotta do this last.
				//queue.deleteTask(task);
				List<QueueTask> ls = queue.getQueuedTasks();
				
				for (Iterator<QueueTask> i = ls.iterator( ); i.hasNext( ); ) {
					QueueTask t = i.next( );
					if(t.id == task.id)
					{
						t.setState(QueueTask.State.deleted);
						t.saveTask();
					}
				}
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return forward;
	}

	protected static void changeStatus(Object object)throws ClassNotFoundException, SQLException
	{
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx=session.beginTransaction();
			session.saveOrUpdate(object);

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
	}
	protected static QueueTask getTask(Long selectedTaskId)	throws ClassNotFoundException, SQLException 
	{
		QueueTask task = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			task = (QueueTask) session.createCriteria(QueueTask.class).add(	Expression.eq("id", selectedTaskId)).uniqueResult();

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		Utility.writeToDebug("Task returned from database: " + task.getJobName()
				+ " " + task.getComponent());
		return task;
	}

}
