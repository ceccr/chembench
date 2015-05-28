<%@ taglib prefix="s" uri="/struts-tags" %>

<header>
  <div id="masthead">
    <h1>
      <s:a action="home"><img src="${pageContext.request.contextPath}/assets/images/masthead.jpg" width="312"
                              height="66" alt="Chembench"></s:a>
    </h1>

    <div class="userbox">
      <s:if test="#session['user']!=null">
        Logged in as
        <s:if test="#session['user'].userName.contains('guest')">
          a <strong>guest</strong>.
        </s:if>
        <s:else>
          <strong><s:property value="#session['user'].userName" /></strong>.
        </s:else>
        <ul class="userbox-links">
          <li><s:a action="logout">log out</s:a></li>
          <li><s:a action="overview" namespace="help">help</s:a></li>
          <s:if test="!#session['user'].userName.contains('guest')">
            <li><s:a action="editProfile">edit profile</s:a></li>
          </s:if>
          <s:if test="#session['user'].isAdmin=='YES'">
            <li><s:a action="admin">admin</s:a></li>
          </s:if>
        </ul>
      </s:if>
    </div>
  </div>

  <nav>
    <ul class="nav-list">
      <li id="nav-button-home"><s:a action="home">Home</s:a></li>
      <li id="nav-button-mybench"><s:a action="jobs">My Bench</s:a></li>
      <li id="nav-button-datasets"><s:a action="dataset">Datasets</s:a></li>
      <li id="nav-button-modeling"><s:a action="modeling">Modeling</s:a></li>
      <li id="nav-button-prediction"><s:a action="prediction">Prediction</s:a></li>
    </ul>
  </nav>

  <noscript>
    <div id="no-js-warning" class="alert alert-danger">
      <strong>Warning:</strong> JavaScript is disabled on your computer. Some parts of Chembench may not work properly.
      Please enable JavaScript.
    </div>
  </noscript>

</header>
