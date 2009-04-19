package edu.unc.ceccr.formbean;

import org.apache.struts.action.ActionForm;

public class SoftwareExpirationForm extends ActionForm{
	
	private String name;
	private int year;
	private int month;
	private int date;
	private int id;
	
	
	public int getId()
	{
		return this.id;
	}
	
	public void setId(int id)
	{
		this.id=id;
	}
	
	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name=name;
	}
	
	public int getYear()
	{
		return this.year;
	}
	public void setYear(int year)
	{
		this.year=year;
	}
	
	public int getMonth()
	{
		return this.month;
	}
	public void setMonth(int month)
	{
		this.month=month;
	}
	
	public int getDate()
	{
		return this.date;
	}
	public void setDate(int date)
	{
		this.date=date;
	}
}