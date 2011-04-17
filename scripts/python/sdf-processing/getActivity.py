# !/usr/bin/env python
# gets activity from the <ACTIVITY> tag in sdf and outputs it to act file
# 

import sys
import string

if len(sys.argv)!=3:
	print "usage: movename.py filename.sdf outfile.act"
	exit(128)

try:
	infile=open(sys.argv[1])
	outfile=open(sys.argv[2],'w')
except IOError as e:
	print("({}))".format(e))
	exit(64)

actline = ""
newmol=False
activitynext=False
namenext=True

for line in infile.readlines():
	if activitynext:
		actline += line
		activitynext=False
	if namenext:
		line = line.replace(" ", "_")
		actline += line.rstrip() + " "
		namenext=False
	if line.startswith('$$$$'):
		namenext=True
	if line.startswith('> <ACTIVITY>'):
		activitynext=True
	if namenext:
		outfile.write(actline)
		actline=""
		activitynext=False
	print(line.rstrip())

infile.close()
outfile.close()
