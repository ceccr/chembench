<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>

<script language="javascript" src="javascript/modeling.js"></script>

<html:form action="/submitQsarWorkflow.do" enctype="multipart/form-data">
			<!-- Datasplit, Random Parameters  -->
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
					<div class="StandardTextDarkGrayParagraph"><i>The modeling set will be divided into training and test sets randomly.</i><br /></div>
					</td>
				</tr>	
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Number of Data Splits:</b></div>
					</td>
					<td align="left" valign="top"><html:text onchange="alert('hi')" property="numSplitsInternalRandom" size="5" id="numSplitsInternalRandom"  /></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Minimum Test Set Size (percent):</b></div>
					</td>
					<td align="left" valign="top"><html:text onchange="alert('hi')" property="randomSplitMinTestSize" styleId="randomSplitMinTestSize" size="5" /></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Maximum Test Set Size (percent):</b></div>
					</td>
					<td align="left" valign="top"><html:text onchange="alert('hi')" property="randomSplitMaxTestSize" styleId="randomSplitMaxTestSize" size="5"/></td>
				</tr>	
						
				</table></td></tr>
				</tbody>
			</table>
</html:form>