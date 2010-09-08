<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">

<p id="ACT"><b>.act files</b></p>

<p>The .act files store activities of each compound in some assay. They must have a corresponding .sdf file 
containing the same compound names. The first line of an activity file may contain a header (optional). </p>

<p>Here is an example of an activity file without any header information: </p>

<pre>
Bulbocapnine 5.36
Chlorpromazine 4.1
Clozapine 4.8
fluphenazine 4.95
NNC01-0004 5.85
NNC01-0012 4.69
</pre>

<p>Here is an example of an activity file with header information: </p>

<pre>
Activities of selected compounds in binding assay
Bulbocapnine	5.36
Chlorpromazine	4.1
Clozapine	4.8
fluphenazine	4.95
NNC01-0004	5.85
NNC01-0012	4.69
</pre>

<p>The chemical identifiers in an activity file may be anything: SMILES strings, chemical names, and index numbers are commonly used. The
only constraint is that the chemical identifiers in your activity file <i>must match</i> the identifiers in your SDF (see below). In
an activity file, each line describes one compound's activity. A space or tab separates the chemical identifier and the activity on each line. </p>

<p id="SDF"><b>.SDF files</b></p>

<p>
(Also referred to as "SD files", since SDF stands for "structure data file".)
SDFs are used to store sets of chemical structures and can be 2D or 3D. 
Here's a sample: </p>

<pre>
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
</pre>

<p>
The first line is the chemical identifier of the first compound. The atom coordinates and bond information come after that. 
There can be many optional fields. These come in an XML-like format (e.g. <MolWeight>128</MolWeight>) A 
compound description ends with $$$$. 
<a href="http://www.epa.gov/NCCT/dsstox/MoreonSDF.html">http://www.epa.gov/NCCT/dsstox/MoreonSDF.html</a>
 has more details on the SDF format.
</p>

<!-- 
We don't need this for now.

<p><b>.x files</b></p>

<p>.x is a file format used by the kNN executables. It is similar to the matrix format accepted by other 
data mining programs such as LibSVM.</p>

<p>
The .x file contains a matrix of compounds and descriptor values, as below:
[LINE 1]: 7 315
The line indicates that a 7 by 315 matrix follows: There are 7 compounds, each with 315 descriptor values.</p>

<p>
[LINE 2]: narecs nvx nedges nrings ncircuits...</p>

<p>The second line contains the names of the descriptors.</p>

<p>[LINE 3]: 1 4254097 0.5 0.609756 0.5625 ...</p>

<p>From the third line on, each line represents one compound. The first value on each line is an index. 
The second value is an ID for the compound. The remaining numbers are the values of the descriptors for 
the compound.
[LINE 4]: 2 4239291 0 0 0.0208333 0.142857 ...</p>


<p>At the end of the file, there may be two additional lines. If a .x file has been normalized, the 
original descriptor values need to be preserved; these lines tell what the range of each descriptor 
was before normalization.

<p>[SECOND TO LAST LINE]: 2 19 2 ...
[LAST LINE]: 4 60 68 ... </p>

<p>The "2" and "4" that begin these two lines indicates that the first descriptor, "narecs", 
originally had a minimum value of 2 and a maximum value of 4 over all compounds in the set.  The 
next two values, 19 and 60, indicate the minimum and maximum values for the second descriptor, "nvx."  
It continues this for all descriptors.
</p>


 -->

<%@include file ="/jsp/help/helpcontents.jsp" %>

</div></td></tr></table>

	<%@include file ="/jsp/main/footer.jsp" %>
</body>
