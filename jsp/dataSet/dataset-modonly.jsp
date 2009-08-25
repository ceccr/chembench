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
			<b>Upload Dataset for Modeling Only</b>
			</p></td>
		</tr>	
		<tr><td colspan="2"><table>
	  <tr>
		<td colspan="2">
		<div class="StandardTextDarkGrayParagraph"><i>A dataset will be created from the <a href="">ACT</a> and <a href="">X</a> files you supply.<br/>
		This allows you to generate your own descriptors and use C-Chembench to build predictors. <br />
		If you choose to supply an <a href="">SDF</a> file, images of your structures will be generated as well.</i><br /></div>
	    </td>
	  </tr>	
	  <tr>
	    <td width="30%"><b class='StandardTextDarkGrayParagraph'>Data type:</b></td>
	    <td align="left">
		<s:radio name="dataTypeModOnly" value="dataTypeModOnly" list="#{'CONTINUOUS':'Continuous','CATEGORY':'Category'}" theme="simple" />
		</td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">ACT File:</b></td>
	    <td align="left">
	    <s:file name="actFileModOnly" id="actFileModOnly" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">X File:</b></td>
	    <td align="left">
	    <s:file name="xFileModOnly" id="xFileModOnly" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">SDF File (optional):</b></td>
	    <td align="left">
	    <s:file name="sdfFileModOnly" id="sdfFileModOnly" theme="simple" />
	    </td>
	  </tr>
  	  </table>
    </td></tr></tbody></table>
</s:div>