package edu.unc.ceccr.persistence;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name="cbench_dataset")
public class DataSet implements java.io.Serializable{
	private Long fileId;
	
	private String fileName;
	
	private String userName;
	
	private String actFile;
	
	private String sdfFile;
	
	private String modelType;
	
	private int numCompound;
	
	private Date createdTime;
	
	private String description;
	
	private String actFormula;
	
	public DataSet(){}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "fileID")
	public Long getFileId()
	{
		return this.fileId;
	}
	public void setFileId(Long fileId)
	{
		this.fileId=fileId;
	}
	
	@Column(name="fileName")
	public String getFileName()
	{
		return this.fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName=fileName;
	}
	
	@Column(name="username")
	public String getUserName()
	{
		return this.userName;
	}
	public void setUserName( String userName)
	{
		this.userName=userName;
	}
	
	@Column(name="actFile")
	public String getActFile()
	{
		return this.actFile;
	}
	public void setActFile(String actFile)
	{
		this.actFile=actFile;
	}
	
	@Column(name="sdfFile")
	public String getSdfFile()
	{
		return this.sdfFile;
	}
	public void setSdfFile(String sdfFile)
	{
		this.sdfFile=sdfFile;
	}
	
	@Column(name="modelType")
	public String getModelType()
	{
		return this.modelType;
	}
	public void setModelType(String modelType)
	{
		this.modelType=modelType;
	}
	
	@Column(name="numCompound")
	public int getNumCompound()
	{
		return this.numCompound;
	}
	public void setNumCompound(int num)
	{
		this.numCompound=num;
	}
	
	@Column(name="createdTime")
	public Date getCreatedTime()
	{
		return this.createdTime;
	}
	public void setCreatedTime(Date date)
	{
		this.createdTime=date;
	}
	
	@Column(name="description")
	public String getDescription()
	{
		return this.description;
	}
	public void setDescription(String description)
	{
		if(description == null){
		description = "";
		}
		this.description=description;
	}
	
	@Column(name="actFormula")
	public String getActFormula() {
		return actFormula;
	}
	public void setActFormula(String actFormula) {
		this.actFormula = actFormula;
	}
	
}
