# Scans through an SDF
# If any compounds share the same name, remove all but one of them
# Usage: python removeSdfDuplicates.py infile outfile
# NOTE: This does not detect *structural* duplicates; just compounds that
# are named the same. It's not a sophisticated tool like JChem Standardizer, 
# it's just a simple script.

import sys


try:             
    infile = open(sys.argv[1], 'r')
    outfile = open(sys.argv[2], 'w')
    print 'infile: ', sys.argv[1], "\n"
    print 'outfile: ', sys.argv[2], "\n"
except:        
    print "Usage: python removeSdfDuplicates.py infile outfile\n"                    
    sys.exit(2)

#read each compound from infile
#check if the compound name has been used already
#if no, print compound to outfile; if yes, skip compound.
#close infile and outfile

usedCompoundNames = []

isCompoundName = True
while 1:
    line = infile.readline()
    if not line:
        break
    line = line.rstrip()
    if line != "":
        if line in usedCompoundNames:
            print "duplicate name - skipping compound ", line, "\n"
            #skip compound
            while line != "$$$$":
                line = infile.readline().rstrip()
        else:
            #print "new compound name", line, "\n"
            outfile.write(line + "\n")
            #write compound to outfile
            usedCompoundNames.append(line)
            while line != "$$$$":
                line = infile.readline().rstrip()
                outfile.write(line + "\n")

outfile.close()
    