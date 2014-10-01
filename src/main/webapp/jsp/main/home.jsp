<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html>
<head>
<%@ include file="/jsp/main/head.jsp"%>
<title>Chembench | Home</title>
</head>
<body>
  <div id="main" class="container">
    <%@ include file="/jsp/main/header.jsp"%>

    <section id="content">
      <div class="row">
        <section class="col-xs-8">
          <h2>Accelerating chemical genomics research</h2>
          <hr>
          <div>
            <p>Chembench is a free portal that enables researchers to mine available chemical and biological data.
              Chembench can help researchers rationally design or select new compounds or compound libraries with
              significantly enhanced hit rates in screening experiments.</p>
            <img class="interstitial-banner" src="assets/images/home-banner.jpg" alt="Molecule image banner">
            <p>
              It provides cheminformatics research support to molecular modelers, medicinal chemists and quantitative
              biologists by integrating robust model builders, property and activity predictors, virtual libraries of
              available chemicals with predicted biological and drug-like properties, and special tools for chemical
              library design. Chembench was initially developed to support researchers in the <a
                href="http://mli.nih.gov/mli/">Molecular Libraries Probe Production Centers Network (MLPCN)</a> and the
              Chemical Synthesis Centers.
            </p>
            <p>
              Please cite this website using the following URL: <a href="http://chembench.mml.unc.edu">http://chembench.mml.unc.edu</a>
            </p>
            <hr>
            <p>
              The Carolina Cheminformatics Workbench (Chembench) is developed by the Carolina Exploratory Center for
              Cheminformatics Research (CECCR) with the support of the <a href="http://www.nih.gov" target="_blank">National
                Institutes of Health</a> (grants <a
                href="http://projectreporter.nih.gov/project_info_details.cfm?aid=7472715&icde=4746318">P20HG003898</a>
              and <a href="http://projectreporter.nih.gov/project_info_description.cfm?aid=7818406&icde=4746305">R01GM066940</a>
              ) and the Environmental Protection Agency (RD83382501 and RD832720). This website has been developed using
              grants from the EPA and NIH. Therefore Chembench adheres to their required terms of use.
            </p>
          </div>
        </section>

        <section class="col-xs-4">
          <s:if test="user == null">
            <h3>Please log in</h3>
          </s:if>
          <s:else>
            <h3>Welcome back</h3>
          </s:else>
          <s:if test="loginFailed=='YES'">
            <div class="alert alert-danger">Username or password incorrect.</div>
          </s:if>
          <s:if test="user==null">
            <s:form action="login" enctype="multipart/form-data" cssClass="form-horizontal" method="post" theme="simple">
              <div class="form-group">
                <label for="username" class="col-xs-4 control-label">Username:</label>
                <div class="col-xs-8">
                  <s:textfield name="username" id="username" cssClass="form-control" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <label for="password" class="col-xs-4 control-label">Password:</label>
                <div class="col-xs-8">
                  <s:password name="password" id="password" cssClass="form-control" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-4 col-xs-8">
                  <input class="login-button" value="Log in" type="submit">
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-4 col-xs-8">
                  <%
                      String ipAddress = request.getHeader("X-FORWARDED-FOR");
                      if (ipAddress == null) {
                          ipAddress = request.getRemoteAddr();
                      }
                      String ip = ipAddress.replaceAll("\\.", "");
                  %>
                  Or, <a id="guest-login" href="/login?username=guest&ip=<%=ip%>">log in as a guest</a>
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-4 col-xs-8">
                  <a href="/forgotPassword">Forgot your password?</a><br> <a href="loadRegistrationPage">Register
                    an account</a>
                </div>
              </div>
            </s:form>
          </s:if>

          <s:if test="user != null && !user.userName.isEmpty()">
            <s:if test="user.userName.contains('guest')">
              Logged in as a <strong>guest</strong>.
          </s:if>
            <s:else>
              Logged in as <strong><s:property value="user.userName" /></strong>.
          </s:else>
            <button class="logout-button" type="button">Log out</button>
          </s:if>

          <h3>Help &amp; Links</h3>
          <p>
            <a href="help-overview" target="_blank">Chembench Overview</a><br> <a href="help-workflows"
              target="_blank">Chembench Workflows &amp; Methodology</a> <br> <a href="softwareList" target="_blank">Links
              to More Cheminformatics Tools</a>
          </p>
          <s:if test="showStatistics!=null || showStatistics=='NO'">
            <h3>Stats</h3>
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
        </section>
      </div>
      <hr>
      <section>
        <p class="sponsor-message">We thank the following commercial sponsors for their support:</p>
        <ul class="sponsor-list">
          <li><a href="http://www.chemcomp.com" target="_blank"><img src="assets/images/sponsors/ccg.jpg"
              alt="Chemical Computing Group" class="img-thumbnail"></a></li>
          <li><a href="http://www.talete.mi.it" target="_blank"><img src="assets/images/sponsors/talete.jpg"
              alt="Talete srl" class="img-thumbnail"></a></li>
          <li><a href="http://www.chemaxon.com" target="_blank"><img src="assets/images/sponsors/chemaxon.jpg"
              class="img-thumbnail"></a></li>
          <li><a href="http://www.edusoft-lc.com" target="_blank"><img src="assets/images/sponsors/edusoft.jpg"
              class="img-thumbnail"></a></li>
          <li><a href="http://www.sunsetmolecular.com" target="_blank"><img
              src="assets/images/sponsors/sunsetmolecular.jpg" class="img-thumbnail"></a></li>
        </ul>
      </section>

      <%@include file="/jsp/main/footer.jsp"%>
    </section>
  </div>
  <%@ include file="/jsp/main/tail.jsp"%>
  <script src="assets/js/home.js"></script>
</body>
</html>
