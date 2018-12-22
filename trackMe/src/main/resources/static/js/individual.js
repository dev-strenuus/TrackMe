let config = {
    headers : {
        'Content-Type': 'application/json;charset=utf-8;'
    }
};

let app = angular.module("individual", []);

app.service('SharedDataService', function () {
    let sharedData = {
        signUp: true,
        login: true,
        home: false,
        notifications: false,
        settings: false,
        token: ''
    };
    return sharedData;
});

app.controller("individualSignUpController", function($scope, $http, SharedDataService) {
    $scope.individual = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitSignUp = function () {

        console.log($scope.individual);

        //$http.post('http://192.168.100.2:8080/auth/individual/signUp', $scope.individual, config);
        $http.post('/auth/individual/signUp', $scope.individual, config).
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

app.controller("individualLoginController", function($scope, $http, SharedDataService) {
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

app.controller("individualController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.showNotifications = function () {
        $scope.sharedDataService.home = false;
        $scope.sharedDataService.notifications = true;
    };

    $scope.showSettings = function () {
        $scope.sharedDataService.home = false;
        $scope.sharedDataService.settings = true;
    }
});

app.controller("individualNotificationsController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.notifications = [];

    $scope.$watch('sharedDataService.notifications', function (newVal, oldVal) {
        if (newVal == oldVal)
            return;

        $http.defaults.headers.common.Authorization = SharedDataService.token;
        //$http.get("/individual/{individual}/notifications")
        $http.get("/individual/notifications")
            .then(function (response) {
                console.log(response);

                // filtering data
                let thirdParties = response.data.map(function (request) {
                    return request.thirdParty
                });

                let vatNumbers = thirdParties.map(function (thirdParty) {
                    return thirdParty.vat
                });

                $scope.notifications = vatNumbers

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
});

app.controller("individualSettingsController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.notifications = [];

    $scope.$watch('sharedDataService.settings', function (newVal, oldVal) {
        $scope.settings = {};

        if (newVal == oldVal)
            return;
        // TODO: completare in base a cosa vogliamo mostrare

        // get personal data

        // post change of password

        // get the accepted requests
        $http.defaults.headers.common.Authorization = SharedDataService.token;
        //$http.get("/individual/{individual}/notifications")
        $http.get("/individual/notifications")
            .then(function (response) {
                console.log(response);

                // filtering data
                let thirdParties = response.data.map(function (request) {
                    return request.thirdParty
                });

                let vatNumbers = thirdParties.map(function (thirdParty) {
                    return thirdParty.vat
                });

                $scope.notifications = vatNumbers

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
});
