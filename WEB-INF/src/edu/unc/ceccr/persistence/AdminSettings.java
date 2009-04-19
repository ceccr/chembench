package edu.unc.ceccr.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "cbench_admin_settings")
@SuppressWarnings("serial")
public class AdminSettings implements java.io.Serializable{
	
	private String type;
	private String value;
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
	
	@Column(name = "type")
	public String getType()
	{
		return this.type;
	}
	public void setType(String type)
	{
		this.type=type;
	}
	
	@Column(name="value")
	public String getValue()
	{
		return this.value;
	}
	public void setValue(String value)
	{
		this.value=value;
	}
}