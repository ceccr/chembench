<%@ taglib prefix="s" uri="/struts-tags"%>

<header>
  <div id="masthead">
    <h1>
      <a href=""><img src="assets/images/masthead.jpg" alt="Chembench"></a>
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
          <li><a href="/logout">log out</a></li>
          <li><a href="help-overview">help</a></li>
          <s:if test="!#session['user'].userName.contains('guest')">
            <li><a href="/editProfile">edit profile</a></li>
          </s:if>
          <s:if test="#session['user'].isAdmin=='YES'">
            <li><a href="/admin">admin</a></li>
          </s:if>
        </ul>
      </s:if>
    </div>
  </div>

  <nav>
    <ul class="nav-list">
      <li id="nav-button-home"><a href=<s:url action="home" />>Home</a></li>
      <li id="nav-button-mybench"><a href=<s:url action="jobs" />>My Bench</a></li>
      <li id="nav-button-datasets"><a href=<s:url action="dataset" />>Datasets</a></li>
      <li id="nav-button-modeling"><a href=<s:url action="modeling" />>Modeling</a></li>
      <li id="nav-button-prediction"><a href=<s:url action="prediction" />>Prediction</a></li>
    </ul>
  </nav>

  <noscript>
    <div id="no-js-warning" class="alert alert-danger">
      <strong>Warning:</strong> JavaScript is disabled on your computer. Some parts of Chembench may not work properly.
      Please enable JavaScript.
    </div>
  </noscript>

</header>
