<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">

<p align="justify" class="ccbHomeStandard">
<b>Overview</b>

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

<%@include file ="/jsp/help/helpcontents.jsp" %>

</div></td></tr></table>

<%@include file ="/jsp/main/footer.jsp" %>

</body>
</html>
