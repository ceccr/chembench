<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ page import="edu.unc.ceccr.global.Constants" %>

<html:html>
<head>
<title>C-CHEMBENCH | C-ChemBench HELP</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script src="javascript/script.js"></script>
</head>
<body onload="selection()">
<table width="749" border="0" align="center" cellpadding="0" cellspacing="0">
<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
</td>
		</span>
	</tr>

	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrindex.jpg">

  <br />  

<p align="justify" class="StandardTextDarkGrayParagraph"><strong>DOCs</strong>  <br />  
    <br />
> <a href="http://ceccr.ibiblio.org/c-chembench/theme/cchembench_userguide.pdf" target="_blank">C-ChemBench User Guide</a>, PDF [1 MB]. <br /> 
> <a href="http://ceccr.ibiblio.org/c-chembench/theme/kNNQSAR.pdf" target="_blank">kNN-QSAR User Guide</a>, PDF [0.3 MB]. <br /> 
  <br />  
  <br /> 
</p>
<p align="justify" class="StandardTextDarkGrayParagraph">
		<strong>Frequently Asked Questions</strong></p>
<blockquote>
  <p><a href="#01">1. Where can I get more information  about the QSAR methodology?</a><br />
    <a href="#02">2. What forms of kNN-QSAR are  supported?</a><br />
    <a href="#03">3. Which accuracy functions are  supported for QSAR Category?</a><br />
    <a href="#04">4. What formats are needed for the  sd and activity files?</a><br />
    <a href="#05">5. How are the values in the model  statistics table computed?</a><br />
    <a href="#06">6. How is the Activity Histogram  generated?</a><br />
    <a href="#07">7. How do the basic kNN parameters  affect the model building?</a><br />
    <a href="#08">8. What are the Advanced kNN  Parameters?</a><br />
    <a href="#09">9. What types of descriptors are  available?</a><br />
    <a href="#10">10. What forms of descriptor  normalization are supported?</a><br />
    <a href="#11">11. How are data sets divided?</a><br />
    <a href="#12">12. What methods of data set  splitting are supported?</a><br />
    <a href="#13">13. How do the sphere exclusion parameters  affect data set splitting?</a><br />
    <a href="#14"><strong>14. How can I reach the C-ChemBench development team?</strong></a></p>
  <p align="justify" class="StandardTextDarkGrayHelpText">&nbsp;</p>
</blockquote>
<p align="justify" class="StandardTextDarkGrayParagraph"><a name="01" id="01"></a></p>
<p align="justify" class="StandardTextDarkGrayHelpText"><strong>1. Where can I get more information about the QSAR methodology?</strong><br />
  <strong><br />
    </strong>For a detailed explanation of the methodology, see the QSAR Manual.<br />
    <br />
    <a name="02" id="02"></a></p>
<p align="justify" class="StandardTextDarkGrayHelpText">
<strong>2. What forms of kNN QSAR are supported?</strong><br />
  <strong><br />
  </strong>We currently support Simulated Annealing kNN QSAR for both continuous and categorical data. <br />
  <br />
  <br />
  <a name="03" id="03"></a><br />
  <strong>3. Which accuracy functions are supported for QSAR Category?</strong><br />
  <strong><br />
  </strong>Currently we allow the use of four different formulae for accuracy.  The default is Correct Classification Rate (CCR) for both optimization and selection of models.   The formula to be used is chosen under the kNN Advanced Parameters tab in Model Building.  The four supported formulae are 
<table width="400" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="225">  <br /><p align="justify" class="StandardTextDarkGrayHelpText">Standard  Accuracy</p></td>
    <td width="175"><img src="theme/img/faq01.gif" width="175" height="45" /></td>
  </tr>
  <tr>
    <td><p align="justify" class="StandardTextDarkGrayHelpText">Correct  Classification Rate</p></td>
    <td><img src="theme/img/faq02.gif" width="175" height="45" /></td>
  </tr>
  <tr>
    <td><p align="justify" class="StandardTextDarkGrayHelpText">Name  1</p></td>
    <td><img src="theme/img/faq03.gif" width="175" height="45" /></td>
  </tr>
  <tr>
    <td><p align="justify" class="StandardTextDarkGrayHelpText">Name  2</p></td>
    <td><img src="theme/img/faq04.gif" width="175" height="45" /></td>
  </tr>
