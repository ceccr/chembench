<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Reset Password</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Reset Password</h2>
    <p>If you've forgotten your password, you can reset it using the form below.</p>

    <s:if test="!getActionErrors().isEmpty()">
      <div class="alert alert-danger">
        <s:iterator value="getActionErrors()">
          <s:property />
        </s:iterator>
      </div>
    </s:if>

    <s:form action="resetPassword" cssClass="form-horizontal" theme="simple">
      <div class="form-group">
        <label for="userName" class="control-label col-xs-3">Your username:</label>
        <div class="col-xs-4"><s:textfield id="userName" name="userName" cssClass="form-control" theme="simple" /></div>
      </div>

      <div class="form-group">
        <label for="email" class="control-label col-xs-3">Your e-mail address:</label>
        <div class="col-xs-4"><s:textfield id="email" name="email" cssClass="form-control" theme="simple" /></div>
      </div>

      <div class="form-group">
        <div class="col-xs-offset-3 col-xs-4">
          <button type="submit" class="btn btn-primary">Submit</button>
        </div>
      </div>
    </s:form>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
</body>
</html>
