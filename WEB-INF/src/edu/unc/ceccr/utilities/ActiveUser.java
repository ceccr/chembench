package edu.unc.ceccr.utilities;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

public class ActiveUser implements HttpSessionListener{
	private static int activeSessions=0;
	
	public static String getActiveSessions()
	{
		return "Currently there are " + activeSessions + " users logged in.";
	}
	public void sessionCreated(HttpSessionEvent se)
	{
		activeSessions++;
	}
	public void sessionDestroyed(HttpSessionEvent se)
	{
		if(activeSessions>0)
			activeSessions--;
	}
	
}