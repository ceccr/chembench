<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">

<p align="justify" class="ccbHomeStandard">
<b>Starter Guide</b>

<p>Chembench is a web-based tool that gives easy access to kNN modeling and prediction. Chembench doesn't require any 
programming or scripting knowledge to use.  It's an interface that lets you skip past the fiddly parts of file 
management and running sets of programs, so you can focus on making and applying predictive models.</p>

<p>The first step in using Chembench is to create an account. Point a browser at http://chembench.mml.unc.edu and hit 
"register here" under the login box. When you submit the form, you'll be sent a password by email.</p>

<p>When you get that password, you can use it to log in. When you're logged in, you can get to the other tabs - JOBS, 
DATA MGMT, and so on. Once logged in, you can also change your password from "edit profile" at the top right of the 
screen.</p>

<p>In Chembench, you can create <u>Datasets</u>, <u>Predictors</u>, and <u>Predictions</u>.  
A dataset is created from files that you upload. Typically, you will use a set of compounds (an SDF), along with 
experimental values for those compounds (an ACT file) to make a dataset. Datasets can be created using the DATA MGMT 
tab. There are many benchmark datasets already on Chembench for you to use.</p>

<p>A predictor is a set of models that can be used to make a prediction. You need a modeling dataset (a dataset that 
contains activity values) to make a predictor. You create predictors from the MODELING tab.</p>

<p>A prediction is made when a predictor is applied to a dataset. Predictions are made from the PREDICTION tab. You 
can make several predictions at once on a dataset by selecting more than one predictor.</p>

<p>Whenever you submit a dataset, predictor, or prediction to be created, you will be taken to the JOBS page. From 
there, you can see the progress of the job currently running, the queue of waiting jobs, and all of the datasets, 
predictors, and predictions available. When a dataset job finishes, you can see the results in the dataset section. 
Similarly, finished modeling jobs appear in the predictors section, and finished prediction jobs go to the 
prediction section of the jobs page.</p>

<p>That should be enough information to get you started -- the other help sections give more detail on each part 
of Chembench, explaining the modeling workflows and the different parameters you can set. You can refer back to 
them as needed, and you can contact <a href="mailto:ceccrhelp@listserv.unc.edu">ceccrhelp@listserv.unc.edu</a> 
with questions.</p>
</p>

2. Datasets

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

3. Modeling

<p align="justify" class="ccbHomeStandard">
<b>Select a Dataset box:</b>

<p>There are two types of activity files you can set in your Dataset; these are used with different modeling 
methods, so they are separated. "Continuous" activity values vary over a range, while "Category" datasets 
are discrete numbers (typically 1, 2, 3...). Continuous data is used with regression data mining methods, 
while Category data is used with classification methods. So, you will see different options enabled on the 
Modeling page if you pick a Continuous dataset versus a Category one.</p>

<p>The "View Activity Histogram" button will show a visualization that divides your activities into ten 
equal-length bins to show the distribution of activity in your dataset.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>Set Descriptor Generation Parameters box:</b>

<p>The descriptor type option defines what descriptors will be used to represent your compounds in the modeling 
process.  If you want to use other descriptor types besides what Chembench can generate, you can create a 
dataset with your own descriptors in it from the DATA MGMT page. Descriptor generation and scaling are 
skipped if you supply your own descriptors.</p>

<p>The generated descriptor values can be scaled by range scaling or auto scaling, or they can be left unscaled. 
Range scaling changes the range of each descriptor: it finds the max and min value of the descriptor, 
subtracts the minimum from each of the descriptor values, then divides each by (max-min) to produce values 
between 0 and 1. In Auto scaling, the mean and standard deviation is found for each descriptor. The mean 
is subtracted from the descriptor's values, and the result is divided by the standard deviation. Auto scaling 
may perform better than range scaling in cases where outliers are expected in the descriptor values. There 
is some debate over whether auto scaling or range scaling should be used in QSAR.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>Choose Internal Data Splitting Method box:</b>

<p>The dataset's external validation set has already been defined. The compounds not in the external set, 
referred to as the "modeling set", will be divided into a training set and a test set for the creation of 
each model. For each such internal split, a model will be built on the training set and applied to the test 
set. (At the end of the modeling process, the good models are collected together into a predictor, and the 
predictor is applied to the external validation set.)</p>

