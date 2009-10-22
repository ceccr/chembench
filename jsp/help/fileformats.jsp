<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">

.SDF files
(Also referred to as "SD files", since SDF stands for "structure data file".)
SDFs are used to store sets of chemical structures and can be 2D or 3D. 
They are typically the input files we use. Here's a sample: 


4254097
MOE2005           2D
44 47  0  0  1  0  0  0  0  0999 V2000
  1.3550   -4.8300    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  1.0920   -3.9960    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
  0.4780   -2.3340    0.0000 C   0  0  3  0  0  0  0  0  0  0  0  0
  ...
  1.0240    3.0240    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
  0.5970    4.3590    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  2.1340    2.1230    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
1  2  1  0  0  0  0
2  3  1  0  0  0  0
3  4  1  0  0  0  0
...
41 42  1  0  0  0  0
41 44  1  0  0  0  0
42 43  1  0  0  0  0
M  END
$$$$
4239291
MOE2005           2D
19 21  0  0  0  0  0  0  0  0999 V2000
  0.9640   -5.2910    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  1.3970   -3.9610    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0
  2.7660   -3.6690    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  ...
 -1.4850    5.1300    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
 -2.3080    3.9980    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
  0.4600   -2.9200    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
1  2  1  0  0  0  0
2  3  1  0  0  0  0
2 19  1  0  0  0  0
...
15 16  1  0  0  0  0
16 17  2  0  0  0  0
17 18  1  0  0  0  0
M  END
$$$$

The first line is the ID of the first compound. The atom coordinates and bond information come after that. There can be many optional fields. These come in an XML-like format (e.g. <MolWeight>128</MolWeight>) A compound description ends with $$$$. Officially, an SDF is not allowed to contain lines of over 200 characters. In practice, many do. This can cause programs like MolconnZ to fail ungracefully, so C-Chembench chops off SDF lines past 200 characters.  http://www.epa.gov/NCCT/dsstox/MoreonSDF.html has more details on the SDF format.

Chemical structures can also be represented as mol files or SMILES strings. All file formats contain roughly the same information, and there are many programs in existence that translate between them, such as JChem's "molconvert".  The descriptor generation executables take SDF inputs. The 3D rotatable molecules you see on the site are from mol files.  SMILES strings entered on the Predictions page are converted to SDFs before use.
.x files
.x is a file format used by the kNN executables. It is similar to the matrix format accepted by other data mining programs such as LibSVM.

The .x file contains a matrix of compounds and descriptor values, as below:
[LINE 1]: 7 315
The line indicates that a 7 by 315 matrix follows: There are 7 compounds, each with 315 descriptor values.

[LINE 2]: narecs nvx nedges nrings ncircuits...

The second line contains the names of the descriptors.

[LINE 3]: 1 4254097 0.5 0.609756 0.5625 ...

From the third line on, each line represents one compound. The first value on each line is an index. The second value is an ID for the compound. The remaining numbers are the values of the descriptors for the compound.
[LINE 4]: 2 4239291 0 0 0.0208333 0.142857 ...


At the end of the file, there may be two additional lines. If a .x file has been normalized, the original descriptor values need to be preserved; these lines tell what the range of each descriptor was before normalization.

[SECOND TO LAST LINE]: 2 19 2 ...
[LAST LINE]: 4 60 68 ... 

The "2" and "4" that begin these two lines indicates that the first descriptor, "narecs", originally had a minimum value of 2 and a maximum value of 4 over all compounds in the set.  The next two values, 19 and 60, indicate the minimum and maximum values for the second descriptor, "nvx."  iIt continues this for all descriptors.


.S files
MolconnZ takes in a .sdf file and outputs a .S file. The .S file contains molconnZ descriptors for the compounds from the SDF. The .S file looks like: 
[descriptor names]:
moleculenumber narecs nvx nedges nrings ncircuits nclass nelem ntpaths molweight molname formula
nX0  nX1  nX2  nXp3  nXp4  nXp5  nXp6  nXp7  nXp8  nXp9  nXp10
X0   X1   X2   Xp3   Xp4   Xp5   Xp6   Xp7   Xp8   Xp9   Xp10
... [descriptor values for molecule 1]:
1 3 44 47 4 11 43 5 7518  635.153 4254097 H(43)C(32)N(2)O(9)Cl(1)
44 47 71 92 115 143 165 188 201 224 249
32.559757 20.627108 19.970501 16.373892 12.664577 9.750673 7.042437 5.126579 3.225958 2.186764 1.456898
...[descriptor values for molecule 2]:
2 2 19 21 3 3 17 4 615  262.351 4239291 H(22)C(15)N(2)O(2)
19 21 30 39 49 56 60 62 64 64 62
13.294681 9.185071 8.442608 7.163390 5.869330 4.134155 2.965950 1.912135 1.281447 0.819887 0.555201

The .S file is straightforward to interpret. First, all the descriptors are listed, and then their values are provided for each compound. There are 11 elements on each line of the file. Occasionally, molconnZ will spit out something insane with crazy characters instead of numbers. This will be caught by the current Java code and dealt with - if you write your own code to deal with .S files from molconnZ, you might need to do that too.

.act files
The .act files store activities (numerical results associated with each chemical; these are what we're building a model on or trying to predict). They must have a corresponding .sdf file with them to describe the chemical.  The system validates that there are the same number of compounds in the .sdf file and its associated .act file.

<%@include file ="/jsp/help/helpcontents.jsp" %>

</div></td></tr></table>

	<%@include file ="/jsp/main/footer.jsp" %>
</body>
