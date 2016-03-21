(function() {
    'use strict';

    function resetForm() {
        // reset form values
        var originalDescription = $('#description-wrapper').find('.value').text();
        var originalPaperReference = $('#paper-reference-wrapper').find('.value').html();
        var form = $('form#object-form');

        form.find('textarea[name="description"]').val(originalDescription);
        form.find('textarea[name="paperReference"]').val(originalPaperReference);
    }

    function toggleForm() {
        $('form#object-form, #description-reference-buttons').toggle();
        $('#description-reference-text, button#edit-description-reference').toggle();
    }

    $(document).ready(function() {
        $('form#object-form, #description-reference-buttons').hide();

        var descriptionWrapper = $('#description-wrapper');
        var description = descriptionWrapper.find('.value');
        if (description.text()) {
            descriptionWrapper.find('.placeholder').hide();
        }

        var paperReferenceWrapper = $('#paper-reference-wrapper');
        var paperReference = paperReferenceWrapper.find('.value');
        // autolink urls within paper reference
        if (paperReference.text()) {
            paperReference.html(paperReference.text().autoLink({target: '_blank'}));
            paperReferenceWrapper.find('.placeholder').hide();
        }


        $('button#edit-description-reference').click(function() {
            toggleForm();
        });

        $('button#cancel-changes').click(function() {
            resetForm();
            toggleForm();
        });

        $('button#save-changes').click(function() {
            var form = $('form#object-form');
            var newDescription = $('textarea[name="description"]').val();
            var newPaperReference = $('textarea[name="paperReference"]').val();
            $.ajax({
                url: form.attr('action'),
                method: 'POST',
                data: form.serialize()
            }).success(function() {
                if (newDescription) {
                    description.show();
                    descriptionWrapper.find('.placeholder').hide();
                    description.text(newDescription);
                } else {
                    description.hide();
                    descriptionWrapper.find('.placeholder').show();
                }
                if (newPaperReference) {
                    paperReference.show();
                    paperReferenceWrapper.find('.placeholder').hide();
                    // XXX needs html() and not text() for autoLink to work
                    paperReference.html(newPaperReference.autoLink({target: '_blank'}));
                } else {
                    paperReference.hide();
                    paperReferenceWrapper.find('.placeholder').show();
                }

                toggleForm();
            }).fail(function() {
                bootbox.alert('Error updating dataset.');
            });
        });
    });
})();
