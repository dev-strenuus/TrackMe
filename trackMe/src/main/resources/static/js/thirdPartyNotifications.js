// third party notifications

angular.module("thirdPartyNotifications", [])
    .controller("thirdPartyNotificationController", function($scope) {
        $scope.notifications = [
            { id: "Andrea",   individual: "Rossi",  status: "Roma"    },
            { id: "Marco",    individual: "Verdi",  status: "Milano"  },
            { id: "Giovanni", individual: "Neri",   status: "Napoli"  },
            { id: "Roberto",  individual: "Gialli", status: "Palermo" }
        ]
    });
