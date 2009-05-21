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

import edu.unc.ceccr.formbean.QsarFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;

public class ViewPreviouslyGeneratedModels extends Action {

	ActionForward forward;

	ActionMapping mapping;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");

		QsarFormBean formBean = (QsarFormBean) form;

		HttpSession session = request.getSession(false);
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}else{
			try {
				User user = (User) session.getAttribute("user");

				Predictor predictor = getPredictor(formBean.getSelectedPredictorName(), user.getUserName());
				predictor.setActFileName(Utility.wrapFileName(predictor.getActFileName()));
				predictor.setSdFileName(Utility.wrapFileName(predictor.getSdFileName()));
				
				String knnType=predictor.getModelMethod().toString();
				
				List<ModelInterface> models = getModels(predictor, "MAINKNN",knnType);
				List<ModelInterface> yRandomModels = getModels(predictor, "RANDOMKNN",knnType);
				
				session.removeAttribute("selectedPredictor");
				session.setAttribute("selectedPredictor", predictor);
				session.setAttribute("KnnType", predictor.getModelMethod());
				session.setAttribute("allkNNValues", models);
				session.removeAttribute("randomKNNValues");
				session.setAttribute("randomKNNValues", yRandomModels);
			
				session.removeAttribute("allExternalValues");
				session.setAttribute("allExternalValues", predictor.getExternalValidationResults() );
				

			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return forward;
	}
	
	protected static Predictor getPredictor(String selectedPredictorName, String user)	throws ClassNotFoundException, SQLException {

		Predictor predictor = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(Predictor.class)	.add(Expression.eq("name", selectedPredictorName))
					.add(Expression.eq("userName", user)).uniqueResult();
			
			predictor.getExternalValidationResults().size();
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
