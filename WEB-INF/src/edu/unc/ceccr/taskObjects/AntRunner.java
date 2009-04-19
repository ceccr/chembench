package edu.unc.ceccr.taskObjects;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.XmlLogger;
import edu.unc.ceccr.utilities.*;

public class AntRunner {
	private Project project;

	/**
	 * Initializes a new Ant Project.
	 * @(protected) _buildFile The build File to use. If none is provided, it will
	 be defaulted to "build.xml".
	 * @(protected) _baseDir The project's base directory. If none is provided, will
	 be defaulted to "." (the current directory).
	 * @(protected) Exception Exceptions are self-explanatory (read their Message)
	 */
	public void init(String _buildFile, String _baseDir) throws Exception {
		// Create a new project, and perform some default initialization
		project = new Project();
		//AntBuildListener antBuildListener = new AntBuildListener();
		Utility.writeToDebug("Initializing Ant runner", _baseDir, "");
		
		
		XmlLogger xmllogger = new XmlLogger();
		xmllogger.setOutputPrintStream(System.out);
		try {
			project.init();
		} catch (BuildException e) {
			Utility.writeToDebug(new Exception("The default task list could not be loaded."));
		}

		// Set the base directory. If none is given, "." is used.
		if (_baseDir == null)
			_baseDir = new String(".");
		try {
			project.setBasedir(_baseDir);
		} catch (BuildException e) {
			throw new Exception(
					"The given basedir doesn't exist, or isn't a directory.");
		}

		// Parse the given buildfile. If none is given, "build.xml" is used.
		if (_buildFile == null)
			_buildFile = new String("build.xml");
		try {
			ProjectHelper.getProjectHelper().parse(project,
					new File(_buildFile));
		} catch (BuildException e) {
			Utility.writeToDebug(e);
			throw new Exception("Configuration file " + _buildFile
					+ " is	invalid, or cannot be read.");
		}
		//try{
		//	project.addBuildListener(xmllogger);
		//}catch (Exception e){
		//	Utility.writeToDebug(e);
		//}
	}

	/**
	 * Sets the project's properties.
	 * May be called to set project-wide properties, or just before a target
	 call to set target-related properties only.
	 * @(protected) _properties A map containing the properties' name/value couples
	 * @(protected) _overridable If set, the provided properties values may be
	 overriden by the config file's values
	 * @(protected) Exception Exceptions are self-explanatory (read their Message)
	 */
public void setProperties(Map _properties, boolean _overridable) throws
	Exception
	    {
	        // Test if the project exists
	        if (project == null) throw new Exception(
	        		"Properties cannot be set because the project has not been initialized. Please call the 'init' method first !");

	        // Property hashmap is null
	        if (_properties == null) throw new Exception("The provided property map is null.");

	        // Loop through the property map
	        Set propertyNames = _properties.keySet();
	        Iterator iter = propertyNames.iterator();
	        while (iter.hasNext())
	        {
	            // Get the property's name and value
	            String propertyName =  (String) iter.next();
	            String propertyValue = (String) _properties.get(propertyName);
	            if (propertyValue == null) continue;

	            // Set the properties
	            if (_overridable) project.setProperty(propertyName, propertyValue);
	            else project.setUserProperty(propertyName, propertyValue);
	        }
	    }
	/**
	 * Runs the given Target.
	 * @(protected) _target The name of the target to run. If null, the project's
	 default target will be used.
	 * @(protected) Exception Exceptions are self-explanatory (read their Message)
	 */
public void runTarget(String _target) throws Exception
	    {
	        // Test if the project exists
	        if (project == null) throw new Exception("No target can be launched" +
	        		"	because the project has not been initialized. Please call the 'init' method " +
	        		"first !");

	        // If no target is specified, run the default one.
	        if (_target == null) _target = project.getDefaultTarget();
	        // Run the target
	        try { project.executeTarget(_target);  }
	        catch (BuildException e)
	        { throw new Exception(e.getMessage()); }
	    }
}
