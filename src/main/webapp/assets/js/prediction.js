$(document).ready(function() {
    $("#prediction-model-selection").find(".dataTables_scrollBody").find("table").DataTable().on("draw", function() {
        $(this).find("tr").click(function() {
            var row = $(this);
            var counter = $("#selected-model-count");
            var count = parseInt(counter.text());
            row.toggleClass("selected").toggleClass("info");
            if (row.hasClass("selected")) {
                counter.text(count + 1);
            } else {
                counter.text(count - 1);
            }
        });
    });
});
