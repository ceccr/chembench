(function() {
    'use strict';

    $(document).ready(function() {
        $('.pagination a').click(function(e) {
            e.preventDefault();
            var target = $(this);
            var targetParent = target.parent();
            var pagination = target.parents('.pagination');
            var active = pagination.children('.active');
            var lastIndex = pagination.children().last().index();
            if (targetParent.hasClass('previous')) {
                if (!targetParent.hasClass('disabled')) {
                    $(pagination.children().get(active.index() - 1)).find('a').click();
                }
            } else if (targetParent.hasClass('next')) {
                if (!targetParent.hasClass('disabled')) {
                    $(pagination.children().get(active.index() + 1)).find('a').click();
                }
            } else {
                pagination.children().removeClass('active');
                targetParent.addClass('active');
                if (targetParent.index() === 1) {
                    pagination.children('.previous').addClass('disabled');
                } else if (targetParent.index() === lastIndex - 1) {
                    pagination.children('.next').addClass('disabled');
                } else {
                    pagination.children('.previous, .next').removeClass('disabled');
                }
            }
        });

        $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
            var pagination = $(e.currentTarget.hash).find('.pagination');
            if (pagination.exists()) {
                var firstFold = pagination.children(':not(".previous, .next")').first();
                firstFold.children('a').click();
            }
        });
    });
})();
