<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<!-- results of SMILES prediction -->
<html>
<body>
<p class="StandardTextDarkGray">
SMILES String: <s:property value="smilesString" />
</p>

<p class="StandardTextDarkGray">
Similarity Cutoff:
<s:if test="%{smilesCutoff == 'N/A'}">
N/A
</s:if>
<s:elseif test="%{smilesCutoff == 0}">
0&sigma;
</s:elseif>
<s:elseif test="%{smilesCutoff == 1}">
1&sigma;
</s:elseif>
<s:elseif test="%{smilesCutoff == 2}">
2&sigma;
</s:elseif>
<s:elseif test="%{smilesCutoff == 3}">
3&sigma;
</s:elseif>
</p>

<table>
<tbody>
<tr>
    <td class="TableRowText01">Predictor</td>
    <td class="TableRowText01">Prediction</td>
    <td class="TableRowText01">Predicting Models</td>
    <td class="TableRowText01">&sigma;</td>
</tr>

<s:iterator value="smilesPredictions">
    <tr>
        <td class="TableRowText02"><s:property value="predictorName" /></td>
        <td class="TableRowText02">
            <s:if test="%{smilesCutoff>zScore || smilesCutoff=='N/A'}">
                <s:property value="predictedValue" /><s:if test="stdDeviation!='N/A'"> &plusmn; <s:property value="stdDeviation" /></s:if>
            </s:if>
            <s:else>
            <font color="red">
                Compound similarity is above cutoff
            </font>
            </s:else>
        </td>
        <td class="TableRowText02"><s:property value="predictingModels" /> / <s:property value="totalModels" /></td>
        <td class="TableRowText02"><s:property value="zScore" />&sigma;</td>
    </tr>
</s:iterator>
</tbody>
</table>

<p class="StandardTextDarkGray">
<b>
    To see a prediction which is not showing, please increase similarity cutoff above listed &sigma; and repredict.
</b>
</p>
<br/>
</body>
</html>
