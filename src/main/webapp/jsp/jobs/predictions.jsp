<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="table table-hover table-bordered datatable" data-url="<s:url action="getPredictions" namespace="/api"
/>" data-object-type="prediction">
  <thead>
  <tr>
    <th data-property="name" class="name-column">Name</th>
    <th data-property="datasetDisplay">Dataset Predicted</th>
    <th data-property="predictorNames">Predictor(s) Used</th>
    <th data-property="dateCreated" class="date-created">Date</th>
  </tr>
  </thead>
  <tbody></tbody>
</table>
