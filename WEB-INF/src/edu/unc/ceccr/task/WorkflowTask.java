package edu.unc.ceccr.task;

public interface WorkflowTask {

	public abstract void setUp() throws Exception;

	public abstract void execute() throws Exception;

	public abstract void cleanUp() throws Exception;

	public abstract void save() throws Exception;

}