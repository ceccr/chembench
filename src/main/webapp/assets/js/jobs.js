$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-mybench").addClass("active");

    $(".jobs-queue-refresh").click(function() {
        location.reload(true);
    });

    $("td.available-descriptors").each(function() {
        var descriptorList = $(this).text();
        $(this).text(descriptorList.replace(/DRAGONH/g, "DragonH")
                .replace(/DRAGONNOH/g, "DragonNoH")
                .replace(/UPLOADED/g, ""));
    });

    $("td.modeling-method").each(function() {
        var modelingMethod = $(this).text();
        $(this).text(modelingMethod.replace(/RANDOMFOREST/, "Random Forest"));
    });

    $(".modi-value").each(function() {
        var row = $(this).closest("tr");
        if ($(this).hasClass("text-warning")) {
            row.addClass("warning");
        } else if ($(this).hasClass("text-success")) {
            row.addClass("success");
        }
    });
});
