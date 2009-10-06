	package edu.unc.ceccr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;

public class PredictionForward extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//use the same session for all data requests
		Session hibernateSession = HibernateUtil.getSession();
		
		ActionForward forward = new ActionForward(); // return value

		// get session state
		HttpSession session = request.getSession(false); // edited to generalize
		if (session == null ){
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}
		else
		{
			User user = (User) session.getAttribute("user");
			session.removeAttribute("predictors");
			session.setAttribute("predictors", PopulateDataObjects.populatePredictors(user.getUserName(), true, true, hibernateSession));
			session.removeAttribute("predictorDatabases");
			session.setAttribute("predictorDatabases", PopulateDataObjects.populateDatasetsForPrediction(user.getUserName(), true, hibernateSession));
			session.removeAttribute("predictions");
			session.setAttribute("predictions", PopulateDataObjects.populatePredictions(user.getUserName(), true, hibernateSession));
			
			forward = mapping.findForward("success");
		}
		hibernateSession.close();
		
		return forward;
	}
}
