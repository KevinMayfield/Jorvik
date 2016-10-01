angular.module('App')
.controller('PatientDetailsController', function ($scope, $http, $ionicLoading, AppService, localStorageService) {
  
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
  $ionicLoading.show();
  $http({
			method : 'GET',
			url : AppService.baseURL + '/fhir/Patient?identifier=http://fhir.nhs.net/Id/nhs-number|'+$scope.NHSNumber+'&_format=json',
			headers: {
			   'Authorization' : 'Bearer eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwOi8vZWMyLTU0LTE5NC0xMDktMTg0LmV1LXdlc3QtMS5jb21wdXRlLmFtYXpvbmF3cy5jb20vIy9zZWFyY2giLCJzdWIiOiIxIiwiYXVkIjoiaHR0cHM6Ly9hdXRob3JpemUuZmhpci5uaHMubmV0L3Rva2VuIiwiZXhwIjoxNDc1NjE2MDgyLCJpYXQiOjE0NzUzMTYwODIsInJlYXNvbl9mb3JfcmVxdWVzdCI6ImRpcmVjdGNhcmUiLCJyZXF1ZXN0ZWRfcmVjb3JkIjp7InJlc291cmNlVHlwZSI6IlBhdGllbnQiLCJpZGVudGlmaWVyIjpbeyJzeXN0ZW0iOiJodHRwOi8vZmhpci5uaHMubmV0L0lkL25ocy1udW1iZXIiLCJ2YWx1ZSI6IjkwMDAwMDAwODQifV19LCJyZXF1ZXN0ZWRfc2NvcGUiOiJwYXRpZW50LyoucmVhZCIsInJlcXVlc3RpbmdfZGV2aWNlIjp7InJlc291cmNlVHlwZSI6IkRldmljZSIsImlkIjoiMSIsImlkZW50aWZpZXIiOlt7InN5c3RlbSI6IldlYiBJbnRlcmZhY2UiLCJ2YWx1ZSI6IkdQIENvbm5lY3QgRGVtb25zdHJhdG9yIn1dLCJtb2RlbCI6IkRlbW9uc3RyYXRvciIsInZlcnNpb24iOiIxLjAifSwicmVxdWVzdGluZ19vcmdhbml6YXRpb24iOnsicmVzb3VyY2VUeXBlIjoiT3JnYW5pemF0aW9uIiwiaWQiOiIxIiwiaWRlbnRpZmllciI6W3sic3lzdGVtIjoiaHR0cDovL2ZoaXIubmhzLm5ldC9JZC9vZHMtb3JnYW5pemF0aW9uLWNvZGUiLCJ2YWx1ZSI6IltPRFNDb2RlXSJ9XSwibmFtZSI6IkdQIENvbm5lY3QgRGVtb25zdHJhdG9yIn0sInJlcXVlc3RpbmdfcHJhY3RpdGlvbmVyIjp7InJlc291cmNlVHlwZSI6IlByYWN0aXRpb25lciIsImlkIjoiMSIsImlkZW50aWZpZXIiOlt7InN5c3RlbSI6Imh0dHA6Ly9maGlyLm5ocy5uZXQvc2RzLXVzZXItaWQiLCJ2YWx1ZSI6IkcxMzU3OTEzNSJ9LHsic3lzdGVtIjoibG9jYWxTeXN0ZW0iLCJ2YWx1ZSI6IjEifV0sIm5hbWUiOnsiZmFtaWx5IjpbIkRlbW9uc3RyYXRvciJdLCJnaXZlbiI6WyJHUENvbm5lY3QiXSwicHJlZml4IjpbIk1yIl19fX0.',
                            'Ssp-From' : '200000000359',
                            'Ssp-InteractionID':'urn:nhs:names:services:gpconnect:fhir:rest:search:patient',
                            'Ssp-To':'200000000360',
                            'Ssp-TraceID':'45a2728a-bafe-ffb9-c898-a456128c507a'
        }
    })
        .success(function (patientdata) {
    $scope.patient = patientdata.entry[0].resource;
    $ionicLoading.hide();
  }).error(function (err) {
    $ionicLoading.show({
      template: 'Could not load patient details. Please try again later.',
      duration: 3000
    });
  })
});

