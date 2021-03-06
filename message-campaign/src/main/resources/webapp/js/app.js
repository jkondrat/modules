(function () {
    'use strict';

        angular.module('messageCampaign', ['motech-dashboard', 'messageCampaign.services', 'messageCampaign.controllers', 'ngCookies', 'ngRoute'])
        .config(['$routeProvider', function ($routeProvider) {

            $routeProvider
                .when('/messageCampaign/campaigns', { templateUrl: '../messagecampaign/resources/partials/campaigns.html', controller: 'CampaignsCtrl' })
                .when('/messageCampaign/enrollments/:campaignName', { templateUrl: '../messagecampaign/resources/partials/enrollments.html', controller: 'EnrollmentsCtrl' })
                .when('/messageCampaign/admin', { templateUrl: '../messagecampaign/resources/partials/admin.html' })
                .when('/messageCampaign/settings', {templateUrl: '../messagecampaign/resources/partials/settings.html', controller: 'SettingsCtrl'});
        }]
    );
}());
