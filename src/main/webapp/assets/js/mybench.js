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
        $(tabHash).find("table.dataTable").DataTable().columns.adjust();
    });

    $("#jobs-queue-refresh").click(function() {
        // currently the job queue refresh button only refreshes the page;
        // it should ideally make an ajax request and repopulate the page with the new data
        location.reload(true);
    });

    // FIXME jobs haven't been converted to ajax format yet
    $("table.datatable").not("[data-url]").each(function() {
        var options = $.extend({}, Chembench.DATATABLE_OPTIONS);
        var table = $(this);
        var dateColumnIndex = table.find('th:contains("Date")').index();
        if (dateColumnIndex > 0) {
            options["order"] = [[dateColumnIndex, "desc"]];
        }
        table.DataTable(options);
    });
});
