import sys
import re
import numpy
import os

def permute_sequence(seq):
	n = len(seq)
	if n <= 1: 
		return seq
		
	mid = int(n/2)
	left = permute_sequence(seq[:mid])
	right = permute_sequence(seq[mid+1:])
	
	ret = [seq[mid]]
	while left or right:
		if left: ret.append(left.pop(0))
 		if right: ret.append(right.pop(0))
 		
	return ret

def range_f(begin,end,step):
	# like range, but works on non-integer too
	seq = []
	while 1:
		if step > 0 and begin > end: break
		if step < 0 and begin < end: break
		seq.append(begin)
		begin = begin + step
	return seq
	
#calculates r^2 given two arrays of numbers
def rSquared(actualValues, predictedValues):
	avg = float(0)
	for av in actualValues:
		avg += av
	avg /= len(actualValues)

	ssErr = float(0)
	for i in range(len(actualValues)):
		residual = actualValues[i] - predictedValues[i]
		ssErr += residual * residual
	
	ssTot = float(0)
	for av in actualValues:
		ssTot += (av - avg) * (av - avg)
	
	rSquared = float(0)
	if ssTot != 0:
		rSquared = 1 - (ssErr / ssTot)
		
	return rSquared

#calculates CCR given two arrays of numbers
def ccr(actualValues, predictedValues):
	#CCR (two-class) = 1/2(TP/#pos + TN/#neg)
	#CCR (three-class) = 1/3(T1/#1 + T2/#2 + T3/#3)
	actualValueCounts = {}
	correctPredictionCounts = {}

	if len(actualValues) != len(predictedValues):
		print "Error: actualValues is of size", len(actualValues), "and predictedValues is of size", len(predictedValues)
		
	for i in range(len(actualValues)):
		av = actualValues[i]
		if av in actualValueCounts.keys():
			actualValueCounts[av] = actualValueCounts[av] + 1
		else:
			actualValueCounts[av] = 1

		if predictedValues[i] == av:
			if av in correctPredictionCounts.keys():
				correctPredictionCounts[av] = correctPredictionCounts[av] + 1
			else:
				correctPredictionCounts[av] = 1
	
	ccr = float(0)
	for d in correctPredictionCounts.keys():
		ccr += float(correctPredictionCounts[d]) / actualValueCounts[d]
	ccr = ccr / len(actualValueCounts.keys())

	return ccr

file = open('svm-params.txt', 'r')
#read params file into vars
while 1:
	line = file.readline()
	if not line:
		break
	match = re.match("([^:]+):\s+(.+)", line)
	if match.group(1) == "list-file":
		listFileName = match.group(2)
	if match.group(1) == "modeling-dir":
		modelingDir = match.group(2)
	if match.group(1) == "y-random-dir":
		yRandomDir = match.group(2)
	if match.group(1) == "svm-type":
		svmType = match.group(2)
	if match.group(1) == "kernel-type":
		kernelType = match.group(2)
	if match.group(1) == "shrinking-heuristics":
		shrinkingHeuristics = match.group(2)
	if match.group(1) == "use-probability-heuristics":
		probabilityHeuristics = match.group(2)
	if match.group(1) == "c-svc-weight":
		cSvcWeight = match.group(2)
	if match.group(1) == "num-cross-validation-folds":
		numCrossFolds = match.group(2)
	if match.group(1) == "tolerance-for-termination":
		tolerance = match.group(2)
	if match.group(1) == "cost-from":
		costFrom = float(match.group(2))
	if match.group(1) == "cost-to":
		costTo = float(match.group(2))
	if match.group(1) == "cost-step":
		costStep = float(match.group(2))
	if match.group(1) == "gamma-from":
		gammaFrom = float(match.group(2))
	if match.group(1) == "gamma-to":
		gammaTo = float(match.group(2))
	if match.group(1) == "gamma-step":
		gammaStep = float(match.group(2))
	if match.group(1) == "degree-from":
		degreeFrom = float(match.group(2))
	if match.group(1) == "degree-to":
		degreeTo = float(match.group(2))
	if match.group(1) == "degree-step":
		degreeStep = float(match.group(2))
	if match.group(1) == "nu-from":
		nuFrom = float(match.group(2))
	if match.group(1) == "nu-to":
		nuTo = float(match.group(2))
	if match.group(1) == "nu-step":
		nuStep = float(match.group(2))
	if match.group(1) == "loss-epsilon-from":
		lossFrom = float(match.group(2))
	if match.group(1) == "loss-epsilon-to":
		lossTo = float(match.group(2))
	if match.group(1) == "loss-epsilon-step":
		lossStep = float(match.group(2))
	if match.group(1) == "model-acceptance-cutoff":
		cutoff = match.group(2)
	if match.group(1) == "activity-type":
		activityType = match.group(2)
file.close()

