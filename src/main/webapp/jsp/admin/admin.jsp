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
            <th>E-mail</th>
            <th>Organization</th>
            <th>Country</th>
            <th>Last Login</th>
            <th class="unsortable">Descriptors?</th>
            <th class="unsortable">Admin?</th>
            <th class="unsortable"></th>
            <th class="unsortable"></th>
          </tr>
          </thead>
          <tbody>
          <s:iterator value="users">
            <tr>
              <td><b><span class="username"><s:property value="userName" /></span></b><br>
                <s:property value="firstName" />&nbsp;<s:property value="lastName" />
              </td>
              <td><a target="_blank" href="mailto:<s:property value="email" />"><s:property value="email" /></a></td>
              <td><s:property value="orgName" /></td>
              <td><s:property value="country" /></td>
              <td><s:date name="lastLogintime" format="yyyy-MM-dd" /></td>
              <td class="text-center">
                <s:hidden name="can-download-descriptors-string" value="%{canDownloadDescriptors}" theme="simple" />
                <input class="can-download-descriptors" type="checkbox" title="Can download descriptors?">
              </td>
              <td class="text-center">
                <s:hidden name="is-admin-string" value="%{isAdmin}" theme="simple" />
                <s:if test="userName != #session['user'].userName">
                  <input class="is-admin" type="checkbox" title="Is administrator?">
                </s:if>
                <s:else><input class="is-admin" disabled="disabled" type="checkbox" title="Iss administrator?"></s:else>
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
        <h3>Object Management</h3>
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
