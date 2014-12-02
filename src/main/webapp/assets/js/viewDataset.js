function toggleForm() {
    $("form#updateDataset, #description-reference-buttons").toggle();
    $("#description-reference-text, button#edit-description-reference").toggle();
}

$(document).ready(function() {
    $("form#updateDataset, #description-reference-buttons").hide();

    $("button#edit-description-reference").click(function() {
        toggleForm();
    });

    $("button#cancel-changes").click(function() {
        // reset form values
        var originalDescription = $('input[type="hidden"]#description').val();
        var originalPaperReference = $('input[type="hidden"]#paperReference').val();

        $("form#updateDataset").find('textarea[name="datasetDescription"]').val(originalDescription);
        $("form#updateDataset").find('textarea[name="datasetReference"]').val(originalPaperReference);

        toggleForm();
    });

    $("button#save-changes").click(function() {
        var form = $("form#updateDataset");
        var newDescription = $('textarea[name="datasetDescription"]').val();
        var newPaperReference = $('textarea[name="datasetReference"]').val();
        $.ajax({
            url : form.attr("action"),
            method : "POST",
            data : form.serialize(),
        }).success(function() {
            if (newDescription) {
                $("#description").text(newDescription);
            } else {
                $("#description").text("(No description given.)");
            }
            if (newPaperReference) {
                $("#paper-reference").text(newPaperReference);
            } else {
                $("#paper-reference").text("(No paper reference given.)");
            }

            toggleForm();
        }).fail(function() {
            bootbox.alert("Error updating dataset.");
        });
    });

    $(".img-thumbnail").popover({
        html : true,
        template: '<div class="popover popover-image" role="tooltip"><div class="arrow"></div><div class="popover-content"></div></div>',
        content : function() {
            return '<img src="' + $(this).attr("src") + '">';
        },
        trigger : "hover",
        placement : "right",
    });
});
