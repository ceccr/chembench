import sys
file = open('svm-params.txt', 'r')

while 1:
	line = file.readline()
	if not line:
		break
	sys.stdout.write(line.replace("\n", ""))
	loopVar = line

#Build models on each training file in listFile according to given parameters. 
#Predict test sets given by listFile.
#Delete any files and outputs that don't pass the model acceptance criteria.




"""
Usage: svm-train [options] training_set_file [model_file]
options:
-s svm_type : set type of SVM (default 0)
	0 -- C-SVC
	1 -- nu-SVC
	2 -- one-class SVM
	3 -- epsilon-SVR
	4 -- nu-SVR
-t kernel_type : set type of kernel function (default 2)
	0 -- linear: u'*v
	1 -- polynomial: (gamma*u'*v + coef0)^degree
	2 -- radial basis function: exp(-gamma*|u-v|^2)
	3 -- sigmoid: tanh(gamma*u'*v + coef0)
	4 -- precomputed kernel (kernel values in training_set_file)
-d degree : set degree in kernel function (default 3)
-g gamma : set gamma in kernel function (default 1/num_features)
-r coef0 : set coef0 in kernel function (default 0)
-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)
-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)
-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)
-m cachesize : set cache memory size in MB (default 100)
-e epsilon : set tolerance of termination criterion (default 0.001)
-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)
-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)
-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)
-v n: n-fold cross validation mode
"""



"""
java code to replace

//input file name
String inputFile = data[0].replace(".x", ".svm");
command += " " + inputFile + " ";

//output file name
String modelFileName = inputFile.replace(".svm", "") + "_d" + degreeStr + "_g" + gammaStr + "_c" + costStr + "_n" + nuStr + "_p" + pEpsilonStr + ".mod";
command += modelFileName;

RunExternalProgram.runCommandAndLogOutput(command, workingDir, "svm-train" + modelFileName);

//run prediction on test set
String testFileName = data[3].replace(".x", ".svm");
String predictionOutputFileName = modelFileName + ".pred-test";

String command2 = "svm-predict " + testFileName + " " + modelFileName + " " + predictionOutputFileName;
if(new File(workingDir + modelFileName).exists()){
	RunExternalProgram.runCommandAndLogOutput(command2, workingDir, "svm-predict" + modelFileName);
}
else{
	continue;
}

//eliminate (delete) model if it doesn't pass its CCR or r^2 cutoff

//get predicted and actual (test set) values from files
//read test .a file
String testActivityFileName = testFileName.replace(".svm", ".a");

BufferedReader br = new BufferedReader(new FileReader(workingDir + testActivityFileName));
String line = "";
ArrayList<Double> testValues = new ArrayList<Double>();
while((line = br.readLine()) != null){
	if(!line.isEmpty()){
		String[] parts = line.split("\\s+");
		testValues.add(Double.parseDouble(parts[1]));
	}
}
br.close();

//read predicted .a file
br = new BufferedReader(new FileReader(workingDir + predictionOutputFileName));
ArrayList<Double> predictedValues = new ArrayList<Double>();
while((line = br.readLine()) != null){
	if(!line.isEmpty()){
		predictedValues.add(Double.parseDouble(line));
	}
}
br.close();

if(testValues.size() != predictedValues.size()){
	Utility.writeToDebug("Warning: test set act file has " + testValues.size() + 
			" entries, but predicted file has " + 
			predictedValues.size() + " entries for file: " + predictionOutputFileName);
}

boolean modelIsGood = true;

if(actFileDataType.equals(Constants.CONTINUOUS)){
	//calculate r^2 for test set prediction
	
	Double avg = 0.0;
	for(Double testValue : testValues){
		avg += testValue;
	}
	avg /= testValues.size();
	Double ssErr = 0.0;
	for(int i = 0; i < testValues.size(); i++){
		Double residual = testValues.get(i) - predictedValues.get(i);
		ssErr += residual * residual;
	}
	Double ssTot = 0.0;
	for(Double testValue : testValues){
		ssTot += (testValue - avg) * (testValue - avg);
	}
	Double rSquared = 0.0;
	if(ssTot != 0){
		rSquared = Double.parseDouble(Utility.roundSignificantFigures("" + (1 - (ssErr / ssTot)), 4));
	}

	log.write(modelFileName + " r2: " + rSquared + "\n");
	if(rSquared < cutoff){
		modelIsGood = false;
	}
}
else if(actFileDataType.equals(Constants.CATEGORY)){
	//calculate CCR for test set prediction
	
	/*
	1/2(TP/#pos + TN/#neg)
	1/3(T1/#1 + T2/#2 + T3/#3)
	*/
	HashMap<Double, Integer> correctPredictionCounts = new HashMap<Double, Integer>();
	HashMap<Double, Integer> observedValueCounts = new HashMap<Double, Integer>();
	
	for(int i = 0; i < testValues.size(); i++){
		if(observedValueCounts.containsKey(testValues.get(i))){
			observedValueCounts.put(testValues.get(i), observedValueCounts.get(testValues.get(i)) + 1);
		}
		else{
			observedValueCounts.put(testValues.get(i), 1);
		}
		
		if(predictedValues.get(i).equals(testValues.get(i))){
			if(correctPredictionCounts.containsKey(testValues.get(i))){
				correctPredictionCounts.put(testValues.get(i), correctPredictionCounts.get(testValues.get(i)) + 1);
			}
			else{
				correctPredictionCounts.put(testValues.get(i), 1);
			}
		}
	}
	
	Double ccr = 0.0;
	for(Double d: correctPredictionCounts.keySet()){
		ccr += new Double(correctPredictionCounts.get(d)) / new Double(observedValueCounts.get(d));
	}
	ccr = ccr / new Double(observedValueCounts.keySet().size());
	
	log.write(modelFileName + " ccr: " + ccr + "\n");
	if(ccr < cutoff){
		//Utility.writeToDebug("bad model: ccr = " + (numCorrect / (numCorrect + numIncorrect)));
		modelIsGood = false;
	}
}
log.flush();
if(! modelIsGood){
	//delete it
	if(new File(workingDir + modelFileName).exists()){
		FileAndDirOperations.deleteFile(workingDir + modelFileName);
	}
}
//pred-test file is no longer needed 
if(new File(workingDir + predictionOutputFileName).exists()){
	FileAndDirOperations.deleteFile(workingDir + predictionOutputFileName);
}

//read MSE and correlation coeff. for prediction
//String s = FileAndDirOperations.readFileIntoString(workingDir + "Logs/" + "svm-predict" + modelFileName + ".log");
//Utility.writeToDebug(s);
"""								
