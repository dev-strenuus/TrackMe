let config = {
    headers: {
        'Content-Type': 'application/json;charset=utf-8;'
    }
};

let app = angular.module("individual", ["ngRoute"]);

app.config(function ($routeProvider) {
    $routeProvider
        .when("/", {
            templateUrl: "individualLogin.html",
            controller: "individualLoginController"
        })
        .when("/home", {
            templateUrl: "individualHome.html",
            controller: "individualController"
        })
        .when("/login", {
            templateUrl: "individualLogin.html",
            controller: "individualLoginController"
        }).when("/signUp", {
        templateUrl: "individualSignUp.html",
        controller: "individualSignUpController"
    }).when("/notifications", {
        templateUrl: "individualNotifications.html",
        controller: "individualNotificationsController"
    }).when("/settings", {
        templateUrl: "individualSettings.html",
        controller: "individualSettingsController"
    }).when("/dataManager", {
        templateUrl: "individualDataManager.html",
        controller: "graphController as graph"
    });
});

app.service('SharedDataService', function () {
    let sharedData = {
        loggedIn: false,
        deviceConnected: false,
        intervalPromise: null,
        data: [],
        username: '',
        token: ''
    };
    return sharedData;
});

app.controller("mainController", function ($scope, $http, $interval, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $http.defaults.headers.common.Authorization = $scope.sharedDataService.token;
    $scope.notification = 0;

    $interval(function(){
        console.log($scope.sharedDataService.loggedIn);
        if($scope.sharedDataService.loggedIn == true){
            $http.get("/individual/" + $scope.sharedDataService.username + "/countNotifications")
            .then(function (response) {
                console.log(response);
                $scope.notification = response.data;

            }).catch(function onError(response) {
                console.log(response);

        });}}, 5000, 5000);

});

app.controller("individualSignUpController", function ($scope, $http, $location, SharedDataService) {
    $scope.individual = {};
    $scope.sharedDataService = SharedDataService;

    $scope.submitSignUp = function () {
        console.log($scope.individual);
        $http.post('/auth/individual/signUp', $scope.individual, config).then(function onSuccess(response) {
            console.log(response);
            $location.path("/login");
        }).catch(function onError(response) {
            console.log(response);
        });
    };
});

app.controller("individualLoginController", function ($scope, $http, $location, SharedDataService) {
    console.log("login");
    $scope.credentials = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitLogin = function () {
        console.log($scope.credentials);
        $http.post('/auth', $scope.credentials, config).then(function onSuccess(response) {
            $scope.sharedDataService.username = $scope.credentials.username;
            $scope.sharedDataService.token = 'Bearer ' + response.data.token;
            $scope.sharedDataService.loggedIn = true;
            console.log($scope.sharedDataService.token);
            $location.path("/home");
        }).catch(function onError(response) {
            console.log(response);
            $scope.loginResult = "Wrong Password or Fiscal code";
        });
    };
});

app.controller("individualController", function ($scope, $http, SharedDataService) {
    console.log("home");
    $scope.sharedDataService = SharedDataService;



});

