# Purpose: converts molconnZ CSV-style descriptors to knn-format .X files
# Usage: python mzToX.py infile.molconnz outfile.x
# Author: Theo Walker
# Date: Dec. 20, 2010
# Version 1.0

import sys
import re

#constants
LINENUM_POS = 0
ERRORCODE_POS = 1
COMPOUNDID_POS = 2

#helper functions
def getMzError(errorCode):
	baseWarning = "Warning: MolconnZ failed for compound on line " + str(lineNum) + \
		" of input file. "
	if errorCode == "1":
		baseWarning += "MolconnZ error code: " + "Too many circuits." 
	elif errorCode == "2":
		baseWarning += "MolconnZ error code: " + "Too many paths."
	elif errorCode == "3":
		baseWarning += "MolconnZ error code: " + "Disconnected molecule graph."
	elif errorCode == "4":
		baseWarning += "MolconnZ error code: " + "Too many edges."
	elif errorCode == "5":
		baseWarning += "MolconnZ error code: " + "Only one atom."
	elif errorCode == "6":
		baseWarning += "MolconnZ error code: " + "Unparameterized element."
	elif errorCode == "90":
		baseWarning += "MolconnZ error code: " + "File read error."
	else:
		baseWarning +="MolconnZ error code: " + errorCode + "."
	baseWarning += " Compound '" + descriptorValues[COMPOUNDID_POS] + "' skipped!"
	return baseWarning

#check input command, test the file i/o

try:             
    print 'input MolconnZ CSV file: ', sys.argv[1]
    print 'output X file: ', sys.argv[2], "\n"
    infileName = sys.argv[1]
    infile = open(sys.argv[1], 'r')
    outfileName = sys.argv[2]
    outfile = open(sys.argv[2], 'w')
except:        
	if len(sys.argv) >= 3:
		print "Error opening input or output file.\n"
	else:
		print "Usage: python mzToX.py infile.molconnz outfile.x\n"   
	sys.exit(2)

# Get descriptors from first line of file
line = infile.readline().rstrip()
descriptorNames = re.split(",", line)
numDescriptors = len(descriptorNames) - 3
if numDescriptors <= 2:
	print "Error: First line of molconnZ file does not contain comma-separated descriptor names. Exiting.\n"
	sys.exit(2)

errorCompounds = []
#check each compound's descriptor values. Print any errors or warnings to stdout.
lineNum = 2
numCompounds = 0
while True:
	line = infile.readline().rstrip()
	if not line:
		break
	errorCompound = False
	descriptorValues = re.split(",", line)
	if len(descriptorValues) - 3 != numDescriptors:
		print "Warning: Only " + str(len(descriptorValues) - 3) + \
		" descriptors found on line " + str(lineNum) + \
		" of input file. Compound skipped!"
		errorCompound = True
	elif descriptorValues[ERRORCODE_POS] != "0":
		print getMzError(descriptorValues[ERRORCODE_POS])
		errorCompound = True
	else:
		#check all values are decimals first
		valueFormatError = False
		for j in range(len(descriptorValues)):
			if j != LINENUM_POS and \
			j != ERRORCODE_POS and \
			j != COMPOUNDID_POS:
				if re.sub("-*\d+\.*\d*", "", descriptorValues[j]) != "":
					valueFormatError = True
					break
		if valueFormatError:
			print "Warning: Invalid descriptor value in input line " + str(lineNum) + \
			" at position " + str(j+1) + " : \"" + descriptorValues[j] + "\"." + \
			" Compund skipped!"
			errorCompound = True
	if errorCompound:
		errorCompounds.append(lineNum - 1)
	else:
		numCompounds += 1
	lineNum += 1

print str(numDescriptors), "descriptors found."
print str(numCompounds), "compounds found."


#write x file header
outfile.write(str(numCompounds) + "\t" + str(numDescriptors) + "\n")

infile.close()
infile = open(sys.argv[1], 'r') #reopen input file, this time for processing
line = infile.readline() #skip descriptor names, already know those

#print the descriptor names to x
for i in range(len(descriptorNames)):
	if i != LINENUM_POS and \
	i != ERRORCODE_POS and \
	i != COMPOUNDID_POS:
		outfile.write(descriptorNames[i] + "\t")
outfile.write("\n")

# Read in the file line by line and convert to X. 
lineNum = 1
xFileIndex = 1
while True:
	line = infile.readline().rstrip()
	if not line:
		break
	descriptorValues = re.split(",", line)
	if not lineNum in errorCompounds:
		#Values are OK. Print them.
		outfile.write(str(xFileIndex) + "\t")
		outfile.write(descriptorValues[COMPOUNDID_POS] + "\t")
		for j in range(len(descriptorValues)):
			if j != LINENUM_POS and \
			j != ERRORCODE_POS and \
			j != COMPOUNDID_POS:
				outfile.write(descriptorValues[j] + "\t")
		outfile.write("\n")
		xFileIndex += 1
	lineNum += 1
print "\nFinished writing file: " + outfileName
outfile.close()
