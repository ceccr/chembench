<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>

<script language="javascript" src="javascript/modeling.js"></script>

<html:form action="/submitQsarWorkflow.do" enctype="multipart/form-data">
			 <!-- SVM Parameters -->
			 <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<b>Set SVM Parameters</b>
					</p>
					</td>
				</tr>	
				<tr><td><table>
				
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>SVM Type (for Category data):</b></div></td>
				<td align="left" valign="top"><html:radio value="0" property="svmTypeCategory" styleId="svmTypeCategory0">C-SVC</html:radio> <html:radio value="1" property="svmTypeCategory" styleId="svmTypeCategory1">nu-SVC</html:radio></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>SVM Type (for Continuous data):</b></div></td>
				<td align="left" valign="top"><html:radio value="0" property="svmTypeContinuous" styleId="svmTypeContinuous0">epsilon-SVR</html:radio> <html:radio value="1" property="svmTypeContinuous" styleId="svmTypeContinuous1">nu-SVR</html:radio></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Kernel Type:</b></div></td>
				<td>			
				<html:radio value="0" property="svmKernel" styleId="svmKernelType0">linear: u'*v</html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="1" property="svmKernel" styleId="svmKernelType1">polynomial: (gamma*u'*v + coef0)^degree</html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="2" property="svmKernel" styleId="svmKernelType2">radial basis function: exp(-gamma*|u-v|^2)</html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="3" property="svmKernel" styleId="svmKernelType3">sigmoid: tanh(gamma*u'*v + coef0)</html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="4" property="svmKernel" styleId="svmKernelType4">precomputed kernel (kernel values in training_set_file)</html:radio>
				</td> 
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Degree in kernel function:</b></div></td>
				<td align="left" valign="top"><html:text property="svmDegree" size="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Gamma in kernel function:</b></div></td>
				<td align="left" valign="top"><html:text property="svmGamma" size="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Cost (C) of C-SVC, epsilon-SVR, and nu-SVR:</b></div></td>
				<td align="left" valign="top"><html:text property="svmCost" size="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Nu of nu-SVC, one-class SVM, and nu-SVR:</b></div></td>
				<td align="left" valign="top"><html:text property="svmNu" size="5" /></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Epsilon in loss function of epsilon-SVR:</b></div></td>
				<td align="left" valign="top"><html:text property="svmPEpsilon" size="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Tolerance of termination criterion:</b></div></td>
				<td align="left" valign="top"><html:text property="svmEEpsilon" size="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Use Shrinking Heuristics:</b></div></td>
				<td align="left" valign="top"><html:radio value="1" property="svmHeuristics" styleId="svmHeuristics1">Yes</html:radio> <html:radio value="0" property="svmHeuristics" styleId="svmHeuristics0">No</html:radio></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Use Probability Estimates:</b></div></td>
				<td align="left" valign="top"><html:radio value="1" property="svmProbability" styleId="svmProbability1">Yes</html:radio> <html:radio value="0" property="svmProbability" styleId="svmProbability0">No</html:radio></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Parameter C of class i to weight*C, for C-SVC:</b></div></td>
				<td align="left" valign="top"><html:text property="svmWeight" size="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of cross-validations (e.g. 5 for 5-fold):</b></div></td>
				<td align="left" valign="top"><html:text property="svmCrossValidation" size="5"/></td></tr>
				
				</table></td></tr>
				</tbody>
			</table>
</html:form>
<br />
<!--LibSVM is "L2" type, where all descriptors are used, and a weighting scheme is applied. -->
<!--"L1" type SVM modeling might be useful as well! That's where some descriptors are eliminated before modeling even begins. -->
