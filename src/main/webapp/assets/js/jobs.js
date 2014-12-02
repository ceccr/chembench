$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-mybench").addClass("active");

    // if given a hash, activate that tab on pageload
    var url = document.location.toString();
    if (url.match("#")) {
        $(".nav-tabs a[href=#" + url.split('#')[1] + "]").tab("show") ;
    }
    // change page hash when a tab is clicked
    $(".nav-tabs a").on("shown.bs.tab", function (e) {
        window.location.hash = e.target.hash;
        window.scrollTo(0, 0);
    });

    $(".jobs-queue-refresh").click(function() {
        // currently the job queue refresh button only refreshes the page;
        // it should ideally make an ajax request and repopulate the page with the new data
        location.reload(true);
    });

    $(".modi-value").each(function() {
        var row = $(this).closest("tr");
        if ($(this).hasClass("text-warning")) {
            row.addClass("warning");
        } else if ($(this).hasClass("text-success")) {
            row.addClass("success");
        }
    });

    // sort initially by Date Created descending
    $('th:contains("Date Created")').each(function() {
        $(this).find(".glyphicon").removeClass("glyphicon-sort").addClass("glyphicon-sort-by-attributes-alt");
        // XXX the triple array is _required_ for sorton to work
        $(this).parents("table").trigger("sorton", [[[$(this).attr("data-column"), "d"]]]);
    });

    $(".delete a").click(function(event) {
        var link = $(this);
        event.preventDefault();
        var objectType = link.closest(".tab-pane").attr("id").slice(0, -1);
        var objectName = link.closest(".name").find("a:first").text();
        var verb = (objectType === "job" ? "cancel" : "delete");
        var message = "Are you sure you want to " + verb + " the " + objectType + ' "' + objectName + '"?';
        bootbox.confirm(message, function(response) {
            if (response === true) {
                $.ajax({
                    method: "POST",
                    url: link.attr("href"),
                }).success(function() {
                    link.closest("tr").fadeOut();
                }).fail(function() {
                    bootbox.alert("Error deleting dataset.");
                });
            }
        });
    });
});
