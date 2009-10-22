package edu.unc.ceccr.action.RegisterActions;

import java.io.File;
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

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class QueuedJobPermissionAction extends Action 
{
	Queue holder = Queue.getInstance();

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {

		ActionForward forward = new ActionForward();
		
		HttpSession session = request.getSession(false);
		if (session == null) 
		{
			forward = mapping.findForward("login");
		}
		else 
			if (session.getAttribute("user") == null) 
			{
				forward = mapping.findForward("login");
				} 
			else 
			{
				try{
					

					String decision=request.getParameter("decision");
					
					if(decision.equalsIgnoreCase("AGREE")){
						//job was permitted by admin.
						Long jobId=Long.parseLong(request.getParameter("jobId"));
					
						Queue q = Queue.getInstance();
						q.getQueuedTasks();
						Utility.writeToDebug("Finding the job with ID " + jobId + " in queue...");
						for(QueueTask t: q.queue){
							if(t.id.equals(jobId)){
								t.setState(QueueTask.State.queued);
								Utility.writeToDebug("Granting permission to task ID " + jobId);
								if(t.task != null){
									Utility.writeToDebug("Task has a workflowTask");
								}
								else{
									Utility.writeToDebug("Task lacks a workflowTask");
								}
							}
						}
					}
					else{
						//Oh no! Denieeeeeed. 
						//toss the job out of the queue.
						Long jobId=Long.parseLong(request.getParameter("jobId"));
						
						Queue q = Queue.getInstance();
						q.getQueuedTasks();
						Utility.writeToDebug("Finding the job with ID " + jobId + " in queue...");

						for(QueueTask t: q.queue){
							if(t.id.equals(jobId)){
								Utility.writeToDebug("Deleting job with ID " + jobId);
								
								//remove associated files
								String BASE=Constants.CECCR_USER_BASE_PATH;
								File file=new File(BASE+t.getUserName()+"/"+t.jobName);
								FileAndDirOperations.deleteDir(file);

								file=new File(BASE+t.getUserName()+"/PREDICTIONS/"+t.jobName);
								FileAndDirOperations.deleteDir(file);

								file=new File(BASE+t.getUserName()+"/PREDICTORS/"+t.jobName);
								FileAndDirOperations.deleteDir(file);

								//remove the task. Gotta do this last.
								q.deleteTask(t);
								
							}
						}
					}
					
				}catch (Exception e)
				{
					forward = mapping.findForward("failure");
				    Utility.writeToDebug(e);
				    }
				}
		
		
		
		forward = mapping.findForward("success");
		return forward;
	}
	
	
	
	@SuppressWarnings("unchecked")
	protected QueueTask loadTaskRecord(Long id) throws HibernateException, ClassNotFoundException, SQLException
	{
		QueueTask task=new QueueTask();
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			task=(QueueTask)s.createCriteria(QueueTask.class).add(Expression.eq("id",id)).uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			s.close();
		}
		
		return task;
	}

	protected void saveTaskRecord(QueueTask t) throws HibernateException,	ClassNotFoundException, SQLException 
	{
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.update(t);
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
