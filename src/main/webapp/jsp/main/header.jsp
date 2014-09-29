<%@ taglib prefix="s" uri="/struts-tags"%>

<header>
  <div class="row">
  <h1 class="col-md-8">
    <a href=""><img src="theme/ccbTheme/images/ccbLogo.jpg" alt="Chembench"></a>
  </h1>

  <div id="userbox" class="col-md-2">
    <s:if test="#session['user']!=null">
      <div>
        Logged in as
        <s:if test="#session['user'].userName.contains('guest')">
          a <strong>guest</strong>.
        </s:if>
        <s:else>
          <strong><s:property value="#session['user'].userName" /></strong>.
        </s:else>
      </div>
      <ul id="userbox-links">
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
      <li id="nav-button-home"><a href="/home">Home</a></li>
      <li id="nav-button-mybench"><a href="/jobs">My Bench</a></li>
      <li id="nav-button-datasets"><a href="/dataset">Datasets</a></li>
      <li id="nav-button-modeling"><a href="/modeling">Modeling</a></li>
      <li id="nav-button-prediction"><a href="/prediction">Prediction</a></li>
    </ul>
  </nav>

  <noscript>Warning: JavaScript is disabled on your computer. Some parts of Chembench may not work properly.
    Please enable JavaScript.</noscript>

</header>
