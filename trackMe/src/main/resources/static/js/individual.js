var config = {
    headers : {
        'Content-Type': 'application/json;charset=utf-8;'
    }
}
var app = angular.module("individual", []);

app.service('SharedDataService', function () {
    var sharedData = {
        signUp: true,
        login: false,
        home: false,
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
            var data = response.data;
            var status = response.status;
            var statusText = response.statusText;
            var headers = response.headers;
            var config = response.config;
            console.log(response);
            $scope.sharedDataService.login =false;
            $scope.sharedDataService.home = true;
            $scope.sharedDataService.token = 'Bearer ' + response.data.token;
            console.log($scope.sharedDataService.token);
        }).
        catch(function onError(response) {
            // Handle error
            var data = response.data;
            var status = response.status;
            var statusText = response.statusText;
            var headers = response.headers;
            var config = response.config;
            console.log(response);
        });
    }
});

app.controller("individualController", function($scope, $http, SharedDataService) {
    $scope.sharedDataService = SharedDataService;
    $scope.people = [
        { fiscalCode: "Marco"}
    ];

    $scope.$watch('sharedDataService.home',function(newVal,oldVal){
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
            var data = response.data;
            var status = response.status;
            var statusText = response.statusText;
            var headers = response.headers;
            var config = response.config;
            console.log(response);
        });
    });

    });
