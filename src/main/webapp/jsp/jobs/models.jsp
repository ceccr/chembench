<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="table table-hover table-bordered datatable" data-url="<s:url action="getModels" namespace="/api"
/>" data-object-type="model">
  <thead>
  <tr>
    <th data-property="name">Name</th>
    <th data-property="datasetDisplay">Modeling Dataset</th>
    <th data-property="externalPredictionAccuracy">R<sup>2</sup> or <abbr title="Correct Classification Rate"
                                                                          class="initialism">CCR</abbr></th>
    <th data-property="modelMethod">Type</th>
    <th data-property="descriptorGeneration">Descriptors</th>
    <th data-property="dateCreated" class="date-created">Date</th>
    <th data-property="userName" class="public-private">Public?</th>
  </tr>
  </thead>
  <tbody></tbody>
</table>
