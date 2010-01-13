


<!-- USER OPTIONS -->

<s:form action="updateUserOptions" enctype="multipart/form-data" theme="simple">
<table border="0" align="center" width="680">
 
<tr>
	<td width="100%" height="24" align="left" colspan="2">
	<p class="StandardTextDarkGrayParagraph">
	<i>Chembench provides sample datasets and predictors for you to experiment with. <br />If you choose
	to hide them, they will no longer appear on the My Bench, Modeling, and Prediction pages.</i>
	</p> 
	</td>
</tr>  

<tr>
	<td>
	<div class="StandardTextDarkGrayParagraph"><b>Show Public Datasets:</b></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="showPublicDatasets" value="showPublicDatasets" list="#{'NONE':'None','SOME':'Some','ALL':'all'}" /></div>
	</td>
</tr>

<tr>
	<td>
	<div class="StandardTextDarkGrayParagraph"><b>Show Public Predictors:</b></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="showPublicPredictors" value="showPublicPredictors" list="#{'NONE':'None','ALL':'all'}" /></div>
	</td>
</tr>	

<tr>
	<td width="100%" height="24" align="left" colspan="2">
	<p class="StandardTextDarkGrayParagraph"><!-- spacer --></p> 
	</td>
</tr>  

<tr>
	<td></td>
	<td class="" valign="top"><input type="button" name="userAction" id="userAction" onclick="this.form.submit()" value="Submit" /> 
	<span id="textarea"></span> <br /></td>
</tr>

</table>
</s:form>

<!-- END USER OPTIONS -->

