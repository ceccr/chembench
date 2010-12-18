# Cuts an SDF into multiple chunks of smaller size.
# Usage: python cutSdfIntoChunks.py infile chunkSize
# Useful in cases where an SDF is too big to process 
#(e.g. memory limits or program limitations)

import sys
import re

try:             
    infileName = sys.argv[1]
    infile = open(sys.argv[1], 'r')
    chunkSize = int(sys.argv[2])
    print 'infile: ', sys.argv[1], "\n"
    print 'chunk size (in compounds): ', sys.argv[2], "\n"
except:        
    print "Usage: python cutSdfIntoChunks.py infile chunkSize\n"                    
    sys.exit(2)

#read each compound from infile
#check if the chunk limit has been reached

readSoFar = 0
chunkNumber = 1
infileName = re.sub(".sdf$", "", infileName)
outFile = open(infileName + "_" + str(chunkNumber) + ".sdf", 'w')

while True:
    line = infile.readline()
    if not line:
        break
    line = line.rstrip()
    outFile.write(line + "\n")
    if line == "$$$$":
        readSoFar += 1
    if readSoFar == chunkSize:
        readSoFar = 0
        outFile.close()
        chunkNumber += 1
        outFile = open(infileName + "_" + str(chunkNumber) + ".sdf", 'w')
outFile.close()
        