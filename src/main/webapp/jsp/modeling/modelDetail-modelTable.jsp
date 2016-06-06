<table class="table table-hover datatable"
       data-object-type="<s:property value="#objectType" />"
       data-is-y-random="<s:property value="#isYRandom" />"
       data-fold-url="<s:url action="viewPredictorFold" />"
       data-object-id="<s:property value="predictor.id" />">
  <thead>
  <tr>
    <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
      <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
        <th data-property="ccr"><abbr title="Correct Classification Rate" class="initialism">CCR</abbr></th>
      </s:if>
      <s:else>
        <th data-property="r2" data-sort-direction="desc">R<sup>2</sup></th>
        <th data-property="mse"><abbr title="Mean Squared Error" class="initialism">MSE</abbr></th>
      </s:else>
      <th data-property="descriptorsUsed">Descriptors Used</th>
    </s:if>
    <s:elseif test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@KNNSA ||
                    predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@KNNGA">
      <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
        <th data-property="KOrR">k</th>
        <th data-property="accuracyTest">Test Accuracy</th>
        <th data-property="CCRNormalizedAccuracyTest" data-sort-direction="desc">Normalized Test Accuracy</th>
        <th data-property="accuracyTraining">Training Accuracy</th>
        <th data-property="CCRNormalizedAccuracyTraining">Normalized Training Accuracy</th>
      </s:if>
      <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
        <th data-property="KOrR">k</th>
        <th data-property="r2Test" data-sort-direction="desc">r<sup>2</sup> (test)</th>
        <th data-property="q2Training">q<sup>2</sup> (training)</th>
        <th data-property="r02Test">R<sub>0</sub><sup>2</sup></th>
        <th data-property="r012Test">R<sub>01</sub><sup>2</sup></th>
        <th data-property="k1Test">k1</th>
        <th data-property="k2Test">k2</th>
      </s:elseif>
      <th class="unsortable" data-property="dimsNames">Descriptors Used</th>
    </s:elseif>
    <s:elseif test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@SVM">
      <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
        <th data-property="ccrTest" data-sort-direction="desc">CCR</th>
        <s:if test="modelParameters.svmKernel == '1'">
          <th data-property="degree">degree</th>
        </s:if>
        <s:if test="modelParameters.svmKernel != '0'">
          <th data-property="gamma">gamma</th>
        </s:if>
        <s:if test="modelParameters.svmTypeCategory == '0'">
          <th data-property="cost">cost</th>
        </s:if>
        <s:if test="modelParameters.svmTypeCategory == '1'">
          <th data-property="nu">nu</th>
        </s:if>
      </s:if>
      <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
        <th data-property="rSquaredTest" data-sort-direction="desc">r<sup>2</sup></th>
        <s:if test="modelParameters.svmKernel == '1'">
          <th data-property="degree">degree</th>
        </s:if>
        <s:if test="modelParameters.svmKernel != '0'">
          <th data-property="gamma">gamma</th>
        </s:if>
        <th data-property="cost">cost</th>
        <s:if test="modelParameters.svmTypeContinuous == '3'">
          <th data-property="epsilon">epsilon (loss)</th>
        </s:if>
        <s:elseif test="modelParameters.svmTypeContinuous == '4'">
          <th data-property="nu">nu</th>
        </s:elseif>
      </s:elseif>
    </s:elseif>
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
      <s:elseif test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@KNNSA ||
                      predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@KNNGA">
        <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
          <td><s:property value="KOrR" /></td>
          <td><s:property value="accuracyTest" /></td>
          <td><s:property value="CCRNormalizedAccuracyTest" /></td>
          <td><s:property value="accuracyTraining" /></td>
          <td><s:property value="CCRNormalizedAccuracyTraining" /></td>
        </s:if>
        <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
          <td><s:property value="KOrR" /></td>
          <td><s:property value="r2Test" /></td>
          <td><s:property value="q2Training" /></td>
          <td><s:property value="r02Test" /></td>
          <td><s:property value="r012Test" /></td>
          <td><s:property value="k1Test" /></td>
          <td><s:property value="k2Test" /></td>
        </s:elseif>
        <td><s:property value="dimsNames" /></td>
      </s:elseif>
      <s:elseif test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@SVM">
        <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
          <td><s:property value="ccrTest" /></td>
          <s:if test="modelParameters.svmKernel == '1'">
            <td><s:property value="degree" /></td>
          </s:if>
          <s:if test="modelParameters.svmKernel != '0'">
            <td><s:property value="gamma" /></td>
          </s:if>
          <s:if test="modelParameters.svmTypeCategory == '0'">
            <td><s:property value="cost" /></td>
          </s:if>
          <s:if test="modelParameters.svmTypeCategory == '1'">
            <td><s:property value="nu" /></td>
          </s:if>
        </s:if>
        <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
          <td><s:property value="rSquaredTest" /></td>
          <s:if test="modelParameters.svmKernel == '1'">
            <td><s:property value="degree" /></td>
          </s:if>
          <s:if test="modelParameters.svmKernel != '0'">
            <td><s:property value="gamma" /></td>
          </s:if>
          <td><s:property value="cost" /></td>
          <s:if test="modelParameters.svmTypeContinuous == '3'">
            <td><s:property value="epsilon" /></td>
          </s:if>
          <s:elseif test="modelParameters.svmTypeContinuous == '4'">
            <td><s:property value="nu" /></td>
          </s:elseif>
        </s:elseif>
      </s:elseif>
    </tr>
  </s:iterator>
  </tbody>
</table>
