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
        $(tabHash).find("table.dataTable").DataTable().columns.adjust();
    });

    $("#jobs-queue-refresh").click(function() {
        // currently the job queue refresh button only refreshes the page;
        // it should ideally make an ajax request and repopulate the page with the new data
        location.reload(true);
    });
});
