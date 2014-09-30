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

      <ul class="nav nav-tabs">
        <li class="active"><a href="#change-password" data-toggle="tab">Change Password</a></li>
        <li><a href="#profile-information" data-toggle="tab">Profile Information</a></li>
        <li><a href="#user-options" data-toggle="tab">User Options</a></li>
      </ul>

      <div class="tab-content">
        <div id="change-password" class="tab-pane active">
          <s:form action="changePassword" enctype="multipart/form-data" cssClass="form-horizontal" theme="simple">
            <div class="form-group">
              <label for="current-password" class="control-label col-xs-3">Current Password:</label>
              <div class="col-xs-4">
                <s:password id="current-password" name="current-password" cssClass="form-control" theme="simple" />
              </div>
            </div>

            <div class="form-group">
              <label for="new-password" class="control-label col-xs-3">New Password:</label>
              <div class="col-xs-4">
                <s:password id="new-password" name="new-password" cssClass="form-control" theme="simple" />
              </div>
            </div>

            <div class="form-group">
              <label for="new-password-confirm" class="control-label col-xs-3">Confirm New Password:</label>
              <div class="col-xs-4">
                <s:password id="new-password-confirm" name="new-password-confirm" cssClass="form-control" theme="simple" />
              </div>
            </div>

            <div class="form-group">
              <div class="col-xs-offset-3 col-xs-4">
                <button type="submit" class="btn btn-default">Submit</button>
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
