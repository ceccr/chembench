package edu.unc.ceccr.persistence;

import javax.persistence.*;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

@Entity
@Table(name = "cbench_RandomForestModel")
public class RandomForestModel implements java.io.Serializable, ModelInterface{
	//see KnnModel.java
	
	@Transient
	public String getModelType() {
		return Constants.RANDOMFOREST;
	}
}