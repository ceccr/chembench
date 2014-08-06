<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" import="java.util.*" %>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script src="/javascript/jquery-1.4.2-development.js" type="text/javascript"></script>
<script language="javascript">

</script>
</head>

<div class="tooltip"> 
 
	<img src="theme/img/tooltip/c1.jpg" alt="Flying screens" height="120" width="120"
		style="float:left;margin:0 15px 20px 0" /> <s:property value="compoundId" />
 
	<table style="margin:0"> 
		<tr> 
			<td class="label">Observed</td> 
			<td><div id="observedValue"><s:property value="compoundObservedValue" /></div></td> 
		</tr> 
		<tr> 
			<td class="label">Predicted</td> 
			<td><div id="predictedValue"><s:property value="compoundPredictedValue" /></div></td> 
		</tr> 	
	</table> 
</div> 

</body>
</html>
