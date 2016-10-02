angular.module('App')
.controller('HomeController', function ($scope, $http, $state, localStorageService) {
    $scope.model = {term: ''};
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
          
    $scope.select = function(NHSNumber, PatientName)
    {
        localStorageService.set("PatientName",PatientName);
        $scope.PatientName = PatientName;
        $scope.NHSNumber = NHSNumber;
        localStorageService.set("NHSNumber",NHSNumber);
        
        $state.go('tabs.details');
    }
    $scope.search = function()
    {
        $scope.results = {
        "resourceType": "Bundle",    
       "entry": [
            {
                 "resource": {
                     "id" : 4,
                    "identifier": [
                        {
                          'system': 'http://fhir.nhs.net/Id/nhs-number',
                          'value': '9000000033'
                        }
                    ],
                    "name": [
                      {
                        "text": "Freya Blackwell (TPP)"
                      }
                    ]
                }
            },
            {
                 "resource": {
                    "id" : 8,
                    "identifier": [
                        {
                          "system": "http://fhir.nhs.net/Id/nhs-number",
                          "value": "9000000084"
                        }
                    ],
                    "name": [
                      {
                        "text": "Blair Wells (EMIS)"
                      }
                    ]
                }
            },
            {
                 "resource": {
                    "id" : 5,
                    "identifier": [
                        {
                          "system": "http://fhir.nhs.net/Id/nhs-number",
                          "value": "9000000041"
                        }
                    ],
                    "name": [
                      {
                        "text": "Brad Case (INPS)"
                      }
                    ]
                }
            },
            {
                 "resource": {
                    "id" : 1,
                    "identifier": [
                        {
                          "system": "http://fhir.nhs.net/Id/nhs-number",
                          "value": "9000000009"
                        }
                    ],
                    "name": [
                      {
                        "text": "Ivor Cox (MicroTest)"
                      }
                    ]
                }
            },
            {
                 "resource": {
                    "id" : 6,
                    "identifier": [
                        {
                          "system": "http://fhir.nhs.net/Id/nhs-number",
                          "value": "9000000068"
                        }
                    ],
                    "name": [
                      {
                        "text": "Rashad Hill"
                      }
                    ]
                }
            }
        ]
            };
    };
});

