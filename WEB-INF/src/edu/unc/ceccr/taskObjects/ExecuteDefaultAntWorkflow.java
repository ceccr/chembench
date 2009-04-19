package edu.unc.ceccr.taskObjects;


import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.task.AntTask;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;

public class ExecuteDefaultAntWorkflow extends AntTask {

	boolean demo; 
	private String userName; 
	private String jobName;
	

	public ExecuteDefaultAntWorkflow(String userName, String jobName, String sdFile,
			String actFile, String numCompoundsExtSet, String knnCategoryOptimization)throws Exception {

		super(Constants.XML_FILE_PATH + "qsar-workflow.xml", Constants.CECCR_USER_BASE_PATH, null, "main", "save", "clean");

		this.userName = userName;
		this.jobName = jobName;
		
		setProperty("jobName", jobName);
		
		setProperty("userName", userName);
		
		setProperty("sdFile.value", sdFile);
		
		setProperty("actFile.value", actFile);
		
		setProperty("numCompoundsExternalSet", numCompoundsExtSet);
		
		setProperty("qsar.directory", userName + "/" + jobName);
		
		setProperty("optimization.value", knnCategoryOptimization);

	}

	private void createDirectories() throws Exception {

		CreateDirectoriesWorkflow.createDirs(userName, jobName);

	}

	void copyFiles() throws Exception {
		
		antRunner.runTarget("copyNonDemoFiles");
		
		String param9path = "/usr/local/chemb/ParameterFiles/param9.txt_demo";
		
		getProperty("type.directory");
		
		/*
			<target name="copyNonDemoFiles" description="Copy non-Demo Files">
				<copy file="../SDF/${type.directory}/${actFile.value}"   toDir="${qsar.directory}" />
				<copy file="../SDF/${type.directory}/${sdFile.value}"   toDir="${qsar.directory}" />
			</target>
		*/
		
	}

	void copy_gifs() throws Exception {
		
		antRunner.runTarget("copy_gifs");
	}

	public void cleanUp() throws Exception {
		
		super.cleanUp();
	}

	public void execute() throws Exception {
		
		super.execute();
		Utility.writeToDebug("ExecuteDefaultAntWorkflow", userName, jobName);
		
		this.copy_gifs();
	}

	public void setUp() throws Exception {
		
		super.setUp();

		Utility.writeToDebug("ExecuteDefaultAntWorkflow: Creating Directories", userName, jobName);
		this.createDirectories();
		
		this.copyFiles();
	}
}
