$(document).ready(function() {
    var tabs = $('#viewPredictionTabs');
    tabs.on('tabsload', function(e, ui) {
        if (ui.tab.text() === "Descriptor Importance") {
            $(this).find('ul.fold-selection').find('li').find('a').click(function(e) {
                ui.panel.addClass('faded');
                e.preventDefault();
                ui.panel.load($(this).attr('href'), function() {
                    ui.panel.removeClass('faded');
                    tabs.trigger('tabsload', ui);
                });
            });
        }
    });
});
