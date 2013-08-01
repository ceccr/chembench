<%@include file ="/jsp/help/helpheader.jsp" %>

<div class = "outer">
<div class="ccbHomeStandard">

<b>Datasets</b>

<p><img src="theme/img/dataset-workflow.png"></p>
<p> The above diagram describes the dataset creation process. 
You begin by uploading a set of compounds, including any activities you want to model. 
The data you upload will be prepared for use with Chembench's modeling and prediction processes.
You will need to enter the options in the boxes on the Dataset page as described below. </p>

<p align="justify" class="ccbHomeStandard">
<b>Upload Dataset Files box:</b>

<p>You can select one of the four dataset types to create. Most of the time, you will be making a modeling set. If 
you just have a bunch of compounds and no activity values for them, you can create a set that is only for use 
with prediction.</p>

<p>At present, Chembench can generate MolconnZ, Dragon, Moe2D, MACCS, and ISIDA descriptors. If you would like to use 
your own descriptors, you can supply them using one of the "With Descriptors" options.</p>

<p>The "With Descriptors" options are useful when companies want to create models of their compounds 
without putting their structures on a public website. The "With Descriptors" options are also useful 
to researchers exploring new methods for descriptor generation. You may scale your descriptors 
before uploading or allow Chembench to scale them for you. See the descriptor generation parameters 
section of the <a href="help-modeling">Modeling help page</a> for more details. 
When you upload descriptors for prediction, Chembench assumes that they have been scaled in the 
same way as your modeling descriptors.</p>

<p>The "Standardize Structures" option is one you will usually want to use. Many chemical structures contain odd 
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
most of your external set, which could make it impossible to a model with high prediction accuracy on the 
external set.</p>

<p>You can also use the Choose Compounds option to define which compounds are in the external set.  (Just type 
in the names of the compounds you want to be external.)</p>

<p>The n-Fold Split option defines multiple external sets for a dataset. The default number of folds is 5. For
a 5-fold split, 5 external sets will be defined. Each external set will contain 1/5 of the compounds in the dataset,
and the external sets will not overlap. Then when the dataset is used in modeling, 5 predictors will be created, one
with each external set. This is a very useful option for testing the accuracy of many different modeling options.
</p>

<p>Once a dataset is created, you cannot change its external set. The dataset's external set is kept consistent
so that results of different modeling procedures on the same dataset can be compared. You will need to create new 
dataset if you need the data to have a different external set.</p>
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
in each of the files match. If you have chosen to standardize your compounds, standardization is performed.
Second, if the dataset is a modeling set, the external compounds are defined. Third, descriptors are generated 
for the dataset. Fourth, visualizations and JPG images for each structure are created. 
You can see the compound sketches and visualizations by clicking on the name of the dataset 
when the job has finished. When the dataset job finishes, if it is a modeling dataset, it will be selectable 
from the Modeling and Prediction pages. (Prediction datasets can only be used from the Prediction page.)</p>
</p>


<%@include file ="/jsp/help/helpcontents.jsp" %>

</div>

    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
</div>
</body>
</html>