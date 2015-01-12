function updateDatasetInfo(idString) {
    var id = parseInt(idString);
    var datasetInfo = $("#dataset-info");
    var datasetInfoHelp = $("#dataset-info-help");

    if (id === 0) {
        datasetInfoHelp.show();
        datasetInfo.hide();
    } else if (id > 0) {
        datasetInfoHelp.hide();
        datasetInfo.show();
        $.get("/ajaxGetDataset", {"id": id}, function (dataset) {
            datasetInfo.html(
                "<h4>Dataset: " + dataset.name + "</h4>" +
                '<dl class="dl-horizontal properties-list">' +
                "<dt>Number of compounds</dt>" +
                "<dd>" + dataset.numCompound + "</dd>" +
                "<dt>Activity type</dt>" +
                "<dd>" + (dataset.continuous === true ? "Continuous" : "Category") + "</dd>" +
                '<dt class="availableDescriptors">Available descriptors</dt>' +
                '<dd class="available-descriptors">' + dataset.availableDescriptors + "</dd>" +
                "</dl>"
            );

            var availableDescriptors = datasetInfo.find(".available-descriptors").text().trim().split(/\s+/);
            if ($.inArray("UPLOADED", availableDescriptors) >= 0) {
                datasetInfo.find("dl").append(
                    "<dt>Uploaded descriptor type</dt>" +
                    "<dd>" + dataset.uploadedDescriptorType + "</dd>"
                );
            }
            // pre-select the default descriptor set, if available
            var defaultDescriptor = $('input[type="hidden"]#defaultDescriptorGenerationType').val();
            var defaultDescriptorOption = $('input[name="descriptorGenerationType"][value="' + defaultDescriptor + '"]');
            if (!defaultDescriptorOption.prop("disabled")) {
                defaultDescriptorOption.prop("checked", true);
            }

            // enable only available descriptors in Descriptor Set selection
            $('input[name="descriptorGenerationType"]').prop("disabled", true).parent().addClass("text-muted");
            for (var i = 0; i < availableDescriptors.length; i++) {
                var availableDescriptorOption = $('input[name="descriptorGenerationType"][value="' + availableDescriptors[i] + '"]');
                availableDescriptorOption.removeAttr("disabled").parent().removeClass("text-muted");
            }

            // pretty-print the available descriptors list when we're done
            formatAvailableDescriptors(datasetInfo.find(".available-descriptors"));
        }).fail(function () {
            datasetInfo.html(
                '<h4 class="text-danger">Error fetching dataset info</h4>' +
                "<p>A server error occurred while fetching dataset information for the selected dataset."
            );
        });
    }
}

$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-modeling").addClass("active");
    var descriptorTypes = $("#descriptor-types");
    descriptorTypes.find("label").addClass("text-muted").find("input").prop("disabled", true);

    var datasetSelect = $("#dataset-selection");
    var viewDatasetDetailButton = $("#view-dataset-detail");
    datasetSelect.prepend('<option selected="selected" value="0">(Select a dataset)</option>');
    datasetSelect.change(function() {
        var datasetId = parseInt(this.value);
        if (datasetId === 0) {
            viewDatasetDetailButton.addClass("disabled");
        } else {
            viewDatasetDetailButton.removeClass("disabled");
        }
        updateDatasetInfo(datasetId);
    });

    viewDatasetDetailButton.click(function(e) {
        e.preventDefault();

        var datasetId = parseInt(datasetSelect.val());
        if (datasetId !== 0) {
            window.open("/viewDataset.action?id=" + datasetId, "_blank");
        }
    });
});
