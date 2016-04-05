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