app.controller("individualNotificationsController", function ($scope, $http, $interval, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.notifications = [];
    $scope.pendingRequests = [];
    $scope.activeRequests = [];
    $scope.answer = {};

    $http.defaults.headers.common.Authorization = $scope.sharedDataService.token;
    $http.get("/individual/" + $scope.sharedDataService.username + "/individualRequests")
        .then(function (response) {
            $scope.pendingRequests = response.data;
        }).catch(function onError(response) {
        console.log(response);
    });

    $http.get("/individual/" + $scope.sharedDataService.username + "/acceptedRequests")
        .then(function (response) {
            $scope.activeRequests = response.data;
        }).catch(function onError(response) {
        console.log(response);
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
            "individual": {
                "fiscalCode": $scope.sharedDataService.username
            },
            "thirdParty": {
                "vat": $scope.answer.vatNumber
            },
            "accepted": $scope.answer.status,
        };


        $http.defaults.headers.common.Authorization = SharedDataService.token;
        $http.post("/individual/individualRequest/answer", content, config).then(function onSuccess(response) {

            if(content.accepted == true) {
                for (i = 0; i < $scope.pendingRequests.length; i++)
                    if ($scope.pendingRequests[i].thirdParty.vat == content.thirdParty.vat) {
                        $scope.activeRequests.push($scope.pendingRequests[i]);
                        $scope.pendingRequests.splice(i, 1);
                        break;
                    }
            }else{
                for (i = 0; i < $scope.pendingRequests.length; i++)
                    if ($scope.pendingRequests[i].thirdParty.vat == content.thirdParty.vat) {
                        $scope.pendingRequests.splice(i, 1);
                        break;
                    }
                for (i = 0; i < $scope.activeRequests.length; i++)
                    if ($scope.activeRequests[i].thirdParty.vat == content.thirdParty.vat) {
                        $scope.activeRequests.splice(i, 1);
                        break;
                    }

            }

        }).catch(function onError(response) {
            console.log(response);
        });
    };

    let notificationsPromise = $interval(function(){$http.get("/individual/" + $scope.sharedDataService.username + "/notifications")
        .then(function (response) {
            console.log(response);
            for(i=0; i<response.data.length; i++)
                $scope.pendingRequests.push(response.data[i]);

        }).catch(function onError(response) {
            console.log(response);
        });}, 5000, 5000);

    $scope.$on('$destroy',function(){
        if(notificationsPromise)
            $interval.cancel(notificationsPromise);
    });
});

app.controller("individualSettingsController", function ($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.settings = {};

    $scope.fiscalCode = $scope.sharedDataService.username;

    $scope.updatePassword = function () {
        //PUT
    };

    $scope.updateCoordinates = function () {
        //PUT
    };

});

app.controller('graphController', function ($scope, $interval, SharedDataService) {

    $scope.sharedDataService = SharedDataService;
    ;
    $scope.width = 600;
    $scope.height = 350;
    $scope.yAxis = ['Heart Rate', 'Systolic Blood Pressure', 'Diastolic Blood Pressure', 'Oxygen Percentage'];
    $scope.xAxis = 'Time';
    $scope.data = $scope.sharedDataService.data;
    $scope.max = [90, 150, 200, 100];


});


app.controller("individualLogoutController", function ($scope, $http, $location, SharedDataService) {
    $scope.sharedDataService = SharedDataService;

    $scope.logout = function () {
        $scope.sharedDataService.loggedIn = false;
        $location.path("/login");
    };
});

app.controller("individualDataRetriever", function ($scope, $http, $interval, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $http.defaults.headers.common.Authorization = SharedDataService.token;
    $scope.cont = 0;
    $scope.res = [];

    $scope.connect = function () {
        console.log("device connected");
        $scope.sharedDataService.deviceConnected = true;
        $scope.sharedDataService.intervalPromise = $interval(function () {
            if ($scope.sharedDataService.data.length > 12) {
                $scope.sharedDataService.data.shift();
                $scope.res.shift();

            }

            $scope.sharedDataService.data.push([
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
                    heartRate: $scope.sharedDataService.data[$scope.sharedDataService.data.length - 1][0].value,
                    systolicBloodPressure: $scope.sharedDataService.data[$scope.sharedDataService.data.length - 1][1].value,
                    diastolicBloodPressure: $scope.sharedDataService.data[$scope.sharedDataService.data.length - 1][2].value,
                    oxygenPercentage: $scope.sharedDataService.data[$scope.sharedDataService.data.length - 1][3].value
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
    };

    $scope.disconnect = function () {
        console.log("device disconnected");
        $scope.sharedDataService.deviceConnected = false;
        $interval.cancel($scope.sharedDataService.intervalPromise);
        $scope.sharedDataService.data = [];
    };
});

