package edu.unc.ceccr.task;

import java.util.HashMap;

import edu.unc.ceccr.taskObjects.AntRunner;

public class AntTask implements Task, WorkflowTask {

	protected AntRunner antRunner;

	HashMap map = new HashMap();

	boolean demo;
	
	private String setUpTarget="setUp", execTarget="exec", cleanTarget="clean", persistenceTarget="save";

	public AntTask(String build, String dir, String setUpTarget, String execTarget, String persistenceTarget, String cleanTarget) throws Exception {
		this(build,dir);
		this.setUpTarget = setUpTarget;
		this.execTarget = execTarget;
		this.cleanTarget = cleanTarget;
		this.persistenceTarget = persistenceTarget;
	}

	public AntTask(String build, String dir)

	throws Exception {
		antRunner = new AntRunner();

		antRunner.init(build, dir);

	}

	public void setProperty(String key, Object value) {
		map.put(key, value);
	}
	public Object getProperty(String key) {
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.task.ExecTask#setUp()
	 */
	public void setUp() throws Exception {
		antRunner.setProperties(map, false);
		if(setUpTarget!=null) {
			antRunner.runTarget(setUpTarget);
		}
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.task.ExecTask#execute()
	 */
	public void execute() throws Exception {
		if(execTarget!=null)
		{
			antRunner.runTarget(execTarget);
		}
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.task.ExecTask#cleanUp()
	 */
	public void cleanUp() throws Exception {
		if(cleanTarget!=null) {
			antRunner.runTarget(cleanTarget);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.unc.ceccr.task.ExecTask#save()
	 */
	public void save() throws Exception {
		if(persistenceTarget!=null) {
			antRunner.runTarget(persistenceTarget);
		}
	}

}
