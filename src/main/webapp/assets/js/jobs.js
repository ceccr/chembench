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
});
