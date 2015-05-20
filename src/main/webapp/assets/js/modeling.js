function updateDatasetInfo(idString) {
    var id = parseInt(idString);
    var datasetInfo = $("#dataset-info");
    $.get("api/ajaxGetDataset", {"id": id}, function(dataset) {
        showSections();
        var activityType = dataset["continuous"] === true ? "Continuous" : "Category";
        var numCompound = parseInt(dataset["numCompound"]);
        datasetInfo.html("<h4>Dataset: " + dataset.name + "</h4>" + '<dl class="dl-horizontal properties-list">' +
                         "<dt>Number of compounds</dt>" + "<dd>" + numCompound + "</dd>" + "<dt>Activity type</dt>" +
                         "<dd>" + activityType + "</dd>" + "<dt>Available descriptors</dt>" +
                         '<dd id="available-descriptors-deferred">' + dataset["availableDescriptors"] + "</dd>" +
                         "<dt>Modelability index</dt>" + "<dd>" + formatModi(dataset["modi"], dataset) + "</dd>" +
                         "</dl>");

        $('input[name="uploaded-descriptors-scaled"]').val(dataset["hasBeenScaled"]);

        var $availableDescriptors = $("#available-descriptors-deferred");
        var availableDescriptors = $availableDescriptors.text().trim().split(/\s+/);
        if ($.inArray("UPLOADED", availableDescriptors) >= 0) {
            var uploadedDescriptorType = dataset["uploadedDescriptorType"];
            datasetInfo.find("dl").append("<dt>Uploaded descriptor type</dt>" + "<dd>" + uploadedDescriptorType +
                                          "</dd>");
            $('input[name="descriptorGenerationType"][value="UPLOADED"]').parents("label").append('<span id="uploaded-descriptor-type">("' +
                                                                                                  uploadedDescriptorType +
                                                                                                  '")</span>');
        } else {
            $("#uploaded-descriptor-type").remove();
        }
        // enable only available descriptors in Descriptor Set selection
        $('input[name="descriptorGenerationType"]').prop("disabled",
            true).removeAttr("checked").parent().addClass("text-muted");
        for (var i = 0; i < availableDescriptors.length; i++) {
            $('input[name="descriptorGenerationType"][value="' + availableDescriptors[i] +
              '"]').removeAttr("disabled").parent("label").removeClass("text-muted");
        }

        // pretty-print now that we are done filtering, no earlier
        $availableDescriptors.text(formatAvailableDescriptors($availableDescriptors.text()));

        var warningBox = $("#small-dataset-warning");
        if (numCompound < 40) {
            warningBox.show();
        } else {
            warningBox.hide();
        }

        // pre-select the default descriptor set, if available
        var defaultDescriptor = $('input[type="hidden"]#defaultDescriptorGenerationType').val();
        var defaultDescriptorOption = $('input[name="descriptorGenerationType"][value="' + defaultDescriptor + '"]');
        if (!defaultDescriptorOption.prop("disabled")) {
            defaultDescriptorOption.prop("checked", true);
        }

        // enable the correct svm type based on the selected dataset's activity value type
        $("#svm-type-continuous, #svm-type-category").hide();
        if (activityType.toLowerCase() === "continuous") {
            $("#svm-type-continuous").show();
            $('input[name="svmTypeContinuous"]:radio:checked').trigger("change");
        } else if (activityType.toLowerCase() === "category") {
            $("#svm-type-category").show();
            $('input[name="svmTypeCategory"]:radio:checked').trigger("change");
        }

        // select the correct internal split type depending on the number of compounds in the dataset
        if (numCompound < 300) {
            $('a[href="#sphere-exclusion"]').tab("show");
        } else {
            $('a[href="#random-split"]').tab("show");
        }

        var internalSplitSection = $("#internal-split-type-section");
        // ... but hide the div containing internal split options if random forest is selected
        // (random forest doesn't use internal splitting)
        if ($("#model-type-section").find(".nav > .active > a").attr("href") === "#random-forest") {
            internalSplitSection.hide();
        } else {
            internalSplitSection.show();
        }
    }).fail(function() {
        datasetInfo.html('<h4 class="text-danger">Error fetching dataset info</h4>' +
                         "<p>A server error occurred while fetching dataset information for the selected dataset.</p>");
    });
}

