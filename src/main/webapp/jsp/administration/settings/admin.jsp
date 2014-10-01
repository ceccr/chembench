<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" import="java.util.*"%>

<html>
<head>
<title>Chembench | Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/ccbStyle.css" rel="stylesheet" type="text/css">
<link href="theme/ccbStyleNavBar.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="theme/screen.css" type="text/css" media="screen, projection">
<link rel="stylesheet" href="theme/print.css" type="text/css" media="print">
<link href="theme/standard.css" rel="stylesheet" type="text/css">
<link href="theme/links.css" rel="stylesheet" type="text/css">
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css">
<link rel="icon" href="/theme/img/mml.ico" type="image/ico">
<link rel="SHORTCUT ICON" href="/theme/img/mml.ico">
<link href="theme/customStylesheet.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.min.css" />

<script src="javascript/chembench.js"></script>
<script src="javascript/admin.js"></script>
<script language="JavaScript" src="javascript/sortableTable.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
</head>
<body onload="setTabToHome();">

  <!-- headers -->
  <div id="bodyDIV"></div>
  <!-- used for the "Please Wait..." box. Do not remove. -->
  <div class="outer">
    <div class="includesHeader"><%@include file="/jsp/main/header.jsp"%></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp"%></div>

    <!--  page content -->
    <div class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
      <div id="tabs">
        <ul>
          <li><a href="#datasets-predictors">Datasets &amp; Predictors</a></li>
          <li><a href="#message">Messages</a></li>
          <li><a href="#users">Users</a></li>
          <li><a href="adminJobs">Jobs</a></li>
        </ul>

        <div id="message" class="StandardTextDarkGrayParagraph">
          <b class="StandardTextDarkGrayParagraph2"> Add new global message: </b>
          <form action="globalNotifAdd">
            <table width="680" border="0">
              <tr>
                <td>Start Date:</td>
                <td><s:textfield name="startDate" value="" size="43" theme="simple" /></td>
              </tr>
              <tr>
                <td>End Date:</td>
                <td><s:textfield name="endDate" value="" size="43" theme="simple" /></td>
              </tr>
              <tr>
                <td>Message:</td>
                <td><s:textfield name="message" value="" size="43" theme="simple" /></td>
              </tr>
              <tr>
                <td></td>
                <td><input type="submit" value="Add" /></td>
              </tr>
            </table>
          </form>
        </div>

        <div id="datasets-predictors" class="StandardTextDarkGrayParagraph">
          <div class="StandardTextDarkGrayParagraph2">
            <b>Dataset Management</b>
          </div>

          <div class="StandardTextDarkGrayParagraph">
            You can promote anyone's dataset to public with dataset name and user name. If you want to delete a public
            dataset, you only need to give the dataset name.
            <form onsubmit="return confirm('Are you sure?')">
              <table width="680" border="0">
                <tr>
                  <td>Dataset Name:</td>
                  <td><s:textfield name="datasetName" value="" size="43" theme="simple" /></td>
                </tr>
                <tr>
                  <td>User name:</td>
                  <td><s:textfield name="userName" value="" size="43" theme="simple" /></td>
                </tr>
                <tr>
                  <td></td>
                  <td><input type="submit" name="promote" onclick="this.form.action='makeDatasetAction'"
                    value="Promote" /> <input type="submit" name="delete"
                    onclick="this.form.action='deleteDatasetAction'" value="Delete" /></td>
                </tr>
              </table>
            </form>
          </div>

          <div class="StandardTextDarkGrayParagraph2">
            <b>Predictor Management</b>
          </div>

          <div class="StandardTextDarkGrayParagraph">
            You can promote anyone's predictor to public with predictor name, user name and set the predictor type. If
            you want to delete a public predictor, you only need to give the predictor name.
            <form onsubmit="return confirm('Are you sure?')">
              <table width="680" border="0">
                <tr>
                  <td>Predictor name:</td>
                  <td><s:textfield name="predictorName" value="" size="43" theme="simple" /></td>
                </tr>
                <tr>
                  <td>User name:</td>
                  <td><s:textfield name="userName" value="" size="43" theme="simple" /></td>
                </tr>
                <tr>
                  <td>Predictor type:</td>
                  <td><s:select name="predictorType" theme="simple"
                      list="#{'':'','DrugDiscovery':'Drug Discovery','ADME':'ADME','Toxicity':'Toxicity','Transporters':'Transporters'}"
                      value="" /></td>
                </tr>
                <tr>
                  <td></td>
                  <td><input type="submit" name="promote" onclick="this.form.action='makePredictorPublicAction'"
                    value="Promote" /> <input type="submit" name="delete"
                    onclick="this.form.action='deletePredictorAction'" value="delete" /></td>
                </tr>
              </table>
            </form>
          </div>
        </div>

        <div id="users" class="StandardTextDarkGrayParagraph">
          <h3>User Impersonation</h3>
          <p>
            As an administrator for QA purposes you can log in as another user here. Note that this will log you out of
            your current session. <br> <label for="impersonation-target">User to log in as: <input
              type="text" id="impersonation-target" /></label> <input type="submit" name="impersonate" value="Log in" />
          </p>

          <div>
            <a href="#" onclick="window.open('/emailToAll','emailToAll','width=1000,height=700')">Send email to all
              users</a> (opens in a new window) <br /> <a href="#" id="sendToAll">Send email to selected users</a> <br />
            <div id="sendEmail" style="display: none; background-color: silver; border: 1px;">
              <form action="emailSelectedUsers">
                <b>Emails need to have HTML markup in them or they will look silly.</b><br />
                <br />
                <table width="480" border="0">
                  <tr>
                    <td width="35" height="18">From:</td>
                    <td>ceccr@email.unc.edu</td>
                  </tr>
                  <tr>
                    <td width="60" height="18">Send to:</td>
                    <td><s:textarea value="" theme="simple" id="sendTo" name="sendTo" /></td>
                  </tr>
                  <tr>
                    <td width="35" height="18">Subject:</td>
                    <td><s:textfield name="emailSubject" value="" size="43" theme="simple" /></td>
                  </tr>
                  <tr>
                    <td height="160" colspan="2"><s:textarea name="emailMessage" value="" rows="10" cols="45"
                        theme="simple" /></td>
                  </tr>
                  <tr>
                    <td width="35" height="18"></td>
                    <td><input type="submit" onclick="return checkContent()" value="Send" /></td>
                  </tr>
                </table>
                <br />
              </form>
            </div>
            <a href="#" id="reset-all-passwords">Reset <strong>all</strong> passwords
            </a> (use caution!)
          </div>

          <table class="sortable" id="userTable">
            <tr>
              <th class="TableRowText01">User Name</th>
              <th class="TableRowText01">Organization</th>
              <th class="TableRowText01">Country</th>
              <th class="TableRowText01">Last Login</th>
              <th class="TableRowText01_unsortable">Can Download Descriptors</th>
              <th class="TableRowText01_unsortable">Admin</th>
              <th class="TableRowText01_unsortable">Send email to</th>
              <th class="TableRowText01_unsortable">Reset password</th>
              <th class="TableRowText01_unsortable">Delete</th>
            </tr>
            <s:iterator value="users">
              <s:if test="!email.contains('ceccr@email.unc.edu')">
                <tr>
                  <td class="TableRowText02"><a href="mailto:<s:property value="email" />"><span
                      class="username"><s:property value="userName" /></span></a><br />
                  <s:property value="firstName" />&nbsp;<s:property value="lastName" /><br>
                  <span class="email"><s:property value="email" /></span></td>
                  <td class="TableRowText02"><s:property value="orgName" /></td>
                  <td class="TableRowText02"><s:property value="country" /></td>
                  <td class="TableRowText02"><s:date name="lastLogintime" format="yyyy-MM-dd HH:mm" /></td>
                  <td class="TableRowText02"><input type="checkbox"
                    onclick="loadUrl('/changeUserDescriptorDownloadStatus?userToChange=<s:property value="userName" />')"
                    <s:if test="canDownloadDescriptors=='YES'">checked</s:if> /></td>
                  <td class="TableRowText02"><input type="checkbox"
                    onclick="loadUrl('/changeUserAdminStatus?userToChange=<s:property value="userName" />')"
                    <s:if test="isAdmin=='YES'">checked</s:if>
                    <s:if test="userName==user.userName">disabled="true"</s:if> /></td>
                  <td class="TableRowText02"><input type="checkbox"
                    <s:if test="userName.contains('guest')||userName.contains('all-users')">disabled="true"</s:if>
                    name="emailSelected" value="<s:property value="email" />" /></td>
                  <td class="TableRowText02"><a class="reset-user-password" href="#">reset pw</a></td>
                  <td class="TableRowText02"><a onclick="return confirmDelete('user')"
                    href="deleteUser?userToDelete=<s:property value="userName" />">delete</a></td>
                </tr>
              </s:if>
            </s:iterator>
          </table>
        </div>
      </div>
    </div>
    <div class="includes"><%@include file="/jsp/main/footer.jsp"%></div>
  </div>

  <script type="text/javascript">
    function checkContent() {
        if (document.getElementById("content").value == "") {
            return (window.confirm("Send emails without content?"));
        } else {
            return true;
        }
    }

    $(document).ready(function() {
        $("#tabs").tabs();

        $('input:checkbox[name=emailSelected]').click(function(event) {
            var s = "";
            $('input:checkbox[name=emailSelected]:checked').each(function(){
                s+=$(this).val()+";";
            });
            $('#sendTo').val(s);
        });

        $("#sendToAll").click(function(event) {
            var s = "";
            $('input:checkbox[name=emailSelected]:checked').each(function(){
                s+=$(this).val()+";";
            });
            $('#sendTo').val(s);
            $('#sendEmail').show();
        });

        $("input[name='impersonate']").click(function(event) {
            event.preventDefault();
            var targetUser = $("input#impersonation-target").val();
            $.post("/login", { username: targetUser }, function(data) {
                document.open();
                document.write(data);
                document.close();
            });
        });

        $("#reset-all-passwords").click(function(event) {
            event.preventDefault();
            if (window.confirm('Are you absolutely sure you wish to reset passwords for ALL users? This cannot be undone!')) {
                $(".reset-user-password").each(function(){
                    var username = $(this).parents("tr").find(".username").html();
                    var email = $(this).parents("tr").find(".email").html();
                    $.post("/resetPassword", { userName: username, email: email });
                    console.log(username + " / " + email);
                });
            }
            alert('Resets complete.');
        });

        $(".reset-user-password").click(function(event) {
            event.preventDefault();
            var username = $(this).parents("tr").find(".username").html();
            var email = $(this).parents("tr").find(".email").html();
            if (window.confirm('Are you sure you wish to reset the password for user "' + username + '"?')) {
                $.post("/resetPassword", { userName: username, email: email }, function(data) {
                    alert('Password reset for user "' + username + '".');
                });
            }
        });
    });
</script>
</body>
</html>

