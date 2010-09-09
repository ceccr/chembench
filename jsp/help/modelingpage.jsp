<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="900" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">

<b>Modeling</b>

<p><img src="/theme/img/modeling-workflow.png"></p>

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
dataset with your own descriptors in it from the DATASET page. Descriptor generation and scaling are 
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

<p>Modeling goes through several steps. First, descriptors for the selected dataset are scaled. Second, 
the training and test sets are created. Third, a y-randomized version of each train-test set  is created, where 
the activity values are scrambled; this is set aside for later. Fourth, the kNN modeling procedure is performed 
on the train-test sets, generating models. Fifth, the kNN procedure is run again, this time on the y-randomized 
train-test sets; this creates the y-randomized models. Sixth, the kNN models are bundled into a predictor and 
applied to the external validation set. When the job is finished, it can be viewed by clicking on its name in the 
Predictors section of the My Bench page.</p>
</p>

<%@include file ="/jsp/help/helpcontents.jsp" %>

</div></td></tr></table>

<%@include file ="/jsp/main/footer.jsp" %>

</body>
</html>