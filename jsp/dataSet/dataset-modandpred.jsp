<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>


<script language="javascript" src="javascript/datasetscripts.js"></script>
<s:div>
	 <b class='StandardTextDarkGrayParagraph'>Upload dataset for modeling</b>
	 <table cellpadding="3" cellspacing="3" frame="border" rules="none">
	  <tr>
	    <td colspan="2"><b class='StandardTextDarkGrayParagraph'>Data type:</b></td>
	  </tr>
	  <tr>
	    <td colspan="2"><input id="con" name="knnType" value="CONTINUOUS" checked="true"  type="radio" />
	        <b class="StandardTextDarkGrayParagraph"> QSAR Continuous</b><br />
	        <input id="cat" name="knnType" value="CATEGORY" type="radio" />
	        <b class="StandardTextDarkGrayParagraph"> QSAR Category</b></td>
	  </tr>
	  <tr>
	    <td colspan="2"><b class='StandardTextDarkGrayParagraph'>Upload:</b></td>
	  </tr>
	  <tr>
	    <td><span tooltip="Upload an ACT file."><b class="StandardTextDarkGrayParagraph">ACT File:</b></span><br />
	        <span tooltip="Upload an SD file."><b class="StandardTextDarkGrayParagraph">SDF File:</b></span></td>
	    <td><input id="loadAct" name="actFile" type="file"/>
	        <br />
	        <input id="loadSdfModeling" name="sdFileModeling" onchange="setDatasetName(this)" type="file" /></td>
	  </tr>
            
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