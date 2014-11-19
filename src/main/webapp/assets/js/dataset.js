$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-datasets").addClass("active");

    $('input[type="file"]').change(function() {
        $(this).parent("div").addClass("bg-success");
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
        var tabName = e.currentTarget.innerText;
        if (tabName.toLowerCase().indexOf("modeling dataset") >= 0) {
            $("#external-set-settings").show();
        } else if (tabName.toLowerCase().indexOf("prediction dataset") >= 0) {
            $("#external-set-settings").hide();
        }
    });
});
