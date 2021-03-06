//Functions to add behavior to the ivr pages.
(function () {
    'use strict';

    var controllers = angular.module('ivr.controllers', []);

    controllers.controller('TestCallController', function ($scope, Provider, Call) {

        $scope.providers = Provider.all();

        $scope.makeCall = function() {
            $scope.dialed = undefined;
            Call.dial($scope.call,
                function success() {
                    $scope.dialed = true;
                },
                function failure() {
                    $scope.dialed = false;
                }
            );
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });
    });

    controllers.controller('CalllogController', function ($scope, CalllogSearch, CalllogCount, CalllogPhoneNumber, CalllogMaxDuration, $http) {
        $scope.phoneNumber = "";
        $scope.sortColumn = "";
        $scope.currentPage = 0;
        $scope.sortReverse = false;
        $scope.pageCount = {'count':0 };
        var pagesWindowSize = 10,
        toCsv = function(list, separator, terminalSeparator) {
            var s = "";
            $.each(list, function(i, item) {
                s += item;
                if (i < list.length - 2) {
                    s += separator;
                } else if (list.length > 1 && i === list.length - 2) {
                    s += terminalSeparator;
                }
            });
            return s;
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        }, {
            show: true,
            button: '#ivr-call-logs-filters'
        });

        $scope.countPages = function () {
            $scope.pageCount = CalllogCount.query(
                { 'phoneNumber':$scope.phoneNumber,
                    'minDuration':$scope.min,
                    'maxDuration':$scope.max,
                    'fromDate':$scope.from,
                    'toDate':$scope.to,
                    'answered':$scope.answered,
                    'busy':$scope.busy,
                    'failed':$scope.failed,
                    'noAnswer':$scope.noAnswer,
                    'unknown':$scope.unknown,
                    'page':0,
                    'sortColumn':"",
                    'sortReverse':false
                });
        };

        $scope.getCalllogs = function () {

            $scope.calllogs = CalllogSearch.query(
                {'phoneNumber':$scope.phoneNumber,
                    'minDuration':$scope.min,
                    'maxDuration':$scope.max,
                    'fromDate':$scope.from,
                    'toDate':$scope.to,
                    'answered':$scope.answered,
                    'busy':$scope.busy,
                    'failed':$scope.failed,
                    'noAnswer':$scope.noAnswer,
                    'unknown':$scope.unknown,
                    'page':$scope.currentPage,
                    'sortColumn':$scope.sortColumn,
                    'sortReverse':$scope.sortReverse
                });
        };

        $scope.sort = function (column) {
            if ($scope.sortColumn === column) {
                $scope.sortReverse = !$scope.sortReverse;
            } else {
                $scope.sortReverse = false;
            }
            $scope.sortColumn = column;

            $('th img').each(function(){
                $(this).removeClass().addClass('sorting-no');
            });
            //when the column is sorting based on anything but a string
            //(ex. int, date), the column to sort on ends with <type>,
            //(ex. <int>). The column header will not have the <type>
            //at the end, so that must be removed before trying to remove
            //the sorting-no class.
            var columnHeader = $scope.sortColumn,
            index = columnHeader.indexOf("<");
            if(index >= 0) {
                columnHeader = columnHeader.substring(0, index);
            }
            if ($scope.sortReverse) {
                $('th.'+columnHeader+' img').removeClass('sorting-no').addClass('sorting-desc');
            }
            else {
                $('th.'+columnHeader+' img').removeClass('sorting-no').addClass('sorting-asc');
            }

            $scope.getCalllogs();
        };

        $scope.search = function () {
            $scope.currentPage = 0;
            $scope.sortColumn = "";
            $scope.sortReverse = false;

            $scope.phoneNumber = $('#phoneNumber').val();
            $scope.min = $("#slider").slider("values", 0);
            $scope.max = $("#slider").slider("values", 1);
            $scope.from = $("#from").val();
            $scope.to = $("#to").val();
            $scope.answered = $("#answered").is('button.active');
            $scope.busy = $("#busy").is('button.active');
            $scope.failed = $('#failed').is('button.active');
            $scope.noAnswer = $('#noAnswer').is('button.active');
            $scope.unknown = $('#unknown').is('button.active');

            $scope.getCalllogs();
            $scope.countPages();

        };

        $scope.prevPage = function () {
            if ($scope.currentPage > 0) {
                $scope.currentPage-=1;
                $scope.getCalllogs();
            }
        };

        $scope.nextPage = function () {
            if ($scope.currentPage < $scope.pageCount.count - 1) {
                $scope.currentPage+=1;
                $scope.getCalllogs();
            }
        };

        $scope.setPage = function () {
            $scope.currentPage = this.selectedPage;
            $scope.getCalllogs();
        };

        $scope.range = function (start, end) {
            var ret = [], i;
            if (!end) {
                end = start;
                start = 0;
            }
            if ($scope.currentPage + pagesWindowSize <= $scope.pageCount.count) {
                start = $scope.currentPage - pagesWindowSize / 2;
            } else {
                start = $scope.pageCount.count - pagesWindowSize;
            }
            if (start < 0) {
                start = 0;
            }
            end = start + pagesWindowSize;
            if (end > $scope.pageCount.count) {
                end = $scope.pageCount.count;
            }
            for (i = start; i < end; i+=1) {
                ret.push(i);
            }
            return ret;
        };

        $scope.isEmpty = function (obj) {
            return angular.equals({}, obj);
        };

        $scope.setActive = function(active){
            if($('#'+active.toString()).is('button.active') === true) {
                $scope.active = false;
            } else {
                $scope.active = true;
            }
        };
    });
}());
