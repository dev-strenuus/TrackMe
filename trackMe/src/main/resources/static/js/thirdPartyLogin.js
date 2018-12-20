angular.module("thirdPartyLogin", [])
    .controller("thirdPartyLoginController", function($scope, $http) {
        $scope.credentials = {};

        $scope.submit = function () {

            let config = {
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                }
            };

            console.log($scope.credentials);

            //$http.post('http://192.168.100.2:8080/auth/thirdParty/Login', $scope.credentials, config);
            $http.post('/auth/thirdParty/Login', $scope.credentials, config);
        }
    });