$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-mybench").addClass("active");

    // if given a hash, activate that tab on page load
    var url = document.location.toString();
    if (url.match("#")) {
        $(".nav-tabs a[href=#" + url.split('#')[1] + "]").tab("show");
    }
    // change page hash when a tab is clicked
    $(".nav-tabs a").on("shown.bs.tab", function(e) {
        var tabHash = e.target.hash;
        window.location.hash = tabHash;
        window.scrollTo(0, 0);

        // fix table layout
        if (tabHash === "#datasets") {
            formatModi();
        }
        setTimeout($(tabHash).find("table.dataTable").DataTable().columns.adjust, 10);
    });

    $("#jobs-queue-refresh").click(function() {
        // currently the job queue refresh button only refreshes the page;
        // it should ideally make an ajax request and repopulate the page with the new data
        location.reload(true);
    });

    $(".delete a").click(function(event) {
        event.preventDefault();
        $(this).blur();

        var link = $(this);
        var objectType = link.closest(".tab-pane").attr("id").slice(0, -1);
        var objectName = link.closest("tr").find(".name-column").find(".object-name").text();
        var verb = (objectType === "job" ? "cancel" : "delete");
        var message = "Are you sure you want to " + verb + " the " + objectType + ' "' + objectName + '"?';
        bootbox.confirm(message, function(response) {
            if (response === true) {
                $.ajax({
                    method: "POST",
                    url: link.attr("href")
                }).success(function() {
                    if (objectType === "job") {
                        window.location.reload();
                    } else {
                        link.closest("tr").fadeOut();
                    }
                }).fail(function() {
                    bootbox.alert("Error deleting " + objectType + ".");
                });
            }
        });
    });

    $("table.datatable").each(function() {
        var table = $(this);
        var options = $.extend({
            "scrollY": "350px",
            "scrollCollapse": true,
            "drawCallback": function() {
                formatModi();
                var api = this.api();
                setTimeout(function() {
                    api.columns.adjust();
                }, 100);
            }
        }, Chembench.DATATABLE_OPTIONS);
        var dateColumnIndex = table.find('th:contains("Date")').index();
        if (dateColumnIndex > 0) {
            options["order"] = [[dateColumnIndex, "desc"]];
        }
        table.DataTable(options);
    });
});
