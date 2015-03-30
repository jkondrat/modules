(function () {
    'use strict';

    var directives = angular.module('csd.directives', []);

    directives.directive('csdDatePicker', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                var elem = angular.element(element);

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    minDate: 0,
                    timeFormat: "HH:mm",
                    minTime: 0,
                    onSelect: function (selectedDateTime) {
                        scope.$apply(function() {
                            ngModel.$setViewValue(selectedDateTime);
                        });
                    }
                });
            }
        };
    });

}());