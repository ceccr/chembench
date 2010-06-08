<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>


<script language="javascript" src="javascript/modeling.js"></script>
<s:div>
			 <!-- SVM Parameters -->
			  <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<br />
					<p class="StandardTextDarkGrayParagraph2">
					<b>Set Random Forest Parameters</b>
					</p>
					</td>
				</tr>	
				<tr><td><table>
				
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of trees:</b></div></td>
				<td align="left" valign="top"><s:textfield id="numTrees" name="numTrees" size="5" theme="simple"/>
				</td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Training Set Size:</b></div></td>
				<td align="left" valign="top"><s:textfield id="trainSetSize" name="trainSetSize" size="5" theme="simple"/>
				</td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Descriptors Per Tree:</b></div></td>
				<td align="left" valign="top"><s:textfield id="descriptorsPerTree" name="descriptorsPerTree" size="5" theme="simple"/>
				</td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Sample With Replacement?</b></div></td>
				<td align="left" valign="top"><s:radio name="sampleWithReplacement" list="#{'true':'Yes','false':'No'}" theme="simple" /></div>
				</td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Class Weights:</b></div></td>
				<td align="left" valign="top"><s:textfield id="classWeight" name="classWeight" size="5" theme="simple"/>
				</td></tr>
				
				</table></td></tr>
				</tbody>
			</table>
</s:div>