angular.module('App', ['ionic'])
    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('home', {
                url :'/home',
                templateUrl: 'views/home/home.html'
            })
            .state('patientdetails', {
                url: '/patientdetails',
                controller: 'PatientDetailsController',
                templateUrl: 'views/patientdetails/patientdetails.html'
            })
            .state('summary', {
                url: '/summary',
                controller: 'SummaryController',
                templateUrl: 'views/summary/summary.html'
            })
            .state('appointments', {
                url: '/appointments',
                controller: 'AppointmentsController',
                templateUrl: 'views/appointments/appointments.html'
            });
        $urlRouterProvider.otherwise('/home');
       
    })
            .factory('AppService', function() {
                return {
       baseURL : '',
       NHSNumber :'9000000084'
  };
})
    .run(function($ionicPlatform) {
        $ionicPlatform.ready(function() {
            if(window.cordova && window.cordova.plugins.Keyboard) {
              cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
            }
            if(window.StatusBar) {
              StatusBar.styleDefault();
            }
        });
    })
