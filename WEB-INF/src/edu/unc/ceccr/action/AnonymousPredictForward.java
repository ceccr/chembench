package edu.unc.ceccr.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
import org.hibernate.criterion.Order;

import edu.unc.ceccr.formbean.LoginFormBean;
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

public class AnonymousPredictForward extends Action {

	ActionForward forward;

	ActionMapping mapping;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");
	
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("user") == null) {
			//user is not logged in
			//Create a temporary user (anonymous), and "log them in" as that.
			session = request.getSession(true);
			session.setMaxInactiveInterval(Constants.SESSION_EXPIRATION_TIME);
			
			User user = new User();
			user.setUserName("anon_" + session.getCreationTime());
			session.setAttribute("anonUser", user);
			Utility.writeToDebug("Anonymous user created: " + user.getUserName());
			try {
				session.setAttribute("predictors", PopulateDataObjects.populatePredictors(user.getUserName(), true, true));
			} catch (Exception ex) {
				Utility.writeToDebug(ex);
			}
			
		}
		//if the user is logged in, though, they can stay logged in and they don't need an anonymous name.

		return forward;
	}

}
