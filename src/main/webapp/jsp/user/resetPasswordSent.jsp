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

    <p>Your password has been reset.</p>

    <p>An email containing a temporary password has been sent to <code><s:property value="email" /></code>. When the
      email arrives, you can log in from the <s:a action="home" namespace="/">home page</s:a>. You can then change your
      temporary password using the "edit profile" link in the top-right userbox.</p>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
</body>
</html>
