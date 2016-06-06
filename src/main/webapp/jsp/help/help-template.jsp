<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Help | PAGENAME</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Help &amp; Resources</h2>
    <hr>
    <div class="row">
      <div class="col-xs-3">
        <%@ include file="help-nav.jsp" %>
      </div>

      <section id="help-content" class="col-xs-9">
        <h3>PAGENAME</h3>
      </section>
    </div>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/help.js"></script>
</body>
</html>
