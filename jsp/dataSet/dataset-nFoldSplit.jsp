<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<script language="javascript" src="javascript/datasetscripts.js"></script>
<s:div>
	<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>	
		<tr>
			<td width="100%" height="24" align="left" colspan="2">
			<br />
			<p class="StandardTextDarkGrayParagraph2">
			<b>Set n-Fold Splitting Parameters</b>
			</p></td>
		</tr>	
		<tr><td colspan="2"><table>
	  
	  <tr><td colspan="2">
	  <div class='StandardTextDarkGrayParagraph'><i>An <i>n</i>-fold split will generate <i>n</i> different external 
	  test sets. When you use the dataset in modeling, <i>n</i> predictors will be created, one for each external test set. 
	  Each external set will contain 1/<i>n</i> of the total dataset, and the external sets will not overlap.</i></div>
	  </td></tr>
	 	<tr>
	  		<td>
			<div class="StandardTextDarkGrayParagraph">
			<b>Number of splits (<i>n</i>):</b>
			</div></td>
		    <td align="left" valign="top"><s:textfield name="numFolds" id="numFolds" theme="simple" /></td>
		</tr>	
	  	<tr>
	  		<td>
			<div class="StandardTextDarkGrayParagraph">
			<b>Use activity binning:</b>
			</div></td>
		    <td><s:checkbox name="useActivityBinningNFold" id="useActivityBinning" theme="simple" /></td>
		</tr>	
		</table>
		</td></tr>
		</tbody>
	</table>
</s:div>