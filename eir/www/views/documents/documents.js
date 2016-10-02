angular.module('App')
    .controller('DocumentsController', function ($scope, AppService, $http, $ionicLoading, localStorageService) {
    
        $scope.$on("$ionicView.enter", function(event, data){
             
               $scope.NHSNumber = localStorageService.get("NHSNumber");
               $scope.PatientName = localStorageService.get("PatientName");
               $scope.load();
         });

         $scope.load = function () {
             $ionicLoading.show();

                 $http({
                     method : 'GET',
                     url : AppService.basehapiURL + '/DocumentReference?subject=Patient/5389&_format=json',
                             //+patientdata.entry[0].resource.id+'&_format=json',
                     headers: {

                     }
                 })
                 .success(function (documentdata) {

                     $scope.documents = documentdata;
             /*
                     $ionicLoading.show({
                       template: 'Loaded',
                       duration: 3000
                     });*/
                     $ionicLoading.hide();

                 })
                 .error(function (err){
                     $ionicLoading.show({
                       template: 'Could not load RLS document details. Please try again later.',
                       duration: 3000
                     });
                 });
                 //$ionicLoading.hide();

         };
    });