function hideSections() {
    $("#dataset-info, form#createModelingJob .panel:not(#dataset-selection-section)").hide();
    $("#dataset-info-help").show();
    $("#view-dataset-detail").addClass("disabled");
    $("#descriptor-types").find("label").addClass("text-muted").find("input").prop("disabled", true).prop("checked",
        false);
}

function showSections() {
    $("#dataset-info, form#createModelingJob .panel").show();
    $("#dataset-info-help").hide();
    $("#view-dataset-detail").removeClass("disabled");
}

$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-modeling").addClass("active");
    $("#descriptor-types").find("label").addClass("text-muted").find("input").prop("disabled", true);
    hideSections();
    $("#degree-settings, #gamma-settings").hide();

    var datasetSelect = $("#dataset-selection");
    datasetSelect.prepend('<option selected="selected" value="0">(Select a dataset)</option>');
    datasetSelect.change(function() {
        var datasetId = parseInt(this.value);
        if (datasetId === 0) {
            hideSections();
        } else {
            updateDatasetInfo(datasetId);
        }
    });

    $("#view-dataset-detail").click(function(e) {
        e.preventDefault();
        var datasetId = parseInt(datasetSelect.val());
        if (datasetId !== 0) {
            window.open("/viewDataset.action?id=" + datasetId, "_blank");
        }
    });

    $("#model-type-section").find('a[data-toggle="tab"]').on("shown.bs.tab", function(e) {
        var tab = $($(e.target).attr("href"));
        var tabId = tab.find('input[type="hidden"][name="modelingTypeConstant"]').val();
        if (tabId === "RANDOMFOREST") {
            $("#internal-split-type-section").hide();
        } else {
            $("#internal-split-type-section").show();
        }
        $('input[name="modelingType"]').val(tabId);
    });

    $('input[name="svmKernel"]').change(function(e) {
        var degreeSettings = $("#degree-settings");
        var gammaSettings = $("#gamma-settings");
        degreeSettings.hide();
        gammaSettings.hide();

        var selectedRadioId = $(e.target).attr("id");
        var selectedKernel = $('label[for="' + selectedRadioId + '"]').text();
        if (selectedKernel === "polynomial") {
            degreeSettings.show();
            gammaSettings.show();
        } else if (selectedKernel === "radial basis function" || selectedKernel === "sigmoid") {
            gammaSettings.show();
        }
    });
    $('input[name="svmKernel"]:radio:checked').trigger("change");

    $('input[name="svmTypeCategory"], input[name="svmTypeContinuous"]').change(function(e) {
        var costSettings = $("#cost-settings");
        var nuSettings = $("#nu-settings");
        var epsilonSettings = $("#epsilon-settings");
        var weightSettings = $("#csvm-weight-settings");
        costSettings.hide();
        nuSettings.hide();
        epsilonSettings.hide();
        weightSettings.hide();

        var selectedRadioId = $(e.target).attr("id");
        var selectedSvm = $('label[for="' + selectedRadioId + '"]').text();
        if (selectedSvm === "C-SVC") {
            costSettings.show();
            weightSettings.show();
        } else if (selectedSvm === "nu-SVC") {
            nuSettings.show();
        } else if (selectedSvm === "epsilon-SVR") {
            epsilonSettings.show();
        } else if (selectedSvm === "nu-SVR") {
            costSettings.show();
            nuSettings.show();
        }
    });

    $(".advanced-settings-toggle").click(function(e) {
        e.preventDefault();
        var container = $(this).parents(".advanced-settings-group");
        container.find(".glyphicon").toggleClass("glyphicon-chevron-up glyphicon-chevron-down");
        container.find(".advanced-settings").slideToggle();
    });

    $('input[name="descriptorGenerationType"]').change(function() {
        var scalingTypes = $("#scaling-types");
        var infoBox = $("#already-scaled-info");
        if ($(this).val() === "UPLOADED" && $('input[name="uploaded-descriptors-scaled"]').val() === "true") {
            scalingTypes.addClass("text-muted").find("input").prop("disabled", true);
            infoBox.show();
            scalingTypes.find('[value="NOSCALING"]').prop("checked", true);
        } else {
            scalingTypes.removeClass("text-muted").find("input").prop("disabled", false);
            infoBox.hide();
            var defaultScalingType = $('input[type="hidden"]#defaultScalingType').val();
            $('input[name="scalingType"][value="' + defaultScalingType + '"]').prop("checked", true);
        }
    })
});
