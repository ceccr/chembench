<%@include file ="/jsp/help/helpheader.jsp" %>

<div class = "outer">
<div class="ccbHomeStandard">

<p>Datasets uploaded to Chembench are expected to contain these types of files.</p>

<br />
<p id="ACT"><b>.act files</b></p>

<p>The .act files store activities of each compound from some assay. An activity file is necessary for 
building predictive models on Chembench. Each line of an activity file is a chemical identifier and an 
activity value. Activity files may contain continuous or category data.</p>

<p><a href="jsp/help/samples/continuousActFileSample.act">Continuous activity file sample</a><br />
<a href="jsp/help/samples/categoryActFileSample.act">Category activity file sample</a>

<p>Continuous activity data can be any decimal number. Typically continuous data comes from
quantitative assays, e.g., of binding affinity.</p>
<pre>
compound1 2.48
compound2 4.89
compound3 7.22
compound4 9.73
compound5 12.19
compound6 14.55
compound7 17.34
</pre>

<p>Category activity data represents endpoints or is discretized from continuous data. Category
activities are typically non-negative consecutive integers (e.g. 0, 1, 2).</p>
<pre>
compound1 0
compound2 0
compound3 1
compound4 1
compound5 1
compound6 2
compound7 2
</pre>

<p>The chemical identifiers in an activity file may be anything: SMILES strings, chemical names, and index numbers are commonly used. The
only constraint is that the chemical identifiers in your activity file <i>must match</i> the identifiers in SDF and X files uploaded in
the same dataset and be in the same order.</p>

<br />
<p id="SDF"><b>.SDF files</b></p>

<p>(Also referred to as "SD files", since SDF stands for "structure data file".)
SDFs are used to store sets of chemical structures and can be 2D or 3D.</p>

<a href="jsp/help/samples/sdfFileSample.sdf">SDF file sample</a>

<pre>
compound1
comment line (can be anything)

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
</pre>

<p>
An SDF contains the structures of a set of compounds; each compound ends with "$$$$". 
<a href="http://www.epa.gov/NCCT/dsstox/MoreonSDF.html">http://www.epa.gov/NCCT/dsstox/MoreonSDF.html</a>
 has more details on the SDF format.
</p>

<br />
<p id="X"><b>.X files</b></p>

<p>.x is a descriptor file format used by Chembench. It is similar to the matrix format accepted by other 
data mining programs. All descriptor values must be numeric.</p>

<p><a href="jsp/help/samples/descriptorsFileSample.x">X File Sample</a> (open with a text editor)</p>

<p>
The .x file contains a matrix of compounds and descriptor values. The format is described below.<br />
<pre>[LINE 1]: 120 50</pre><br />
This header line indicates that a 120 by 50 matrix follows: There are 120 compounds, each with 50 descriptor values.</p>

<p>
<pre>[LINE 2]: descriptor1 descriptor2 descriptor3...</pre>
</p>

<p>The second line contains the names of the descriptors.</p>

<p>
<pre>
[LINE 3]: 1 compound1 0.5 0.609756 0.5625 ...
[LINE 4]: 2 compound2 0 0 0.0208333 0.142857 ...
[LINE 5]: 3 compound3 0 0 0.0208333 0.142857 ...
</pre>
</p>

<p>From the third line on, each line represents one compound. The first value on each line is an index, starting at 1. 
The second value is an ID for the compound that matches with the IDs in the corresponding SDF and ACT files. 
The remaining numbers are the values of the descriptors for the compound.
</p>

<%@include file ="/jsp/help/helpcontents.jsp" %>

</div>
    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
    </div>
</body>
