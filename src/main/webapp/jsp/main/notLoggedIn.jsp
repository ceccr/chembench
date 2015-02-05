<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Login Required</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Login Required</h2>

    <p>You are seeing this page because you are currently not logged in, or your session has expired.</p>

    <p>
      If you need an account, you can make one from the <a href="/">Home</a> page. Creating an account is quick and
      free. <br> Or, if you already have an account, you can log in using the form below.
    </p>

    <p>
      Thank you for using Chembench! If you encounter any problems, please contact us at <a
        href="mailto:ceccr@email.unc.edu.">ceccr@email.unc.edu.</a>
    </p>

    <hr>

    <s:form action="login" enctype="multipart/form-data" cssClass="form-horizontal" method="post" theme="simple">
      <div class="form-group">
        <label for="username" class="col-xs-3 control-label">Username:</label>

        <div class="col-xs-4">
          <s:textfield name="username" id="username" cssClass="form-control" theme="simple" />
        </div>
      </div>
      <div class="form-group">
        <label for="password" class="col-xs-3 control-label">Password:</label>

        <div class="col-xs-4">
          <s:password name="password" id="password" cssClass="form-control" theme="simple" />
        </div>
      </div>
      <div class="form-group">
        <div class="col-xs-offset-3 col-xs-4">
          <input class="login-button" value="Log in" type="submit">
        </div>
      </div>
      <div class="form-group">
        <div class="col-xs-offset-3 col-xs-4">
          <%
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
              ipAddress = request.getRemoteAddr();
            }
            String ip = ipAddress.replaceAll("\\.", "");
          %>
          Or, <a id="guest-login" href="/login?username=guest&ip=<%=ip%>">log in as a guest</a>
        </div>
      </div>
      <div class="form-group">
        <div class="col-xs-offset-3 col-xs-4">
          <a href="/forgotPassword">Forgot your password?</a><br> <a href="loadRegistrationPage">Register an
          account</a>
        </div>
      </div>
    </s:form>
  </section>

  <%@include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
</body>
</html>
