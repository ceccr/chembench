<%@include file ="/jsp/help/helpheader.jsp" %>

<div class="outer">
<div class="ccbHomeStandard">

<p align="justify" class="ccbHomeStandard">
<b>Version 1.2.0 (Jan. 16, 2011)</b>

<p>
Updates:<br />
<br />
<u>Datasets</u><br />
- n-Fold external validation datasets may now be created. Using this option creates <i>n</i> different external sets for the
dataset. When modeling is run on the dataset, <i>n</i> predictors will be produced at once, each with a different external set.<br />
<br />
<u>Modeling</u><br />
- Major improvement on speed, space-efficiency, and predictivity of SVM models.<br />
- SVM models now provide detailed output to aid users in finding the best parameter ranges.<br />
- SVM modeling can now be run on LSF, allowing greater parallelism when needed.<br />
<br />
<u>Prediction</u><br />
- Predictions have been made more space-efficient, and will automatically resume if the server is patched while the prediction is running.<br />
<u>General</u><br />
- Thanks to extensive testing by users and lab members, several edge-case bugs have been found and fixed.<br />
- Interface has been tuned in several spots to improve intuitiveness; for example, job names containing spaces may now be submitted.<br />
<br />

<p align="justify" class="ccbHomeStandard">
<b>Version 1.1.0 (Oct. 2, 2010)</b>

<p>
Updates:<br />
<br />
<u>Datasets</u><br />
- Datasets may now be uploaded that include user-created descriptors. The descriptors must be in the 
<a href="help-fileformats#X">X file format</a> we specify. If there is demand, we may add support for other 
formats later. <br />
- Datasets that include descriptors may omit the SDF; hence it is possible to use Chembench for modeling
while keeping compound structures private. <br />
<br />
<u>Modeling</u><br />
- kNN modeling jobs submitted to LSF now use the 'idle' queue instead of the 'month' or 'week' queues. <br />
This allows for larger-scale modeling jobs to be supported and avoids queue slot issues.<br />
- Modeling can now be performed on uploaded descriptors. <br />
- Fixed a bug that occurred when modeling datasets that have no external compounds defined.<br />
<br />
<u>Prediction</u><br />
- Prediction may now be performed on uploaded descriptors.<br />
<br />

<p align="justify" class="ccbHomeStandard">
<b>Version 1.0.0</b>

<p>
Current supported features:<br /> 
 <br />
<u>Dataset Preparation</u> <br />
- User uploads a set of compounds, with activity values (optional) <br />
- Standardizes compounds. Removes fragments, standardizes aromatization, and cleans. <br />
- Generates descriptors (MolconnZ, Dragon, Maccs, MOE, and ISIDA)<br />
- Splits external sets either automatically (activity binning available) or manually based on user input. <br />
- Generates an image for each compound. <br />
- Builds and displays a heatmap. <br />
 <br />
<u>Model Building</u> <br />
- Descriptors can be scaled and can be narrowed based on a correlation cutoff  <br />
- Does training / test splitting either randomly or by sphere exclusion. <br />
- Builds models with kNN (with genetic algorithm or simulated annealing variable selection) <br />
- Builds models with Random Forest <br />
- Displays summary information about created predictors, including plot and r^2 (continuous) or confusion matrix (category) <br />
- 50+ public datasets available for comparing and testing modeling methods <br />
 <br />
<u>Prediction</u> <br />
- Can predict full datasets (submitted job) or single compounds (interactive) <br />
- Can predict using multiple predictors at once <br />
- Six published predictors available: Blood-Brain Barrier, Anticonvulsants, 5HT2B, Antimalarial, T.Pyriformis, and P-Glycoprotein.  <br />
 <br />
<u>Jobs / Job Tracking</u> <br />
- Runs hundreds of simultaneous kNN jobs through LSF. Other jobs run using local processing power; up to 6 jobs can be running at once. <br />
- Reports time estimate for modeling jobs and emails users on completion; all jobs have progress indicators <br />
- Tracks time taken by each job for performance analysis <br />

</p>

<%@include file ="/jsp/help/helpcontents.jsp" %>

</div>
    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
    </div>

</body>
</html>