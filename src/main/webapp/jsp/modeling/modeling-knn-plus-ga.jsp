<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" import="java.util.*" %>


<script language="javascript" src="javascript/modeling.js"></script>
<s:div>
	<!-- kNN Parameters -->
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>	
		<tr>
			<td width="100%" height="24" align="left" colspan="2">
			<br />
			<p class="StandardTextDarkGrayParagraph2">
			<b>Set Parameters for k-Nearest Neighbors with Genetic Algorithm Descriptor Selection</b>
			</p>
			</td>
		</tr>	
		<tr><td><table width="100%">

		<!-- knn+ parameters  -->
		
			<!-- overall parameters -->
			<tr><td colspan="2"><div class="StandardTextDarkGrayParagraph"><i>For information on what these parameters do, refer to the <u><a href="help-faq" onclick="window.open('/help-faq'); return true;" >help pages</a></u>.<br /></i></div></td>
			</tr>
			<tr><td width="33%"><div class="StandardTextDarkGrayParagraph"><b>Descriptors Per Model:</b></div></td>
			<td align="left" valign="top"><div class="StandardTextDarkGrayParagraphNoIndent">Minimum: <s:textfield id="knnMinNumDescriptors" name="knnMinNumDescriptors" size="5" theme="simple"/> Maximum: <s:textfield id="knnMaxNumDescriptors" name="knnMaxNumDescriptors" size="5" theme="simple"/></div></td></tr>
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Min. Nearest Neighbors:</b></div></td>
			<td align="left" valign="top"><s:textfield id="knnMinNearestNeighbors" name="knnMinNearestNeighbors" size="5" theme="simple"/></td></tr>		
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Max. Nearest Neighbors:</b></div></td>
			<td align="left" valign="top"><s:textfield id="knnMaxNearestNeighbors" name="knnMaxNearestNeighbors" size="5" theme="simple"/></td></tr>		
			<!-- end overall parameters -->
			
			<!-- Genetic Algorithm Parameters (basic) -->
            <s:if test="user.showAdvancedKnnModeling=='YES'">
			<tr><td><div class="StandardTextDarkGrayParagraph"><b><u><br />Genetic Algorithm Parameters:</u></b></div></td>
			<td><br /><br /></td></tr>	
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Population Size:</b></div></td>
			<td align="left" valign="top"><s:textfield id="gaPopulationSize" name="gaPopulationSize" size="5" theme="simple"/></td></tr>
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Maximum Number of Generations:</b></div></td>
			<td align="left" valign="top"><s:textfield id="gaMaxNumGenerations" name="gaMaxNumGenerations" size="5" theme="simple"/></td></tr>
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Stop if Stable For This Many Generations:</b></div></td>
			<td align="left" valign="top"><s:textfield id="gaNumStableGenerations" name="gaNumStableGenerations" size="5" theme="simple"/></td></tr>
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Group Size for Tournament Selection:</b></div></td>
			<td align="left" valign="top"><s:textfield id="gaTournamentGroupSize" name="gaTournamentGroupSize" size="5" theme="simple"/></td></tr>
			<!-- end Genetic Algorithm Parameters (basic) -->
			
			<!-- Genetic Algorithm Parameters (advanced) -->
                <tr><td><div class="StandardTextDarkGrayParagraph"><b><u><br />Genetic Algorithm Parameters (Advanced):</u></b></div></td>
                <td><br /><br /></td></tr>	
                <tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Fitness Difference To Proceed (in log<sub>10</sub> units):</b></div></td>
                <td align="left" valign="top"><s:textfield id="gaMinFitnessDifference" name="gaMinFitnessDifference" size="5" theme="simple"/></td></tr>
            </s:if>
            <s:else>
                <tr><td class="notice" colspan="2">
                    (Some advanced kNN settings have been hidden. To enable these, please change your settings by 
                    <a href="/editProfile">editing your profile</a>.)
                </td></tr>
            </s:else>
			<!-- end Genetic Algorithm Parameters (advanced) -->
			
			<!-- Model Acceptance Parameters -->
			<tr><td><div class="StandardTextDarkGrayParagraph"><b><u><br />Model Acceptance Parameters:</u></b></div></td>
			<td><br /><br /></td></tr>	
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Applicability Domain Cutoff:</b></div></td>
			<td align="left" valign="top"><s:textfield id="knnApplicabilityDomain" name="knnApplicabilityDomain" size="5" theme="simple"/></td></tr>	
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum for Training Set:</b></div></td>
			<td align="left" valign="top"><s:textfield id="knnMinTraining" name="knnMinTraining" size="5" theme="simple"/></td></tr>	
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum for Test Set:</b></div></td>
			<td align="left" valign="top"><s:textfield id="knnMinTest" name="knnMinTest" size="5" theme="simple"/></td></tr>	
			<tr><td><div class="StandardTextDarkGrayParagraph"><b>Use Error Based Fit Index:</b></div></td>
			<td align="left" valign="top"><s:checkbox id="knnSaErrorBasedFit" name="knnSaErrorBasedFit" theme="simple"/></td></tr>	
			<!-- end Model Acceptance Parameters -->
			
		<!-- end knn+ parameters -->

		</table></td></tr>
		</tbody>
	</table>
</s:div>	
