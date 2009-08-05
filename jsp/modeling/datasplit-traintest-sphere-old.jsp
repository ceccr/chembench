<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>

<script language="javascript" src="javascript/modeling.js"></script>

<html:form action="/submitQsarWorkflow.do" enctype="multipart/form-data">
			<!-- Datasplit, Sphere Exclusion Parameters  -->
			<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<b>Set Internal Data Splitting Parameters</b>
					</p>
					</td>
				</tr>	
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
					<td align="left" valign="top"><html:text property="numSplitsInternalSphere" size="5"/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Force Minimum Activity Compound into all Training Sets:</b></div>
					</td>
					<td align="left" valign="top">
					<html:radio value="1" property="splitIncludesMin" styleId="splitIncludesMin">Yes</html:radio> <html:radio value="0" property="splitIncludesMin" styleId="splitIncludesMin">No</html:radio></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Force Maximum Activity Compound into all Training Sets:</b></div>
					</td>
					<td align="left" valign="top">
					<html:radio value="1" property="splitIncludesMax" styleId="splitIncludesMax">Yes</html:radio> <html:radio value="0" property="splitIncludesMax" styleId="splitIncludesMax">No</html:radio></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Minimum Test Set Size (percent):</b></div>
					</td>
					<td align="left" valign="top"><html:text property="sphereSplitMinTestSize" styleId="sphereSplitMinTestSize" size="5"/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Selection of Next Training Set Point is Based on:</b></div>
					</td>
					<td> 
					<select name="selectionNextTrainPt" id="selectionNextTrainPt" value="0">
						<option value="0">Random Selection</option>
						<option value="1">Expand Outwards from Already Selected Points</option> <!-- SUM-MIN, tumor-like -->
						<option value="2">Even Coverage of Descriptor Space</option> <!-- MIN_MAX, lattice-like -->
						<option value="3">Work Inwards from Boundaries of Descriptor Space</option> <!-- SUM-MAX -->
					</select>			
					</td>
				</tr>			
		
				</table></td></tr>
				</tbody>
			</table>
</html:form>