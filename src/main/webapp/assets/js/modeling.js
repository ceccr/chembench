(function() {
    "use strict";

    function datasetSelectedCallback(dataset) {
        showSections();
        $('input[name="uploaded-descriptors-scaled"]').val(dataset.hasBeenScaled);

        var availableDescriptors = dataset.availableDescriptors.trim().split(/\s+/);
        if (availableDescriptors.contains(Chembench.Constants.UPLOADED)) {
            $('input[name="descriptorGenerationType"][value="' + Chembench.Constants.UPLOADED +
              '"]').parents("label").append('<span id="uploaded-descriptor-type">("' + dataset.uploadedDescriptorType +
                                            '")</span>');
        } else {
            $("#uploaded-descriptor-type").remove();
        }

        // enable only available descriptors in Descriptor Set selection
        $('input[name="descriptorGenerationType"]').prop("disabled",
            true).removeAttr("checked").parent().addClass("text-muted");
        $.each(availableDescriptors, function(i, descriptor) {
            $('input[name="descriptorGenerationType"][value="' + descriptor +
              '"]').removeAttr("disabled").parent("label").removeClass("text-muted");
        });

        if (dataset.numCompound < 40) {
            $("#small-dataset-warning").show();
        } else {
            $("#small-dataset-warning").hide();
        }

        // pre-select the default descriptor set, if available
        var defaultDescriptor = $('input[type="hidden"]#defaultDescriptorGenerationType').val();
        var defaultDescriptorOption = $('input[name="descriptorGenerationType"][value="' + defaultDescriptor + '"]');
        if (!defaultDescriptorOption.prop("disabled")) {
            defaultDescriptorOption.prop("checked", true);
        }

        // enable the correct svm type based on the selected dataset's activity value type
        $("#svm-type-continuous, #svm-type-category").hide();
        if (dataset.modelType === Chembench.Constants.CONTINUOUS) {
            $("#svm-type-continuous").show();
            $('input[name="svmTypeContinuous"]:radio:checked').trigger("change");
        } else if (dataset.modelType === Chembench.Constants.CATEGORY) {
            $("#svm-type-category").show();
            $('input[name="svmTypeCategory"]:radio:checked').trigger("change");
        }

        // select the correct internal split type depending on the number of compounds in the dataset
        if (dataset.numCompound < 300) {
            $('a[href="#sphere-exclusion"]').tab("show");
        } else {
            $('a[href="#random-split"]').tab("show");
        }

        var internalSplitSection = $("#internal-split-type-section");
        // ... but hide the div containing internal split options if random forest is selected
        // (random forest doesn't use internal splitting)
        if ($("#model-type-section").find(".nav").children(".active").children("a").attr("href") === "#random-forest") {
            internalSplitSection.hide();
        } else {
            internalSplitSection.show();
        }
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
            if ($(this).val() === Chembench.Constants.UPLOADED &&
                $('input[name="uploaded-descriptors-scaled"]').val() === "true") {
                scalingTypes.addClass("text-muted").find("input").prop("disabled", true);
                infoBox.show();
                scalingTypes.find('[value="NOSCALING"]').prop("checked", true);
            } else {
                scalingTypes.removeClass("text-muted").find("input").prop("disabled", false);
                infoBox.hide();
                var defaultScalingType = $('input[type="hidden"]#defaultScalingType').val();
                $('input[name="scalingType"][value="' + defaultScalingType + '"]').prop("checked", true);
            }
        });

        var datasetSelectTable = $("#dataset-selection-section").find("table.datatable");
        datasetSelectTable.DataTable().on("init", function() {
            $(this).find('input[type="radio"]').change(function(e) {
                var rowSelector = $(this).closest("tr");
                var dataset = rowSelector.closest("table").DataTable().row(rowSelector).data();
                datasetSelectedCallback(dataset);
            });
        }).on("draw", function() {
            var modified = false;
            var api = $(this).DataTable();
            api.rows().every(function() {
                var row = this.data();
                if (!(row.datasetType === Chembench.Constants.MODELING) ||
                    row.datasetType === Chembench.Constants.MODELINGWITHDESCRIPTORS) {
                    this.remove();
                    modified = true;
                }
            });
            // XXX without this conditional the callback will recurse infinitely
            if (modified) {
                api.draw();
            }
        });
    });
})();
