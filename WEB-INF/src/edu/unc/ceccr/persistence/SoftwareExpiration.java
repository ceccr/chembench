package edu.unc.ceccr.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "cbench_software")
@SuppressWarnings("serial")
public class SoftwareExpiration implements java.io.Serializable{
	
	private String name;
	private int year;
	private int month;
	private int date;
	private int id;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public int getId()
	{
		return this.id;
	}
	public void setId(int id)
	{
		this.id=id;
	}
	
	@Column(name="name")
	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name=name;
	}
	@Column(name="year")
	public int getYear()
	{
		return this.year;
	}
	public void setYear(int year)
	{
		this.year=year;
	}
	
	@Column(name="month")
	public int getMonth()
	{
		return this.month;
	}
	public void setMonth(int month)
	{
		this.month=month;
	}
	
	@Column(name="date")
	public int getDate()
	{
		return this.date;
	}
	public void setDate(int date)
	{
		this.date=date;
	}
}