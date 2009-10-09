package edu.unc.ceccr.action;

import java.sql.SQLException;
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
import org.hibernate.criterion.Order;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class ViewCompletedJob extends Action {

	ActionForward forward;

	ActionMapping mapping;

	Queue queue = Queue.getInstance();

	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");

		final int PAGESIZE=20;
		final int LISTSIZE=20;
		
		HttpSession session = request.getSession(false); // edited to
		
		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {

			try {

				
				int numberOfPage=0,start=0,end=0,total=0;
				Long selectedTaskId = Long.parseLong(request.getParameter("id"));
				QueueTask task = getTask(selectedTaskId);

				if (task.getJobType() == jobTypes.modeling) {

					session.removeAttribute("workflow");
					session.setAttribute("workflow",task);
					
					Predictor predictor = getPredictor(task.getUserName(), task.getJobName());
					predictor.setActFileName(Utility.wrapFileName(predictor.getActFileName()));
					predictor.setSdFileName(Utility.wrapFileName(predictor.getSdFileName()));
					
					String knnType=predictor.getModelMethod().toString();
					
					session.removeAttribute("selectedPredictor");
					session.setAttribute("selectedPredictor", predictor);
					session.setAttribute("KnnType", knnType);
					
					List<ModelInterface> models = getModels(predictor, "MAINKNN",knnType);
					List<ModelInterface> yRandomModels = getModels(predictor, "RANDOMKNN",knnType);
					session.setAttribute("allkNNValues", models);
					
					session.removeAttribute("randomKNNValues");
					session.setAttribute("randomKNNValues", yRandomModels);

					Session hibernateSession = HibernateUtil.getSession();
					List<ExternalValidation> externalValValues = PopulateDataObjects.getExternalValidationValues(predictor, hibernateSession);
					hibernateSession.close();
					session.setAttribute("allExternalValues", externalValValues);
					
					predictor.setStatus("saved");
					changeStatus(predictor);
					
					forward = mapping.findForward("mod");
				} else if(task.getJobType() == jobTypes.prediction){
					
					int page=1;
					Prediction predictionJob = getPrediction(task.getUserName(), task.getJobName());
					predictionJob.setStatus("saved");
					changeStatus(predictionJob);
					
					predictionJob.getPredictedValues().size();
					total= getPredictionValSize(task.getUserName(), task.getJobName());
					numberOfPage=getNumberOfPage(total,PAGESIZE);
					List<PredictionValue> predValueList=null;
					predValueList=getPredictionVal(task.getUserName(),task.getJobName(),PAGESIZE);

					//What? You say the code is buggy?
					//*stick fingers in ears*
					//la la la la la la la
					//now comment things out
					//predictionJob.setPredictorName(getPredictorUsedInPrediction(predictionJob.getPredictorId()));
					predictionJob.setDatabase(Utility.wrapFileName(predictionJob.getDatabase()));
					
					start=1;
					if(total<=PAGESIZE)
						{end=total;}else{end=PAGESIZE;}
					
					int numOfTurn=numberOfPage/LISTSIZE;
					
					session.setAttribute("startRange", 1);
					if(numOfTurn>0){
					    session.setAttribute("endRange",LISTSIZE);
						session.setAttribute("forth",1);
						session.setAttribute("back",0);
						}
					else{
						session.setAttribute("endRange",numberOfPage);
						session.setAttribute("forth",0);
						session.setAttribute("back",0);
					}
					
					session.removeAttribute("page");
					session.setAttribute("page",page);
					session.removeAttribute("total");
					session.setAttribute("total",total);
					session.removeAttribute("start");
					session.setAttribute("start",start);
					session.removeAttribute("end");
					session.setAttribute("end",end);
					session.removeAttribute("numberOfPage");
					session.setAttribute("numberOfPage",numberOfPage);
					
					session.removeAttribute("predValueList");
					session.setAttribute("predValueList",predValueList);
					session.removeAttribute("predictionJob");
					session.setAttribute("predictionJob", predictionJob);
					session.removeAttribute("predictionId");
					session.setAttribute("predictionId", predictionJob.getPredictionId());
					session.removeAttribute("predictionTask");
					session.setAttribute("predictionTask", task);
				    
					
					forward = mapping.findForward("predictor");
				}
				else if(task.getJobType() == jobTypes.dataset){
					session.setAttribute("fileName", task.getJobName());
					queue.deleteTask(task);
					forward = mapping.findForward("dataset");
				}
				//queue.deleteTask(task);

			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return forward;

	}
	
	
	
@SuppressWarnings("unchecked")
protected List<PredictionValue>  getPredictionVal(String userName, String jobName,int pageSize)throws ClassNotFoundException, SQLException {

Prediction predictionJob = null;
List<PredictionValue> predVal=null;
Session session = HibernateUtil.getSession();
Transaction tx = null;
try {
	tx = session.beginTransaction();
	predictionJob = (Prediction) session.createCriteria(Prediction.class)
			.add(Expression.eq("userName", userName))
			.add(Expression.eq("jobName", jobName)).uniqueResult();

	predVal= session.createFilter( predictionJob.getPredictedValues(), "").setMaxResults(pageSize).list();
	tx.commit();
} catch (RuntimeException e) {
	if (tx != null)
		tx.rollback();
	Utility.writeToDebug(e);
} finally {
	session.close();
}

return predVal;
}

@SuppressWarnings("unchecked")
protected int  getPredictionValSize(String userName, String jobName)throws ClassNotFoundException, SQLException {

Prediction predictionJob = null;
List<PredictionValue> predVal=null;
Session session = HibernateUtil.getSession();
Transaction tx = null;
try {
	tx = session.beginTransaction();
	predictionJob = (Prediction) session.createCriteria(Prediction.class).add(Expression.eq("userName", userName))
			.add(Expression.eq("jobName", jobName)).uniqueResult();

	predVal= session.createFilter( predictionJob.getPredictedValues(), "").list();
	tx.commit();
} catch (RuntimeException e) {
	if (tx != null)
		tx.rollback();
	Utility.writeToDebug(e);
} finally {
	session.close();
}

return predVal.size();
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
		System.out.println("Task returned from database: " + task.getJobName()
				+ " " + task.getJobType());
		return task;
	}

	protected static Predictor getPredictor(String userName, String jobName)throws ClassNotFoundException, SQLException 
	{

		Predictor predictor = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(Predictor.class)	.add(Expression.eq("userName", userName))
					.add(Expression.eq("name", jobName))	.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

		return predictor;
	}

	@SuppressWarnings("unchecked")
	protected static List getModels(Predictor pred, String flowType, String knnType)	throws ClassNotFoundException, SQLException 
	{

		List<ModelInterface> models = null;
		String orderBy;
		if(knnType.equals(Constants.CONTINUOUS))
		{
			orderBy="RSquared";
		}else{
			orderBy="normalizedTestAcc";
		}
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			models = session.createCriteria(Model.class).add(Expression.eq("predictor", pred)).add(Expression.eq("flowType",flowType))
			                  .addOrder(	Order.desc(orderBy)).list();

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

		return models;
	}

	
	
	protected static Prediction getPrediction(String userName, String jobName)
			throws ClassNotFoundException, SQLException {

		Prediction predictionJob = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictionJob = (Prediction) session.createCriteria(Prediction.class).add(Expression.eq("userName", userName))
					.add(Expression.eq("jobName", jobName)).uniqueResult();

			predictionJob.getPredictedValues().size();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

		return predictionJob;
	}
	
	protected static String getPredictorUsedInPrediction(Long predictorIdUsed)
			throws ClassNotFoundException, SQLException {

		Predictor predictor = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(Predictor.class)	.add(Expression.eq("predictorId", predictorIdUsed)).uniqueResult();

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

		return predictor.getName();
	}
	protected static int getNumberOfPage(int total, int pageSize)
	{
		int number=total/pageSize;
		if(number%pageSize>0){return number++;}
		
		return number;
	}

}
