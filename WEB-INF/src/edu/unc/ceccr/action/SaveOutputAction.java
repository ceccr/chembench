package edu.unc.ceccr.action;

import java.sql.SQLException;
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

import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.utilities.Utility;

public class SaveOutputAction extends Action {
	public ActionForward execute(ActionMapping actionMapping, ActionForm arg1,HttpServletRequest request, HttpServletResponse arg3)
	throws Exception
{
ActionForward forward = actionMapping.findForward("success");
	
	HttpSession session = request.getSession(false);
	if (session == null) {
		forward = actionMapping.findForward("login");
	} else if (session.getAttribute("user") == null) {
		forward = actionMapping.findForward("login");
	} else {
		
		try
		{
			if (session.getAttribute("workflow") instanceof Queue.QueueTask)
			{
				//if it was a predictor, set predictor's type to "Private" so it shows up on
				//the Predictors page
				Queue.QueueTask task = ((Queue.QueueTask)session.getAttribute("workflow"));
				Predictor predictor = ViewCompletedJob.getPredictor(task.getUserName(), task.getJobName());
				predictor.setPredictorType("Private");
				
				Session s = HibernateUtil.getSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					s.saveOrUpdate(predictor);
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null)
						tx.rollback();
					Utility.writeToDebug(e);
				} finally {
					s.close();
				}
				
				//remove it from the Queue / Jobs page.
				task.cleanFiles();
				Queue queue = Queue.getInstance();
				queue.deleteTask((Queue.QueueTask)session.getAttribute("workflow"));
			}
			else if (session.getAttribute("predictionTask") instanceof Queue.QueueTask)
			{
				((Queue.QueueTask)session.getAttribute("predictionTask")).cleanFiles();
				Queue queue = Queue.getInstance();
				queue.deleteTask((Queue.QueueTask)session.getAttribute("predictionTask"));
			}

		} catch (Exception e) {
			forward = actionMapping.findForward("failure");
			Utility.writeToDebug(e);
		}
	}
	return forward;
}

}
