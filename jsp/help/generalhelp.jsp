<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">
<b>Glossary</b><br /><br />
A list of some common terms used in C-Chembench.

<ul>
<li><u>Activity</u>- A measure of the response a compound produces.</li>
<li><u>Dataset</u> - A set of chemical structures usable for creating Models and Predictions. May include: chemical structures (.SDF files), compound activity assay results (.ACT files), and descriptor information (.X files).</li>
<li><u>Descriptor</u> - A value calculated for a chemical. For example, a descriptor could be a count of the number of carbon atoms a molecule has. There are a number of different types of descriptors used, including MOLCONNZ, Dragon, MACCS, and MOE.</li>
<li><u>Model</u> - A statistical function that models the activity for a specific dataset based on a subset of its descriptors.</li>
<li><u>Predictor</u> - A set of models. Using many models to make a consensus predictor helps to overcome the weaknesses of each individual model.  A predictor is used to predict the likely activity of a set of as yet untested compounds.</li>
<li><u>Prediction</u> - The output of applying a predictor to a dataset.</li>
<li><u>Job</u> - A process that's submitted into C-Chembench's queue.</li>
</ul>
</div></td></tr></table>


<%@include file ="/jsp/main/footer.jsp" %>

</body>
</html>