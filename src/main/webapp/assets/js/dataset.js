$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-datasets").addClass("active");

    $('input[type="file"].optional-sdf').change(function() {
        $(this).parents(".text-muted").removeClass("text-muted");
        $(".optional-sdf-standardization").show();
    });
});
