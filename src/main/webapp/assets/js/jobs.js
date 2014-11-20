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

    $.tablesorter.themes.bootstrap = {
        sortNone: "glyphicon glyphicon-sort",
        sortAsc: "glyphicon glyphicon-sort-by-attributes",
        sortDesc: "glyphicon glyphicon-sort-by-attributes-alt",
    };

    $(".tablesorter").tablesorter({
        sortStable: true,

        theme: "bootstrap",
        headerTemplate: "{content} {icon}",
        widgets: ["uitheme"],
    });

    // sort initially by Date Created descending
    $('th:contains("Date Created")').each(function() {
        $(this).find(".glyphicon").removeClass("glyphicon-sort").addClass("glyphicon-sort-by-attributes-alt");
        // XXX the triple array is _required_ for sorton to work
        $(this).parents("table").trigger("sorton", [[[$(this).attr("data-column"), "d"]]]);
    });
});
