let config = {
    headers : {
        'Content-Type': 'application/json;charset=utf-8;'
    }
};

let app = angular.module("individual", []);

app.service('SharedDataService', function () {
    let sharedData = {
        signUp: false,
        login: true,
        home: false,
        notifications: false,
        settings: false,
        username: '',
        token: ''
    };
    return sharedData;
});

app.controller("individualSignUpController", function($scope, $http, SharedDataService) {
    $scope.individual = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitSignUp = function () {

        console.log($scope.individual);

        $http.post('/auth/individual/signUp', $scope.individual, config).
        then(function onSuccess(response) {
            console.log(response);
            $scope.sharedDataService.signUp = false;
            $scope.sharedDataService.login = true;
        }).
        catch(function onError(response) {
            console.log(response);
        });
    };
    $scope.showLogin = function () {
        $scope.sharedDataService.signUp = false;
        $scope.sharedDataService.login = true;
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
            $scope.sharedDataService.username = $scope.credentials.username;
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
    };
    $scope.showSignUp = function () {
        $scope.sharedDataService.login = false;
        $scope.sharedDataService.signUp = true;
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
    $scope.answer = {};

    $scope.$watch('sharedDataService.notifications', function (newVal, oldVal) {
        if (newVal == oldVal)
            return;

        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $http.get("/individual/" + $scope.sharedDataService.username + "/notifications")
            .then(function (response) {

                console.log(response);

                // filtering data
                console.log(response.data);

                let result = response.data.forEach(function(element) {
                    element.map(function (el) {
                        return el.individualRequest.thirdParty.vat
                    })
                });
                /*
                let individualRequests = response.data.map(function (elem) {
                    return response.data[elem]
                });

                console.log(individualRequests);

                let thirdParties = individualRequests.map(function (request) {
                    return request.thirdParty
                });

                console.log(thirdParties);

                let vatNumbers = thirdParties.map(function (thirdParty) {
                    return thirdParty.vat
                });

                console.log(vatNumbers);
                */

                $scope.notifications = result

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

    $scope.manageRequest = function ($scope, $http) {
        let content = {
            "thirdParty" : $scope.answer.vatNumber,
            "accepted" : $scope.answer.status
        };

        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $http.post("/individual/individualRequest/answer", content, config).
        then(function onSuccess(response) {
            console.log(response);

            // TODO: controllare se  possibile definire una funzione per non ripetere il codice
            $http.defaults.headers.common.Authorization = SharedDataService.token;
            $http.get("/individual/" + $scope.sharedDataService.username + "/notifications")
                .then(function (response) {
                    console.log(response);

                    // TODO: stesso problema delle notifiche individuali
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

    };

    $scope.acceptRequest = function ($scope, $http) {
        $scope.answer.status = true;
        $scope.manageRequest($scope, $http)
    };

    $scope.refuseRequest = function ($scope, $http) {
        $scope.answer.status = true;
        $scope.manageRequest($scope, $http)
    };

    $scope.backHome = function () {
                        $scope.sharedDataService.home = true;
                        $scope.sharedDataService.notifications = false;
    }
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
        $http.get("/individual/"+$scope.sharedDataService.username+"/notifications")
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

    $scope.backHome = function () {
                        $scope.sharedDataService.home = true;
                        $scope.sharedDataService.settings = false;
    }
});

app.controller("individualLogoutController", function ($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.logout = function () {
        $scope.sharedDataService.notifications = false;
        $scope.sharedDataService.settings = false;
        $scope.sharedDataService.home = false;
        $scope.sharedDataService.login = true;
    };
});
