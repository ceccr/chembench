package edu.unc.ceccr.taskObjects;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.outputObjects.Pred_Output;
import edu.unc.ceccr.task.Task;
import edu.unc.ceccr.utilities.Utility;

public class ViewPredictionOutputTask implements Task {

	private String filepath;

	private Pred_Output createPredObject(String[] extValues) {

		if (extValues == null) {
			return null;
		}

		Pred_Output predOutput = new Pred_Output();
		predOutput.setCompoundID(extValues[0]);
		predOutput.setNumOfModels(extValues[1]);
		predOutput.setPredictedValue(extValues[2]);

		return predOutput;

	}

	private ArrayList parsePredOutput(String fileLocation) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileLocation));
		String inputString;

		Utility.writeToDebug("ViewPredOutputActionTask: Parsing prediction output from file: " + fileLocation);
		
		ArrayList allPredValue = new ArrayList();

		while (!(inputString = in.readLine()).equals(""))
			;
		while ((inputString = in.readLine()).equals(""))
			;
		do {
			String[] predValues = inputString.split("\\s+");
			Pred_Output extValOutput = createPredObject(predValues);
			((ArrayList) allPredValue).add(extValOutput);
		} while ((inputString = in.readLine()) != null);
		return allPredValue;
	}

	private ArrayList allPredValue;

	public ViewPredictionOutputTask(String userName, String filename)throws Exception {
		filepath = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTIONS/"+ filename;
	}

	public void execute() throws Exception {
		allPredValue = parsePredOutput(filepath);
	}

	public void cleanUp() throws Exception {
	}

	public ArrayList getAllPredValue() {
		return allPredValue;
	}

	public void setUp() throws Exception {
	}

	public String getFilepath() {
		return filepath;
	}
}
