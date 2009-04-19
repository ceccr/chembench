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
import org.apache.struts.validator.LazyValidatorForm;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.PredictionJob;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;

public class ViewModelsAction extends Action {

	ActionForward forward;

	ActionMapping mapping;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");

		// get session state
		HttpSession session = request.getSession(false);
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}else{
			try {
			

			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return forward;

	}
@SuppressWarnings("unchecked")
	protected static List getPredictor(Long selectedPredictorId)
			throws ClassNotFoundException, SQLException {
			
		Utility.writeToDebug("ViewModelsAction: getPredictor");
	
		List<ModelInterface> models = null;;
		Predictor predictor = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(PredictionJob.class)
					.add(Expression.eq("predictorId", selectedPredictorId))
					.uniqueResult();

			models = session.createCriteria(Model.class).add(
					Expression.eq("predictor", predictor)).addOrder(
					Order.asc("RSquared")).list();

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
@SuppressWarnings("unchecked")
	protected static List getModels(Predictor pred)
			throws ClassNotFoundException, SQLException {

		List<Model> models = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			models = session.createCriteria(Model.class).add(
					Expression.eq("predictor", pred)).addOrder(
					Order.asc("RSquared")).list();

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
