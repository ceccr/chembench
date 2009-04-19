package edu.unc.ceccr.formbean;

import org.apache.struts.validator.ValidatorForm;

public class LoginFormBean extends ValidatorForm {
//	 --------------------------------------------------------- Instance Variables

	/** loginName property */
	private String loginName;

	/** loginPassword property */
	private String loginPassword;

	/** loginPasswordConfirm property */
	private String loginPasswordConfirm;

	// --------------------------------------------------------- Methods

	/** 
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	/*
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {

			ActionErrors errors = new ActionErrors();

			// Validate the fields in your form, adding
			// adding each error to this.errors as found, e.g.

			//  must include loginName
			if ((loginName == null) || (loginName.length() == 0)) {
				errors.add("ActionErrors.GLOBAL_ERROR", new ActionMessage("error.login.noname"));
			}
		
			//  must include loginPassword
			if ((loginPassword == null) || (loginPassword.length() == 0)) {
				errors.add("ActionErrors.GLOBAL_ERROR", new ActionMessage("error.login.nopassword"));
			}

			//  if loginPasswortdConfirm present, must match loginPassword
			
			if ( (mapping.getInput()).equals("/strutsLoginCreate.jsp") )  {
				if ( ! ((loginPasswordConfirm == null) || (loginPasswordConfirm.length() == 0)) ) {
					if ( ! (loginPasswordConfirm.equals(loginPassword)) )
						errors.add("ActionErrors.GLOBAL_ERROR", new ActionMessage("error.loginCreate.nopasswordconfirmmatch"));
				}  else 
						errors.add("ActionErrors.GLOBAL_ERROR", new ActionMessage("error.loginCreate.nopasswordconfirm"));

			}


			return errors;	
			
		}*/

	/** 
	 * Returns the loginName.
	 * @return String
	 */
	public String getLoginName() {
		return loginName;
	}

	/** 
	 * Set the loginName.
	 * @param loginName The loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getLoginPasswordConfirm() {
		return loginPasswordConfirm;
	}

	public void setLoginPasswordConfirm(String loginPasswordConfirm) {
		this.loginPasswordConfirm = loginPasswordConfirm;
	}
}
