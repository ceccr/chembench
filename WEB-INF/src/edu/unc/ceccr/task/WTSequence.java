package edu.unc.ceccr.task;

import java.util.ArrayList;
import java.util.List;

public class WTSequence implements WorkflowTask {

	List<WorkflowTask> list = new ArrayList<WorkflowTask>();
	
	public void setUp() throws Exception {
		for(WorkflowTask t: list) {
			t.setUp();
		}
	}

	public void execute() throws Exception {
		for(WorkflowTask t: list) {
			t.execute();
		}
	}

	public void cleanUp() throws Exception {
		for(WorkflowTask t: list) {
			t.cleanUp();
		}
	}

	public void save() throws Exception {
		for(WorkflowTask t: list) {
			t.save();
		}
	}
	
	public void add(WorkflowTask t) {
		list.add(t);
	}

}
