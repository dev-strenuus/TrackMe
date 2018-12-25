let config = {
    headers : {
        'Content-Type': 'application/json;charset=utf-8;'
    }
};

let app = angular.module("thirdParty", ["ngRoute"]);

app.config(function($routeProvider) {
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
    });
});

app.service('SharedDataService', function () {
    let sharedData = {
        loggedIn: false,
        uername: '',
        token: ''
    };
    return sharedData;
});

app.controller("thirdPartySignUpController", function($scope, $http, $location, SharedDataService) {
    $scope.thirdParty = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitSignUp = function () {

        console.log($scope.thirdParty);

        $http.post('/auth/thirdParty/signUp', $scope.thirdParty, config).
        then(function onSuccess(response) {
            console.log(response);
            $scope.sharedDataService.signUp = false;
            $location.path("/login");
        }).
        catch(function onError(response) {
            console.log(response);
        });
    }
});

app.controller("thirdPartyLoginController", function($scope, $http, $location, SharedDataService) {
    $scope.credentials = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitLogin = function () {
        console.log($scope.credentials);
        $http.post('/auth', $scope.credentials, config).
        then(function onSuccess(response) {
            console.log(response);
            $scope.sharedDataService.loggedIn = true;
            $scope.sharedDataService.token = 'Bearer ' + response.data.token;
            $scope.sharedDataService.username = $scope.credentials.username;
            $location.path("/home");
            console.log($scope.sharedDataService.token);
        }).
        catch(function onError(response) {
            console.log(response);
        });
    }
});


app.controller("thirdPartyController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.people = [];


    $http.defaults.headers.common.Authorization = SharedDataService.token;
    $http.get("/people")
        .then(function(response) {
            console.log(response);
            $scope.people = response.data;
        }).
    catch(function onError(response) {
        console.log(response);
    });

    $scope.data={
        individual:{fiscalCode:""},
        thirdParty:{vat: ""}
    }

    $scope.submitIndividualRequest = function () {

        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $scope.data.thirdParty.vat=SharedDataService.username;
        $http.post('/thirdParty/individualRequest', $scope.data, config).
        then(function onSuccess(response) {
            console.log(response);
            $scope.indReqResult = "The Individual Request has been correctly sent.";
        }).
        catch(function onError(response) {
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

app.controller("thirdPartySettingsController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.settings = {};

    //TODO
});

app.controller("thirdPartyNotificationsController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.notifications = [];

    $http.defaults.headers.common.Authorization = SharedDataService.token;
    $http.get("/thirdParty/"+$scope.sharedDataService.username+"/notifications")
        .then(function (response) {
            console.log(response);
            $scope.notifications = response.data;
        }).catch(function onError(response) {
            console.log(response);
        });

    $scope.requestPastData = function (fiscalCode) {

        };
});

app.controller("thirdPartyLogoutController", function ($scope, $http, $location, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.logout = function () {
        $scope.sharedDataService.loggedIn = false;
        $location.path("/login");
    };
});


