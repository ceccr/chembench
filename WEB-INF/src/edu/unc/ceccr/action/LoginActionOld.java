package edu.unc.ceccr.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.formbean.LoginFormBean;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.utilities.PopulateDataObjects;


public class LoginActionOld extends Action {

/*	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)	throws Exception
	{
		
		//getSession(false) means: Don't create a session if it doesn't exist yet
		HttpSession session = request.getSession(false);
		if (session != null)
		{
		    session.invalidate();
		}
		//getSession(true) means: Create a session if it doesn't exist yet
	    session = request.getSession(true); 
	    
	    Utility.writeToDebug("" + session.getAttribute("loginName"));
	    
			if (utility.compareEncryption(utility.encrypt(loginPassword),password)){
				session.setAttribute("user", user);
				Cookie ckie=new Cookie("login","true");
				response.addCookie(ckie);
				
				Utility.writeToUsageLog("Logged in", user.getUserName());
				forward = mapping.findForward("success");	
			}
			else if(user.getUserName().equals("guest")){
				Utility.writeToUsageLog("Logged in", user.getUserName());
				forward = mapping.findForward("success");
				session.setAttribute("user", user);
				Cookie ckie=new Cookie("login","true");
				response.addCookie(ckie);
				
				Utility.writeToUsageLog("Logged in", user.getUserName());
				forward = mapping.findForward("success");		
			}
			else{
				forward = mapping.findForward("failure");
			}
		
		return forward;
	}
	*/
}
