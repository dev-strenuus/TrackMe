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
    });
});

app.service('SharedDataService', function () {
    let sharedData = {
        loggedIn: false,
        username: '',
        pointedIndividual: '',
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

app.controller("mainController", function ($scope, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
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
            $scope.sharedDataService.loggedIn = true;
            $scope.sharedDataService.token = 'Bearer ' + response.data.token;
            $scope.sharedDataService.username = $scope.credentials.username;
            $location.path("/home");
            console.log($scope.sharedDataService.token);
        }).catch(function onError(response) {
            console.log(response);
            $scope.loginResult = "Wrong Password or VAT";
        });
    }
});


app.controller("thirdPartyController", function ($scope, $http, SharedDataService) {
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

    //GROUP REQUEST
    /*$scope.submitGroupRequest = function () {

          $http.defaults.headers.common.Authorization = SharedDataService.token;
          $http.post('/thirdParty/groupRequest', $scope.data, config).
          then(function onSuccess(response) {
              console.log(response);
              $scope.indReqResult = "The Group Request has been correctly sent.";
          }).
          catch(function onError(response) {
              console.log(response);
              $scope.indReqResult = "The Group Request has not been sent. Check the parameters and retry.";
          });
      }*/

});

app.controller("thirdPartySettingsController", function ($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.settings = {};

    //TODO
});

app.controller("thirdPartyNotificationsController", function ($scope, $http, $location, SharedDataService, SavedNewDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.individualRequests = [];
    $scope.savedNewData = SavedNewDataService;

    $http.defaults.headers.common.Authorization = SharedDataService.token;
    $http.get("/thirdParty/" + $scope.sharedDataService.username + "/notifications")
        .then(function (response) {
            console.log(response);
            for (let i = 0; i < response.data.length; i++) {
                let notification = response.data[i];
                if (notification.individualRequest != null)
                    $scope.individualRequests.push(notification.individualRequest);
                if (notification.individualDataList != null && notification.individualDataList.length > 0) //TODO: i nuovi dati vanno passati alla pagina successiva, sotto la get a riga 192 (eventualmente introducendo un divisorio nell'hmtl)
                // TODO: i nuovi dati sono salvati e accessibili tramite il metodo getData() del servizio SavedNewDataService
                    $scope.savedNewData.addDatum(notification.individualDataList);
            }
        }).catch(function onError(response) {
        console.log(response);
    });

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

app.controller("thirdPartyWatchDataRequestController", function ($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.data = [];

    $http.defaults.headers.common.Authorization = SharedDataService.token;

    $http.get("/thirdParty/" + $scope.sharedDataService.username + "/" + $scope.sharedDataService.pointedIndividual + "/data")
        .then(function (response) {
            console.log(response);
            for (let i = 0; i < response.data.length; i++) {
                let datum = response.data[i];
                console.log(datum);
                $scope.data.push(datum);
            }
        }).catch(function onError(response) {
        console.log(response);
    });


    $scope.sendRequest = function () {
        // get data generated between the two dates
        $http.get("/thirdParty/" + $scope.sharedDataService.username + "/" + $scope.sharedDataService.pointedIndividual + "/" + Date($scope.request.startingDate) + "/" + Date($scope.request.endingDate) + "/data") //TODO: da testare, non so se è corretto con il Date()
            .then(function (response) {
                console.log(response);
                $scope.pastDataReqResult = "The Past Data Request has been correctly sent. Here follow the data that you requested:";
                for (let i = 0; i < response.data.length; i++) {
                    let datum = response.data[i];
                    console.log(datum);
                    $scope.data.push(datum);
                }
            }).catch(function onError(response) {
            $scope.pastDataReqResult = "Something went wrong. Check the coherence of the dates!";
            console.log(response);
        });
    };

});


