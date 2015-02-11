<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="predictions" class="tab-pane">
  <h3>Predictions</h3>
  <table class="table table-hover table-bordered datatable">
    <thead>
    <tr>
      <th>Name</th>
      <th>Prediction Dataset</th>
      <th>Predictor(s) Used</th>
      <th class="date-created">Date</th>
    </tr>
    </thead>
    <tbody>
    <s:iterator value="userPredictions">
      <tr>
        <td class="name-column">
          <s:url var="viewPrediction" action="viewPrediction">
            <s:param name="id" value="%{id}" />
          </s:url>
          <s:a href="%{viewPrediction}">
            <span class="object-name"><s:property value="name" /></span>
          </s:a>
        </td>
        <td>
          <s:url var="viewPredictionDataset" action="viewDataset">
            <s:param name="id" value="datasetId" />
          </s:url>
          <s:a href="%{viewPredictionDataset}">
            <s:property value="datasetDisplay" />
          </s:a>
        </td>
        <td>
          <s:property value="predictorNames" />
        </td>
        <td class="date-created">
          <s:date name="dateCreated" format="yyyy-MM-dd" />
        </td>
      </tr>
    </s:iterator>
    </tbody>
  </table>
</div>