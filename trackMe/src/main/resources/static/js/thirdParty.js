let config = {
    headers : {
        'Content-Type': 'application/json;charset=utf-8;'
    }
};

let app = angular.module("thirdParty", []);

app.service('SharedDataService', function () {
    let sharedData = {
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
            let data = response.data;
            let status = response.status;
            let statusText = response.statusText;
            let headers = response.headers;
            let config = response.config;
            console.log(response);
            $scope.sharedDataService.login = false;
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

});


