<script>
  (function() {
    "use strict";

    // Chembench-specific JS globals
    window.Chembench = {
      "MODI_MODELABLE": "<s:property value="@edu.unc.ceccr.chembench.global.Constants@MODI_MODELABLE" />",
      "Constants": {
        "MODELING": "<s:property value="@edu.unc.ceccr.chembench.global.Constants@MODELING" />",
        "MODELINGWITHDESCRIPTORS": "<s:property
        value="@edu.unc.ceccr.chembench.global.Constants@MODELINGWITHDESCRIPTORS" />",
        "CONTINUOUS": "<s:property value="@edu.unc.ceccr.chembench.global.Constants@CONTINUOUS" />",
        "CATEGORY": "<s:property value="@edu.unc.ceccr.chembench.global.Constants@CATEGORY" />",
        "UPLOADED": "<s:property value="@edu.unc.ceccr.chembench.global.Constants@UPLOADED" />",
        "MYBENCH_URL": "<s:url action="mybench" />"
      }
    };
  })();
</script>

<script type="text/javascript"
        src="https://cdn.datatables.net/t/bs-3.3.6/jqc-1.12.0,dt-1.10.11/datatables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootbox.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/common.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/common.datatables.js"></script>
