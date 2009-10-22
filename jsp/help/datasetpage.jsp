<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">


Datasets

<p align="justify" class="ccbHomeStandard">
<b>Upload Dataset Files box:</b>

<p>You can select one of the four dataset types to create. Most of the time, you will be making a modeling set. If 
you just have a bunch of compounds and no activity values for them, you can create a set that is only for use 
with prediction.</p>

<p>At present, Chembench can generate MolconnZ, Dragon, Moe2D, and MACCS descriptors. If you would like to use 
different descriptors, you can supply them using one of the "With Descriptors" options.</p>

<p>In the special case where you only have descriptors, you'd also use one of the "With Descriptors" options. This 
is used when companies want to create models of their compounds without the risk of putting their trade-secret 
structures on a public website.</p>

<p>The  "Standardize Structures" option is one you will usually want to use. Many chemical structures contain odd 
notations, valences, or resonance structures that are incompatible with descriptor-generation tools.  The 
"Standardize" runs JChem's standardizing software on your dataset, automatically fixing these problems.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>Define External Set box:</b>
<p>
When a modeling dataset is uploaded, an external set is immediately defined for it before any modeling is done. 
That way, the external set will be consistent across every model you build, so you can directly compare the 
models. So, a set of compounds is chosen as the external validation set. </p>

<p>If you choose to randomly define the external set (Random Split), the external compounds will be chosen 
randomly. Selecting the "Use activity binning" option will make a less random selection:  first, the dataset 
will be divided into n bins (where n = number of external compounds), and then one compound from each bin 
will be chosen for the external set. The bins are based on the activity values. </p>

<p>Example: Suppose you had compounds with activity values of (1.2, 1.4, 1.6, 1.8, 2.0, 2.2, 2.4, 2.6, 2.8) 
and used activity binning for 3 external compounds. The 3 bins would be (1.2, 1.4, 1.6), (1.8, 2.0, 2.2), 
(2.4, 2.6, 2.8), and one compound would be chosen from each bin, so your external set might be the compounds 
with activity values of 1.4, 2.2, and 2.4. Binning in this way ensures that the external set will span over 
your activity values. Without activity binning, there is a chance that weird outlier compounds will form 
most of your external set, which could make modeling impossible.</p>

<p>You can also use the Choose Compounds option to define which compounds are in the external set.  (Just type 
in the names of the compounds you want to be external.)</p>

<p>Once a dataset is created, you cannot change its external set. You will need to create another dataset to 
do that.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>Create Dataset box:</b>

<p>Name your dataset something descriptive. Naming all your datasets "test_1", "test_2"... will undoubtedly 
lead to confusion later on. The Reference field is an optional place where you can record any literature 
references associated with the dataset you're uploading. Including a short description is never a bad idea 
either.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>The Dataset Job:</b>

<p>When a Dataset job runs, it does four things. First, it checks the validity of the files you put in: it 
makes sure that the file formats are correct, that there are no repeated compounds, and that the compounds 
in each of the files match. Second, it generates sketches, which are JPG images for each structure. Third, 
if the dataset is a modeling set, the external compounds are defined. Fourth, visualizations are generated 
for the dataset. You can see the compound sketches and visualizations by clicking on the name of the dataset 
when the job has finished. When the dataset job finishes, if it is a modeling dataset, it will be selectable 
from the Modeling and Prediction pages. (Prediction datasets can only be used from the Prediction page.)</p>
</p>

<%@include file ="/jsp/help/helpcontents.jsp" %>

<%@include file ="/jsp/main/footer.jsp" %>

</body>
</html>