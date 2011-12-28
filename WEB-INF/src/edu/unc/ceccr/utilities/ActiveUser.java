package edu.unc.ceccr.utilities;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

import edu.unc.ceccr.action.HomeAction;
import edu.unc.ceccr.persistence.User;

public class ActiveUser implements HttpSessionListener{
	private static int activeSessions=0;
	
	public static String getActiveSessions()
	{
		return "" + activeSessions;
	}
	
	public void sessionCreated(HttpSessionEvent se)
	{
		activeSessions++;
	}
	public void sessionDestroyed(HttpSessionEvent se)
	{
		
		if(activeSessions>0 && se!=null){
			activeSessions--;
			if(se.getSession()!=null && se.getSession().getAttribute("userType")!=null){
				User user = (User)se.getSession().getAttribute("user");
				String type = (String)se.getSession().getAttribute("userType");
				if(user!=null && user.getUserName()!=null && user.getUserName().contains("guest") && type!=null && type.equals("guest")){
					new HomeAction().deleteGuest(user);
					Utility.writeToDebug("GUEST USER DELETED on SESSION TIMEOUT:"+user.getUserName());
				}
			}
		}
	}
	
}