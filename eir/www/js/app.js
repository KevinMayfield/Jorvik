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
            .state('login', {
                url: '/login',
                controller: 'LoginController',
                templateUrl: 'views/login/login.html'
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
            .state('tabs.documents', {
                url: '/documents',
                views: {
                    'documents-tab' : {
                        controller: 'DocumentsController',
                        templateUrl: 'views/documents/documents.html'
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
        $urlRouterProvider.otherwise('/login');
        //sessionService.persist('NHSNumber', 'data')
    })
    //
    //
            .factory('AppService', function() {
                
                /*
                return {
       baseURL : '',
       basehapiURL : 'hapi',
       NHSNumber :'9000000084'
  };*/
  
   return {
       baseURL : 'http://ec2-54-194-109-184.eu-west-1.compute.amazonaws.com',
       basehapiURL : 'http://fhirtest.uhn.ca/baseDstu2',
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

