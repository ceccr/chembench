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
			<b>Upload Dataset for Modeling and Prediction</b>
			</p>
			</td>
		</tr>	
		<tr><td colspan="2"><table>
	  <tr>
		<td colspan="2">
		<div class="StandardTextDarkGrayParagraph"><i>A dataset will be created from the <a href="">SDF</a> and <a href="">ACT</a> files you supply.</i><br /></div>
	    </td>
	  </tr>			
	  <tr>
	    <td width="30%"><b class='StandardTextDarkGrayParagraph'>Data type:</b></td>
	    <td align="left">
	    <s:radio name="dataTypeModPred" value="dataTypeModPred" list="#{'CONTINUOUS':'Continuous','CATEGORY':'Category'}" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">ACT File:</b></td>
	    <s:file name="actFileModPred" id="actFileModPred" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">SDF File:</b></td>
	    <td align="left">
	    <s:file name="sdfFileModPred" id="sdfFileModPred" theme="simple" />
	    </td>
	  </tr>
  	  </table>
    </td></tr></tbody></table>
</s:div>