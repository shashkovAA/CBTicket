var app = angular.module("CBTicket", []);

app.controller("TestController", function($scope, $http) {


    $scope.ticket = {
            cbNumber: "",
            cbDate: "",
            cbUrl: "",
            ucidOld: "",
            cbType: "",
            cbSource: ""
        };

    $scope.addTicket = function() {

        var method = "POST";
        var url = '/api/ticket/add';

        $http({
            method: method,
            url: url,
            data: angular.toJson($scope.ticket),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(  function(res) { // success
                      var jsonResp = angular.toJson(res.data);
                      $scope.addTicketResult = jsonResp
                      console.log("Response: " + jsonResp);
                  },
                  function(res) { // error
                      console.log("Error: " + res.status + " : " + res.data);
                      $scope.addTicketResult = "Request failed. " + "Error: " + res.status;
                  });
    };

    $scope.testGetOneTicket = function() {

        var method = "GET";
        var url = "/api/ticket/" + $scope.ticketId;

        $http({
            method: method,
            url: url,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(
            function(res) { // success
                var jsonResp = angular.toJson(res.data);
                $scope.getOneResult = jsonResp
                console.log("Response: " + jsonResp);
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
                $scope.getOneResult = "Request failed. " + "Error: " + res.status;
            });

    };

    $scope.getJobTickets = function() {

        var method = "GET";
        var url = "/api/ticket/job?count=" + $scope.jobCount;

        $http({
            method: method,
            url: url,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(
            function(res) { // success
                var jsonResp = angular.toJson(res.data);
                $scope.getJobResult = jsonResp
                console.log("Response: " + jsonResp);
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
                $scope.getJobResult = "Request failed. " + "Error: " + res.status;
            });

    };

    $scope.testJobTickets = function() {

            var method = "GET";
            var url = "/test/ticket/job?count=" + $scope.jobCount2;

            $http({
                method: method,
                url: url,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(
                function(res) { // success
                    var jsonResp = angular.toJson(res.data);
                    $scope.testJobResult = jsonResp
                    console.log("Response: " + jsonResp);
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                     $scope.testJobResult = "Request failed. " + "Error: " + res.status;
                });

        };

    // Clear the form
    $scope.clearFormData = function() {

        console.log("Run clearFormData ()");
        $scope.cbNumber = "";
        $scope.cbDate = "";
        $scope.cbUrl = "";
        $scope.ucidOld = "";
        $scope.cbType = "";
        $scope.cbSource = "";

        $scope.ticketId = "";
        $scope.jobCount = "";
        $scope.jobCount2 = "";

        $scope.addTicketResult = "";
        $scope.getOneResult = "";
        $scope.getJobResult = "";
        $scope.testJobResult = "";

    };

});