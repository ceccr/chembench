<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" import="java.util.*" %>

<script language="javascript" src="javascript/modeling.js"></script>
<s:div>
<!-- Datasplit, Sphere Exclusion Parameters  -->
			<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<br />
					<p class="StandardTextDarkGrayParagraph2">
					<b>Set Sphere Exclusion Splitting Parameters</b>
					</p>
					</td>
				</tr>	
                <tr>
                    <td colspan="2" class="notice">
                        Recommended for smaller datasets (under 300 compounds).
                    </td>
				<tr><td><table>
				
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>The modeling set will be divided into training and test sets by sphere exclusion.</i><br /></div>
					</td>
				</tr>	
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Number of Data Splits:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="numSplitsInternalSphere" id="numSplitsInternalSphere" size="5" theme="simple" onchange='calculateRuntimeEstimate()'/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Minimum Test Set Size (percent):</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="sphereSplitMinTestSize" id="sphereSplitMinTestSize" size="5" theme="simple"/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Force Minimum Activity Compound into all Training Sets:</b></div>
					</td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio list="#{'true':'Yes','false':'No'}" name="splitIncludesMin" theme="simple"/></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Force Maximum Activity Compound into all Training Sets:</b></div>
					</td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio list="#{'true':'Yes','false':'No'}" name="splitIncludesMax" theme="simple"/></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Selection of Next Training Set Point is Based on:</b></div>
					</td>
					<td> 
					<s:select name="selectionNextTrainPt" id="selectionNextTrainPt" list="#{'0':'Random Selection','1':'Expand Outwards from Already Selected Points','2':'Even Coverage of Descriptor Space','3':'Work Inwards from Boundaries of Descriptor Space'}" theme="simple"/>
						<!--<option value="0">Random Selection</option> -->
						<!--<option value="1">Expand Outwards from Already Selected Points</option>  SUM-MIN, tumor-like -->
						<!--<option value="2">Even Coverage of Descriptor Space</option>  MIN_MAX, lattice-like -->
						<!--<option value="3">Work Inwards from Boundaries of Descriptor Space</option> SUM-MAX -->
					</td>
				</tr>			
		
				</table></td></tr>
				</tbody>
			</table>
</s:div>
