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
			<b>Modeling Dataset</b>
			</p>
			</td>
		</tr>	
		<tr><td colspan="2"><table>
	  <tr>
		<td colspan="2">
		<div class="StandardTextDarkGrayParagraph"><i>A dataset for modeling and prediction will be created from the <a href="/jsp/help/fileformats.jsp#SDF">SDF</a> and <a href="/jsp/help/fileformats.jsp#ACT">ACT</a> files you supply.</i><br />
		Chembench will generate descriptors for this dataset during the modeling process.</div>
	    </td>
	  </tr>	
	  <tr>
	    <td><b class='StandardTextDarkGrayParagraph'>ACT data type:</b></td>
	    <td align="left">
	    <s:radio name="dataTypeModeling" value="dataTypeModeling" list="#{'CONTINUOUS':'Continuous','CATEGORY':'Category'}" theme="simple" />
	    </td>
	  </tr>	
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">ACT File:</b></td>
	    <td align="left">
	    <s:file name="actFileModeling" id="actFileModeling" theme="simple" />
	    </td>
	  </tr>
	  <tr>
	    <td><b class="StandardTextDarkGrayParagraph">SDF File:</b></td>
	    <td align="left">
	    <s:file name="sdfFileModeling" id="sdfFileModeling" theme="simple" />
	    </td>
	  </tr>		
	  <tr>
		<td>
		<div class="StandardTextDarkGrayParagraph">
		<b>Standardize structures: </b>
		</div></td>
		<td><s:checkbox name="standardizeModeling" id="standardizeModeling" theme="simple" /></td>
	  </tr>
  	  </table>
    </td></tr></tbody></table>
</s:div>