<p>The internal train/test splits can be made randomly or by sphere exclusion. Sphere exclusion is a process that 
chooses training set compounds which are close to the test set compounds in the descriptor space. This can 
help modeling by ensuring that each model will be presented with test cases that the model can reasonably 
predict.</p>

<p>The options to force the minimum and maximum activity compounds into training sets are useful in kNN modeling. 
The kNN-produced models will only make predictions within the range of values in their training sets. So, if 
you make a consensus predictor out of many kNN models with randomly chosen training sets, their predictions 
will artificially end up closer to the average of the activity values in the training set. This is clearly 
undesirable.</p>

<p>Sphere exclusion is a controversial procedure; some argue that it produces models with artificial statistics 
that may not imply a good real-life performance. The paper "How not to develop a quantitative structure-activity 
or structure-property relationship (QSAR/QSPR)" discusses this and other controversies in modeling, and is 
recommended reading.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>Choose Model Generation Method box:</b>

<p>At present, kNN is the only data mining option available; more methods will be added in the near future. For 
each train/test split, kNN will generate at least one model. Increasing the "Number of Runs" option will multiply 
the number of generated models. The "descriptors per model" parameter can again multiply the number of generated 
models; if kNN tries to make a model with 5 descriptors, a model with 6 descriptors, and a model with 7 descriptors 
in each case, the total number of models will be tripled.</p>

<p>So, to calculate the number of models that will be generated: (Number of train-test splits) * (Number of Runs) * 
(Number of different values "Descriptors Per Model" can take). This is an important thing to consider before 
starting a modeling run: Too few models will be unlikely to create a good predictor, while too many models can 
take a very long time to generate. Chembench will request permission from an administrator before running a job 
with a very large number of models.</p>

<p>For modeling of continuous datasets, the r^2 and q^2 cutoffs are also important. In initial experimentation, it 
may be useful to lower these cutoffs to 0 while you tune the other parameters. Any models generated that perform 
below your cutoffs will be eliminated. This can result in modeling runs which produce no useful output.</p>

<p>The other kNN parameters are detailed in the "help pages" link from the Model Generation box.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>The Modeling Job:</b>
<p>
Modeling goes through several steps. First, descriptors for the selected dataset are generated and scaled. Second, 
the training and test sets are created. Third, a y-randomized version of each train-test set  is created, where 
the activity values are scrambled; this is set aside for later. Fourth, the kNN modeling procedure is performed 
on the train-test sets, generating models. Fifth, the kNN procedure is run again, this time on the y-randomized 
train-test sets; this creates the y-randomized models. Sixth, the kNN models are bundled into a predictor and 
applied to the external validation set. When the job is finished, it can be viewed by clicking on its name in the 
Predictors section of the jobs page.</p>
</p>

4. Prediction

<p align="justify" class="ccbHomeStandard">
Selecting Predictors:

<p>Start by selecting one or more of the predictors. If you have built many predictors on the same modeling dataset, 
you may want to choose all of them, so that you can compare their predictions. In most cases, however, you will 
only need to use one of the predictors. Clicking the "Select Predictors" button will bring you to dataset selection.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>Dataset Selection:</b>

<p>Choose a dataset from the dropdown list, and specify a similarity cutoff. Compounds in your prediction dataset 
 that are outside the cutoff range will not be predicted by a kNN model. Setting the cutoff higher (e.g. to 5) 
 will result in more predictions, but the predictions will be of lower confidence as they are likely to be 
 outside the models' applicability domain. The cutoff value is measured in standard deviations, so it is nonlinear.</p>
 </p>

<p align="justify" class="ccbHomeStandard">
<b>Single molecule prediction:</b>

<p>A prediction can be made on a single molecule instead of an entire dataset. You can enter in a SMILES string 
directly and hit "Predict". You may also draw a molecule using the Java applet, and click "Get SMILES" to retrieve 
its SMILES string for prediction. Single molecule predictions do not require the running of a job; the predictions 
will be calculated immediately, while you are on the Predictions page.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>The Prediction Job:</b>

<p>In a prediction, the descriptor type of each predictor is examined. These descriptors are then generated for the 
dataset to be predicted. The descriptors are normalized according to the ranges used in the predictor's training 
set. Then, the predictors are each applied to the scaled descriptor sets to create predictions. The results can 
be viewed by clicking on the name of the prediction in the Predictions section of the jobs page. </p>
</p>


<%@include file ="/jsp/main/footer.jsp" %>

</body>
</html>
