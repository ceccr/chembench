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

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.utilities.PopulateDataObjects;

public class CheckSessionAction extends Action 
{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)	throws Exception
	{
		
		ActionForward forward = new ActionForward();
		
		if(!Constants.isCustomized)
		{
			String path=getServlet().getServletContext().getRealPath("WEB-INF/systemConfig.xml");
			Utility.setAdminConfiguration(path);
		}
		
		//StringBuffer reqURL=request.getRequestURL();
		
		//if(reqURL.toString().contains(Constants.WHICHBENCH))

		if(Constants.WORKBENCH.toLowerCase().contains(""))
		{
			Constants.WORKBENCH=Constants.CCHEMBENCH;
		}else{
			Constants.WORKBENCH=Constants.CTOXBENCH;
		}
		
		HttpSession session=request.getSession(false);
		//||(session.getAttribute("userName") != null && session.getAttribute("userName").equals("_all"))
		if(session==null||session.getAttribute("user")==null)
		{
			if(session.getAttribute("user")!=null)
			{session.removeAttribute("user");}

			/*
			//Make it so the user "guest" is logged in.
			User user = new User();
			
			Session s = HibernateUtil.getSession();// query
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				user = (User) s.createCriteria(User.class).add(
						Expression.eq("userName", "_all"))
						.uniqueResult();
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {s.close();}
			
			session.setAttribute("user", user);
			
			Utility.writeToDebug("No login provided. Logging in as guest.");
			session.setAttribute("userName", "_all");
			session = request.getSession(true);
			session.setMaxInactiveInterval(1800);
			*/
			return forward=mapping.findForward("login");
		}
		
        Cookie[] cookies=request.getCookies();
        
        Cookie cookie;       
        
        for(int i=0;i<cookies.length;i++)
        {
        	cookie=cookies[i];
        	
        	if(cookie.getName().equalsIgnoreCase("login")&&cookie.getValue().equalsIgnoreCase("true"))
        	{
        		forward=mapping.findForward("loggedin");
        		
            	return forward;
            	}
            }
        forward = mapping.findForward("login");
        
        return forward;
        
        }
	}

