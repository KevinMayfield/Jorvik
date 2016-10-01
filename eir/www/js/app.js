angular.module('App', ['ionic','LocalStorageModule'])

 .filter("fhirDtasDate", function () {
        return function (input) {
            return new Date(input);
        };
    })
    .config(function ($stateProvider, $urlRouterProvider, localStorageServiceProvider) {
             localStorageServiceProvider
                .setPrefix('eir-fhir');
            $stateProvider
           
            .state('tabs', {
                url: '/tabs',
                abstract : true,
                templateUrl: 'views/tabs/tabs.html'
            })
            .state('tabs.details', {
                url: '/details',
                views: {
                    'details-tab' : {
                        controller: 'PatientDetailsController',
                        templateUrl: 'views/patientdetails/patientdetails.html'
                    }
                }
            })
            .state('tabs.home', {
                url: '/home',
                views: {
                    'home-tab' : {
                        controller: 'HomeController',
                        templateUrl: 'views/home/home.html'
                    }
                }
            })
             .state('tabs.appointments', {
                url: '/appointments',
                views: {
                    'appointments-tab' :{
                        templateUrl: 'views/appointments/appointments.html',
                        controller: 'AppointmentsController'
                    }
                }
            })
            .state('tabs.summary', {
                url: '/summary',
                views: {
                    'summary-tab' :{
                        controller: 'SummaryController',
                        templateUrl: 'views/summary/summary.html'
                    }
                }
            });
        $urlRouterProvider.otherwise('/tabs/home');
        //sessionService.persist('NHSNumber', 'data')
    })
    
    //http://ec2-54-194-109-184.eu-west-1.compute.amazonaws.com
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

