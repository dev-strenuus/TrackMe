let config = {
    headers: {
        'Content-Type': 'application/json;charset=utf-8;'
    }
};

let app = angular.module("thirdParty", ["ngRoute"]);

app.config(function ($routeProvider) {
    $routeProvider
        .when("/", {
            templateUrl: "thirdPartyLogin.html",
            controller: "thirdPartyLoginController"
        })
        .when("/home", {
            templateUrl: "thirdPartyHome.html",
            controller: "thirdPartyController"
        }).when("/signUp", {
        templateUrl: "thirdPartySignUp.html",
        controller: "thirdPartySignUpController"
    }).when("/login", {
        templateUrl: "thirdPartyLogin.html",
        controller: "thirdPartyLoginController"
    }).when("/notifications", {
        templateUrl: "thirdPartyNotifications.html",
        controller: "thirdPartyNotificationsController"
    }).when("/settings", {
        templateUrl: "thirdPartySettings.html",
        controller: "thirdPartySettingsController"
    }).when("/watchDataRequest", {
        templateUrl: "thirdPartyWatchDataRequest.html",
        controller: "thirdPartyWatchDataRequestController"
    }).when("/watchDataGroupAnswer", {
        templateUrl: "thirdPartyWatchGroupAnswer.html",
        controller: "thirdPartyWatchGroupAnswerController"
    });
});

app.service('SharedDataService', function () {
    let sharedData = {
        loggedIn: false,
        username: '',
        pointedIndividual: '',
        pointedGroup: '',
        token: ''
    };
    return sharedData;
});

app.service('SavedNewDataService', function () {
    let newData = [];

    return {
        getData: function () {
            return newData;
        },
        addDatum: function (datum) {
            newData.push(datum);
        }
    };
});

app.controller("mainController", function ($scope, $http, $interval, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $http.defaults.headers.common.Authorization = SharedDataService.token;
    $scope.notification = 0;


    $interval(function(){
        if($scope.sharedDataService.loggedIn == false)
            return;
        $http.get("/thirdParty/" + $scope.sharedDataService.username + "/notifications/countIndividualRequests")
        .then(function (response) {
            console.log(response);
            $scope.notification = response.data;

        }).catch(function onError(response) {
            console.log(response);
        });}, 5000, 5000);

});

app.controller("thirdPartySignUpController", function ($scope, $http, $location, SharedDataService) {
    $scope.thirdParty = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitSignUp = function () {

        console.log($scope.thirdParty);

        $http.post('/auth/thirdParty/signUp', $scope.thirdParty, config).then(function onSuccess(response) {
            console.log(response);
            $scope.sharedDataService.signUp = false;
            $location.path("/login");
        }).catch(function onError(response) {
            console.log(response);
        });
    }
});

app.controller("thirdPartyLoginController", function ($scope, $http, $location, SharedDataService) {
    $scope.credentials = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitLogin = function () {
        console.log($scope.credentials);
        $http.post('/auth', $scope.credentials, config).then(function onSuccess(response) {
            console.log(response);
            $scope.sharedDataService.token = 'Bearer ' + response.data.token;
            $scope.sharedDataService.username = $scope.credentials.username;
            $scope.sharedDataService.loggedIn = true;
            $location.path("/home");
            console.log($scope.sharedDataService.token);
        }).catch(function onError(response) {
            console.log(response);
            $scope.loginResult = "Wrong Password or VAT";
        });
    }
});


app.controller("thirdPartyController", function ($scope, $http, $route,  SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.people = [];


    $http.defaults.headers.common.Authorization = SharedDataService.token;
    $http.get("/people")
            .then(function (response) {
                console.log(response);
                $scope.people = response.data;
            }).catch(function onError(response) {
            console.log(response);
            });

    $scope.refreshPeople = function () {
        $route.reload();
    };

    $scope.data = {
        individual: {
            fiscalCode: ""
        },
        thirdParty: {
            vat: ""
        }
    };

    $scope.submitIndividualRequest = function () {

        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $scope.data.thirdParty.vat = SharedDataService.username;
        $http.post('/thirdParty/individualRequest', $scope.data, config).then(function onSuccess(response) {
            console.log(response);
            $scope.indReqResult = "The Individual Request has been correctly sent.";
        }).catch(function onError(response) {
            console.log(response);
            $scope.indReqResult = "The Individual Request has not been sent. Check the Fiscal Code and retry.";
        });
    }

    $scope.groupData = {
        thirdParty: {
            vat: ""
        },
        startAge: null,
        endAge: null,
        lat1: null,
        lat2: null,
        lon1: null,
        lon2: null
    };

    $scope.submitGroupRequest = function () {

          $http.defaults.headers.common.Authorization = SharedDataService.token;
          $scope.groupData.thirdParty.vat = SharedDataService.username;
          $http.post('/thirdParty/anonymousRequest', $scope.groupData, config).
          then(function onSuccess(response) {
              console.log(response);
              $scope.groupReqResult = "The Group Request has been correctly sent.";
          }).
          catch(function onError(response) {
              console.log(response);
              $scope.groupReqResult = "The Group Request has not been sent. Check the parameters and retry.";
          });
      }

});

