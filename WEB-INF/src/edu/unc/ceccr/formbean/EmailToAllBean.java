package edu.unc.ceccr.formbean;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class EmailToAllBean extends ActionForm{
	private String cc;
	private String bcc;
	private String subject;
	private String content;
	
	public String getCc()
	{
		return this.cc;
	}
	public void setCc(String cc)
	{
		this.cc=cc;
	}
	
	public String getBcc()
	{
		return this.bcc;
	}
	public void setBcc(String bcc)
	{
		this.bcc=bcc;
	}
	
	public String getSubject()
	{
		return this.subject;
	}
	public void setSubject(String subject)
	{
		this.subject=subject;
	}
	
	public String getContent()
	{
		return this.content;
	}
	public void setContent(String content)
	{
		this.content=content;
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		this.bcc=null;
		this.cc=null;
		this.content=null;
		this.subject=null;
	}
}