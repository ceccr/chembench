function updateDatasetInfo(idString) {
    var id = parseInt(idString);
    var datasetInfo = $("#dataset-info");
    $.get("/ajaxGetDataset", {"id": id}, function(dataset) {
        datasetInfo.html("<h4>Dataset: " + dataset.name + "</h4>" + '<dl class="dl-horizontal properties-list">' +
                         "<dt>Number of compounds</dt>" + "<dd>" + dataset["numCompound"] + "</dd>" +
                         "<dt>Activity type</dt>" + "<dd>" +
                         (dataset["continuous"] === true ? "Continuous" : "Category") + "</dd>" +
                         '<dt class="availableDescriptors">Available descriptors</dt>' +
                         '<dd class="available-descriptors">' + dataset["availableDescriptors"] + "</dd>" + "</dl>");

        var availableDescriptors = datasetInfo.find(".available-descriptors").text().trim().split(/\s+/);
        if ($.inArray("UPLOADED", availableDescriptors) >= 0) {
            datasetInfo.find("dl").append("<dt>Uploaded descriptor type</dt>" + "<dd>" +
                                          dataset["uploadedDescriptorType"] + "</dd>");
        }
        // enable only available descriptors in Descriptor Set selection
        $('input[name="descriptorGenerationType"]').prop("disabled",
            true).removeAttr("checked").parent().addClass("text-muted");
        for (var i = 0; i < availableDescriptors.length; i++) {
            $('input[name="descriptorGenerationType"][value="' + availableDescriptors[i] +
              '"]').removeAttr("disabled").parent("label").removeClass("text-muted");
        }

        // pre-select the default descriptor set, if available
        var defaultDescriptor = $('input[type="hidden"]#defaultDescriptorGenerationType').val();
        var defaultDescriptorOption = $('input[name="descriptorGenerationType"][value="' + defaultDescriptor + '"]');
        if (!defaultDescriptorOption.prop("disabled")) {
            defaultDescriptorOption.prop("checked", true);
        }

        // pretty-print the available descriptors list when we're done
        formatAvailableDescriptors(datasetInfo.find(".available-descriptors"));
    }).fail(function() {
        datasetInfo.html('<h4 class="text-danger">Error fetching dataset info</h4>' +
                         "<p>A server error occurred while fetching dataset information for the selected dataset.");
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
    if ($("#model-type-section").find(".nav > .active > a").attr("href") === "#random-forest") {
        $("#internal-split-type-section").hide();
    }
}

$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-modeling").addClass("active");
    $("#descriptor-types").find("label").addClass("text-muted").find("input").prop("disabled", true);

    var datasetSelect = $("#dataset-selection");
    datasetSelect.prepend('<option selected="selected" value="0">(Select a dataset)</option>');
    datasetSelect.change(function() {
        var datasetId = parseInt(this.value);
        if (datasetId === 0) {
            hideSections();
        } else {
            showSections();
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

    $('a[data-toggle="tab"]').on("shown.bs.tab", function(e) {
        if ($(e.target).attr("href") === "#random-forest") {
            $("#internal-split-type-section").hide();
        } else {
            $("#internal-split-type-section").show();
        }
    });
});
