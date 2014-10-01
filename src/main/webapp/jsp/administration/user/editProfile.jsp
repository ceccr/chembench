<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html>
<head>
<%@ include file="/jsp/main/head.jsp"%>
<title>Chembench | Edit Profile</title>
</head>
<body>
  <div id="main" class="container">
    <%@ include file="/jsp/main/header.jsp"%>

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
          <p class="tab-description">To change your password, first enter your current password, then type and
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

        <div id="profile-information" class="tab-pane"></div>

        <div id="user-options" class="tab-pane"></div>
      </div>
    </section>

    <%@include file="/jsp/main/footer.jsp"%>
  </div>

  <%@ include file="/jsp/main/tail.jsp"%>
  <script src="assets/js/editProfile.js"></script>
</body>
</html>
