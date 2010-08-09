package edu.unc.ceccr.persistence;

import javax.persistence.*;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

@Entity
@Table(name = "cbench_randomForestGrove")
public class RandomForestGrove implements java.io.Serializable{
	
	/*
create table cbench_randomForestGrove (
id INT(12) UNSIGNED auto_increment PRIMARY KEY,
predictor_id int(10) unsigned NOT NULL DEFAULT '0',
name VARCHAR(255)
isYRandomModel VARCHAR(255),
descriptorsUsed VARCHAR(2550),
r2 VARCHAR(255),
mse VARCHAR(255),
ccr VARCHAR(255),
FOREIGN KEY (predictor_id) REFERENCES cbench_predictor(predictor_id) ON DELETE CASCADE ON UPDATE CASCADE
);
	 */

	private Long id;
	private Long predictor_id;
	private String name;
	private String isYRandomModel;
	private String descriptorsUsed;
	private String r2;
	private String mse;
	private String ccr;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "predictor_id")
	public Long getPredictor_id() {
		return predictor_id;
	}
	public void setPredictor_id(Long predictorId) {
		predictor_id = predictorId;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "isYRandomModel")
	public String getIsYRandomModel() {
		return isYRandomModel;
	}
	public void setIsYRandomModel(String isYRandomModel) {
		this.isYRandomModel = isYRandomModel;
	}

	@Column(name = "ccr")
	public String getCcr() {
		return ccr;
	}
	public void setCcr(String ccr) {
		if(ccr != null && ccr.length() > 255){
			ccr = ccr.substring(0, 254);
		}
		this.ccr = ccr;
	}
	
	@Column(name = "r2")
	public String getR2() {
		return r2;
	}
	public void setR2(String r2) {
		if(r2 != null && r2.length() > 255){
			r2 = r2.substring(0, 254);
		}
		this.r2 = r2;
	}

	@Column(name = "mse")
	public String getMse() {
		return mse;
	}
	public void setMse(String mse) {
		if(mse != null && mse.length() > 255){
			mse = mse.substring(0, 254);
		}
		this.mse = mse;
	}

	@Column(name = "descriptorsUsed")
	public String getDescriptorsUsed() {
		return descriptorsUsed;
	}
	public void setDescriptorsUsed(String descriptorsUsed) {
		if(descriptorsUsed != null && descriptorsUsed.length() > 2550){
			descriptorsUsed = descriptorsUsed.substring(0, 2549);
		}
		this.descriptorsUsed = descriptorsUsed;
	}
	
}