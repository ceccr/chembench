<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">


<p align="justify" class="ccbHomeStandard">
<b>Version 1.0</b>

<p>
Current supported features:<br />
Dataset Preparation <br />
- User uploads a set of compounds, with activity values (optional) <br />
- Standardizes compounds. Removes fragments, standardizes aromatization, and cleans. <br />
- Generates descriptors (MolconnZ, Dragon, Maccs, and MOE)<br />
- Splits external sets either automatically (activity binning available) or manually based on user input. <br />
- Generates an image for each compound. <br />
- Builds and displays a heatmap. <br />
 <br />
Model Building
- Descriptors can be scaled and can be narrowed based on a correlation cutoff  <br />
- Does training / test splitting either randomly or by sphere exclusion. <br />
- Builds models with kNN (with genetic algorithm or simulated annealing variable selection) <br />
- Builds models with Random Forest <br />
- Displays summary information about created predictors, including plot and r^2 (continuous) or confusion matrix (category) <br />
- 50+ public datasets available for comparing and testing modeling methods <br />
 <br />
Prediction <br />
- Can predict full datasets (submitted job) or single compounds (interactive) <br />
- Can predict using multiple predictors at once <br />
- Six published predictors available: Blood-Brain Barrier, Anticonvulsants, 5HT2B, Antimalarial, T.Pyriformis, and P-Glycoprotein.  <br />
 <br />
Jobs / Job Tracking <br />
- Runs hundreds of simultaneous kNN jobs through LSF. Other jobs run using local processing power; up to 6 jobs can be running at once. <br />
- Reports time estimate for modeling jobs and emails users on completion; all jobs have progress indicators <br />
- Tracks time taken by each job for performance analysis <br />

</p>

<%@include file ="/jsp/help/helpcontents.jsp" %>

</div></td></tr></table>

<%@include file ="/jsp/main/footer.jsp" %>

</body>
</html>