app.controller("thirdPartySettingsController", function ($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.settings = {};

    $scope.vat = $scope.sharedDataService.username;

    $scope.updatePassword = function () {
            //PUT
        };
});

app.controller("thirdPartyNotificationsController", function ($scope, $http, $location, $interval, SharedDataService, SavedNewDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.individualRequests = [];
    $scope.groupRequest = [];
    $scope.savedNewData = SavedNewDataService;

    $http.defaults.headers.common.Authorization = SharedDataService.token;
    $http.get("/thirdParty/" + $scope.sharedDataService.username + "/individualRequests")
        .then(function (response) {
            console.log(response);
            for (let i = 0; i < response.data.length; i++) {
                    $scope.individualRequests.push(response.data[i]);
            }
        }).catch(function onError(response) {
        console.log(response);
    });

    $http.get("/thirdParty/" + $scope.sharedDataService.username + "/anonymousRequests")
        .then(function (response) {
            console.log(response);
            for (let i = 0; i < response.data.length; i++) {
                $scope.groupRequest.push(response.data[i]);
            }
        }).catch(function onError(response) {
        console.log(response);
    });

    let notificationsPromise = $interval(function(){$http.get("/thirdParty/" + $scope.sharedDataService.username + "/notifications/individualRequests")
        .then(function (response) {
            console.log(response);
            for(i=0; i<response.data.length; i++)
                for(j=0; j<$scope.individualRequests.length; j++)
                    if(response.data[i].individual.fiscalCode == $scope.individualRequests[j].individual.fiscalCode)
                        $scope.individualRequests[j].accepted = response.data[i].accepted;

        }).catch(function onError(response) {
            console.log(response);
        });}, 5000, 5000);

    $scope.$on('$destroy',function(){
        if(notificationsPromise)
            $interval.cancel(notificationsPromise);
    });

    $scope.watchDataGroupAnswer = function (id) {
        $location.path("/watchDataGroupAnswer");
        $scope.sharedDataService.pointedGroup = id;
    };

    $scope.watchDataRequest = function (fiscalCode) {
        $location.path("/watchDataRequest");
        $scope.sharedDataService.pointedIndividual = fiscalCode;
    };

});

app.controller("thirdPartyLogoutController", function ($scope, $http, $location, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.logout = function () {
        $scope.sharedDataService.loggedIn = false;
        $location.path("/login");

    };
});

app.controller("thirdPartyWatchDataRequestController", function ($scope, $http, $interval, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.data = [];
    $scope.newData = [];
    $scope.isThereNewData = false;

    $http.defaults.headers.common.Authorization = SharedDataService.token;

    $http.get("/thirdParty/" + $scope.sharedDataService.username + "/" + $scope.sharedDataService.pointedIndividual + "/data")
        .then(function (response) {
            console.log(response);
            for (let i = 0; i < response.data.length; i++) {
                let datum = response.data[i];
                console.log(datum);
                $scope.data.unshift(datum);
            }
        }).catch(function onError(response) {
        console.log(response);
    });


    let promise = $interval(function(){
        $http.get("/thirdParty/"+$scope.sharedDataService.username+"/notifications/"+$scope.sharedDataService.pointedIndividual)
            .then(function (response) {
                console.log("inside");
                $scope.isThereNewData = true;
                console.log(response);
                for(i=0; i<response.data.length; i++){
                    $scope.newData.unshift(response.data[i]);
                }
            }).catch(function onError(response) {
            console.log(response);
        });
    }, 5000, 5000);

    $scope.$on('$destroy',function(){
        if(promise)
            $interval.cancel(promise);
    });
});

app.controller("thirdPartyWatchGroupAnswerController", function ($scope, $http, $interval, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.newData = [];


    $http.defaults.headers.common.Authorization = SharedDataService.token;
    $http.get("/thirdParty/" + $scope.sharedDataService.username + "/anonymousAnswer/"+ $scope.sharedDataService.pointedGroup)
        .then(function (response) {
            console.log(response);
            for (let i = 0; i < response.data.length; i++) {
                let answer = response.data[i];
                for(let j=0; j< answer.individualDataList.length; j++)
                    $scope.newData.unshift(answer.individualDataList[j]);
            }
        }).catch(function onError(response) {
        console.log(response);
    });

    let promise = $interval(function(){
        $http.get("/thirdParty/" + $scope.sharedDataService.username + "/anonymousAnswer/notifications/"+ $scope.sharedDataService.pointedGroup)
            .then(function (response) {
                console.log("inside");
                console.log(response);
                for (let i = 0; i < response.data.length; i++) {
                    let answer = response.data[i];
                    for(let j=0; j< answer.individualDataList.length; j++)
                        $scope.newData.unshift(answer.individualDataList[j]);
                }
            }).catch(function onError(response) {
            console.log(response);
        });
    }, 5000, 5000);

    $scope.$on('$destroy',function(){
        if(promise)
            $interval.cancel(promise);
    });

});


