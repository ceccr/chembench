<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Edit Profile</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Edit Profile</h2>

    <p>From this page, you can change your password, edit your user information, or select options to customize
      Chembench.</p>

    <s:if test="!errorMessages.isEmpty()">
      <div class="alert alert-danger">
        <s:iterator value="errorMessages">
          <s:property />
        </s:iterator>
      </div>
    </s:if>

    <ul class="nav nav-tabs">
      <li class="active"><a href="#change-password" data-toggle="tab">Change Password</a></li>
      <li><a href="#profile-information" data-toggle="tab">Profile Information</a></li>
      <li><a href="#user-options" data-toggle="tab">User Options</a></li>
    </ul>

    <div class="tab-content">
      <div id="change-password" class="tab-pane active">
        <h3>Change Password</h3>

        <p class="margin-below">To change your password, first enter your current password, then type and
          confirm your new password.</p>
        <s:form action="changePassword" enctype="multipart/form-data" cssClass="form-horizontal" theme="simple">
          <div class="form-group">
            <label for="oldPassword" class="control-label col-xs-3">Current Password:</label>

            <div class="col-xs-4">
              <s:password id="oldPassword" name="oldPassword" cssClass="form-control" theme="simple" />
            </div>
          </div>

          <hr>
          <div class="form-group">
            <label for="newPassword" class="control-label col-xs-3">New Password:</label>

            <div class="col-xs-4">
              <s:password id="newPassword" name="newPassword" cssClass="form-control" theme="simple" />
            </div>
          </div>

          <div class="form-group">
            <label for="confirmNewPassword" class="control-label col-xs-3">Confirm New Password:</label>

            <div class="col-xs-4">
              <s:password id="confirmNewPassword" name="confirmNewPassword" cssClass="form-control" theme="simple" />
            </div>
          </div>

          <div class="form-group">
            <div class="col-xs-offset-3 col-xs-4">
              <button type="submit" class="btn btn-primary">Submit</button>
            </div>
          </div>
        </s:form>
      </div>

      <div id="profile-information" class="tab-pane">
        <h3>Profile Information</h3>

        <p class="margin-below">
          Here you can update your user profile information. Note that fields marked with an asterisk (<span
            class="glyphicon glyphicon-asterisk"></span>) are required.
        </p>

        <s:form action="updateUserInfo" enctype="multipart/form-data" cssClass="form-horizontal" theme="simple">
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

          <div class="form-group required">
            <label for="email" class="control-label col-xs-3">Email Address:</label>

            <div class="col-xs-4">
              <s:textfield id="email" name="email" cssClass="form-control" />
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

          <div class="form-group">
            <div class="col-xs-offset-3 col-xs-4">
              <button type="submit" class="btn btn-primary">Submit</button>
            </div>
          </div>
        </s:form>
      </div>

      <div id="user-options" class="tab-pane">
        <h3>User Options</h3>

        <p class="margin-below">Here you can adjust user-specific settings for Chembench.</p>
        <hr>
        <s:form action="updateUserOptions" enctype="multipart/form-data" cssClass="form-horizontal" theme="simple">
          <h4>Public Datasets and Predictors</h4>

          <p>
            Chembench provides sample datasets and predictors for you to experiment with.<br> If you choose to
            hide them, they will no longer appear on the My Bench, Modeling, and Prediction pages.
          </p>

          <div class="form-group">
            <label class="control-label col-xs-4">Show Public Datasets:</label>

            <div class="inline-radio-group col-xs-8">
              <s:radio name="showPublicDatasets" value="showPublicDatasets"
                       list="#{'NONE':'None','SOME':'Some','ALL':'All'}" />
            </div>
          </div>

          <div class="form-group">
            <label class="control-label col-xs-4">Show Public Predictors:</label>

            <div class="inline-radio-group col-xs-8">
              <s:radio name="showPublicPredictors" value="showPublicPredictors" list="#{'NONE':'None','ALL':'All'}" />
            </div>
          </div>

          <div class="form-group">
            <div class="col-xs-offset-4 col-xs-8">
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
</body>
</html>
