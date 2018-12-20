angular.module("thirdPartySignUp", [])
    .controller("thirdPartySignUpController", function($scope, $http) {
        $scope.thirdParty = {};

        $scope.submit = function () {

            let config = {
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                }
            };

            console.log($scope.thirdParty);

            //$http.post('http://192.168.100.2:8080/auth/thirdParty/signUp', $scope.thirdParty, config);
            $http.post('/auth/thirdParty/signUp', $scope.individual, config);
        }
    });