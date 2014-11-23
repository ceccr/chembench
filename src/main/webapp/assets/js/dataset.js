$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-datasets").addClass("active");

    var datasetTypeInput = $("input#datasetType");
    datasetTypeInput.val($(".tab-pane:first-child").find('input[name="dataset-type"]').val());

    // add red background for unfilled file upload fields
    $('input[type="file"]:not(.optional-sdf-select)').each(function() {
        if (!$(this).val()) {
            $(this).parent("div").addClass("bg-danger");
        }
    });

    // change background for file upload fields when file is selected/deselected
    $('input[type="file"]').change(function() {
        var parent = $(this).parent("div");
        if ($(this).val()) {
            parent.removeClass("bg-danger");
            parent.addClass("bg-success");
        } else {
            parent.removeClass("bg-success");
            parent.addClass("bg-danger");
        }
    });

    $('input[type="file"].optional-sdf-select').change(function() {
        $(this).parents(".optional-sdf").removeClass("text-muted");
        $(".optional-sdf-options").show();
    });

    $('.descriptor-type input[type="radio"]').click(function() {
        var parent = $(this).parents(".descriptor-type");
        var otherParent = $(parent).siblings(".descriptor-type");

        // disable the other descriptor type
        otherParent.addClass("text-muted");
        otherParent.find('input[type="text"], select').prop("disabled", true);

        // enable this type
        parent.removeClass("text-muted");
        parent.find('input[type="text"], select').prop("disabled", false);
    });

    $('#dataset-type-selection a[data-toggle="tab"]').on("shown.bs.tab", function(e) {
        console.log(e);
        var tabName = e.currentTarget.innerText;
        if (tabName.toLowerCase().indexOf("modeling dataset") >= 0) {
            $("#external-set-settings").show();
        } else if (tabName.toLowerCase().indexOf("prediction dataset") >= 0) {
            $("#external-set-settings").hide();
        }

        var tab = $(e.currentTarget.hash);
        datasetTypeInput.val(tab.find('input[name="dataset-type"]').val());
    });
});
