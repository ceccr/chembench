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
			<b>Prediction Dataset With Descriptors</b>
			</p></td>
		</tr>	
		<tr><td colspan="2"><table>
	  <tr>
		<td colspan="2">
		<div class="StandardTextDarkGrayParagraph"><i>A dataset will be created from the <a href="help-fileformats#X">X</a> file you supply.<br/>
		This allows you to generate descriptors outside of Chembench, and use them for prediction. <br />
		The descriptor type for the prediction dataset must match the descriptor type used in the modeling dataset. <br />
		If you choose to supply an <a href="help-fileformats#SDF">SDF</a> file, images of your structures will be created.</i><br /></div>
	    </td>
	  </tr>	
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">SDF File (optional):</b></td>
	    <td align="left">
	    <s:file name="sdfFilePredDesc" id="sdfFilePredDesc" theme="simple" />
	    </td>
	  </tr>
	  <tr>
		<td>
		<div class="StandardTextDarkGrayParagraph">
		<b>Standardize structures: </b>
		</div></td>
		<td><s:checkbox name="standardizeModDesc" id="standardizeModDesc" theme="simple" /></td>
	  </tr>
	  <tr id="generateImages_trp" style="display:none">
		<td>
		<div class="StandardTextDarkGrayParagraph">
		<b>Generate M-heatmap: </b>
		</div></td>
		<td><s:checkbox name="generateImagesPWD" id="generateImagesPWD" theme="simple" /><span class="StandardTextDarkGrayParagraph"><i>(Unchecking this box will accelerate dataset generation but will eliminate heatmap based on Mahalanobis distance measure)</i></span></td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">X File:</b></td>
	    <td align="left">
	    <s:file name="xFilePredDesc" id="xFilePredDesc" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">Please select or enter descriptor type:</b></td>
	    <td>
		    <table>
		    <tr>
		    	<td><input class="StandardTextDarkGrayParagraph" type="radio" name="predictorNameD" id="newDescriptorNameD" onclick="getSelectedDescriptorD();" checked="checked" />New type</td>
		    	<td><s:textfield name="descriptorNewNameD" id="descriptorNewNameD" label="Enter a new type" theme="simple"></s:textfield></td>
		    </tr>
		    <s:if test="%{userUploadedDescriptorTypes.size()>0}">
		    <tr>
		    	<td><input class="StandardTextDarkGrayParagraph" type="radio" name="predictorNameD" id="usedDescriptorNameD" onclick="getSelectedDescriptorD();" />Used type</td>
		    	<td><s:select name="selectedDescriptorUsedNameD" id="descriptorUsedNameD" list="userUploadedDescriptorTypes" label="Select type" disabled="true" theme="simple"/></td>
		    </tr>
		    </s:if>
		    </table>
	    </td>
 	  </tr>
	  <tr>
		<td colspan="2">
		<div class="StandardTextDarkGrayParagraph"><i>Your uploaded descriptors must have the same scaling as those of your modeling dataset.</i></div>
		</td>
	  </tr>
  	  </table>
    </td></tr></tbody></table>
</s:div>