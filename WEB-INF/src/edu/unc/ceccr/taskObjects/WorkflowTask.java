package edu.unc.ceccr.taskObjects;

import edu.unc.ceccr.global.Constants;

public abstract class WorkflowTask {

	public String jobList = Constants.INCOMING;
	public Long lookupId;
	public String jobType;
	
	public abstract void setUp() throws Exception; 
	//creates any needed dirs
	//creates Job object and adds it to Incoming queue

	public abstract void preProcess() throws Exception; 
	//Does any work that must be done locally to prepare for main calculation
	//Things that go in here: Data splitting, normalizing of descriptors
	
	public abstract void executeLocal() throws Exception;
	//does the main calculation work using the Chembench server's processing power
	//Things that go in here: Calls to kNN Modeling, kNN prediction. 
	
	public abstract void executeLSF() throws Exception;
	//an alternative to executeLocal; should perform the same functions as executeLocal.
	//does the main calculation work by submitting it to LSF
	//Things that go in here: Calls to kNN Modeling, kNN prediction. 
	//Copying files to / from LSF is done in the preProcess / postProcess step.

	public abstract void postProcess() throws Exception; 
	//Does any work that must be done locally, after the main calculation work is done
	//Things that go in here: Reading in all models from a modeling run and calculating stats for display,
	//averaging results from the different predictors in a prediction run
	//Also, saves all results to the database
	
	public abstract void delete() throws Exception; 
	//Removes files, directories, and database entries associated with a workflowTask

	public abstract String getProgress() throws Exception; 
	//returns a detailed status message (% progress, etc)
}