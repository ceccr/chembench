var guestMessage = "A guest account allows a user to explore the functionality of Chembench using publicly "
        + "available datasets, predictions on single molecules, and modeling using Random Forests. "
        + "<br><br> All guest data is deleted when you leave the site or become inactive for 90 minutes. "
        + "For additional functionality, please register an account.";

$(document).ready(function() {
    // navigation button handlers
    $(".nav-list li").mouseup(function(event) {
        if (event.which === 1) {
            window.location = $(this).find("a").attr("href");
        }
    });
    $(".nav-list li").on("mouseenter mouseleave", function(event) {
        $(this).find("a").toggleClass("hovered", event.type === "mouseenter");
    });

    // default highlighted button should be Home
    $("#nav-button-home").addClass("active");

    // login & logout button handlers
    $(document).ready(function() {
        $('input[name="username"]').focus();
    });
    $("#guest-login").click(function(event) {
        event.preventDefault();
        bootbox.confirm(guestMessage, function(response) {
            if (response === true) {
                window.location = $("#guest-login").attr("href");
            }
        });
    });
    $(".logout-button").click(function() {
        window.location = "logout";
    });

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
});
