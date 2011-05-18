package edu.unc.ceccr.utilities;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

public class ActiveUser implements HttpSessionListener{
	private static int activeSessions=0;
	
	public static String getActiveSessions()
	{
		String s = "";
		if(activeSessions == 1){
			s = "Currently there is 1 user logged in.";
		}
		else{
			s = "Currently there are " + activeSessions + " users logged in.";
		}
		return s;
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