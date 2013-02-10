<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ page language="java" import="java.util.*" %>

<html>
<head>
    <sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | Administration</title>
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

    <script src="javascript/script.js"></script>
    <script src="javascript/admin.js"></script>
    <script language="JavaScript" src="javascript/sortableTable.js"></script>
    <script language="javascript" src="javascript/jquery-1.6.4.min.js"></script>

    <script type="text/javascript">
        function checkContent()
        {
            if(document.getElementById("content").value=="")
            {return (window.confirm("Send emails without content?"));}
            else{return true;}
        }
    </script>
</head>
<body onload="setTabToHome();">

<!-- headers -->
<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
<div class="outer">
    <div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>

    <div><br /></div>
    <!--  page content -->

    <div class="StandardTextDarkGrayParagraph">
        Build Date: <s:property value="buildDate" /><br />
        <br />
        Documentation: <br />
        <a href="/documentation/C-Chembench Developer Guide.docx">Chembench Developer's Guide</a><br />
        <a href="/documentation/Database Design.docx">Database Design</a><br />
        <a href="/documentation/ceccr design notes.txt">Notes</a><br />
        <a href="/documentation/Install Guide.txt">Install Guide</a><br />
        <br /><br />
    </div>

    <div class="StandardTextDarkGrayParagraph">
        <a href="#" onclick="window.open('/emailToAll','emailToAll','width=1000,height=700')">Send email to all users</a> (opens in a new window)
        <br />
        <a href="#" id="sendToAll">Send email to selected users</a>
        <br />
        <div id="sendEmail" style="display: none;background-color: silver;border: 1px;">
            <form action="emailSelectedUsers">
                <b>Emails need to have HTML markup in them or they will look silly.</b><br /><br />
                <table width="480" border="0">
                    <tr><td width="35" height="18">From:</td><td>ceccr@email.unc.edu</td></tr>
                    <tr><td width="60" height="18">Send to:</td><td><s:textarea value="" theme="simple" id="sendTo" name="sendTo"/></td></tr>
                    <tr><td width="35" height="18">Subject:</td><td><s:textfield name="emailSubject" value="" size="43" theme="simple" /></td></tr>
                    <tr><td height="160" colspan="2"><s:textarea name="emailMessage" value="" rows="10" cols="45" theme="simple" /></td></tr>
                    <tr><td width="35" height="18"></td><td><input type="submit" onclick="return checkContent()" value="Send" /></td></tr>
                </table>
                <br />
            </form>
        </div>
    </div>
    <br /><br />

    <table class="sortable" id="userTable">
        <tr>
            <th class="TableRowText01">User Name</th>
            <th class="TableRowText01">First Name</th>
            <th class="TableRowText01">Last Name</th>
            <th class="TableRowText01">Organization</th>
            <th class="TableRowText01">Country</th>
            <th class="TableRowText01">Email</th>
            <th class="TableRowText01">Last Login</th>
            <th class="TableRowText01_unsortable">Can Download Descriptors</th>
            <th class="TableRowText01_unsortable">Administrator</th>
            <th class="TableRowText01_unsortable">Send email to</th>
            <th class="TableRowText01_unsortable">Delete</th>
        </tr>
        <s:iterator value="users">

            <s:if test="!email.contains('ceccr@email.unc.edu')">
                <tr>
                    <td class="TableRowText02"><s:property value="userName" /></td>
                    <td class="TableRowText02"><s:property value="firstName" /></td>
                    <td class="TableRowText02"><s:property value="lastName" /></td>
                    <td class="TableRowText02"><s:property value="orgName" /></td>
                    <td class="TableRowText02"><s:property value="country" /></td>
                    <td class="TableRowText02"><a href="mailto:<s:property value="email" />"><s:property value="email" /></a></td>
                    <td class="TableRowText02"><s:date name="lastLogintime" format="yyyy-MM-dd HH:mm" /></td>
                    <td class="TableRowText02"><input type="checkbox" onclick="loadUrl('/changeUserDescriptorDownloadStatus?userToChange=<s:property value="userName" />')" <s:if test="canDownloadDescriptors=='YES'">checked</s:if> /></td>
                    <td class="TableRowText02"><input type="checkbox" onclick="loadUrl('/changeUserAdminStatus?userToChange=<s:property value="userName" />')" <s:if test="isAdmin=='YES'">checked</s:if>  <s:if test="userName==user.userName">disabled="true"</s:if> /></td>
                    <td class="TableRowText02"><input type="checkbox" <s:if test="userName.contains('guest')||userName.contains('all-users')">disabled="true"</s:if> name="emailSelected" value="<s:property value="email" />"/></td>
                    <td class="TableRowText02"><a onclick="return confirmDelete('user')" href="deleteUser?userToDelete=<s:property value="userName" />">delete</a></td>
                </tr>
            </s:if>

        </s:iterator>
    </table>
    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
</div>
<script language="javascript">
    $(document).ready(function() {


        $('input:checkbox[name=emailSelected]').live("click",function(){
            var s = "";
            $('input:checkbox[name=emailSelected]:checked').each(function(){
                s+=$(this).val()+";";
            });
            $('#sendTo').val(s);
        });

        $("#sendToAll").live("click",function(){
            var s = "";
            $('input:checkbox[name=emailSelected]:checked').each(function(){
                s+=$(this).val()+";";
            });
            $('#sendTo').val(s);
            $('#sendEmail').show();
        });



    });

</script>
</body>
</html>