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
			<b>Modeling Dataset With Descriptors</b>
			</p></td>
		</tr>	
		<tr><td colspan="2"><table>
	  <tr>
		<td colspan="2">
		<div class="StandardTextDarkGrayParagraph"><i>A dataset will be created from the <a href="">X</a> and <a href="">ACT</a> files you supply.<br/>
		This allows you to generate descriptors outside of C-Chembench, and upload them for use for modeling and prediction. <br />
		If you choose to supply an <a href="">SDF</a> file, images of your structures will be created.</i><br /></div>
	    </td>
	  </tr>	
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">ACT File:</b></td>
	    <td align="left">
	    <s:file name="actFileModDesc" id="actFileModDesc" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">SDF File (optional):</b></td>
	    <td align="left">
	    <s:file name="sdfFileModDesc" id="sdfFileModDesc" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">X File:</b></td>
	    <td align="left">
	    <s:file name="xFileModDesc" id="xFileModDesc" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class='StandardTextDarkGrayParagraph'>ACT data type:</b></td>
	    <td align="left">
		<s:radio name="dataTypeModDesc" value="dataTypeModDesc" list="#{'CONTINUOUS':'Continuous','CATEGORY':'Category'}" theme="simple" />
		</td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">Descriptor Type:</b></td>
	    <td align="left">
	    <s:textfield name="descriptorTypeModDesc" id="descriptorTypeModDesc" theme="simple" />
	    </td>
	  </tr>
  	  </table>
    </td></tr></tbody></table>
</s:div>