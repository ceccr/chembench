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
			<b>Set 5-Fold Splitting Parameters</b>
			</p></td>
		</tr>	
		<tr><td colspan="2"><table>
	  
	  <tr><td colspan="2">
	  <div class='StandardTextDarkGrayParagraph'><i>A 5-fold split will generate 5 different external sets. When
	  you do a modeling run on the dataset, 5 predictors will be created, one for each internal/external split. 
	  Each external set will be 1/5 (20%) of the total number of compounds, and the external sets will not overlap.</i></div>
	  </td></tr>
	  
	  <tr>
	  		<td>
			<!-- ./datasplit activator_protein_43.sdf.x -M=R -4EXT -S=10 -N=1 -OUT=actbin -A=10 -->
			<div class="StandardTextDarkGrayParagraph">
			<b>Use activity binning:</b>
			</div></td>
		    <td><s:checkbox name="useActivityBinning5Fold" id="useActivityBinning" theme="simple" /></td>
		</tr>	
		</table>
		</td></tr>
		</tbody>
	</table>
</s:div>