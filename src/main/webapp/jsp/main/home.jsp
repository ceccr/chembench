<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html>
<head>
<title>Chembench | Home</title>
<%@ include file="/jsp/main/head.jsp"%>
</head>
<body>
  <div class="outer">

    <div class="includesHeader"><%@ include file="/jsp/main/header.jsp"%></div>
    <div class="includesNavbar"><%@ include file="/jsp/main/centralNavigationBar.jsp"%></div>

    <div class="homeLeft topMarginBench">
      <h2 class="ccbHomeHeadings">Accelerating chemical genomics research</h2>
      <hr>
      <div class="ccbHomeStandard">
        <p>Chembench is a free portal that enables researchers to mine available chemical and biological data.
          Chembench can help researchers rationally design or select new compounds or compound libraries with
          significantly enhanced hit rates in screening experiments.</p>
        <img style="width: 100%; height: 101px;" alt="molecular_image"
          src="/theme/ccbTheme/images/ccbHomeMolecule3d.jpg"> <br> <br>
        <p>
          It provides cheminformatics research support to molecular modelers, medicinal chemists and quantitative
          biologists by integrating robust model builders, property and activity predictors, virtual libraries of
          available chemicals with predicted biological and drug-like properties, and special tools for chemical library
          design. Chembench was initially developed to support researchers in the <a href="http://mli.nih.gov/mli/">Molecular
            Libraries Probe Production Centers Network (MLPCN)</a> and the Chemical Synthesis Centers.
        </p>
        <p>
          Please cite this website using the following URL: <a href="http://chembench.mml.unc.edu">http://Chembench.mml.unc.edu</a>
        </p>
        <hr>
        <p>
          The Carolina Cheminformatics Workbench (Chembench) is developed by the Carolina Exploratory Center for
          Cheminformatics Research (CECCR) with the support of the <a href="http://www.nih.gov" target="_blank">National
            Institutes of Health</a> (grants <a
            href="http://projectreporter.nih.gov/project_info_details.cfm?aid=7472715&icde=4746318">P20HG003898</a> and
          <a href="http://projectreporter.nih.gov/project_info_description.cfm?aid=7818406&icde=4746305">R01GM066940</a>
          ) and the Environmental Protection Agency (RD83382501 and RD832720). This website has been developed using
          grants from the EPA and NIH. Therefore Chembench adheres to their required terms of use.
        </p>
      </div>
    </div>
    <div class="homeRight topMarginBench bottomMarginDataset">
      <div class="ccbHomeStandard border">
        <s:if test="user == null">
          <span class="ccbLoginBoxHeading">Please log in</span>
        </s:if>
        <s:else>
          <span class="ccbLoginBoxHeading">Welcome back</span>
        </s:else>
        <s:if test="user==null">
          <s:form action="login" enctype="multipart/form-data" method="post" theme="simple">
            <table>
              <tbody>
                <tr>
                  <td>Username:</td>
                  <td><s:textfield name="username" id="username" size="12" theme="simple" /></td>
                </tr>
                <tr>
                  <td>Password:</td>
                  <td><s:password name="password" id="password" size="12" theme="simple" /></td>
                </tr>
                <tr>
                  <td><input class="login-button" value="Log in" type="submit"></td>
                </tr>
              </tbody>
            </table>
          </s:form>
          <%
              String ipAddress = request.getHeader("X-FORWARDED-FOR");
                  if (ipAddress == null) {
                      ipAddress = request.getRemoteAddr();
                  }
                  String ip = ipAddress.replaceAll("\\.", "");
          %>
                Or, <a id="guest-login" href="/login?username=guest&ip=<%=ip%>">log in as a guest</a>
          <br>
        </s:if>
        <br>
        <s:if test="loginFailed=='YES'">
          <br>
          <font color="red">Username or password incorrect. </font>
          <br>
        </s:if>
        <s:if test="user==null">
                Forget your password?
                <a href="/forgotPassword">click here</a>
          <br>
        </s:if>
        <s:if test="user!=null">
          <s:if test="user.userName!=''">
            <s:if test="user.userName.contains('guest')">
              Logged in as a <strong>guest</strong>
            </s:if>
            <s:else>
              Logged in as <strong><s:property value="user.userName" /></strong>
            </s:else>
          </s:if>
          <s:else>
            <span class="StandardTextDarkGray4"> ERROR: Username empty. Please log out or restart your browser.</span>
          </s:else>
          <button class="logout-button" type="button">Log out</button>
        </s:if>
        <br> <span class="ccbLoginBoxHeading">New Users</span> <br> Please <a href="loadRegistrationPage">register
          here</a> <br> <br> <span class="ccbLoginBoxHeading">Help &amp; Links</span> <br> <a
          href="help-overview" target="_blank">Chembench Overview</a> <br> <a href="help-workflows" target="_blank">Chembench
          Workflows &amp; Methodology</a> <br> <a href="softwareList" target="_blank">Links to More Cheminformatics
          Tools</a> <br> <br>
        <s:if test="showStatistics!=null || showStatistics=='NO'">
          <div class="ccbLoginBoxHeading">Statistics</div>
          <s:property value="visitors" />
          <br>
          <s:property value="userStats" />
          <br>
          <s:property value="jobStats" />
          <br>
          <s:property value="cpuStats" />
          <br>
          <s:property value="activeUsers" />
          <br>
          <s:property value="runningJobs" />
        </s:if>
      </div>
      <br>
      <div class="ccbHomeStandard border">
        <span class="ccbLoginBoxHeading">We thank the following commercial vendors:</span>
        <table>
          <tbody>
            <tr>
              <td><a href="http://www.chemcomp.com/"><img src="/theme/img/logos/CCG.jpg"></a></td>
              <td><a href="http://www.talete.mi.it/"><img src="/theme/img/logos/Talete.jpg"></a></td>
            </tr>
            <tr>
              <td><a href="http://www.chemaxon.com/"><img src="/theme/img/logos/ChemAxon.jpg"></a></td>
              <td><a href="http://www.edusoft-lc.com"><img src="/theme/img/logos/edusoft.jpg"></a></td>
            </tr>
            <tr>
              <td><a href="http://www.sunsetmolecular.com/"><img src="/theme/img/logos/sunsetMolecularLogo.png"></a></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <%@include file="/jsp/main/footer.jsp"%>
  </div>

  <%@ include file="/jsp/main/tail.jsp"%>
  <script>
  <!-- focus the username input box -->
  $(document).ready(function() {
      $('input[name="username"]').focus();

      setTabToHome();

      $("#guest-login").click(function() {
          var guestMessage = "A guest account allows a user to explore the functionality of Chembench using " +
                  "publicly available datasets, predictions on single molecules, and modeling using Random Forests. " +
                  "All guest data is deleted when you leave the site or become inactive for 90 minutes. For " +
                  "additional functionality, please register an account.";
          alert(guestMessage);
      });

      $(".logout-button").click(function() {
          logout();
      });
  });
  </script>
</body>
</html>
