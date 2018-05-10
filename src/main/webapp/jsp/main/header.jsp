<%@ taglib prefix="s" uri="/struts-tags" %>

<header>
  <div id="masthead">
    <h1><s:a action="home" namespace="/"><img id="chembench-logo"
                                              src="${pageContext.request.contextPath}/assets/images/logo.png"
                                              height="50">
      Chembench</s:a></h1>

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
          <li><s:a action="overview" namespace="/help">help</s:a></li>
          <s:if test="!#session['user'].userName.contains('guest')">
            <li><s:a action="editProfile">edit profile</s:a></li>
          </s:if>
          <s:if test="#session['user'].isAdmin=='YES'">
            <li><s:a namespace="/admin" action="home">admin</s:a></li>
          </s:if>
        </ul>
      </s:if>
    </div>
  </div>

  <nav>
    <ul class="nav-list">
      <li id="nav-button-home"><s:a action="home" namespace="/">Home</s:a></li>
      <li id="nav-button-mybench"><s:a action="mybench" namespace="/">My Bench</s:a></li>
      <li id="nav-button-datasets"><s:a action="datasets" namespace="/">Datasets</s:a></li>
      <li id="nav-button-modeling"><s:a action="modeling" namespace="/">Modeling</s:a></li>
      <li id="nav-button-prediction"><s:a action="prediction" namespace="/">Prediction</s:a></li>
	  <li id="nav-button-mudra"><a href="/mudra">MUDRA</a></li>
    </ul>
  </nav>

  <noscript>
    <div id="no-js-warning" class="alert alert-danger">
      <strong>Warning:</strong> JavaScript is disabled on your computer. Some parts of Chembench may not work properly.
      Please enable JavaScript.
    </div>
  </noscript>

</header>
