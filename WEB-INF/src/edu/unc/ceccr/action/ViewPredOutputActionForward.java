package edu.unc.ceccr.action;

import java.sql.SQLException;
import java.util.*;

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

import edu.unc.ceccr.formbean.ViewOutputFormBean;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.PredictionJob;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.utilities.Utility;

public class ViewPredOutputActionForward extends Action {

	ActionMapping mapping;

	private ActionForward forward;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");
		final int PAGESIZE=20;
		final int LISTSIZE=20;
		
		HttpSession session = request.getSession(false); 
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}else{

            int numberOfPage=0,start=0,end=0,total=0,startRange=0,endRange=0;
           
			try {
				
				String sortBy=request.getParameter("sortBy");
				String pageNum=request.getParameter("page");
				String direction=request.getParameter("order");
				String going=request.getParameter("going");
				String goback=request.getParameter("BACK");
				String goforward=request.getParameter("FORWARD");
				String sRange=request.getParameter("startRange");
				String eRange=request.getParameter("endRange");
				
				int stepForward=0, stepBack=0;
				if(goback!=null){stepBack=Integer.parseInt(goback);}
				if(goforward!=null){stepForward=Integer.parseInt(goforward);}
				
				
				if(direction==null){direction="DESC";}
				
				session.removeAttribute("direction");
				session.setAttribute("direction",direction);
				
				int pageNumber=0;
				if(pageNum!=null)
				{
					pageNumber=Integer.parseInt(pageNum);
				}
				PredictionJob predictionJob=null;
				List<PredictionValue> predictionValues=null;
				Long predictionId;
				if(sortBy==null&&pageNum==null)
				{
					ViewOutputFormBean formBean = (ViewOutputFormBean) form;
					predictionId = formBean.getPredictionJobId();
					predictionJob = getPrediction(predictionId);
					//Sort by number of models by default
					predictionValues=getPredictionValues("numModel",direction,predictionId,pageNumber,PAGESIZE);
					
					total=getTotal(predictionId);
					numberOfPage=getNumberOfPage(total,PAGESIZE);
					start=1;
					if(total<=PAGESIZE)
						{end=total;}else{end=PAGESIZE;}
				}
				else
				{
					
					predictionId=Long.parseLong(request.getParameter("id"));
					predictionJob = getPrediction(predictionId);
					predictionValues=this.getPredictionValues(sortBy,direction, predictionId,pageNumber,PAGESIZE);
					
					total=getTotal(predictionId);
					numberOfPage=getNumberOfPage(total,PAGESIZE);
					if(pageNumber==0)
					{
						start=1;
						if(total<=PAGESIZE)
							{end=total;}else{end=PAGESIZE;}
					}else{
						start=pageNumber*PAGESIZE+1;
						if(pageNumber==numberOfPage)
						{end=pageNumber*PAGESIZE+total%PAGESIZE;}
						else{end=(pageNumber+1)*PAGESIZE; }
					}
				}
				
				predictionJob.setPredictorName(getPredictor(predictionJob.getPredictorId()));
				predictionJob.setDatabase(Utility.wrapFileName(predictionJob.getDatabase()));
				
				int numOfTurn=numberOfPage/LISTSIZE;
					if(going!=null)
					{
						if(going.equalsIgnoreCase("forth"))
						{
							
							startRange=stepForward*LISTSIZE+1;
							if(stepForward==numOfTurn){
								endRange=startRange+numberOfPage%LISTSIZE;
								stepForward=0;
								
							}else{
								endRange=startRange+LISTSIZE-1;
							stepForward=stepForward+1;}
							stepBack=stepBack+1;
						}
						else{
							if(going.equalsIgnoreCase("back"))
							{
							startRange=(stepBack-1)*LISTSIZE+1;
							endRange=startRange+LISTSIZE-1;
							if(stepBack>stepForward&&stepForward==0)
							{
								stepForward=stepBack;
								stepBack=stepBack-1;
							}else{
							stepBack=stepBack-1;
							stepForward=stepForward-1;
							}
						     
						}
						else{
							if(sRange!=null){startRange=Integer.parseInt(sRange);}
							if(eRange!=null){endRange=Integer.parseInt(eRange);}
						}
					}
					session.setAttribute("startRange", startRange);
					session.setAttribute("endRange",endRange);
					session.setAttribute("forth",stepForward);
					session.setAttribute("back",stepBack);
				}else{
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
				}
				
				
				
				session.removeAttribute("page");
				session.setAttribute("page",pageNumber);
				
				session.removeAttribute("total");
				session.setAttribute("total",total);
				session.removeAttribute("start");
				session.setAttribute("start",start);
				session.removeAttribute("end");
				session.setAttribute("end",end);
				
				session.removeAttribute("sortItem");
				session.setAttribute("sortItem",sortBy);
				
				session.removeAttribute("predictionValues");
				session.setAttribute("predictionValues", predictionValues);
				
				session.removeAttribute("predictionId");
				session.setAttribute("predictionId",predictionId);
								
				session.removeAttribute("numberOfPage");
				session.setAttribute("numberOfPage",numberOfPage);
				
				session.removeAttribute("predictionJob");
				session.setAttribute("predictionJob", predictionJob);
				
			}catch (Exception e) 
			{
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}

		}
		return (forward);

	}
	
	
	
	
