package edu.unc.ceccr.taskObjects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.struts.validator.LazyValidatorForm;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.KnnOutputComparator;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.task.AntTask;
import edu.unc.ceccr.task.WorkflowTask;
import edu.unc.ceccr.utilities.Utility;

public class ViewModelsActionTask implements WorkflowTask {

	ArrayList<ModelInterface> allkNNValues = null;

	ArrayList<ModelInterface> sortedkNNValues = null;

	private String userName;

	int size;

	private boolean noModelsGenerated;

	private AntTask ee;

	private String jobName;

	//private LazyValidatorForm formBean;

	private String sdFile;

	private String actFile;

	private ArrayList allExternalValues;

	private String modelName;

	Queue queue = Queue.getInstance();

	public ViewModelsActionTask(String user, String jobName) throws Exception {

		this.userName = user;
		this.jobName = jobName;
		this.modelName = this.jobName;

		//this.formBean = formBean;
	}

	public void setUp() throws Exception {
	}

	public void execute() throws Exception {

		if (jobName == null)
			jobName = modelName;
		
		ee = new ExecuteExternal(userName, jobName, this.sdFile, this.actFile,
				modelName);
		
		if (modelName != null) {

			loadData(userName, modelName);
			ee = new ExecuteExternal(userName, jobName, this.sdFile, this.actFile, modelName);
			this.noModelsGenerated = this.allkNNValues.isEmpty();
			if (!noModelsGenerated) {
				sortModels();
				execExternalValidation();
			}
			setParamters();
		}

	}

	private void execExternalValidation() throws Exception {

		String user_path = this.userName + "/" + this.jobName + "/";
		String filePath = Constants.CECCR_USER_BASE_PATH + user_path;

		ee.setUp();
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath
				+ "knn-output.list"));
		for (ModelInterface m : allkNNValues) {
			String file = m.getFile();
			int i = file.indexOf('_', file.indexOf('.'));
			String n1 = file.substring(0, i);
			i = n1.indexOf('.');
			String n2 = n1.substring(0, i) + "1" + n1.substring(i);
			String n3 = file.substring(0, file.indexOf('.',	file.indexOf('.') + 1))	+ ".mod";
			bw.write(n1 + " " + n2 + " " + n3 + "\n");
		}
		bw.close();
		ee.execute();

		allExternalValues = QsarModelingTask.parseExternalValidationOutput(filePath + Constants.EXTERNAL_VALIDATION_OUTPUT_FILE, user_path);
	}

	private void loadPredictor(String userName2, String file2)
			throws HibernateException, ClassNotFoundException, SQLException {
		Predictor predictor = null;
		Session sess = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = sess.beginTransaction();
			predictor = (Predictor) sess.createCriteria(Predictor.class)
			.add(Expression.eq("name", file2))
			.add(Expression.eq("userName", userName2))
			.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			sess.close();
		}

		sdFile = predictor.getSdFileName();
		actFile = predictor.getActFileName();
	}

	private void loadData(String userName2, String file2)//, LazyValidatorForm form)
			throws HibernateException, ClassNotFoundException, SQLException {

		loadPredictor(userName2, file2);
		List models = null;
		Session sess = HibernateUtil.getSession();
		
		Transaction tx = null;
		try {
			tx = sess.beginTransaction();
			Criteria crite = sess.createCriteria(Model.class);
			
			crite.createCriteria("predictor").add(Expression.eq("name", file2)).add(Expression.eq("userName", userName2));
			models = crite.list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			sess.close();
		}
	
		allkNNValues = new ArrayList<ModelInterface>(models);
	}

	private List getList(LazyValidatorForm form, String string) {
		return (List) form.get(string);
	}

	public void cleanUp() throws Exception {
		ee.cleanUp();
	}

	private void setParamters() {

		size = allkNNValues.size();
	}

	private void sortModels() {
		KnnOutputComparator knnOutputComparator = new KnnOutputComparator();
		Collections.sort(allkNNValues, knnOutputComparator);

		// only keep top 10 models with the largest r-squared values
		sortedkNNValues = new ArrayList();
		for (int i = allkNNValues.size(); i > 0; i--) {
			sortedkNNValues.add(allkNNValues.get(i - 1));
		}

	}

	public ArrayList getAllExternalValues() {
		return allExternalValues;
	}

	public void save() throws Exception {

		queue.deleteTask(ee);

		ee.save();
		Predictor predictor = new Predictor();

		predictor.setName(this.jobName);

		HashSet<Model> models = new HashSet<Model>();
		for (ModelInterface m : allkNNValues) {
			models.add((Model) m);
		}
		predictor.setModels(models);

		for (ModelInterface m : models) {
			m.setPredictor(predictor);
			m.setId(null);
		}

		predictor.setActFileName(this.actFile);
		predictor.setSdFileName(this.sdFile);
		
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(predictor);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
	}
	public void cleanFiles()
	{
		Utility.writeToMSDebug("CleanFiles::"+Constants.CECCR_USER_BASE_PATH +this.userName+"/PREDICTORS/"+this.jobName);
		File file=new File(Constants.CECCR_USER_BASE_PATH +this.userName+"/PREDICTORS/"+this.jobName);
	   Utility.deleteDir(file);
	}
}
