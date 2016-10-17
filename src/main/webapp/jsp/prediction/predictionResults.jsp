<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="table">
  <s:iterator value="smilesPredictions">
  <dl class="dl-horizontal properties-list">
    <dt>SMILES string</dt>
    <dd><s:property value="smiles" /></dd>

    <dt>Similarity cutoff</dt>
    <dd>
      <s:if test="%{cutoff == 'N/A'}">
        N/A
      </s:if>
      <s:else>
        <s:property value="cutoff" />&sigma;
      </s:else>
    </dd>
  </dl>

  <thead>
  <tr>
    <th>Model</th>
    <th>Prediction</th>
    <th>Predicting Models</th>
    <th>&sigma;</th>
  </tr>
  </thead>

  <tbody>
    <tr>
      <td><span class="object-name"><s:property value="predictorName" /></span></td>
      <td><s:if test="%{(Number(cutoff)||0) > (Number(zScore)||0) || cutoff=='N/A'}">
        <s:property value="predictedValue" />
        <s:if test="stdDeviation!='N/A'"> &plusmn; <s:property value="stdDeviation" />
        </s:if>
      </s:if>
        <s:else>
          <span class="text-danger">Compound similarity is above cutoff </span>
        </s:else></td>
      <td><s:property value="predictingModels" /> / <s:property value="totalModels" /></td>
      <td><s:property value="zScore" />&sigma;</td>
    </tr>
  </tbody>
  </s:iterator>
</table>

<p class="help-block">
  To see a prediction which is not showing, please increase similarity cutoff above listed &sigma; and
  repredict.
</p>