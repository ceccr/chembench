<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Registration</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Registration Successful</h2>

    <p>
      <s:property value="firstName" />,<br>
      Thank you for your interest in Chembench!
    </p>

    <p><s:property value="outputMessage" /></p>

    <p>While you wait, you might like to read the <s:a action="overview"
                                                       namespace="help">overview of Chembench</s:a>.</p>

    <p>
      Enjoy,<br>
      The Chembench Team
    </p>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
</body>
</html>
