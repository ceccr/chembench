<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" import="java.util.*"%>

<div class="homeLeft">
  <img src="theme/ccbTheme/images/ccbLogo.jpg" alt="Chembench logo" id="header-image" name="header-image">
</div>

<div class="StandardTextDarkGrayParagraph loginRight">
  <s:if test="#session['user']!=null">
    <s:if test="#session['user'].userName!=''">
      <div align="right">
        <span style="color: #F00"></span><span class="StandardTextDarkGray4">Logged in as <s:if
            test="#session['user'].userName.contains('guest')">
            <b>guest</b>.
                  </s:if> <s:else>
            <b><s:property value="#session['user'].userName" /></b>.
                  </s:else>
        </span>
      </div>
      <div align="right">
        <a href="/logout">log out</a>
        <s:if test="!#session['user'].userName.contains('guest')">
                    | <a href="editProfile">edit profile</a>
        </s:if>
        | <a href="help-overview" target="_top">help pages</a>
        <s:if test="#session['user'].isAdmin=='YES'">
                    | <a href="admin">admin</a>
        </s:if>
      </div>
    </s:if>
    <s:else>
      <p align="right">
        <span><span class="StandardTextDarkGray4"> ERROR: Username empty. Logout or restart your browser.
            <button onclick="logout()" type="button" class="LoginBoxes1"
              style="border-style: solid; border-color: gray; border-width: 1px">logout</button>
        </span></span>
      </p>
    </s:else>
  </s:if>
</div>
