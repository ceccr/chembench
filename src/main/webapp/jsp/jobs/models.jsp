<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="models" class="tab-pane">
  <h3>Models</h3>
  <table class="table table-hover table-bordered tablesorter">
    <thead>
    <tr>
      <th>Name</th>
      <th>Modeling Dataset</th>
      <th>R<sup>2</sup> or <abbr title="Correct Classification Rate" class="initialism">CCR</abbr></th>
      <th>Type</th>
      <th>Descriptors</th>
      <th class="date-created">Date</th>
      <th class="public-private">Public?</th>
    </tr>
    </thead>
    <tbody>
    <s:iterator value="userPredictors">
      <tr>
        <td class="name-column">
          <s:url var="viewPredictor" action="viewPredictor">
            <s:param name="id" value="%{id}" />
          </s:url>
          <s:a href="%{viewPredictor}">
            <span class="object-name"><s:property value="name" /></span>
          </s:a>
        </td>
        <td class="name-column">
          <s:url var="viewModelingDataset" action="viewDataset">
            <s:param name="id" value="%{datasetId}" />
          </s:url>
          <s:a href="%{viewModelingDataset}">
            <s:property value="datasetDisplay" />
          </s:a>
        </td>
        <td>
          <s:if test="childType.equals('NFOLD')">
            <s:if test="!externalPredictionAccuracyAvg.equals('0.0 � 0.0')">
              <s:property value="externalPredictionAccuracyAvg" />
            </s:if>
            <s:else>
              NA
            </s:else>
          </s:if>
          <s:else>
            <s:if test="!externalPredictionAccuracy.equals('0.0')">
              <s:property value="externalPredictionAccuracy" />
            </s:if>
            <s:else>
              NA
            </s:else>
          </s:else>
        </td>
        <td class="modeling-method">
          <s:property value="modelMethod" />
        </td>
        <td class="available-descriptors">
          <s:if test="descriptorGeneration.equals('UPLOADED')">
            * <s:property value="uploadedDescriptorType" />
          </s:if>
          <s:else>
            <s:property value="descriptorGeneration" />
          </s:else>
        </td>
        <td class="date-created">
          <s:date name="dateCreated" format="yyyy-MM-dd" />
        </td>
        <td class="public-private">
          <s:if test="userName.equals('all-users')">
                      <span class="text-primary"><span class="glyphicon glyphicon-eye-open" title="Public"></span>
                        Yes</span>
          </s:if>
          <s:else>
                      <span class="text-muted"><span class="glyphicon glyphicon-eye-close" title="Private"></span>
                        No</span>
          </s:else>
        </td>
      </tr>
    </s:iterator>
    </tbody>
  </table>
</div>
