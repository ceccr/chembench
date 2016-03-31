(function() {
    'use strict';

    /**
     * Update the previous, next, and active paging states and return the new page index.
     *
     * @param anchor - the paging <a> element that was clicked
     * @returns {Number} the new active page index
     */
    Chembench.updatePages = function(anchor) {
        var target = $(anchor);
        var parent = target.parent();
        var pagination = target.parents('.pagination');
        var active = pagination.find('.active');
        var lastIndex = pagination.children().last().index();
        var currentIndex = active.index();

        if (parent.hasClass('disabled')) {
            return currentIndex;
        } else if (parent.hasClass('previous') || parent.hasClass('next')) {
            var newIndex = currentIndex;
            if (parent.hasClass('previous')) {
                newIndex--;
            } else if (parent.hasClass('next')) {
                newIndex++;
            }
            Chembench.updatePages($(pagination.children().get(newIndex)).find('a'));
            return newIndex;
        } else {
            pagination.children().removeClass('active');
            parent.addClass('active');
            if (parent.index() === 1) {
                pagination.children('.previous').addClass('disabled');
                pagination.children('.next').removeClass('disabled');
            } else if (parent.index() === lastIndex - 1) {
                pagination.children('.next').addClass('disabled');
                pagination.children('.previous').removeClass('disabled');
            } else {
                pagination.children('.previous, .next').removeClass('disabled');
            }
            return parent.index();
        }
    }
})();