@SuppressWarnings("unchecked")
protected int  getTotal(Long predictionId ) throws ClassNotFoundException, SQLException {

	PredictionJob predictionJob = null;
	List<PredictionValue> predVal=null;
	Session session = HibernateUtil.getSession();
	Transaction tx = null;
	try {
		tx = session.beginTransaction();
		
		predictionJob = (PredictionJob) session
				.createCriteria(PredictionJob.class).add(Expression.eq("predictionJobId",predictionId))
						.uniqueResult();
		
		predVal= session.createFilter( predictionJob.getPredictedValues(), "" ).list();
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
	/**
	 * Used to sort predicted values by any column name
	 * @param sortBy
	 * @param selectedPredictionId
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private List<PredictionValue> getPredictionValues(String sortBy,String direction, Long selectedPredictionId, int pageNumber, int pageSize)
			throws ClassNotFoundException, SQLException {

		List predictionValues = null;

		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			PredictionJob predictionJob = (PredictionJob) session.createCriteria(
					PredictionJob.class).add(Expression.eq("predictionJobId", selectedPredictionId))
					.uniqueResult();
            String columnName = "";
			if(sortBy.equalsIgnoreCase("compoundId"))
			{
				columnName="compoundName";
			}else if(sortBy.equalsIgnoreCase("value"))
			{
				columnName="predictedValue";
			}else if(sortBy.equalsIgnoreCase("numModel"))
			{
				columnName="numModelsUsed";
			}
			else{columnName="NOTSET";}
			if(columnName!="NOTSET")
			{
			predictionValues = session.createFilter( predictionJob.getPredictedValues(), "order by this." + columnName +" "+direction )
			                        .setFirstResult(pageNumber*pageSize).setMaxResults(pageSize).list();
			}
			else{
				predictionValues = session.createFilter( predictionJob.getPredictedValues(), "").setFirstResult(pageNumber*pageSize).setMaxResults(pageSize).list();
			}
			tx.commit();

		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

		return predictionValues;
	}	
	
	

	protected static PredictionJob getPrediction(Long selectedPredictionId)
			throws ClassNotFoundException, SQLException {

		PredictionJob predictionJob = null;
				
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			predictionJob = (PredictionJob) session
					.createCriteria(PredictionJob.class).add(Expression.eq("predictionJobId",selectedPredictionId))
							.uniqueResult();
			
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
	
	
	protected static int getNumberOfPage(int total, int pageSize)
	{
		int number=total/pageSize;
		if(number%pageSize>0){return number++;}
		
		return number;
	}


	protected static String getPredictor(Long predictorIdUsed)
			throws ClassNotFoundException, SQLException {

		Predictor predictor = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(
					Predictor.class).add(
					Expression.eq("predictorId", predictorIdUsed))
					.uniqueResult();

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
	
	
}
