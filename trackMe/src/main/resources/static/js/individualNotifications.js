// individual notifications

angular.module("individualNotifications", [])
    .controller("individualNotificationController", function($scope) {
            $scope.notifications = [
                { id: "Andrea",   thirdParty: "Rossi",  status: "Roma"    },
                { id: "Marco",    thirdParty: "Verdi",  status: "Milano"  },
                { id: "Giovanni", thirdParty: "Neri",   status: "Napoli"  },
                { id: "Roberto",  thirdParty: "Gialli", status: "Palermo" }
            ]
        });


/*
<div  ng-controller="notificationController">
    <table>
    <tr><th>Id</th><th>Company</th><th>Status</th></tr>
<tr ng-repeat="notification in notifications">
    <td>{{notification.id}}</td><td>{{notification.thirdParty}}</td><td>{{notification.status}}</td>
</tr>
</table>
</div>
*/