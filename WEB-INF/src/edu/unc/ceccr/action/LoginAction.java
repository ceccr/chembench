package edu.unc.ceccr.action;

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
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.utilities.PopulateDataObjects;


public class LoginAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)	throws Exception
	{
		
		if(!Constants.isCustomized)
		{
			String path=getServlet().getServletContext().getRealPath("WEB-INF/systemConfig.xml");
			Utility.setAdminConfiguration(path);
		}
		
		ActionForward forward = new ActionForward(); 
	
		//start up the queues, if they're not running yet
		CentralDogma.getInstance();
		
		//getSession(false) means: Don't create a session if it doesn't exist yet
		HttpSession session = request.getSession(false);
		if (session != null)
		{
		    session.invalidate();
		}
		//getSession(true) means: Create a session if it doesn't exist yet
	    session = request.getSession(true); 
	    
	    Utility.writeToDebug("" + session.getAttribute("loginName"));
	    
		session.setMaxInactiveInterval(Constants.SESSION_EXPIRATION_TIME);
		
		Utility utility=new Utility();
		LoginFormBean loginBean = (LoginFormBean) form;

		User user = new User();

		Utility.writeToDebug("Starting session");
		Session s = null;
		try{
			s = HibernateUtil.getSession();// query
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		Utility.writeToDebug("Session started.");
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			user = (User) s.createCriteria(User.class).add(
					Expression.eq("userName", loginBean.getLoginName()))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		

		if (user != null){
			byte[] password=user.getPassword();
			String loginPassword = loginBean.getLoginPassword().trim();
			
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
		} 
		else{
			forward = mapping.findForward("failure");
		}
		
		//The only reason we use HTTPS at all is to encode login information
		//now that that's done, we want to redirect the user out of HTTPS-land
/*		
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        String contextPath = request.getContextPath();
        String destination = request.getServletPath()
                + ((pathInfo == null) ? "" : pathInfo)
                + ((queryString == null) ? "" : ("?" + queryString));

        String redirectUrl = "http://" + request.getServerName();
        if(! redirectUrl.endsWith("/")){
        	redirectUrl += "/";
        }
        redirectUrl += "jobs";
            
        // Add jsession id to end of redirection URL
       if (request.getSession(false) != null)
        {
            redirectUrl += ";jsessionid=" +  request.getSession(false).getId();
        }

        ((HttpServletResponse) response)
                .sendRedirect(((HttpServletResponse) response)
                        .encodeRedirectURL(redirectUrl));*/
		//end redirect
        
		return forward;
	}
}
