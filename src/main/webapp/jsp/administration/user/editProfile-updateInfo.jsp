<%@ taglib prefix="s" uri="/struts-tags" %>


<!-- UPDATE USER INFO -->

<div id="updateUserInfo">

  <s:form action="updateUserInfo" enctype="multipart/form-data" theme="simple">
    <table border="0" align="left" width="680">

      <tr>
        <td height="24" align="left" colspan="2">
          <p class="StandardTextDarkGrayParagraph2">
            <br />
            <b>Update Information</b>
          </p>
        </td>
      </tr>

      <tr height="20">
        <td colspan="2" class="StandardTextDarkGray">
          <p align="justify" class="StandardTextDarkGrayParagraph">
            This is the information collected when you first registered for Chembench. Update any fields you need to,
            then click "Submit". An asterisk (*) indicates required fields.<br />
        </td>
        <td width="250" align="left"></td>
      </tr>

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
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">Last Name *</td>
        <td width="250"><s:textfield name="lastName" size="30" /></td>
        <td width="250" align="left"></td>
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
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">Position in Organization *</td>
        <td width="250"><s:textfield name="organizationPosition" size="30" /></td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">Address</td>
        <td width="250"><s:textfield name="address" size="30" /></td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">City *</td>
        <td width="250"><s:textfield name="city" size="30" /></td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">State/Province</td>
        <td width="250"><s:textfield name="stateOrProvince" size="30" /></td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">Zip Code</td>
        <td width="250"><s:textfield name="zipCode" size="30" /></td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">Country *</td>
        <td width="250"><s:textfield name="country" size="30" /></td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">Phone Number</td>
        <td width="250"><s:textfield name="phoneNumber" size="30" /></td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="6">
        <td width="180" class="StandardTextDarkGray"></td>
        <td width="250">
          <div class="StandardTextDarkGray">
            <i>
              <small><br />Please use your organization email account.</small>
            </i>
          </div>
        </td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180" class="StandardTextDarkGray">Email *</td>
        <td width="250"><s:textfield name="email" size="30" /></td>
        <td width="250" align="left"></td>
      </tr>

      <!-- 
<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Work Bench</td>
<td width="250"><s:radio name="workBench" list="#{'cchem':'CHEM','ctox':'TOX'}" /></td>
 -->
      <!-- The idea of having a separate workbench for tox people and for chem people may come back someday. Removed it for now. -->
      <s:hidden name="workbench" value="cchem" />

      <tr height="20">
        <td width="180" class="StandardTextDarkGray"></td>
        <td width="250">
          <div class="StandardTextDarkGray">
            <!-- spacer -->
        </td>
        <td width="250" align="left"></td>
      </tr>

      <tr height="20">
        <td align="right" width="180"></td>
        <td width="250">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp <input
            type="button" name="userAction" id="userAction" onclick="this.form.submit()" value="Submit" />
        </td>
      </tr>
    </table>
  </s:form>
</div>

<!-- END UPDATE USER INFO -->
