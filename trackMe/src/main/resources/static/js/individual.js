let config = {
    headers: {
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

app.controller("individualSignUpController", function ($scope, $http, SharedDataService) {
    $scope.individual = {};
    $scope.sharedDataService = SharedDataService;

    $scope.submitSignUp = function () {
        console.log($scope.individual);
        $http.post('/auth/individual/signUp', $scope.individual, config).then(function onSuccess(response) {
            console.log(response);
            $scope.sharedDataService.signUp = false;
            $scope.sharedDataService.login = true;
        }).catch(function onError(response) {
            console.log(response);
        });
    };
    $scope.showLogin = function () {
        $scope.sharedDataService.signUp = false;
        $scope.sharedDataService.login = true;
    }
});

app.controller("individualLoginController", function ($scope, $http, SharedDataService) {
    $scope.credentials = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitLogin = function () {
        console.log($scope.credentials);
        $http.post('/auth', $scope.credentials, config).then(function onSuccess(response) {
            // Handle success
            let data = response.data;
            let status = response.status;
            let statusText = response.statusText;
            let headers = response.headers;
            let config = response.config;
            console.log(response);
            $scope.sharedDataService.login = false;
            $scope.sharedDataService.home = true;
            $scope.sharedDataService.username = $scope.credentials.username;
            $scope.sharedDataService.token = 'Bearer ' + response.data.token;
            console.log($scope.sharedDataService.token);
        }).catch(function onError(response) {
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

app.controller("individualController", function ($scope, $http, SharedDataService) {
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

app.controller("individualNotificationsController", function ($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.notifications = [];
    $scope.answer = {};

    $scope.$watch('sharedDataService.notifications', function (newVal, oldVal) {
        if (newVal == oldVal || newVal == false)
            return;

        $http.defaults.headers.common.Authorization = $scope.sharedDataService.token;
        $http.get("/individual/" + $scope.sharedDataService.username + "/notifications")
            .then(function (response) {

                $scope.pendingRequests = response.data;

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

    $scope.acceptRequest = function (vatNumber) {
        console.log($scope.answer);
        $scope.answer.vatNumber = vatNumber;
        $scope.answer.status = true;
        $scope.manageRequest()
    };

    $scope.refuseRequest = function (vatNumber) {
        console.log($scope.answer);
        $scope.answer.vatNumber = vatNumber;
        $scope.answer.status = false;
        $scope.manageRequest()
    };

    $scope.manageRequest = function () {

        let content = {
            "individual" : {
                "fiscalCode" : $scope.sharedDataService.username
            },
            "thirdParty": {
                "vat" : $scope.answer.vatNumber
            },
            "accepted": $scope.answer.status
        };

        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $http.post("/individual/individualRequest/answer", content, config).then(function onSuccess(response) {

            $http.get("/individual/" + $scope.sharedDataService.username + "/notifications")
                .then(function (response) {

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
        }).catch(function onError(response) {
            // Handle error
            let data = response.data;
            let status = response.status;
            let statusText = response.statusText;
            let headers = response.headers;
            let config = response.config;
            console.log(response);
        });
    };

    $scope.backHome = function () {
        $scope.sharedDataService.home = true;
        $scope.sharedDataService.notifications = false;
    }
});

app.controller("individualSettingsController", function ($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.notifications = [];

    $scope.$watch('sharedDataService.settings', function (newVal, oldVal) {
        $scope.settings = {};

        if (newVal == oldVal || newVal == false)
            return;
        // TODO: completare in base a cosa vogliamo mostrare

        // get personal data

        // post change of password

        // get the accepted requests
        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $http.get("/individual/" + $scope.sharedDataService.username + "/notifications")
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

app.controller('graphController', function ($scope, $interval, $http, SharedDataService) {

    $scope.sharedDataService = SharedDataService;
    $scope.$watch('sharedDataService.home', function (newVal, oldVal) {
        if (newVal == oldVal || newVal == false)
            return;
        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $scope.width = 600;
        $scope.height = 350;
        $scope.yAxis = ['Heart Rate', 'Systolic Blood Pressure', 'Diastolic Blood Pressure', 'Oxygen Percentage'];
        $scope.xAxis = 'Time';
        $scope.data = [];
        $scope.res = [];
        $scope.max = [90, 150, 200, 100];
        $scope.cont = 0;

        $interval(function () {
            if ($scope.data.length > 12) {
                $scope.data.shift();
                $scope.res.shift();

            }

            $scope.data.push([
                {
                    value: Math.floor((Math.random() * 40) + 50)
                },
                {
                    value: Math.floor((Math.random() * 100) + 50)
                },
                {
                    value: Math.floor((Math.random() * 100) + 100)
                },
                {
                    value: Math.floor((Math.random() * 50) + 50)
                }
            ]);
            $scope.res.push(
                {
                    timestamp: new Date(),
                    heartRate: $scope.data[$scope.data.length - 1][0].value,
                    systolicBloodPressure: $scope.data[$scope.data.length - 1][1].value,
                    diastolicBloodPressure: $scope.data[$scope.data.length - 1][2].value,
                    oxygenPercentage: $scope.data[$scope.data.length - 1][3].value
                }
            );
            $scope.cont = $scope.cont + 1;
            $scope.cont = $scope.cont % 12;
            if ($scope.cont == 0) {
                console.log($scope.res);
                $http.post("/individual/" + $scope.sharedDataService.username + "/data", $scope.res, config).then(function onSuccess(response) {
                    console.log(response);
                }).catch(function onError(response) {
                    console.log(response);
                });
            }


        }, 1000, 1000);
    });
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

