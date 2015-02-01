<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script language="javascript" src="javascript/modeling.js"></script>
<s:div>
  <!-- SVM Parameters -->
  <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
    <tbody>
    <tr>
      <td width="100%" height="24" align="left" colspan="2"><br />

        <p class="StandardTextDarkGrayParagraph2">
          <b>Set SVM Parameters</b>
        </p></td>
    </tr>
    <tr>
      <td>
        <table>

          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>SVM Type (for Category data):</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                <s:radio name="svmTypeCategory" disabled="true" id="svmTypeCategory"
                         list="#{'0':'C-SVC','1':'nu-SVC'}" onchange='changeSvmType(); calculateRuntimeEstimate()'
                         theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>SVM Type (for Continuous data):</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                <s:radio name="svmTypeContinuous" id="svmTypeContinuous" list="#{'3':'epsilon-SVR','4':'nu-SVR'}"
                         onchange='changeSvmType(); calculateRuntimeEstimate()' theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Kernel Type:</b>
              </div>
            </td>
            <td>
              <div class="StandardTextDarkGrayParagraphNoIndent">
                <s:radio name="svmKernel"
                         list="#{'0':'linear','1':'polynomial','2':'radial basis function','3':'sigmoid'}"
                         onchange='changeSvmType(); calculateRuntimeEstimate()' theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Degree in kernel function:</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                From:
                <s:textfield id="svmDegreeFrom" name="svmDegreeFrom" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                To:
                <s:textfield id="svmDegreeTo" name="svmDegreeTo" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                Step:
                <s:textfield id="svmDegreeStep" name="svmDegreeStep" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Gamma in kernel function:</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                From: 2^
                <s:textfield id="svmGammaFrom" name="svmGammaFrom" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                To: 2^
                <s:textfield id="svmGammaTo" name="svmGammaTo" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                Step: 2^
                <s:textfield id="svmGammaStep" name="svmGammaStep" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Cost (C) of C-SVC, epsilon-SVR, and nu-SVR:</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                From: 2^
                <s:textfield id="svmCostFrom" name="svmCostFrom" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                To: 2^
                <s:textfield id="svmCostTo" name="svmCostTo" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                Step: 2^
                <s:textfield id="svmCostStep" name="svmCostStep" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Nu of nu-SVC and nu-SVR:</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                From:
                <s:textfield id="svmNuFrom" name="svmNuFrom" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                To:
                <s:textfield id="svmNuTo" name="svmNuTo" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                Step:
                <s:textfield id="svmNuStep" name="svmNuStep" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Epsilon in loss function of epsilon-SVR:</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                From:
                <s:textfield id="svmPEpsilonFrom" name="svmPEpsilonFrom" size="5"
                             onchange='calculateRuntimeEstimate()' theme="simple" />
                To:
                <s:textfield id="svmPEpsilonTo" name="svmPEpsilonTo" size="5" onchange='calculateRuntimeEstimate()'
                             theme="simple" />
                Step:
                <s:textfield id="svmPEpsilonStep" name="svmPEpsilonStep" size="5"
                             onchange='calculateRuntimeEstimate()' theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Tolerance of termination criterion:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="svmEEpsilon" name="svmEEpsilon" size="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Use Shrinking Heuristics:</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                <s:radio name="svmHeuristics" list="#{'1':'Yes','0':'No'}" theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Use Probability Estimates:</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                <s:radio name="svmProbability" list="#{'1':'Yes','0':'No'}" theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Parameter C of class i to weight*C, for C-SVC:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="svmWeight" name="svmWeight" size="5" theme="simple" />
            </td>
          </tr>
          <!--
				Removed option for cross-validation. Why? LibSVM doesn't generate models
				when you specify cross-validation! It just gives you some nice info about
				your dataset and exits. Not what we want. May be useful in calculating q^2
				someday, but aside from that there's no reason to use it.

				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of cross-validations (e.g. 5 for 5-fold):</b></div></td>
				<td align="left" valign="top"><s:textfield id="svmCrossValidation" name="svmCrossValidation" size="5" theme="simple"/>
				</td></tr>
				-->
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>CCR or r<sup>2</sup> cutoff for model acceptance:
                </b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="svmCutoff" name="svmCutoff" size="5" theme="simple" />
            </td>
          </tr>

        </table>
      </td>
    </tr>
    </tbody>
  </table>
  <!--LibSVM is "L2" type, where all descriptors are used, and a weighting scheme is applied. -->
  <!--"L1" type SVM modeling might be useful as well! That's where some descriptors are eliminated before modeling even begins. -->

</s:div>
