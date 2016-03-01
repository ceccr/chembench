(function() {
    'use strict';

    $.fn.exists = function() {
        return this.length !== 0;
    };

    String.prototype.toProperCase = function() {
        return this.replace(/\b\w+/g, function(s) {
            return s.charAt(0).toUpperCase() + s.substr(1).toLowerCase();
        });
    };

    String.prototype.contains = function(needle) {
        return this.indexOf(needle) > -1;
    };

    Array.prototype.contains = function(needle) {
        for (var i = 0; i < this.length; i++) {
            if (this[i] === needle) {
                return true;
            }
        }
        return false;
    };

    /**
     * Checks if a dataset can generate a MODI value.
     *
     * Note: this is a direct copy of persistence.Dataset#canGenerateModi(); attempting to reuse that code by making an
     * AJAX call per dataset would incur too much of a speed penalty, so it is reproduced here in JS.
     *
     * @param dataset - the dataset to check
     * @returns true if MODI can be generated, false otherwise
     */
    Chembench.canGenerateModi = function(dataset) {
        var actFile = dataset.actFile;
        var availableDescriptors = dataset.availableDescriptors;
        return !!actFile && actFile.length > 0 &&
               (availableDescriptors.contains('DRAGONH') || availableDescriptors.contains('CDK'));
    };

    Chembench.formatJobType = function(text) {
        return text.toProperCase();
    };

    Chembench.formatActivityType = function(text) {
        return text.toProperCase();
    };

    Chembench.formatDatasetType = function(text) {
        return text.replace('MODELING', 'Modeling').replace('PREDICTION', 'Prediction').replace('WITHDESCRIPTORS',
                ', with descriptors');
    };

    Chembench.formatSplitType = function(text) {
        return text.replace('NFOLD', 'N-fold').replace('RANDOM', 'Random Split').replace('USERDEFINED', 'User-defined');
    };

    Chembench.formatModelingMethod = function(text) {
        return text.replace('RANDOMFOREST', 'RF');
    };

    Chembench.formatDescriptorType = function(text) {
        return text.replace('DRAGON', 'Dragon').replace('NOH', 'NoH').replace('MOLCONNZ',
                'MolconnZ').replace(Chembench.Constants.UPLOADED, 'Uploaded');
    };

    Chembench.formatAvailableDescriptors = function(text) {
        var descriptorList = text.trim().split(/\s+/);
        var newDescriptorList = descriptorList.map(function(d) {
            return Chembench.formatDescriptorType(d);
        });
        return newDescriptorList.join(', ');
    };

    Chembench.formatModi = function(text, dataset) {
        var html;
        if (dataset && Chembench.canGenerateModi(dataset) === false) {
            html = '<span class="text-muted">Not available</span>';
        } else if (dataset && dataset.modiGenerated === false) {
            html = '<span class="text-warning">Not generated</span>' +
                   '<button class="btn btn-primary btn-xs generate-modi">Generate MODI</button>';
        } else {
            var value = parseFloat(text);
            var cssClass = 'text-danger';
            var tooltip = 'Not modelable';
            if (value >= Chembench.MODI_MODELABLE) {
                cssClass = 'text-success';
                tooltip = 'Modelable';
            }
            html = '<span title="' + tooltip + '" class="' + cssClass + ' modi-value">' + value.toFixed(2) + '</span>';
        }
        return html;
    };

    Chembench.formatExternalPredictionAccuracy = function(fullValue) {
        var cssClass;
        if (fullValue) {
            var value = (fullValue.contains(' ± ')) ? fullValue.split(' ± ')[0] : fullValue;
            if (value >= 0.7) {
                cssClass = 'text-success';
            } else if (value > 0.5) {
                cssClass = 'text-warning';
            } else {
                cssClass = 'text-danger';
            }
        }
        return '<span class="' + cssClass + ' external-acc-value">' + fullValue + '</span>';
    };

    Chembench.addRowHighlighting = function(row) {
        // add contextual highlighting for rows with MODI or R^2/CCR values
        var match = /text-(danger|warning|success)/.exec(row.find('.modi-value, .external-acc-value').attr('class'));
        if (match !== null) {
            row.addClass(match[1]);
        }
    };

    $(document).ready(function() {
        $.get('api/getCurrentUser', function(data) {
            Chembench.CURRENT_USER = data;
        });

        $('.nav-tabs li a').click(function() {
            history.pushState(null, null, $(this).attr('href'));
        });

        // navigation button handlers
        $('.nav-list li').mouseup(function(event) {
            if (event.which === 1) {
                window.location = $(this).find('a').attr('href');
            }
        }).on('mouseenter mouseleave', function(event) {
            $(this).find('a').toggleClass('hovered', event.type === 'mouseenter');
        });

        // default highlighted button should be Home
        $('#nav-button-home').addClass('active');

        $('.guest-login').click(function(event) {
            event.preventDefault();

            var guestMessage = 'A guest account allows a user to explore the functionality of Chembench using publicly ' +
                               'available datasets, predictions on single molecules, and modeling using Random Forests. ' +
                               '<br><br> All guest data is deleted when you leave the site or become inactive for 90 minutes. ' +
                               'For additional functionality, please register an account.';

            bootbox.confirm(guestMessage, function(response) {
                if (response === true) {
                    window.location = $('.guest-login').attr('href');
                }
            });
        });

        // replace ugly capitalization for constants
        $('.available-descriptors').each(function() {
            var element = $(this);
            element.text(Chembench.formatAvailableDescriptors(element.text()));
        });

        $('.modeling-method').each(function() {
            var element = $(this);
            element.text(Chembench.formatModelingMethod(element.text()));
        });

        $('.split-type').each(function() {
            var element = $(this);
            element.text(Chembench.formatSplitType(element.text()));
        });

        $('.dataset-type').each(function() {
            var element = $(this);
            element.text(Chembench.formatDatasetType(element.text()));
        });

        $('.activity-type').each(function() {
            var element = $(this);
            element.text(Chembench.formatActivityType(element.text()));
        });

        $('.job-type').each(function() {
            var element = $(this);
            element.text(Chembench.formatJobType(element.text()));
        });

        $('.modi-value').each(function() {
            var element = $(this);
            // XXX n.b. use of html(), not text()
            element.html(Chembench.formatModi(element.text()));
        });
    });
})();
