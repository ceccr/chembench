# Replaces the compound names in an SDF with index numbers (1,2,3...)
# Usage: python renameSdfCompounds.py infile outfile
# Useful in cases where an SDF contains weird names with special characters

import sys
import re

try:             
    infileName = sys.argv[1]
    infile = open(sys.argv[1], 'r')
    outFile = open(sys.argv[2], 'w')
    print 'infile: ', sys.argv[1], "\n"
except:        
    print "Usage: python renameSdfCompounds.py infile outfile\n"                    
    sys.exit(2)


readSoFar = 1

#replace first compound ID
line = infile.readline()
outFile.write(str(readSoFar) + "\n")

#read each compound after that from infile and replace compound ID when writing to outfile
while True:
    line = infile.readline()
    if not line:
        break
    line = line.rstrip()
    if line == "$$$$":
        outFile.write(line + "\n")
        readSoFar += 1
        line = infile.readline() #compound ID line (to be replaced)
        if line.rstrip() != "":
			outFile.write(str(readSoFar) + "\n")
    else:
        outFile.write(line + "\n")
outFile.close()
        