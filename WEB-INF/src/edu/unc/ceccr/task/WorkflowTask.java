package edu.unc.ceccr.task;

public interface WorkflowTask {

	public abstract void setUp() throws Exception;

	public abstract void execute() throws Exception;

	public abstract void cleanUp() throws Exception;

	public abstract void save() throws Exception;

	public abstract String getProgress() throws Exception;

	public abstract String getNumCompounds() throws Exception;
	
	public abstract String getNumModels() throws Exception;
}