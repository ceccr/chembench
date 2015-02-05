<%@ taglib prefix="s" uri="/struts-tags" %>
<%
  response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
%>

<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Error</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Error</h2>

    <p>
      <s:iterator value="errorStrings">
        <s:property />
      </s:iterator>
      <s:actionerror />
    </p>

    <p>
      <input class="back-button btn btn-primary" type="button" value="Back">
    </p>

    <hr>
    <p>
      To report a bug, or if you need help with Chembench, you can reach us at <a href="ceccr@email.unc.edu">ceccr@email.unc.edu</a>.
      <br>
      Include this error text in your email, along with a description of the problem.
    </p>

    <p>
      Thanks,
      <br>
      The Chembench Team
    </p>
  </section>

  <%@include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
</body>
</html>
