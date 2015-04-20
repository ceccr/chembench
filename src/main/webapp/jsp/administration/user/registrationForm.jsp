<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <title>Chembench | Registration</title>
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

  <script src="javascript/chembench.js"></script>
  <script src="javascript/yahoo/yahoo.js"></script>
  <script src="javascript/dom/dom.js"></script>
  <script src="javascript/event/event.js"></script>
  <script src="javascript/container/container_core.js"></script>
  <script src="javascript/registerFormValidation.js"></script>

</head>
<body>
<div class="outer">

  <div class="includesHeader">
    <%@ include file="/jsp/main/header.jsp" %>
  </div>
  <div class="includesNavbar">
    <%@ include file="/jsp/main/centralNavigationBar.jsp" %>
  </div>
    <span id="maincontent">

      <div class="benchBackground includes">
        <div class="homeLeft">
          <br />

          <div class="StandardTextDarkGrayParagraph">
            <p>
              <br />
              <br /> Welcome to Carolina Cheminformatics Workbench (Chembench)- an integrated toolkit developed by the
              Carolina Exploratory Center for Cheminformatics Research (CECCR) with the support of the National
              Institutes of Health. <br />
              <br />Please enter your information here in order to start using Chembench. An asterisk (*) indicates
              required fields.<br /> <br />
              <br />
            </p>
          </div>
        </div>
        <div class="border StandardTextDarkGrayParagraph2 benchAlign bottomMargin">
          <s:form action="registerUser" theme="simple">

          <table border="0" align="center" width="680">

            <!-- error message (if any) -->
            <tr height="20">
              <td colspan="2" class="StandardTextDarkGray">
                <div class="StandardTextDarkGray">
                  <font color="red"><br /> <s:iterator value="errorMessages">
                    <s:property />
                    <br />
                  </s:iterator></font>
                </div>
              </td>
              <td width="250" align="left"></td>
            </tr>

            <!-- user information form -->
            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">First Name *</td>
              <td width="250"><s:textfield name="firstName" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv1"></span></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">Last Name *</td>
              <td width="250"><s:textfield name="lastName" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv2"></span></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">Type of Organization *</td>
              <td width="250"><s:select name="organizationType"
                                        list="#{'Academia':'Academia','Government':'Government','Industry':'Industry','Nonprofit':'Nonprofit','Other':'Other'}" />
              </td>
              <td width="250" align="left"></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">Name of Organization *</td>
              <td width="250"><s:textfield name="organizationName" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv4"></span></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">Position in Organization *</td>
              <td width="250"><s:textfield name="organizationPosition" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv5"></span></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">Address</td>
              <td width="250"><s:textfield name="address" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv6"></span></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">City *</td>
              <td width="250"><s:textfield name="city" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv7"></span></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">State/Province</td>
              <td width="250"><s:textfield name="stateOrProvince" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv8"></span></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">Zip Code</td>
              <td width="250"><s:textfield name="zipCode" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv9"></span></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">Country *</td>
              <td width="250"><s:textfield name="country" size="30" /></td>
              <td width="250" align="left"></td>
            </tr>

            <tr height="20">
              <td align="right" width="180" class="StandardTextDarkGray">Phone Number</td>
              <td width="250"><s:textfield name="phoneNumber" size="30" /></td>
              <td width="250" align="left"><span id="messageDiv11"></td>
            </tr>

            <tr height="6">
              <td width="180" class="StandardTextDarkGray"></td>
              <td width="250">
                <div class="StandardTextDarkGray">
                  <i>
                    <small><br />Please use your organization email account. Your password will be sent
                      to this email address when you register.
                    </small>
                  </i>
                </div>
              </td>
              <td width="250" align="left"></span></td>
  </tr>

  <tr height="20">
    <td align="right" width="180" class="StandardTextDarkGray">Email *</td>
    <td width="250"><s:textfield name="email" size="30" /></td>
    <td width="250" align="left"><span id="messageDiv12"></span></td>
  </tr>

  <!--
<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Work Bench</td>
<td width="250"><s:radio name="workBench" list="#{'cchem':'CHEM','ctox':'TOX'}" /></td>
 -->
  <!-- The idea of having a separate workbench for tox people and for chem people may come back someday. Removed it for now. -->
  <s:hidden name="workbench" value="cchem" />

  <tr height="20">
    <td align="right" width="180" class="StandardTextDarkGray">User Name *</td>
    <td width="250"><s:textfield name="newUserName" size="30" /></td>
    <td width="250" align="left"><span id="messageDiv13"></span></td>
  </tr>

  <!-- CAPTCHA -->

  <!--
<%! String image()
{
  java.util.Random generator=new java.util.Random();
  String imgNum=Integer.toString(generator.nextInt(4));
return imgNum;
}
%>
-->
  <tr height="50">
    <td align="right" width="180"></td>
    <td width="250"></td>
    <td width="250" align="left"></td>
  </tr>
  <tr height="20">
    <td align="right" width="180" class="StandardTextDarkGray"><u>Verification</u></td>
    <td width="250"></td>
    <td width="250" align="left"></td>
  </tr>
  <tr height="20">
    <td colspan="2" align="right">
      <script>
        var RecaptchaOptions = {
          theme: 'white',
          tabindex: 2
        };
      </script>
      <script type="text/javascript"
        <%-- src="http://api.recaptcha.net/challenge?k=<s:property value="recaptchaPublicKey" />"> --%>
              src="https://www.google.com/recaptcha/api/challenge?k=<s:property value="recaptchaPublicKey" />">
      </script>

      <noscript>
          <%-- <iframe src="http://api.recaptcha.net/noscript?k=<s:property value="recaptchaPublicKey" />" --%>
        <iframe
            src="https://www.google.com/recaptcha/api/noscript?k=<s:property value="recaptchaPublicKey" />"
            height="40" width="200" frameborder="0"></iframe>
        <br>
                    <textarea name="recaptcha_challenge_field" rows="1" cols="35">
   </textarea>
        <input type="hidden" name="recaptcha_response_field" value="manual_challenge">
      </noscript>
    </td>
    <td width="250" align="left"></td>
  </tr>

  <tr height="20">
    <td align="right" width="180" class="StandardTextDarkGray"></td>
    <td width="250"></td>
    <td width="250" align="left"></td>
  </tr>

  <tr height="40">
    <td align="right" width="180"></td>
    <td width="250"></td>
    <td width="250" align="left"></td>
  </tr>

  <!-- Submit Button -->

  <tr height="20">
    <td align="right" width="180"></td>
    <td width="250">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp <input
        type="button" name="userAction" id="userAction" onclick="if(true){ this.form.submit() }"
        value="Submit" />
      <!-- <html:submit property="userAction" onclick="return submitForm1(this,document.getElementById('textarea'));" value="Submit" />  -->
    </td>
    <td width="250" align="left"><span id="textarea"></span></td>
  </tr>
  <tr height="20">
    <td align="right" width="180"></td>
    <td width="250"></td>
    <td width="250" align="left"></td>
  </tr>
  <tr height="20">
    <td align="right" width="180"></td>
    <td width="250"></td>
    <td width="250" align="left"></td>
  </tr>

  </table>
  </p>
  </s:form>
  </td>
  </span>
</div>
<div class="includes">
  <%@ include file="/jsp/main/footer.jsp" %>
</div>

</div>
</body>
</html>
