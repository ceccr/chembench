package edu.unc.ceccr.action;

import java.util.List;
import java.util.Iterator;
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

import org.hibernate.criterion.Expression;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.PredictionJob;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class DeleteAction extends Action {

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
					((Queue.QueueTask)session.getAttribute("workflow")).cleanFiles();
					Queue queue = Queue.getInstance();
					queue.deleteTask((Queue.QueueTask)session.getAttribute("workflow"));
				}
				
				if (session.getAttribute("selectedPredictor") != null) 
				 {
						Predictor predictor = (Predictor) session.getAttribute("selectedPredictor");
						List<PredictionJob> predList=null;
						predList=hasPrediction(predictor.getPredictorId());
						int size=predList.size();
					if(size>0)
					{
						String hasPrediction="<Font size='4' color='red'>Warning</font><br/><br/><br/><br/>"+
							"The predictor <font color=blue>"+predictor.getName()+" </font>can not be deleted, because it has<font color=red> "+size
						    +"</font>  predictions in database.<br/>";
						String iterms="";
						Iterator it=predList.iterator();
						while(it.hasNext())
						{
							iterms=iterms+"<br/><font color=lightblue>"+((PredictionJob)(it.next())).getJobName()+"</font><br/>";
						}
						String warning="If you do want delete this predictor, you may delete these predictions first."
							+"<br/><br/><br/><a href='modelbuilders.do'><u>Back to Model Building</u></a>";
	
						request.removeAttribute("hasPredictions");
						request.setAttribute("hasPredictions", hasPrediction+iterms+warning);
						forward = actionMapping.findForward("failure");
					}
					else{
						Utility.writeToDebug("Removing predictor " + predictor.getName());
						deletePredictor(predictor);
						File file=new File(Constants.CECCR_USER_BASE_PATH+predictor.getUserName()+"/"+predictor.getName());
						FileAndDirOperations.deleteDir(file);
					}
				}
			} catch (Exception e) {
				forward = actionMapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return forward;
	}
	
	protected static void deletePredictor(Predictor predictor) throws ClassNotFoundException, SQLException {

		Session session = HibernateUtil.getSession();
		
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			session.delete(predictor);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
	}

	
 
	@SuppressWarnings("unchecked")
	protected List<PredictionJob> hasPrediction(Long id)throws ClassNotFoundException, SQLException	
	{
		Session session = HibernateUtil.getSession();
		
		List<PredictionJob> predictionJob=null;
		
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			predictionJob=session.createCriteria(PredictionJob.class).add(Expression.eq("predictorId",id)).list();
			tx.commit();
			} catch (RuntimeException e) 
			{
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
				} finally {
					session.close();
					}
		return predictionJob;
	}
}