</table>
  <br />
  <p align="justify" class="StandardTextDarkGrayHelpText">For  more information about CCR, see for example “Combinatorial QSAR Modeling of  P-Glycoprotein Substrates” by Lima et al. In  general, the first two formulae are more appropriate for nominal data and the  last two, for ordinal data.</p>

    <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="04" id="04"></a><br />
      4.  What formats are needed for the SD and activity files?</strong><br />
    <strong><br />
    </strong>The SD file is a standard chemical structure storage format as described on the  <a href="http://www.mdli.com/downloads/public/ctfile/ctfile.jsp" target="_blank">Symyx MDL web site</a>. The  activity files can either be two column (space or tab separated) text files or  Excel spreadsheets. In either case,  there should be a header in the file and the two columns should be a compound  ID and the activity value. </p>
    <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="05" id="05"></a><br />
      5.  How are the values in the model statistics table computed?</strong><br />
      <br />
      <strong> </strong>For QSAR continuous, the values are: <br />
    <em>nnn</em>: the  number of nearest neighbors used in this model.<br />
    <em>q<sup>2</sup></em>: The leave-one-out (LOO) cross-validated correlation  coefficient R<sup>2</sup> (q<sup>2</sup>) for the training set: </p>
  <p><img src="theme/img/faq05.gif" width="160" height="84" class="StandardTextDarkGrayHelpText" /><br />
      <br />
        <p align="justify" class="StandardTextDarkGrayHelpText">where  y<sub>i</sub><sup>pred</sup>, y<sub>i</sub><sup>obs</sup> and <img src="theme/img/faq0y.gif" /> are predicted, observed and average activities of the  i-th compound of the training set.<br />
      <em>n</em>: number of test set compounds  predicted by the model.<br />
      <em>r</em>: the measure of the correlation  between the observed and predicted activity values.  <br />
      The value is computed using Pearson  correlation coefficient.  Specifically,</p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><img src="theme/img/faq06.gif" width="203" height="53" /></p>
  <p align="justify" class="StandardTextDarkGrayHelpText">where y<sub>i</sub> and <img src="theme/img/faq0y2.gif" /><sub>i</sub> are observed  and predicted activities respectively. We display both r and r<sup>2</sup> as a convenience to the user.  </p>
      <p align="justify" class="StandardTextDarkGrayHelpText"><em>r</em><sup>2</sup></em>: correlation between the observed  and predicted activity values using the Pearson correlation coefficient.
      <br />
      <em>R<sub>01</sub><sup>2</sup></em>: the  coefficients of determination for regressions through the origin between  predicted and observed activities. Specifically,</br>
  <p align="justify" class="StandardTextDarkGrayHelpText"><img src="theme/img/faq07.gif" width="160" height="49" /></p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><em>R<sub>02</sub><sup>2</sup></em>: the  coefficients of determination for regressions through the origin between  observed and predicted activities. Specifically,</p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><img src="theme/img/faq08.gif" width="160" height="46" /></p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><em>k<sub>1</sub></em>: The slope of the 0-origin line  fitted for predicted vs. observed  activities. Specifically,</p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><img src="theme/img/faq09.gif" width="91" height="45" /></p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><em>k<sub>2</sub> </em>: The slope of the 0-origin line  fitted for observed vs. predicted activities.  Specifically, <br />
  </p>
  <p class="StandardTextDarkGrayHelpText"><img src="theme/img/faq10.gif" width="91" height="44" /></p><br />
  <p align="justify" class="StandardTextDarkGrayHelpText">For  QSAR category, the following additional values</p>
  <a name="06" id="06"></a><br />
    <p align="justify" class="StandardTextDarkGrayHelpText"><strong>6.  How is the Activity Histogram generated?</strong><br />
      <strong><br />
      </strong>The range of activity values is always divided into 10 bins.</p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="07" id="07"></a><br />
    7.  How do the basic kNN parameters affect the model building?</strong><br />
    <br />
    The kNN modeling technique identifies descriptor subspaces in which similar  compounds have similar response variables. The QSAR modeling techniques  explores subspaces of different dimensions as defined by the descriptor  minimum, maximum, and step size. For  example, the default settings of minimum 5, maximum 20, and step size 5 will  explore subspaces of dimensions 5, 10, 15 and 20.<br />
    Because  the process is stochastic in nature, different runs may optimize to different  models. In order to fully explore each  subspace, we generate multiple models. The number of runs identifies how many models are generated.</p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><a name="08" id="08"></a><br />
    <strong>8. What  are the Advanced kNN Parameters?</strong><br />
    <strong><br />
    </strong>This  set of parameters is intended for the user who is fully familiar with the kNN  QSAR model development procedure. For  those not familiar with it, we recommend leaving them as is. For those  who are familiar with the process, the following details these parameters.<br />
    <br />
     Number of  nearest neighbors:  During model  development, this is the maximum number of neighbors used in the kNN pattern  recognition.  For each set of descriptors  selected throughout the process, k will be optimized between 1 and this number. <br />
     <br />
     Percentage  for pseudo neighbors:  In order to reduce  the computational time of model building, we do not compute the distance to  every other compound in the data set.   Rather we use the technique of pseudo-neighbors that is described in  (), where a subset of the compounds is used as the potential nearest  neighbors.  This number represents the  percentage of the data set to be used.   Note that the default setting of 100 directs that the full set be used.<br />
     <br />
     Number of  Permutations:  Each time that a model is  built, a simulated annealing process is used to optimize the descriptors  selected for the model.  This number  determines how many descriptors are replaced in each cycle of the simulated  annealing process. <br />
     <br />
     Number of  Cycles:  This is the maximum number of  cycles that will be run prior to a temperature reduction in the simulated  annealing process.  (Temperature  reductions can also be made based on finding a better set of selected  descriptors based on q2.)<br />
     <br />
    Log  Initial Temperature: The temperature at which the simulated annealing process  is initialized in log10 units.<br />
    <br />
    Log Final  Temperature: The temperature below which the simulated annealing process is  terminated in log10 units.  The final  temperature must be lower than the initial temperature.  Note that the number of different temperatures  in the simulated annealing process is a significant contributor to  computational time required.  <br />
    <br />
    Mu: The factor by which you reduce the  temperature.  Specifically, the new  temperature is computed by multiplying the old temperature by mu.<br />
    <br />
    Minimum q2  (Continuous only):  The minimum q2  of an acceptable model.  Only acceptable models are displayed and used  in consensus prediction of external sets.   This will be able to be altered in the model analysis phase (coming  soon).  See the description of the model  statistics for more information on q2.<br />
    <br />
    Minimum r2  (Continuous only):    The minimum r2  of an acceptable model.  Only acceptable models are displayed and used  in consensus prediction of external sets.   This will be able to be altered in the model analysis phase (coming  soon). See the description of the model statistics for more information on r2.<br />
    <br />
    Minimum  slope and maximum slope (Continuous only):   The cutoffs for the acceptable slope, k1, of the 0-origin line fitted  for predicted vs. observed and slope k2 of the 0-origin line fitted for observed  vs. predicted.  If both k1 and k2 fall  outside this range, the model is rejected.  <br />
    <br />
    Relative_diff_R_R0  (Continuous only):  A measure of the  quality of the predictive power of the model.   Models with a value above this are considered unreliable and are  rejected.  The value is computed as  follows:  <br />
    <br />
    Min(ABS  (1 – r201/r2),  ABS (1 – r202/r2))<br />
    <br />
    Diff_R01_R02  (Continuous only):  Also a measure of the  predictive power of the model.  Models  with a value above this are considered unreliable and are rejected.  The value is computed as follows:  
    <br />
    <br />
    ABS  (r201 – r202)<br />
    <br />
    Minimum  Accuracy for Training Set (Category only): The minimum accuracy of an  acceptable model as calculated by the selected accuracy function from the KNN  basic parameters.  Models that have an  accuracy less than this value for LOO prediction of the training set are  rejected.<br />
    <br />
    Minimum  Accuracy for Test Set (Category only): The minimum accuracy of an acceptable  model as calculated by the selected accuracy function from the KNN basic  parameters.  Models that have an accuracy  less than this value for prediction of the test set are rejected.<br />
    <br />
    Applicability  Domain Cutoff:  Each predictive model is  essentially a subspace of selected descriptors in which similar compounds  display similar activity.  Within this  subspace, we calculate the average distance (and standard deviation) from each  compound to its k nearest neighbors.   Only compounds that have a distance within some number of standard  deviations of that average will be predicted.   This cutoff defines that number of standard deviations.   As the cutoff increases, prediction coverage  increases while accuracy of prediction may drop.  The default value allows prediction of  compounds with a total distance within 1 standard deviation of the average seen  in the training set. </p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="09" id="09"></a><br />
    9. What  types of descriptors are available?</strong><br />
    <strong><br />
    </strong>Currently, we only generate MolConn-Z descriptors.  For more information about these descriptors,  see the <a href="http://www.edusoft-lc.com/molconn/" target="_top">eduSoft</a> website..</p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="10" id="10"></a><br />
    10. What  forms of descriptor normalization are supported?</strong><br />
        <strong><br />
        </strong>Currently, we only support range scaling.   Range scaling scales the descriptor values based on the minimum and  maximum values actually seen in the data set -- normalizing them from 0 to 1.</p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="11" id="11"></a><br />
    11. How are  data sets divided?</strong><br />
    <br />
    Data  sets are divided into three groups -- the training set, the test set, and the  external validation set – in order to provide robust and predictive  models.  This division is required but  the user has the capability to control the size of external test set and the  ability to choose how the splitting is done.</p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="12" id="12"></a><br />
    12. What  methods of data set splitting are supported?</strong><br />
    <strong><br />
    </strong>At this  time, we only support the sphere exclusion method of rational data set  division.  <br />
    For details on this  methodology, see <a href="http://www.springerlink.com/content/j171577n21753511/" target="_blank">J Comput Aided Mol Des. 2003  Feb-Apr;17(2-4):241-53</a>.  </p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="13" id="13"></a><br />
    13. How do  the sphere exclusion parameters affect data set splitting?</strong><br />
        <strong><br />
        </strong>Sphere  exclusion is a structured methodology for dividing the data set into training  and test sets.  It does this by selecting  a compound and selecting each neighbor with a certain radius as a member of  either the training or test set.  It  eliminates all selected compounds and repeats this process until all compounds  have been assigned to one of the two sets.<br />
        <br />
        Number of  Sphere Radii:  Allows the user to create  multiple training and test sets to be used in model building by defining the  number of different radii to be used.   The value of the different radii is determined by Rmin+i*(Rmax-Rmin)/(4*N)  where Rmin is the minimum distance between  two points in the dataset, Rmax is the maximum distance between two points in  the dataset.<br />
        <br />
        Number of  Starting Points:  The process can begin  with either one or two selected points.   When two points are used, they are the compounds with the minimum and  maximum activity values.  If one point is  selected, it is <br />
        <br />
        Selection  of Next Training Set Point is Based on:   When the assigned compounds are removed from the data set, another point  needs to be selected.  This parameter  determines how that next point is chose:   randomly or as close or as far as possible from the previous sphere  center. </p>
  <p align="justify" class="StandardTextDarkGrayHelpText"><strong><a name="14" id="132"></a><br />
14. How can I reach the C-ChemBench Development Team?</strong><br />
<strong><br />
</strong>The C-ChemBench Development team can be reached at ceccrhelp (at) listserv.unc.edu.</p>
  <br />  
  <br />  

		</p>  </td>
		
</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
