var app = angular.module("individualSignUp", []);
app.controller("notificationController", function($scope) {
            $scope.notifications = [
                { id: "Andrea",   thirdParty: "Rossi",  status: "Roma"    },
                { id: "Marco",    thirdParty: "Verdi",  status: "Milano"  },
                { id: "Giovanni", thirdParty: "Neri",   status: "Napoli"  },
                { id: "Roberto",  thirdParty: "Gialli", status: "Palermo" }
            ]
        });
