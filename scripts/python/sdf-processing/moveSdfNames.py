#!/usr/bin/env python

import sys
import string

if len(sys.argv)!=2:
    print "usage: movename.py filename"
    exit(128)

try:
    infile=open(sys.argv[1])
except IOError as e:
    print("({}))".format(e))
    exit(64)

outfile=open('fixed.sdf','w')

currmol=[]
newmol=False
namenext=False

for line in infile.readlines():
    if namenext:
        currmol[0]=line
        namenext=False
    if line.startswith('$$$$'):
        newmol=True
    if line.startswith('>  <CompID>'):
        namenext=True
    currmol.append(line)
    if newmol:
        for outline in currmol:
            outfile.write(outline)
        currmol=[]
        newmol=False
        namenext=False

infile.close()
outfile.close()

