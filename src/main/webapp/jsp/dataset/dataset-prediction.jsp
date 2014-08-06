<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" import="java.util.*" %>


<script language="javascript" src="javascript/datasetscripts.js"></script>
<s:div>
	<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>	
		<tr>
			<td width="100%" height="24" align="left" colspan="2">
			<br />
			<p class="StandardTextDarkGrayParagraph2">
			<b>Prediction Dataset</b>
			</p>
			</td>
		</tr>	
		<tr><td colspan="2"><table>
	  <tr>
		<td colspan="2">
		<div class="StandardTextDarkGrayParagraph"><i>A dataset will be created from the <a href="help-fileformats#SDF">SDF</a> file you supply.</i><br /></div>
	    </td>
	  </tr>		
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">SDF File:</b></td>
	    <td align="left">
	    <s:file name="sdfFilePrediction" id="sdfFilePrediction" theme="simple" />
		</td>
	  </tr>
	  <tr>
		<td>
		<div class="StandardTextDarkGrayParagraph">
		<b>Standardize structures: </b>
		</div></td>
        <td>
            <s:checkbox name="standardizePrediction" id="standardizePrediction" theme="simple" />
            <span class="StandardTextDarkGrayParagraph"><em>Note: If you choose not to standardize, ensure that your structure file contains explicit hydrogens, or Dragon descriptor generation will fail.</em></span>
        </td>
	  </tr>
	  <tr>
		<td>
		<div class="StandardTextDarkGrayParagraph">
		<b>Generate M-heatmap: </b>
		</div></td>
		<td><s:checkbox name="generateImagesP" id="generateImagesP" theme="simple" /><span class="StandardTextDarkGrayParagraph"><i>(Unchecking this box will accelerate dataset generation but will eliminate heatmap based on Mahalanobis distance measure)</i></span></td>
	  </tr>
  	  </table>
    </td></tr></tbody></table>
</s:div>
