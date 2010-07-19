package edu.unc.ceccr.persistence;

import javax.persistence.*;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

@Entity
@Table(name = "cbench_RandomForestTree")
public class RandomForestTree implements java.io.Serializable{
	
	/*
		create table cbench_randomForestTree (
		id INT(12) UNSIGNED auto_increment PRIMARY KEY,
		randomForestModelId int(12) unsigned NOT NULL DEFAULT '0',
		treeFileName VARCHAR(255),
		r2 VARCHAR(255),
		mse VARCHAR(255),
		descriptorsUsed VARCHAR(2550),
		FOREIGN KEY (randomForestModelId) REFERENCES cbench_randomForestModel(id) ON DELETE CASCADE ON UPDATE CASCADE
		);
	 */

	private Long id;
	private Long randomForestModelId;
	private String treeFileName;
	private String r2; //r squared
	private String mse;
	private String descriptorsUsed;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "randomForestModelId")
	public Long getRandomForestModelId() {
		return randomForestModelId;
	}
	public void setRandomForestModelId(Long randomForestModelId) {
		this.randomForestModelId = randomForestModelId;
	}

	@Column(name = "treeFileName")
	public String getTreeFileName() {
		return treeFileName;
	}
	public void setTreeFileName(String treeFileName) {
		this.treeFileName = treeFileName;
	}

	@Column(name = "r2")
	public String getR2() {
		return r2;
	}
	public void setR2(String r2) {
		this.r2 = r2;
	}

	@Column(name = "mse")
	public String getMse() {
		return mse;
	}
	public void setMse(String mse) {
		this.mse = mse;
	}

	@Column(name = "descriptorsUsed")
	public String getDescriptorsUsed() {
		return descriptorsUsed;
	}
	public void setDescriptorsUsed(String descriptorsUsed) {
		this.descriptorsUsed = descriptorsUsed;
	}
	
}