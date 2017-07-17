<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<input type="hidden" name="dataset-type" value=<s:property
    value="@edu.unc.ceccr.chembench.global.Constants@MODELINGWITHDESCRIPTORS" />>

<p>
  A <b>Modeling Dataset with Descriptors</b> can be used for both modeling and prediction. Choose this
  option if you have your own descriptors that you want to upload. (Otherwise, select <b>Modeling
  Dataset</b> instead.)
</p>

<p>
  You will need to supply an <s:a action="file-formats" namespace="/help" anchor="X">X file</s:a>
  containing your descriptor data, and an <s:a action="file-formats" namespace="/help"
                                               anchor="ACT">ACT file</s:a> containing your compounds'
  activity values. Optionally, you may provide a <s:a action="file-formats" namespace="/help"
                                                      anchor="SDF">SDF file</s:a> containing the
  structures of the compounds in your dataset. If you do, Chembench will generate descriptors for you in
  addition to the ones you upload, as well as images of your compound structures.
</p>

<div class="form-group">
  <label class="control-label col-xs-3">Activity file (.act):</label>

  <div class="col-xs-9">
    <s:file name="actFileModDesc" id="actFileModDesc" theme="simple" />
  </div>
</div>
<div class="form-group">
  <label class="control-label col-xs-3">Activity type:</label>

  <div class="inline-radio-group col-xs-9">
    <s:radio name="dataTypeModDesc" value="dataTypeModDesc"
             list="#{'CONTINUOUS':'Continuous (regression)','CATEGORY':'Category (classification)'}"
             theme="simple" />
  </div>
</div>
<hr>
<div class="form-group">
  <label class="control-label col-xs-3">Descriptor data (.x):</label>

  <div class="col-xs-9">
    <s:file name="descriptorXModDesc" id="descriptorXFileModDesc" theme="simple" />
  </div>
</div>
<div class="form-group">
  <div class="control-label col-xs-3">
    <label>Descriptor type:</label> <span class="help-block">e.g. "Dragon", "Hybrid", etc.</span>
  </div>
  <div class="col-xs-9">
    <div class="uploaded-descriptor-type">
      <div class="control-label col-xs-3">
        <input type="radio" name="predictorName" id="newDescriptorName" checked="checked"><label
          for="newDescriptorName">New type</label>
      </div>
      <div class="col-xs-9 uploaded-descriptor-type-entry">
        <s:textfield name="descriptorNewName" id="descriptorNewName" theme="simple"
                     cssClass="form-control" />
      </div>
    </div>
    <s:if test="userUploadedDescriptorTypes.size() > 0">
      <div class="uploaded-descriptor-type text-muted">
        <div class="control-label col-xs-3">
          <input type="radio" name="predictorName" id="usedDescriptorName"><label
            for="usedDescriptorName">Existing type</label>
        </div>
        <div class="col-xs-9 uploaded-descriptor-type-entry">
          <s:select name="selectedDescriptorUsedName" id="descriptorUsedName"
                    list="userUploadedDescriptorTypes" label="Select type" disabled="true"
                    theme="simple" />
        </div>
      </div>
    </s:if>
  </div>
</div>
<div class="form-group">
  <div class="col-xs-offset-3 col-xs-9">
    <label for="hasBeenScaled-MWD"><s:checkbox name="hasBeenScaled" id="hasBeenScaled-MWD"
                                               theme="simple" />My
      descriptors have been scaled</label>

    <p class="help-block">If you leave this unchecked, Chembench will scale your descriptors for you.
    </p>
  </div>
</div>
<hr>
<div class="form-group optional-sdf text-muted">
  <div class="control-label col-xs-3">
    <label>Structure file (.sdf):</label><span class="help-block">Optional.</span>
  </div>
  <div class="col-xs-9">
    <s:file name="sdfFileModDesc" id="sdfFileModDesc" theme="simple" cssClass="optional-sdf-select" />
  </div>
</div>
<div class="optional-sdf-options">
  <div class="form-group">
    <div class="col-xs-offset-3 col-xs-9">
      <label for="standardizeModDesc"><s:checkbox name="standardizeModDesc" id="standardizeModDesc"
                                                  theme="simple" />Standardize molecule
        structures</label>

      <p class="help-block">
        If you choose not to standardize, ensure that your structure file contains explicit
        hydrogens.<br> Otherwise, Dragon descriptors will not be available.
      </p>
    </div>
  </div>
  <div class="form-group">
    <div class="col-xs-offset-3 col-xs-9">
      <label for="generateImagesMWD"><s:checkbox name="generateImagesMWD" id="generateImagesMWD"
                                                 theme="simple" />Generate Mahalanobis heatmap</label>

      <p class="help-block">Unchecking this box will accelerate dataset generation but will eliminate
        heatmap based on Mahalanobis distance measure.
      </p>
    </div>
  </div>
</div>