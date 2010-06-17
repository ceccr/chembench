package edu.unc.ceccr.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity()
@Table(name = "cbench_randomForestParameters")
public class RandomForestParameters{
	private Long id;
	
	private String numTrees;
	private String trainSetSize;
	private String descriptorsPerTree;
	private String sampleWithReplacement;
	private String classWeights;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "numTrees")
	public String getNumTrees() {
		return numTrees;
	}
	public void setNumTrees(String numTrees) {
		this.numTrees = numTrees;
	}
	
	@Column(name = "trainSetSize")
	public String getTrainSetSize() {
		return trainSetSize;
	}
	public void setTrainSetSize(String trainSetSize) {
		this.trainSetSize = trainSetSize;
	}
	
	@Column(name = "descriptorsPerTree")
	public String getDescriptorsPerTree() {
		return descriptorsPerTree;
	}
	public void setDescriptorsPerTree(String descriptorsPerTree) {
		this.descriptorsPerTree = descriptorsPerTree;
	}
	
	@Column(name = "sampleWithReplacement")
	public String getSampleWithReplacement() {
		return sampleWithReplacement;
	}
	public void setSampleWithReplacement(String sampleWithReplacement) {
		this.sampleWithReplacement = sampleWithReplacement;
	}
	
	@Column(name = "classWeights")
	public String getClassWeights() {
		return classWeights;
	}
	public void setClassWeights(String classWeights) {
		this.classWeights = classWeights;
	}
}