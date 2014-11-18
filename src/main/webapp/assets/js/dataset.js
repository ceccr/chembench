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

    $('.new-descriptor-type input[type="radio"]').click(function() {

    });

    $('.existing-descriptor-type input[type="radio"]').click(function() {

    });
});
