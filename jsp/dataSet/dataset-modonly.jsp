 <%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>


<script language="javascript" src="javascript/datasetscripts.js"></script>
<s:div>
<table>
 <tr>
	  <td><div class='StandardTextDarkGrayParagraph'><b>Dataset name:</b>&nbsp;
        <input type="text" id="datasetname" name="datasetname" size="60"/>
	  </div></td>
      </tr>
      <tr>
        <td><b class='StandardTextDarkGrayParagraph'>Files description:</b><br />
        <textarea class='StandardTextDarkGrayParagraph' name="dataSetDescription" id="dataSetDescription" style="height: 50px; width: 100%"></textarea></td>
      </tr>
      <tr>
        <td colspan="3"><input class='StandardTextDarkGrayParagraph' name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('datasetname').value, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ checkSpaces(this,document.getElementById('datasetname').value); }" value="Create Dataset" type="button" />
        </td>
      </tr>
</table>
</s:div>