angular.module('App')
.controller('SummaryController', function ($scope,AppService, $http, $ionicLoading) {
  $ionicLoading.show();
  var SummaryUrl = '';
  var SummaryMethod = 'POST';
  if (AppService.baseURL.length > 1)
  {
      SummaryUrl = AppService.baseURL + '/fhir/Patient/$gpc.getcarerecord';
  }
  else
  {
      SummaryUrl = 'careRecord/'+AppService.NHSNumber+'.json';
      SummaryMethod = 'GET';
  }
  $http({
			method : SummaryMethod,
			url : SummaryUrl,
			headers: {
			   'Authorization' : 'Bearer eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwOi8vZWMyLTU0LTE5NC0xMDktMTg0LmV1LXdlc3QtMS5jb21wdXRlLmFtYXpvbmF3cy5jb20vIy9zZWFyY2giLCJzdWIiOiIxIiwiYXVkIjoiaHR0cHM6Ly9hdXRob3JpemUuZmhpci5uaHMubmV0L3Rva2VuIiwiZXhwIjoxNDc1NjE2MDgyLCJpYXQiOjE0NzUzMTYwODIsInJlYXNvbl9mb3JfcmVxdWVzdCI6ImRpcmVjdGNhcmUiLCJyZXF1ZXN0ZWRfcmVjb3JkIjp7InJlc291cmNlVHlwZSI6IlBhdGllbnQiLCJpZGVudGlmaWVyIjpbeyJzeXN0ZW0iOiJodHRwOi8vZmhpci5uaHMubmV0L0lkL25ocy1udW1iZXIiLCJ2YWx1ZSI6IjkwMDAwMDAwODQifV19LCJyZXF1ZXN0ZWRfc2NvcGUiOiJwYXRpZW50LyoucmVhZCIsInJlcXVlc3RpbmdfZGV2aWNlIjp7InJlc291cmNlVHlwZSI6IkRldmljZSIsImlkIjoiMSIsImlkZW50aWZpZXIiOlt7InN5c3RlbSI6IldlYiBJbnRlcmZhY2UiLCJ2YWx1ZSI6IkdQIENvbm5lY3QgRGVtb25zdHJhdG9yIn1dLCJtb2RlbCI6IkRlbW9uc3RyYXRvciIsInZlcnNpb24iOiIxLjAifSwicmVxdWVzdGluZ19vcmdhbml6YXRpb24iOnsicmVzb3VyY2VUeXBlIjoiT3JnYW5pemF0aW9uIiwiaWQiOiIxIiwiaWRlbnRpZmllciI6W3sic3lzdGVtIjoiaHR0cDovL2ZoaXIubmhzLm5ldC9JZC9vZHMtb3JnYW5pemF0aW9uLWNvZGUiLCJ2YWx1ZSI6IltPRFNDb2RlXSJ9XSwibmFtZSI6IkdQIENvbm5lY3QgRGVtb25zdHJhdG9yIn0sInJlcXVlc3RpbmdfcHJhY3RpdGlvbmVyIjp7InJlc291cmNlVHlwZSI6IlByYWN0aXRpb25lciIsImlkIjoiMSIsImlkZW50aWZpZXIiOlt7InN5c3RlbSI6Imh0dHA6Ly9maGlyLm5ocy5uZXQvc2RzLXVzZXItaWQiLCJ2YWx1ZSI6IkcxMzU3OTEzNSJ9LHsic3lzdGVtIjoibG9jYWxTeXN0ZW0iLCJ2YWx1ZSI6IjEifV0sIm5hbWUiOnsiZmFtaWx5IjpbIkRlbW9uc3RyYXRvciJdLCJnaXZlbiI6WyJHUENvbm5lY3QiXSwicHJlZml4IjpbIk1yIl19fX0.',
                            'Ssp-From' : '200000000359',
                            'Ssp-InteractionID':'urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord',
                            'Ssp-To':'200000000360',
                            'Ssp-TraceID':'4117ee37-7125-13b5-cbfc-7e096cfe1ad3',
                            'Accept' : 'application/json',
                            'Content-Type' : 'application/json'
                        }
                       ,
                        body : {
                            'resourceType' : 'Parameters',
                            'parameter' : [
                                {
                                    'name' : 'patientNHSNumber',
                                    'valueIdentifier' : 
                                    { 
                                        'value' : AppService.NHSNumber 
                                    }
                                },
                                {
                                    'name' : 'recordSection',
                                    'valueString' : 'PRB'
                                },
                                {
                                    'name' : 'timePeriod',
                                    'valuePeriod': 
                                    { 
                                        'start' : '2015',
                                        'end' : "2016" 
                                    }
                                }
                            ]
                        }
    })
        .success(function (patientdata) {
    $scope.summary = patientdata.entry[3].resource;
    $ionicLoading.hide();
  }).error(function (err) {
    $ionicLoading.show({
      template: 'Could not load patient summary. Please try again later.',
      duration: 3000
    });
  })
});
