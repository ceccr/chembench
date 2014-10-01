$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-mybench").addClass("active");

    $(".jobs-queue-refresh").click(function() {
        location.reload(true);
    });
});
