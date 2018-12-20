angular.module("individualSignUp", [])
    .controller("individualSignUpController", function($scope, $http) {
            $scope.individual = {};

            $scope.submit = function () {

                let config = {
                    headers: {
                        'Content-Type': 'application/json;charset=utf-8'
                    }
                };

                console.log($scope.individual);

                //$http.post('http://192.168.100.2:8080/auth/individual/signUp', $scope.individual, config);
                $http.post('/auth/individual/signUp', $scope.individual, config);
            }
        });