#Build models on each training file in listFile according to given parameters. 
#Predict test sets given by listFile.
#Delete any files and outputs that don't pass the model acceptance criteria.

for dirindex in range(0, 2):
	if dirindex == 0:
		workingDir = modelingDir
	else:
		workingDir = yRandomDir

	#write header for output file
	outfile = open(workingDir + "svm-results.txt", 'w')
	outfile.write("rSquared\t" + "ccr\t" + "MSE\t" + "degree\t" + "gamma\t" + "cost\t" + "nu\t" + "loss (epsilon)" + "\n")
	outfile.close()

	listFile = open(listFileName, 'r')
	while 1:
		line = listFile.readline()
		if not line:
			break
		if "#" in line:
			line = listFile.readline()
			
		match = re.match("([^ ]+)\s+([^ ]+)\s+([^ ]+)\s+([^ ]+)\s+([^ ]+)\s+", line)
		trainSvm = workingDir + match.group(1)
		trainActivity = workingDir + match.group(2)
		testSvm = workingDir + match.group(4)
		testActivity = workingDir + match.group(5)
		trainSvm = re.sub("\.x", ".svm", trainSvm)
		testSvm = re.sub("\.x", ".svm", testSvm)
		
		#read test set activities
		actualValues = []
		testActFile = open(testActivity, 'r')
		while 1:
			line = testActFile.readline()
			if not line:
				break
			match = re.match("([^ ]+)\s+([^ ]+)", line)
			actualValues.append(float(match.group(2)))
		testActFile.close()
		
		degreeRange = permute_sequence(range_f(degreeFrom, degreeTo+0.0001, degreeStep))
		gammaRange = permute_sequence(range_f(gammaFrom, gammaTo+0.0001, gammaStep))
		
		if svmType in ("0", "3", "4"):
			costRange = permute_sequence(range_f(costFrom, costTo+0.0001, costStep))
		else:
			costRange = ["NA"]
		
		if svmType in ("1", "4"):
			nuRange = permute_sequence(range_f(nuFrom, nuTo+0.0001, nuStep))
		else:
			nuRange = ["NA"]
		
		if svmType == "3":
			lossRange = permute_sequence(range_f(lossFrom, lossTo+0.0001, lossStep))
		else:
			lossRange = ["NA"]
		
		for cost in costRange:
			for degree in degreeRange:
				for nu in nuRange:
					for loss in lossRange:
						for gamma in gammaRange:
							modelFileName = trainSvm.replace(".svm", "")+"_d"+str(degree)+"_g"+str(gamma)+"_c"+str(cost)+"_n"+str(nu)+"_p"+str(loss)+".mod"
							command = "svm-train " + "-s " + svmType + " -t " + kernelType 
							command += " -h " + shrinkingHeuristics + " -b " + probabilityHeuristics 
							if numCrossFolds != str(0):
								command += " -v " + numCrossFolds
							command += " -wi " + cSvcWeight +  " -e " + tolerance
							command += " -d " + str(degree) + " -g " + str(gamma) + " -c " + str(cost) + " -n " + str(nu) + " -p " + str(loss)
							command += " " + trainSvm + " " + modelFileName
							os.system(command)

							#now predict the test set and get the results
							predictionFileName = modelFileName + ".pred-test"
							predictCommand = "svm-predict " + testSvm + " " + modelFileName + " " + predictionFileName
							os.system(predictCommand)

							#read predicted activities 
							predictedValues = []
							predOutFile = open(predictionFileName, 'r')
							while 1:
								line = predOutFile.readline()
								if not line:
									break
								predictedValues.append(float(line))
							predOutFile.close()
							
							predictionResult = 0.0
							if activityType == "CONTINUOUS":
								predictionResult = rSquared(actualValues, predictedValues)
							else:
								predictionResult = ccr(actualValues, predictedValues)
							
							outfile = open(workingDir + "svm-results.txt", 'a')
							if activityType == "CONTINUOUS":
								#header: "rSquared\t" + "ccr\t" + "MSE\t" + "degree\t" + "gamma\t" + "cost\t" + "nu\t" + "loss (epsilon)" + "\n"
								outfile.write(str(predictionResult) + "\t" + "NA\t" + "NA\t" + str(degree) + "\t" + str(gamma) + "\t" + str(cost) + "\t" + str(nu) + "\t" + str(loss) + "\n")
							else:
								#header : "rSquared\t" + "ccr\t" + "MSE\t" + "degree\t" + "gamma\t" + "cost\t" + "nu\t" + "loss (epsilon)" + "\n"
								outfile.write("NA\t" + str(predictionResult) + "\t" + "NA\t" + str(degree) + "\t" + str(gamma) + "\t" + str(cost) + "\t" + str(nu) + "\t" + str(loss) + "\n")
							outfile.close()
							
							if predictionResult < float(cutoff):
								#delete the model file and prediction output file
								os.remove(modelFileName)
								os.remove(predictionFileName)
