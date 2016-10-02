angular.module('App')
.controller('LoginController', function ($scope, $http, $state, localStorageService) {
    $scope.model = {username: '', password:''};
    $scope.NHSNumber = "";
    $scope.PatientName = "";
    if (localStorageService.get("NHSNumber")) {
              $scope.NHSNumber = localStorageService.get("NHSNumber");
          } else {
              $scope.NHSNumber = "9000000084";
          }
  if (localStorageService.get("PatientName")) {
              $scope.PatientName = localStorageService.get("PatientName");
          } else {
              $scope.PatientName = "";
          }
          
    $scope.login = function(NHSNumber, PatientName)
    {
        localStorageService.set("PatientName","");
        
        localStorageService.set("NHSNumber","");
        $state.go('tabs.home');
    }
    
});

