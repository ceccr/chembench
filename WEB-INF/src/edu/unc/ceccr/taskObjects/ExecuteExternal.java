package edu.unc.ceccr.taskObjects;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.task.AntTask;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;
import edu.unc.ceccr.workflows.KnnModelBuildingWorkflow;

public class ExecuteExternal extends AntTask {

	private String userName; 
	private String jobName;
	private String sdFile; 
	private String actFile;
	
	public ExecuteExternal(String userName, String jobName, String sdFile,String actFile, String file) throws Exception
	{
		super(Constants.XML_FILE_PATH + "post-knn-workflow.xml", Constants.CECCR_USER_BASE_PATH, null, "executeParseStructGenMerge", "save", "clean");

		this.userName = userName;
		this.jobName = jobName;
		
		setProperty("fileName", file);
		setProperty("jobName", jobName);
		setProperty("userName", userName);
		setProperty("sdFile.value", sdFile);
		setProperty("actFile.value", actFile);
		setProperty("qsar.directory", userName + "/" + jobName);
		setProperty("tomcat.directory", Constants.TOMCAT_PATH);
	}
	
	public ExecuteExternal(String userName, String jobName, String sdFile,	String actFile)
			throws Exception {

		super(Constants.XML_FILE_PATH + "post-knn-workflow.xml", Constants.CECCR_USER_BASE_PATH, null, "executeParseStructGenMerge", "save", "clean");

		this.userName = userName;
		this.jobName = jobName;
		
		setProperty("jobName", jobName);
		setProperty("userName", userName);
		setProperty("sdFile.value", sdFile);
		setProperty("actFile.value", actFile);
		setProperty("qsar.directory", userName + "/" + jobName);
		setProperty("tomcat.directory", Constants.TOMCAT_PATH);
	}

	public void cleanUp() throws Exception {
		
		super.cleanUp();
	}

	public void execute() throws Exception {
		
		Utility.writeToDebug("ExecuteExternal", userName, jobName);
		KnnModelBuildingWorkflow.RunExternalSet(userName, jobName, sdFile, actFile);
	}

	public void setUp() throws Exception {
		
		super.setUp();
		Utility.writeToDebug("ExecuteExternal: CreateDirsWorkflow");
		CreateDirectoriesWorkflow.createDirs(userName, jobName);
		
	}
}
