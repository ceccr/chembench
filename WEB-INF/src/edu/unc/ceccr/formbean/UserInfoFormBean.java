package edu.unc.ceccr.formbean;

import org.apache.struts.validator.ValidatorForm;
import java.util.Date;
import org.apache.struts.action.*;
import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("serial")
public class UserInfoFormBean extends ActionForm 
{
	private String firstName;
	private String lastName;
	private String orgnization;
	private String nameOfOrg;
	private String position;
	private String address;
	private String city;
	private String state;
	private String zipcCode;
	private String phone;
	private String country;
	private String userName;
	private String email;
	//private String password1;
	private String workbench;
	private Date dateRegistered;
	
	public String getFirstName()
	{
		return this.firstName;
	}
	public void setFirstName(String firstName)
	{
		this.firstName=firstName;
	}
	
	public String getLastName()
	{
		return this.lastName;
	}
	public void setLastName(String lastName)
	{
		this.lastName=lastName;
	}
	
	public String getOrganization()
	{
		return this.orgnization;
	}
	public void setOrganization(String organization)
	{
		this.orgnization=organization;
	}
	
	public String getNameOfOrg()
	{
		return this.nameOfOrg;
	}
	public void setNameOfOrg(String nameOfOrg )
	{
		this.nameOfOrg=nameOfOrg;
	}
	
	public String getPosition()
	{
		return this.position;
	}
	public void setPosition(String position )
	{
		this.position=position;
	}
	
	public String getAddress()
	{
		return this.address;
	}
	public void setAddress(String address)
	{
		this.address=address;
	}
	
	public String getCity()
	{
		return this.city;
	}
	public void setCity(String city )
	{
		this.city=city;
	}
	
	public String getState()
	{
		return this.state;
	}
	public void setState(String state )
	{
		this.state=state;
	}
	
	public String getCountry()
	{
		return this.country;
	}
	public void setCountry(String country )
	{
		this.country=country;
	}
	
	
	public String getZipCode()
	{
		return this.zipcCode;
	}
	public void setZipCode(String zipCode )
	{
		this.zipcCode=zipCode;
	}
	
	public String getPhone()
	{
		return this.phone;
	}
	public void setPhone(String phone )
	{
		this.phone=phone;
	}
	
	public String getEmail()
	{
		return this.email;
	}
	public void setEmail(String  email)
	{
		this.email=email;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	public void setUserName(String userName)
	{
		this.userName=userName;
	}
	
	/*public String getPassword1()
	{
		return this.password1;
	}
	public void setPassword1(String password1)
	{
		this.password1=password1;
	}*/
	
	public String getWorkbench()
	{
		return this.workbench;
	}
	public void setWorkbench(String bench)
	{
		this.workbench=bench;
	}
	
	public Date getDateRegistered()
	{
		return this.dateRegistered;
	}
	public void setDateRegistered(Date date)
	{
		this.dateRegistered=date;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		this.address=null;
		this.city=null;
		this.country=null;
		this.dateRegistered=null;
		this.email=null;
		this.firstName=null;
		this.lastName=null;
		this.nameOfOrg=null;
		this.orgnization=null;
		this.phone=null;
		this.position=null;
		this.state=null;
		this.userName=null;
		this.zipcCode=null;
		
	}
}