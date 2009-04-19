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
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

//This class handles all requests relating to single molecule prediction
//This includes: 
//- loading the single molecule prediction page
//- interpreting requests from the page
//- displaying results from single molecule predictions.


public class SelectPredictorActionForward extends Action {

	ActionForward forward;

	ActionMapping mapping;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");
	
		HttpSession session = request.getSession(false);
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}else{
			try {
				User user = (User) session.getAttribute("user");
				Long predictorId = Long.parseLong(request.getParameter("id"));
				Predictor predictor = PopulateDataObjects.getPredictorById(predictorId);
				
				session.removeAttribute("datasetNames");
				session.removeAttribute("predictorNames");
				session.removeAttribute("predictionNames");
				session.removeAttribute("taskNames");
				session.removeAttribute("selectedPredictor");
				
				session.setAttribute("datasetNames", PopulateDataObjects.populateDatasetNames(user.getUserName(), true));
				session.setAttribute("predictorNames", PopulateDataObjects.populatePredictorNames(user.getUserName(), true));
				session.setAttribute("predictionNames", PopulateDataObjects.populatePredictionNames(user.getUserName(), true));
				session.setAttribute("taskNames", PopulateDataObjects.populateTaskNames(user.getUserName(), false));
				session.setAttribute("selectedPredictor", predictor);
				
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return forward;
	}
	
	

	@SuppressWarnings("unchecked")
	protected static List<ModelInterface> getModels(Predictor pred, String flowType,String knnType)throws ClassNotFoundException, SQLException {

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
					.addOrder(Order.desc(orderBy)).list();

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


}
