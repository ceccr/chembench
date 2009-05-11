package edu.unc.ceccr.action;

import java.io.File;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.PredictionJob;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;

public class DeletePredOutputAction extends Action {

	ActionMapping mapping;

	private ActionForward forward;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");

		HttpSession session = request.getSession(false);
		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {

			User user = (User) session.getAttribute("user");
			PredictionJob predictionJob = (PredictionJob) session
					.getAttribute("predictionJob");

			try {
				deletePrediction(predictionJob);
				
				if (session.getAttribute("predictionTask") instanceof Queue.QueueTask)
				{
					Queue queue = Queue.getInstance();
					queue.deleteTask((Queue.QueueTask)session.getAttribute("predictionTask"));
				}
				
				File file=new File(Constants.CECCR_USER_BASE_PATH +user.getUserName()+"/predictor/"+predictionJob.getJobName());
				FileAndDirOperations.deleteDir(file);
				
				session.removeAttribute("predictionJob");
				System.out.println("Just Deleted Predictor");
				/*
				 * String filePath = ((ViewPredOutputActionTask) session
				 * .getAttribute("workflow")).getFilepath();
				 * DeletePredOutputActionTask executeAntWorkflow = new
				 * DeletePredOutputActionTask( user.getUsername(), filePath);
				 * executeAntWorkflow.setUp(); executeAntWorkflow.execute();
				 * executeAntWorkflow.cleanUp();
				 * 
				 */
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
				Utility.writeToDebug(e);
			}
		}
		return forward;

	}

	protected static void deletePrediction(PredictionJob predictionJob)
			throws ClassNotFoundException, SQLException {

		Session session = HibernateUtil.getSession();

		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(predictionJob);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
	}

}
