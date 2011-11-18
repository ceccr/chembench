package edu.unc.ceccr.action;




//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 


import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;

public class CeccrBaseAction extends ActionSupport {

	public String loadPage() throws Exception {
		
		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
		}

		return result;
	}

	//====== variables used for displaying the JSP =====//
	private User user;
		
	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}