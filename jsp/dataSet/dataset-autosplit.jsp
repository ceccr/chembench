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
			<b>Set Automatic Splitting Parameters</b>
			</p></td>
		</tr>	
		<tr><td colspan="2"><table>
	  <tr>
	  		<td>
			<!-- ./datasplit activator_protein_43.sdf.x -M=R -4EXT -S=10 -N=1 -OUT=actbin -A=10 -->
			<div class="StandardTextDarkGrayParagraph"><input type="checkbox" checked>Use activity binning</input></div></td>
		    <td></td>
		</tr>	
		<tr>
			<td>
			<div class="StandardTextDarkGrayParagraph"><b>Number of Compounds in the External Set:</b></div>
			</td>
			<td align="left" valign="top"><s:textfield name="numCompoundsExternalSet" id="numCompoundsExternalSet" size="5" theme="simple" /></td>
		</tr>
		</table>
		</td></tr>
		</tbody>
	</table>
</s:div>