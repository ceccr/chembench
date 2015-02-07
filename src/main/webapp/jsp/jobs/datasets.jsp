<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="datasets" class="tab-pane">
  <h3>Datasets</h3>
  <table class="table table-hover table-bordered tablesorter">
    <thead>
    <tr>
      <th>Name</th>
      <th>Size</th>
      <th>Type</th>
      <th>Descriptors</th>
      <th class="sorter-modi">Modelability</th>
      <th class="date-created">Date</th>
      <th class="public-private">Public?</th>
    </tr>
    </thead>

    <tbody>
    <s:iterator value="userDatasets">
      <tr>
        <td class="name-column">
          <s:url var="datasetId" action="viewDataset">
            <s:param name="id" value="%{id}" />
          </s:url>
          <s:a href="%{datasetId}">
            <span class="object-name"><s:property value="name" /></span>
          </s:a>
          <br>

          <div class="button-group">
            <div class="download">
              <s:url var="datasetDownload" value="datasetFilesServlet">
                <s:param name="datasetName" value="%{name}" />
                <s:param name="user" value="%{userName}" />
              </s:url>
              <span class="glyphicon glyphicon-save"></span>
              <s:a href="%{datasetDownload}">Download</s:a>
            </div>
            <s:if test="!userName.equals(@edu.unc.ceccr.chembench.global.Constants@ALL_USERS_USERNAME)">
              <div class="delete">
                <span class="glyphicon glyphicon-remove"></span>
                <s:url var="datasetDelete" action="deleteDataset">
                  <s:param name="id" value="%{id}" />
                </s:url>
                <s:a href="%{datasetDelete}">Delete</s:a>
              </div>
            </s:if>
          </div>
        </td>
        <td>
          <s:property value="numCompound" />
        </td>
        <td class="dataset-type activity-type">
          <s:property value="datasetType" />
          <s:if test="hasActivities()">
            (<s:property value="modelType" />)
          </s:if>
        </td>
        <td class="available-descriptors">
          <s:property value="availableDescriptors" />
          <s:if test="!uploadedDescriptorType.isEmpty()">
            ("<s:property value="uploadedDescriptorType" />")
          </s:if>
        </td>
        <td class="modi-value">
          <s:if test="!canGenerateModi()">
            Not available
          </s:if>
          <s:else>
            <s:if test="modiGenerated">
              <s:property value="modi" />
            </s:if>
            <s:else>
              <input type="hidden" name="dataset-id" value="<s:property value=" id" />">
              <span class="text-warning">Not generated</span>
              <button class="btn btn-primary btn-xs generate-modi">Generate MODI</button>
            </s:else>
          </s:else>
        </td>
        <td class="date-created">
          <s:date name="createdTime" format="yyyy-MM-dd" />
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
