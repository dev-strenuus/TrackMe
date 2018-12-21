var config = {
    headers : {
        'Content-Type': 'application/json;charset=utf-8;'
    }
}
var app = angular.module("thirdParty", []);

app.service('SharedDataService', function () {
    var sharedData = {
        signUp: true,
        login: false,
        home: false,
        token: ''
    };
    return sharedData;
});

app.controller("thirdPartySignUpController", function($scope, $http, SharedDataService) {
    $scope.thirdParty = {};
    $scope.sharedDataService = SharedDataService;
    $scope.submitSignUp = function () {

        console.log($scope.thirdParty);

        //$http.post('http://192.168.100.2:8080/auth/thirdParty/signUp', $scope.thirdParty, config);
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




