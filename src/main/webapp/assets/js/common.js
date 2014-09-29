$(document).ready(function() {
    $(".nav-list li").click(function() {
        window.location = $(this).find("a").attr("href");
    });
});
