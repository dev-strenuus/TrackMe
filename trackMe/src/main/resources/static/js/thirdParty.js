let config = {
    headers : {
        'Content-Type': 'application/json;charset=utf-8;'
    }
};

let app = angular.module("thirdParty", []);

app.service('SharedDataService', function () {
    let sharedData = {
        signUp: true,
        login: true,
        home: false,
        settings: false,
        notifications: false,
        uername: '',
        token: ''
    };
    return sharedData;
});

app.controller("thirdPartySignUpController", function($scope, $http, SharedDataService) {
    $scope.thirdParty = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitSignUp = function () {

        console.log($scope.thirdParty);

        $http.post('/auth/thirdParty/signUp', $scope.thirdParty, config).
        then(function onSuccess(response) {
            console.log(response);
            $scope.sharedDataService.signUp = false;
            $scope.sharedDataService.login = true;
        }).
        catch(function onError(response) {
            console.log(response);
        });
    }
});

app.controller("thirdPartyLoginController", function($scope, $http, SharedDataService) {
    $scope.credentials = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitLogin = function () {
        console.log($scope.credentials);
        $http.post('/auth', $scope.credentials, config).
        then(function onSuccess(response) {
            // Handle success
            let data = response.data;
            let status = response.status;
            let statusText = response.statusText;
            let headers = response.headers;
            let config = response.config;
            console.log(response);
            $scope.sharedDataService.login = false;
            $scope.sharedDataService.signUp = false;
            $scope.sharedDataService.home = true;
            $scope.sharedDataService.token = 'Bearer ' + response.data.token;
            $scope.sharedDataService.username = $scope.credentials.username
            console.log($scope.sharedDataService.token);
        }).
        catch(function onError(response) {
            // Handle error
            let data = response.data;
            let status = response.status;
            let statusText = response.statusText;
            let headers = response.headers;
            let config = response.config;
            console.log(response);
        });
    }
});


app.controller("thirdPartyController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.people = [];

    $scope.$watch('sharedDataService.home', function(newVal,oldVal){
        if(newVal == oldVal)
            return;

        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $http.get("/people")
            .then(function(response) {
                console.log(response);
                $scope.people = response.data;
            }).
        catch(function onError(response) {
            // Handle error
            let data = response.data;
            let status = response.status;
            let statusText = response.statusText;
            let headers = response.headers;
            let config = response.config;
            console.log(response);
        });
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


    $scope.showSettings = function () {
            $scope.sharedDataService.home = false;
            $scope.sharedDataService.settings = true;
    }

    $scope.showNotifications = function () {
            $scope.sharedDataService.home = false;
            $scope.sharedDataService.notifications = true;
    }

});

app.controller("thirdPartySettingsController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.$watch('sharedDataService.settings', function (newVal, oldVal) {
        $scope.settings = {};

        if (newVal == oldVal)
            return;
        // TODO: completare in base a cosa vogliamo mostrare

    });

    $scope.backHome = function () {
                $scope.sharedDataService.home = true;
                $scope.sharedDataService.settings = false;
    }
});

app.controller("thirdPartyNotificationsController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.notifications = [];

    $scope.$watch('sharedDataService.notifications', function (newVal, oldVal) {
        if (newVal == oldVal)
            return;
        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $http.get("/thirdParty/"+$scope.sharedDataService.username+"/notifications")
            .then(function (response) {
                console.log(response);
                $scope.notifications = response.data;
            }).catch(function onError(response) {
                // Handle error
                let data = response.data;
                let status = response.status;
                let statusText = response.statusText;
                let headers = response.headers;
                let config = response.config;
                console.log(response);
            });
    });

    $scope.backHome = function () {
                    $scope.sharedDataService.home = true;
                    $scope.sharedDataService.notifications = false;
    }

    $scope.requestPastData = function (fiscalCode) {

        };
});

app.controller("thirdPartyLogoutController", function ($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.logout = function () {
        $scope.sharedDataService.notifications = false;
        $scope.sharedDataService.settings = false;
        $scope.sharedDataService.home = false;
        $scope.sharedDataService.login = true;
    };
});


