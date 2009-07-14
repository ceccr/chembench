<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>

<script language="javascript" src="javascript/modeling.js"></script>

<html:form action="/submitQsarWorkflow.do" enctype="multipart/form-data">
			 <!-- kNN Parameters -->
			 <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<b>Set kNN Parameters</b>
					</p>
					</td>
				</tr>	
				<tr><td><table>
				
				<!-- kNN, Basic parameters  -->
				<tr><td colspan="2"><div class="StandardTextDarkGrayParagraph"><i>For information on what these parameters do, refer to the <u><a href="#help" onclick="window.open('/help.do'); return true;" >help pages</a></u>.<br /></i></div></td>
				</tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Descriptor Step Size:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon01" property="stepSize" size="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Number of Descriptors:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon02" property="minNumDescriptors" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Maximum Number of Descriptors:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon03" property="maxNumDescriptors" size="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of Runs:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon11" property="numRuns" size="5" value="5"/></td></tr>	
				
				<!-- kNN, Advanced Parameters -->
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Max. Number of Nearest Neighbors:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon06" property="nearest_Neighbors" size="5" /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Percentage of Pseudo Neighbors:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon07" property="pseudo_Neighbors" size="5" /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of Permutations:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon04" property="numMutations" size="5" /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of Cycles:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon05" property="numCycles" size="5" /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Log Initial Temperature:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon08" property="t1" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Log Final Temperature:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon09" property="t2" size="5" /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Mu:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon10" property="mu" size="5" /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Applicability Domain Cutoff:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon19" property="cutoff" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Stop Condition:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon20" property="stop_cond" size="5"/></td></tr>	
				<!-- Everything up to this point is used by both Continuous and Category kNN. -->
			 
				<!-- The following parameters are JUST for continuous kNN. -->
				<tr><td><div class="StandardTextDarkGrayParagraph"><b><u><br />Continuous kNN Parameters:</u></b></div></td>
				<td><br /><br /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum q<sup>2</sup>:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon13" property="minAccTraining" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum r<sup>2</sup>:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon14" property="minAccTest" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Slope:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon15" property="minSlopes" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Maximum Slope:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon16" property="maxSlopes" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Relative_diff_R_R0:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon17" property="relativeDiffRR0" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Diff_R01_R02:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCon18" property="diffR01R02" size="5"/></td></tr>	
				<!-- End continuous kNN parameters -->
				
				<!-- The parameters below are specific to Category kNN -->
				<tr><td><div class="StandardTextDarkGrayParagraph"><b><u><br />Category kNN Parameters:</u></b></div></td>
				<td><br /><br /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Accuracy for Training Set:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCat13" styleId="knnCat13" disabled="true" property="minAccTraining" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Accuracy for Test Set:</b></div></td>
				<td align="left" valign="top"><html:text styleId="knnCat14" styleId="knnCat14" disabled="true" property="minAccTest" size="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Optimization Method:</b></div></td>
				<td>			
				<html:radio value="1" property="knnCategoryOptimization" disabled="true" styleId="knnCatOpt01"><img src="/theme/img/formula01.gif" /></html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="2" property="knnCategoryOptimization" disabled="true" styleId="knnCatOpt02"><img src="/theme/img/formula02.gif" /></html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="3" property="knnCategoryOptimization" disabled="true" styleId="knnCatOpt03"><img src="/theme/img/formula03.gif" /></html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="4" property="knnCategoryOptimization" disabled="true" styleId="knnCatOpt04"><img src="/theme/img/formula04.gif" /></html:radio>
				</td></tr>
				<!-- End Category Specific kNN Parameters  -->
				
				</table><br /></td></tr>
				</tbody>
			</table>
</html:form>
<br />		