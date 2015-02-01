<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
  <title>CHEMBENCH | Cheminformatics Tools</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <link rel="icon" href="theme/img/mml.ico" type="image/ico"></link>
  <link rel="SHORTCUT ICON" href="theme/img/mml.ico"></link>
  <link href="theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
  <script language="JavaScript" src="javascript/chembench.js"></script>
  <script language="JavaScript" src="javascript/sortableTable.js"></script>
  <script language="JavaScript">

    function validateSoftwareLinkForm() {
      var func = document.getElementById('function').value;
      var name = document.getElementById('name').value;
      var avail = document.getElementById('availability').value;

      if (name.length == 0) {
        alert("Please enter a name for the software.");
        return false;
      }

      if (avail.length == 0) {
        alert("Please enter the software's availability.");
        return false;
      }

      if (func.length == 0) {
        alert("Please enter the software's functions.");
        return false;
      }

      return true;
    }

  </script>

</head>
<body onload="setTabToHome();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td>
      <%@include file="/jsp/main/header.jsp" %>
    </td>
  </tr>
</table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td>
      <%@include file="/jsp/main/centralNavigationBar.jsp" %>
    </td>
  </tr>
</table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="180" colspan="5" valign="top" background="theme/img/backgrindex.jpg"
        STYLE="background-repeat: no-repeat;"><br />

      <p class="StandardTextDarkGrayParagraph">
        <b>Links to Cheminformatics Tools</b>
      </p> <br />
      <table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td>
            <p class="StandardTextDarkGrayParagraph">This page links to other online cheminformatics resources and
              downloadable software. If you know of software that is not listed here, please use the form at the
              bottom to add a link to it!</p>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<s:set name="loggedInUser" value="userName" />

<!-- Tables of software links, one table for each software type -->
<s:iterator value="softwareTypes" var="softwareType">
  <table width="924" align="center">
    <tr>
      <td><br />
        <br />

        <p class="StandardTextDarkGrayParagraph2">
          <b><s:property value="value" /></b>
        </p></td>
    </tr>
  </table>

  <table width="924" align="center" class="sortable" id="<s:property value="value" />">
    <tr>
      <th class="TableRowText01">Name</th>
      <th class="TableRowText01">Function</th>
      <th class="TableRowText01">Availability</th>
      <th class="TableRowText01">Reference</th>
      <th class="TableRowText01">Submitted By</th>
      <th class="TableRowText01_unsortable">Delete</th>
    </tr>
    <s:iterator value="softwareLinks">
      <s:if test="type==#softwareType.value">
        <tr>
          <td class="TableRowText02">
            <s:if test="url!=''">
            <a href="<s:property value="url" />">
              </s:if>
              <s:else>
              <b>
                </s:else>
                  <s:property value="name" />
                <s:if test="url!=''">
            </a>
            </s:if>
            <s:else>
              </b>
            </s:else></td>
          <td class="TableRowText02"><s:property value="function" /></td>
          <td class="TableRowText02"><s:property value="availability" /></td>
          <td class="TableRowText02"><s:property value="reference" /></td>
          <td class="TableRowText02"><s:property value="userName" /></td>
          <td class="TableRowText02"><s:if test="userIsAdmin || userName==#loggedInUser">
            <a href="deleteSoftwareLink?id=<s:property value="id" />">Delete</a>
          </s:if></td>

        </tr>
      </s:if>
    </s:iterator>
  </table>
</s:iterator>

<br />
<br />
<!-- Add a Resource -->
<s:form id="addSoftware" action="addSoftware" enctype="multipart/form-data" theme="simple">
  <table width="924" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
    <tbody>
    <tr>
      <td width="100%" height="24" align="left" colspan="2">
        <div class="StandardTextDarkGrayParagraph2">
          <br />
          <b>Add a Resource</b>
        </div>
        <br /> <s:if test="userName!=''">
        <div class="StandardTextDarkGrayParagraph">
          <i>(Fields marked with a * are required.)</i>
        </div>
      </s:if>
      </td>
    </tr>

    <s:if test="userName!=''">
      <!-- only allow logged in users to do this -->
      <tr>
        <td height="26">
          <div align="right" class="StandardTextDarkGray">
            <b>Select a Type: </b>
          </div>
        </td>
        <td align="left" valign="top"><s:select name="type" list="softwareTypes" id="type" listKey="key"
                                                listValue="value" /></td>
      </tr>
      <s:if test="userIsAdmin">
        <tr>
          <td height="26">
            <div align="right" class="StandardTextDarkGray">
              <b>Or, Add New Type (admin only): </b>
            </div>
          </td>
          <td align="left" valign="top"><s:textfield name="newType" id="newType" size="60" maxlength="950" /></td>
        </tr>
      </s:if>
      <tr>
        <td height="26">
          <div align="right" class="StandardTextDarkGray">
            <b>Name * : </b>
          </div>
        </td>
        <td align="left" valign="top"><s:textfield name="name" id="name" size="60" maxlength="950" /></td>
      </tr>
      <tr>
        <td height="26">
          <div align="right" class="StandardTextDarkGray">
            <b>URL : </b>
          </div>
        </td>
        <td align="left" valign="top"><s:textfield name="url" id="url" size="60" maxlength="950" /></td>
      </tr>
      <tr>
        <td height="26">
          <div align="right" class="StandardTextDarkGray">
            <b>Function * : </b>
          </div>
        </td>
        <td align="left" valign="top"><s:textfield name="function" id="function" size="60" maxlength="950" /></td>
      </tr>
      <tr>
        <td height="26">
          <div align="right" class="StandardTextDarkGray">
            <b>Availability * : </b>
          </div>
        </td>
        <td align="left" valign="top"><s:textfield name="availability" id="availability" size="60"
                                                   maxlength="950" /></td>
      </tr>
      <tr>
        <td height="26">
          <div align="right" class="StandardTextDarkGray">
            <b>Reference : </b>
          </div>
        </td>
        <td align="left" valign="top"><s:textfield name="reference" id="reference" size="60" maxlength="950" /></td>
      </tr>
      <tr>
        <td height="26">
          <div align="right" class="StandardTextDarkGray">
            <b>&nbsp;</b>
          </div>
        </td>
        <td align="left" valign="top"><input type="button" name="userAction" id="userAction"
                                             onclick="if(validateSoftwareLinkForm()) { document.forms['addSoftware'].submit(); }"
                                             value="Submit" /></td>
      </tr>
    </s:if>

    <s:else>
      <!-- User isn't logged in; make them do that first. -->
      <tr>
        <td align="left" colspan="2">
          <div class="StandardTextDarkGrayParagraph" align="left">
            <i>To add a new link, you must <a href="home">log in</a> first. If you don't have an account yet,
              you can register one. Registration is fast and free, and will give you access to all the Chembench
              tools.
            </i>
          </div>
          <br />
        </td>
      </tr>
    </s:else>
    </tbody>
  </table>
</s:form>

</div>
</td>
</tr>
</tbody>
</table>

<%@include file="/jsp/main/footer.jsp" %>
</body>
</html>