(function() {
    'use strict';

    function clearRefreshingState() {
        var button = $('#jobs-queue-refresh');
        button.removeClass('active disabled').blur().find('#refresh-text').text('Refresh');
    }

    function refreshJobQueues() {
        var button = $('#jobs-queue-refresh');
        button.addClass('active disabled').find('#refresh-text').text('Refreshing...');

        var xhrs = [];
        var start = Date.now();
        $('table.datatable[data-url]').DataTable().one('xhr', function(e, settings, json, xhr) {
            xhrs.push(xhr);
        }).ajax.reload();

        $.when.apply(this, xhrs).done(function() {
            var elapsed = Date.now() - start;
            if (elapsed < 1000) {
                setTimeout(clearRefreshingState, 1000 - elapsed);
            } else {
                clearRefreshingState();
            }
        });
    }

    $(document).ready(function() {
        $('.nav-list li').removeClass('active');
        $('#nav-button-mybench').addClass('active');

        // if given a hash, activate that tab on page load
        var url = document.location.toString();
        if (url.match('#')) {
            $('.nav-tabs a[href=#' + url.split('#')[1] + ']').tab('show');
        }

        // change page hash when a tab is clicked
        $('.nav-tabs a').on('shown.bs.tab', function(e) {
            var tabHash = e.target.hash;
            window.location.hash = tabHash;
            $(tabHash).find('table.datatable').DataTable().columns.adjust();
        });

        $('#jobs-queue-refresh').click(function() {
            refreshJobQueues();
        });

        var intervalSelect = $('#autorefresh-interval');
        var refreshTask = setInterval(refreshJobQueues, intervalSelect.val() * 1000);
        intervalSelect.change(function() {
            var newInterval = parseInt($(this).val());
            if (refreshTask) {
                clearInterval(refreshTask);
            }
            if (newInterval !== 0) {
                refreshTask = setInterval(refreshJobQueues, newInterval * 1000);
            }
        });
    });
})();
