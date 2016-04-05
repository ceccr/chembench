<table class="table table-hover datatable" data-scroll="false"
       data-object-type="<s:property value="#objectType" />"
       data-is-y-random="<s:property value="#isYRandom" />">
  <thead>
  <tr>
    <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
      <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
        <th data-property="ccr"><abbr title="Correct Classification Rate" class="initialism">CCR</abbr></th>
      </s:if>
      <s:else>
        <th data-property="r2">R<sup>2</sup></th>
        <th data-property="mse"><abbr title="Mean Squared Error" class="initialism">MSE</abbr></th>
      </s:else>
      <th data-property="descriptorsUsed">Descriptors Used</th>
    </s:if>
  </tr>
  </thead>
  <tbody>
  <!-- use static table data if provided (for single-fold models) -->
  <s:iterator value="#staticTableData">
    <tr>
      <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
        <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
          <td><s:property value="ccr" /></td>
        </s:if>
        <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
          <td><s:property value="r2" /></td>
          <td><s:property value="mse" /></td>
        </s:elseif>
        <td><s:property value="descriptorsUsed" /></td>
      </s:if>
    </tr>
  </s:iterator>
  </tbody>
</table>
