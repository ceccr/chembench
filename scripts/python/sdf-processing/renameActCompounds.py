# Replaces the compound names in an ACT with index numbers (1,2,3...)
# Usage: python renameActCompounds.py infile outfile
# Useful in cases where an ACT contains weird names with special characters

import sys
import re

try:             
    infileName = sys.argv[1]
    infile = open(sys.argv[1], 'r')
    outFile = open(sys.argv[2], 'w')
    print 'infile: ', sys.argv[1], "\n"
except:        
    print "Usage: python renameActCompounds.py infile outfile\n"                    
    sys.exit(2)


compoundNum = 1

#read each compound after that from infile and replace compound ID when writing to outfile
while True:
    line = infile.readline()
    if not line:
        break
    line = line.rstrip()
    idAndValue = line.split()
    outFile.write(str(compoundNum) + " " + idAndValue[1] + "\n")
    compoundNum += 1
outFile.close()
        