<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Model: <s:property value="selectedPredictor.name" /></title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>
      Model Details: <s:property value="selectedPredictor.name" />
      <s:a action="jobs" anchor="models">
        <button class="btn btn-primary">Back to Models</button>
      </s:a>
    </h2>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
</body>
</html>
