#Changes the CSV output from Java into SQL insert statements
#useful for porting databases

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
