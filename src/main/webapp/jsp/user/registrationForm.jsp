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
    <h2>Registration</h2>

    <s:if test="!getActionErrors().isEmpty()">
      <div class="alert alert-danger">
        <s:iterator value="getActionErrors()">
          <s:property /><br>
        </s:iterator>
      </div>
    </s:if>

    <p class="margin-below">Thank you for your interest in Chembench. Please complete the form below to sign up for an
      account. Fields marked with an asterisk (<span class="glyphicon glyphicon-asterisk"></span>) are required.
    </p>

    <s:form action="registerUser" cssClass="form-horizontal" theme="simple">
      <div class="form-group required">
        <label for="firstName" class="control-label col-xs-3">First Name:</label>

        <div class="col-xs-4">
          <s:textfield id="firstName" name="firstName" cssClass="form-control" theme="simple" />
        </div>
      </div>

      <div class="form-group required">
        <label for="lastName" class="control-label col-xs-3">Last Name:</label>

        <div class="col-xs-4">
          <s:textfield id="lastName" name="lastName" cssClass="form-control" theme="simple" />
        </div>
      </div>

      <hr>
      <div class="form-group required">
        <label for="organizationType" class="control-label col-xs-3">Type of Organization:</label>

        <div class="col-xs-4">
          <s:select id="organizationType" name="organizationType" cssClass="form-control"
                    list="#{'Academia':'Academia','Government':'Government','Industry':'Industry','Nonprofit':'Nonprofit','Other':'Other'}" />
        </div>
      </div>

      <div class="form-group required">
        <label for="organizationName" class="control-label col-xs-3">Name of Organization:</label>

        <div class="col-xs-4">
          <s:textfield id="organizationName" name="organizationName" cssClass="form-control" />
        </div>
      </div>

      <div class="form-group required">
        <label for="organizationPosition" class="control-label col-xs-3">Position in Organization:</label>

        <div class="col-xs-4">
          <s:textfield id="organizationPosition" name="organizationPosition" cssClass="form-control" />
        </div>
      </div>

      <hr>
      <div class="form-group">
        <label for="address" class="control-label col-xs-3">Address:</label>

        <div class="col-xs-4">
          <s:textfield id="address" name="address" cssClass="form-control" />
        </div>
      </div>

      <div class="form-group required">
        <label for="city" class="control-label col-xs-3">City:</label>

        <div class="col-xs-4">
          <s:textfield id="city" name="city" cssClass="form-control" />
        </div>
      </div>

      <div class="form-group">
        <label for="stateOrProvince" class="control-label col-xs-3">State or Province:</label>

        <div class="col-xs-4">
          <s:textfield id="stateOrProvince" name="stateOrProvince" cssClass="form-control" />
        </div>
      </div>

      <div class="form-group">
        <label for="zipCode" class="control-label col-xs-3">ZIP Code:</label>

        <div class="col-xs-4">
          <s:textfield id="zipCode" name="zipCode" cssClass="form-control" />
        </div>
      </div>

      <div class="form-group required">
        <label for="country" class="control-label col-xs-3">Country:</label>

        <div class="col-xs-4">
          <s:textfield id="country" name="country" cssClass="form-control" />
        </div>
      </div>

      <div class="form-group">
        <label for="phoneNumber" class="control-label col-xs-3">Phone Number:</label>

        <div class="col-xs-4">
          <s:textfield id="phoneNumber" name="phoneNumber" cssClass="form-control" />
        </div>
      </div>
      <hr>
      <div class="form-group required">
        <label for="email" class="control-label col-xs-3">Email:</label>

        <div class="col-xs-4">
          <s:textfield id="email" name="email" cssClass="form-control" />
          <p class="help-block">Please use your organization email account. Your password will be sent to this email
            address when you register.
          </p>
        </div>
      </div>

      <div class="form-group required">
        <label for="newUserName" class="control-label col-xs-3">Username:</label>

        <div class="col-xs-4">
          <s:textfield id="newUserName" name="newUserName" cssClass="form-control" />
          <p class="help-block">This is what you will use to log in.<br>
              Only alphanumeric characters (no spaces, symbols, or special characters) are allowed.</p>
        </div>
      </div>

      <div class="form-group">
        <div class="col-xs-offset-3 col-xs-4">
          <script>
            var RecaptchaOptions = {
              theme: 'white',
              tabindex: 2
            };
          </script>
          <script type="text/javascript"
                  src="https://www.google.com/recaptcha/api/challenge?k=<s:property value="recaptchaPublicKey" />"></script>
        </div>
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
