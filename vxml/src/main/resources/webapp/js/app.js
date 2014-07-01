(function () {
    'use strict';

    /* App Module */

    angular.module('vxml', ['motech-dashboard', 'vxml.controllers', 'vxml.directives', 'ngCookies', 'ui.bootstrap', 'ngRoute', 'ngSanitize']).config(
    ['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/vxml/send', {templateUrl: '../vxml/resources/partials/send.html', controller: 'SendController'}).
                when('/vxml/log', {templateUrl: '../vxml/resources/partials/log.html', controller: 'LogController'}).
                when('/vxml/settings', {templateUrl: '../vxml/resources/partials/settings.html', controller: 'SettingsController'});
    }]);
}());
