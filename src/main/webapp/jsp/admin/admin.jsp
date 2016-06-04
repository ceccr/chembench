<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Administration</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Administration</h2>

    <ul class="nav nav-tabs">
      <li class="active"><a href="#user-management" data-toggle="tab">User Management</a></li>
      <li><a href="#object-management" data-toggle="tab">Object Management</a></li>
    </ul>

    <div class="tab-content">
      <div id="user-management" class="tab-pane active">
        <h3>User Management</h3>
        <table id="users" class="table table-bordered table-hover datatable">
          <thead>
          <tr>
            <th>Username</th>
            <th>Organization</th>
            <th>Country</th>
            <th>Last Login</th>
            <th class="unsortable">Flags</th>
            <th class="unsortable"></th>
            <th class="unsortable"></th>
          </tr>
          </thead>
          <tbody>
          <s:iterator value="users">
            <s:if test="userName == #session['user'].userName">
              <tr class="info">
            </s:if>
            <s:else>
              <tr>
            </s:else>
            <td><b><span class="username"><s:property value="userName" /></span></b><br>
              <s:property value="firstName" />&nbsp;<s:property value="lastName" /><br>
              <a target="_blank" href="mailto:<s:property value="email" />"><s:property value="email" /></a>
            </td>
            <td><s:property value="orgName" /></td>
            <td><s:property value="country" /></td>
            <td><s:date name="lastLogintime" format="yyyy-MM-dd" /></td>
            <td>
              <s:hidden name="can-download-descriptors-string" value="%{canDownloadDescriptors}" theme="simple" />
              <label class="user-flags">
                <input name="can-download-descriptors" type="checkbox">
                All descriptors
              </label>
              <br>
              <s:hidden name="is-admin-string" value="%{isAdmin}" theme="simple" />
              <s:if test="userName != #session['user'].userName">
                <label class="user-flags"><input name="is-admin" type="checkbox"> Is administrator</label>
              </s:if>
              <s:else>
                <label class="text-muted user-flags">
                  <input name="is-admin" disabled="disabled" type="checkbox">
                  Is administrator
                </label>
              </s:else>
            </td>
            <td>
              <s:if test="userName != #session['user'].userName">
                <a href="#" class="impersonate-user text-nowrap">log in as</a></s:if>
              <s:else><span class="text-muted text-nowrap">log in as</span></s:else>
            </td>
            <td><s:if test="userName != #session['user'].userName">
              <s:url namespace="/admin" action="deleteUser" var="deleteUserLink">
                <s:param name="userToDelete" value="userName" />
              </s:url><s:a href="%{deleteUserLink}">delete</s:a></s:if>
              <s:else><span class="text-muted">delete</span></s:else></td>
            </tr>
          </s:iterator>
          </tbody>
        </table>
      </div>

      <div id="object-management" class="tab-pane">
        <h3 class="margin-below">Object Management</h3>

        <h4>Make Dataset Public</h4>
        <s:form action="makeDatasetPublic" namespace="/admin" cssClass="form-horizontal" theme="simple">
          <div class="form-group">
            <label for="datasetId" class="control-label col-xs-3">Dataset id:</label>
            <div class="col-xs-4"><s:textfield name="datasetId" id="datasetId" cssClass="form-control"
                                               theme="simple" /></div>
          </div>

          <div class="form-group">
            <div class="col-xs-offset-3 col-xs-4">
              <button type="submit" class="btn btn-primary">Submit</button>
            </div>
          </div>
        </s:form>

        <h4>Make Predictor Public</h4>
        <s:form action="makePredictorPublic" namespace="/admin" cssClass="form-horizontal" theme="simple">
          <div class="form-group">
            <label for="predictorId" class="control-label col-xs-3">Predictor id:</label>
            <div class="col-xs-4"><s:textfield name="predictorId" id="predictorId" cssClass="form-control"
                                               theme="simple" /></div>
          </div>

          <div class="form-group text-muted">
            <div class="col-xs-3 control-label">
              <label for="predictorType">Public predictor type:</label><span class="help-block">Optional.</span>
            </div>
            <div class="col-xs-4"><s:textfield name="predictorType" id="predictorType" cssClass="form-control"
                                               theme="simple" /></div>
          </div>

          <div class="form-group">
            <div class="col-xs-offset-3 col-xs-4">
              <button type="submit" class="btn btn-primary">Submit</button>
            </div>
          </div>
        </s:form>
      </div>
    </div>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script>
  (function() {
    'use strict';

    window.Chembench.CHANGE_USER_FLAGS_URL = '<s:url action="changeUserFlags" namespace="/admin" />';
    window.Chembench.LOGIN_URL = '<s:url action="login" />';
  })();
</script>
<script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</body>
</html>
