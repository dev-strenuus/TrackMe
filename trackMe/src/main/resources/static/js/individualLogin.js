angular.module("individualLogin", [])
    .controller("individualLoginController", function($scope, $http) {
        $scope.credentials = {};

        $scope.submit = function () {

            let config = {
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                }
            };

            console.log($scope.credentials);

            //$http.post('http://192.168.100.2:8080/auth/individual/Login', $scope.credentials, config);
            $http.post('/auth/individual/Login', $scope.credentials, config);
        }
    });