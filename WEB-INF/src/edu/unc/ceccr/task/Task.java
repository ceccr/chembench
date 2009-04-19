package edu.unc.ceccr.task;

public interface Task {

	void setUp() throws Exception;
	
	void execute() throws Exception;
	
	void cleanUp() throws Exception;
	
}
