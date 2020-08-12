var app = angular.module("CBTicket", []);

app.controller("TestController", function($scope, $http) {

    clearForm();
    $scope.home = true;

    $scope.form2_ticket = {
            cbNumber: "",
            cbDate: "",
            cbUrl: "",
            ucidOld: "",
            cbType: "",
            cbSource: "",
            cbOriginator: "",
            cbMaxAttempts: "",
            cbAttemptsTimeout: ""
        };

    $scope.form8_getTicketBtn = false;

    $scope.form8_submitBtn = true;

    $scope.form2_addTicket = function() {

        var method = "POST";
        var url = '/api/ticket/add';

        $http({
            method: method,
            url: url,
            data: angular.toJson($scope.form2_ticket),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(  function(res) { // success
                      var jsonResp = angular.toJson(res.data);
                      $scope.form2_addTicketResult = jsonResp
                      console.log("Response: " + jsonResp);
                  },
                  function(res) { // error
                      console.log("Error: " + res.status + " : " + angular.toJson(res.data));
                      $scope.form2_addTicketResult = "Request failed. " + "Error: " + res.status + " : " + angular.toJson(res.data);
                  });
    };

    $scope.form3_getTicketById = function() {

        var method = "GET";
        var url = "/api/ticket/" + $scope.form3_ticketId;

        $http({
            method: method,
            url: url,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(
            function(res) { // success
                //forcompcodem8_ticket = res.data;
                /*console.log("Response: " + res.data);
                console.log("cbNumber: " + res.data.cbNumber);
                console.log("ticketParams: " + res.data.ticketParams);
                console.log("attempts: " + res.data.attempts);
                console.log("attempts.length: " + res.data.attempts.length);
                console.log("lastCompletionCode: " + res.data.lastCompletionCode);*/
                var jsonResp = angular.toJson(res.data);
                $scope.form3_getTicketByIdResult = jsonResp
                //console.log("Response: " + jsonResp);
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
                $scope.form3_getTicketByIdResult = "Request failed. " + "Error: " + res.status + " : " + angular.toJson(res.data);
            });

    };

    $scope.form4_getTicketsByNumber = function() {

            var method = "GET";
            var url = "/api/ticket/find?number=" + $scope.form4_cbNumber;

            $http({
                method: method,
                url: url,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(
                function(res) { // success
                    var jsonResp = angular.toJson(res.data);
                    $scope.form4_getTicketsByNumberResult = jsonResp
                    console.log("Response: " + jsonResp);
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                    $scope.form4_getTicketsByNumberResult = "Request failed. " + "Error: " + res.status + " : " + angular.toJson(res.data);
                });

    };

    $scope.form5_cancelTicket = function() {

        var method = "POST";
        var url = "/api/ticket/cancel/" + $scope.form5_Id;

        $http({
            method: method,
            url: url,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(
            function(res) { // success
                 var jsonResp = angular.toJson(res.data);
                 $scope.form5_cancelTicketResult = jsonResp
                 //console.log("Response: " + jsonResp);
            },
            function(res) { // error
                 //console.log("Error: " + res.status + " : " + res.data);
                 $scope.form5_cancelTicketResult = "Request failed. " + "Error: " + res.status + " : " + angular.toJson(res.data);
            });

    };

    $scope.form6_deleteTicket = function() {

        var method = "DELETE";
        var url = "/api/ticket/delete/" + $scope.form6_Id;

        $http({
              method: method,
              url: url,
              headers: {
                'Content-Type': 'application/json'
              }
        }).then(
              function(res) { // success
                 var jsonResp = angular.toJson(res.data);
                 $scope.form6_deleteTicketResult = jsonResp
                 //console.log("Response: " + jsonResp);
              },
              function(res) { // error
                  //console.log("Error: " + res.status + " : " + res.data);
                  $scope.form6_deleteTicketResult = "Request failed. " + "Error: " + res.status + " : " + angular.toJson(res.data);
              });

    };

    $scope.form7_getTicketsForJob = function() {

        var method = "GET";
        var url = "/api/ticket/job?count=" + $scope.form7_jobCount;

        $http({
            method: method,
            url: url,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(
            function(res) { // success
                var jsonResp = angular.toJson(res.data);
                $scope.form7_getTicketsForJobResult = jsonResp
                //console.log("Response: " + jsonResp);
            },
            function(res) { // error
                //console.log("Error: " + res.status + " : " + res.data);
                $scope.form7_getTicketsForJobResult = "Request failed. " + "Error: " + res.status + " : " + angular.toJson(res.data);
            });

    };

    var form8_ticket;

    $scope.form8_getOneTicketInDialingState = function() {

            var method = "GET";
            var url = "/api/ticket/dialing";

            $http({
                method: method,
                url: url,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(
                function(res) { // success
                    form8_ticket= res.data;
                    //console.log("form8_ticket:" + form8_ticket);
                    var jsonResp = angular.toJson(res.data);
                    $scope.form8_oneTicketInDialingStateResult = jsonResp

                    $scope.form8_getTicketBtn = true;
                    $scope.form8_submitBtn = false;

                },
                function(res) { // error
                    $scope.form8_oneTicketInDialingStateResult = "Request failed. " + "Error: " + res.status + " : " + angular.toJson(res.data);
                });

        };

    var form8_compcode;

    $scope.form8_updateSelectedCompCode = function() {

            console.log("Select: " + $scope.form8_select);

            var method = "GET";
            var url = "/api/compcode/fetch?sysname=" + $scope.form8_select;

            $http({
                method: method,
                url: url,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(
                function(res) { // success
                    form8_compcode = res.data;
                    var jsonResp = angular.toJson(res.data);
                    console.log("Response: " + jsonResp);
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                });

        }

    $scope.form8_updateTicket = function()  {

                form8_ticket.lastCompletionCode = form8_compcode;

                var lastAttemptIndex = form8_ticket.attempts.length - 1;

                form8_ticket.attempts[lastAttemptIndex].ucid = $scope.form8_ucid;
                //console.log(" form8_ticket.attempts.ucid: " +  form8_ticket.attempts[lastAttemptIndex].ucid);

                form8_ticket.attempts[lastAttemptIndex].callId = $scope.form8_callId;
                //console.log(" form8_ticket.attempts.callId: " +  form8_ticket.attempts[lastAttemptIndex].callId);

                form8_ticket.attempts[lastAttemptIndex].phantomNumber = $scope.form8_phNumber;
                //console.log(" form8_ticket.attempts.phantomNumber: " +  form8_ticket.attempts[lastAttemptIndex].phantomNumber);

                form8_ticket.attempts[lastAttemptIndex].operatorNumber = $scope.form8_opNumber;
                //console.log(" form8_ticket.attempts.operatorNumber: " +  form8_ticket.attempts[lastAttemptIndex].operatorNumber);

                form8_ticket.attempts[lastAttemptIndex].completionCode = form8_compcode;
                //console.log(" form8_ticket.attempts.completionCode: " +  form8_ticket.attempts[lastAttemptIndex].completionCode);

                var method = "POST";
                var url = "/api/ticket/update";

                $http({
                        method: method,
                        url: url,
                        data: angular.toJson(form8_ticket),
                        headers: {
                            'Content-Type': 'application/json'
                        }
                }).then(
                        function(res) { // success
                            var jsonResp = angular.toJson(res.data);
                            $scope.form8_updateTicketResult = jsonResp

                            var json = angular.toJson(form8_ticket);
                            $scope.form8_oneTicketInDialingStateResult = json

                        },
                        function(res) { // error
                            $scope.form8_updateTicketResult = "Request failed. " + "Error: " + res.status + " : " + angular.toJson(res.data);
                });

    };

    $scope.clearForm2 = function() {

        $scope.form2_ticket.cbNumber = "";
        $scope.form2_ticket.cbDate = "";
        $scope.form2_ticket.cbOriginator = "";
        $scope.form2_ticket.cbUrl = "";
        $scope.form2_ticket.ucidOld = "";
        $scope.form2_ticket.cbType = "";
        $scope.form2_ticket.cbSource = "";

        $scope.form2_addTicketResult = "";
     };

     $scope.clearForm3 = function() {

        $scope.form3_ticketId = "";
        $scope.form3_getTicketByIdResult = "";
     };

     $scope.clearForm4 = function() {

        $scope.form4_cbNumber = "";
        $scope.form4_getTicketsByNumberResult = "";
     };

    $scope.clearForm5 = function() {

        $scope.form5_Id = "";
        $scope.form5_cancelTicketResult = "";
    };

    $scope.clearForm6 = function() {

        $scope.form6_Id = "";
        $scope.form6_deleteTicketResult = "";
    };

    $scope.clearForm7 = function() {

        $scope.form7_jobCount = "";
        $scope.form7_getTicketsForJobResult = "";
     };

    $scope.clearForm8 = function() {

        $scope.form8_ucid = "";
        $scope.form8_callId = "";
        $scope.form8_phNumber = "";
        $scope.form8_opNumber = "";
        $scope.form8_stopDate = "";

        $scope.form8_oneTicketInDialingStateResult = "";
        $scope.form8_updateTicketResult = "";

        $scope.form8_getTicketBtn = false;
        $scope.form8_submitBtn = true;

    };


    $scope.switch = function(number) {
                
        clearForm();

        switch (number) { 
            case 1:  $scope.home = true; break;
            case 2:  $scope.form2 = true; break;
            case 3:  $scope.form3 = true; break;
            case 4:  $scope.form4 = true; break;
            case 5:  $scope.form5 = true; break;
            case 6:  $scope.form6 = true; break;
            case 7:  $scope.form7 = true; break;
            case 8:  $scope.form8 = true; break;
        }
    }     

    function clearForm() {
        $scope.home = false;
        $scope.form2 = false;
        $scope.form3 = false;
        $scope.form4 = false;
        $scope.form5 = false;
        $scope.form6 = false;
        $scope.form7 = false;
        $scope.form8 = false;
    }
});