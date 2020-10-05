
app.controller("TestController", function($scope, $http) {

    $scope.compcodes = [];
    $scope.compcode = {
        id : 0,
        name : "",
        sysname : "",
        description : "",
        recall : false
    };

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
    $scope.cbDate = "";
    $scope.cbTime = "";

    clearForm();

    getCompCodes();

    $scope.form8_getTicketBtn = false;

    $scope.form8_submitBtn = true;

    $scope.form2_addTicket = function() {

        var currDate = new Date().toJSON().slice(0,10);
        var cbDateTime = "";

        if ($scope.cbDate == "")
            cbDateTime = currDate;
        else
            cbDateTime = $scope.cbDate;

        if ( $scope.cbTime == "")
             cbDateTime = cbDateTime + ' 00:00';
        else
             cbDateTime = cbDateTime + ' ' + $scope.cbTime;

        //console.log(angular.toJson($scope.form2_ticket));

        $http({
            method: "POST",
            url: '/api/ticket/add',
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

    //var form8_compcode;

    $scope.form8_updateSelectedCompCode = function() {

            console.log("Select: " + $scope.compcode.name);

             $scope.compcodes.forEach(function (cmpcode) {
                    //console.log("1" + angular.toJson(cmpcode));
                        if (cmpcode.name == $scope.compcode.name) {
                        $scope.cmpCodeActive = cmpcode;
                        //console.log("2" + angular.toJson(cmpcode));
                        }
             });

           /* var method = "GET";
            var url = "/api/compcode/fetch?sysname=" + $scope.compcode.sysname;

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
                });*/

        }



    $scope.form8_updateTicket = function()  {

                form8_ticket.lastCompletionCode = $scope.cmpCodeActive;

                var lastAttemptIndex = form8_ticket.attempts.length - 1;

                form8_ticket.attempts[lastAttemptIndex].ucid = $scope.form8_ucid;
                //console.log(" form8_ticket.attempts.ucid: " +  form8_ticket.attempts[lastAttemptIndex].ucid);

                form8_ticket.attempts[lastAttemptIndex].callId = $scope.form8_callId;
                //console.log(" form8_ticket.attempts.callId: " +  form8_ticket.attempts[lastAttemptIndex].callId);

                form8_ticket.attempts[lastAttemptIndex].phantomNumber = $scope.form8_phNumber;
                //console.log(" form8_ticket.attempts.phantomNumber: " +  form8_ticket.attempts[lastAttemptIndex].phantomNumber);

                form8_ticket.attempts[lastAttemptIndex].operatorNumber = $scope.form8_opNumber;
                //console.log(" form8_ticket.attempts.operatorNumber: " +  form8_ticket.attempts[lastAttemptIndex].operatorNumber);

                form8_ticket.attempts[lastAttemptIndex].completionCode = $scope.cmpCodeActive;
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

    function getCompCodes() {

        $http({
            method: 'GET',
            url: '/api/compcode/all'
            }).then(function(res) { // success
                $scope.compcodes = res.data;
            },  function(res) { // error
                 alert("Error: " + res.status + ". Message : " + res.data.message);
            });
            /*$scope.showEditProperty = false;*/
            }

    $scope.clearForm2 = function() {

        $scope.cbDate = "";
        $scope.cbTime = "";

        $scope.form2_ticket.cbNumber = "";
        $scope.form2_ticket.cbDate = "";
        $scope.form2_ticket.cbOriginator = "";
        $scope.form2_ticket.cbUrl = "";
        $scope.form2_ticket.ucidOld = "";
        $scope.form2_ticket.cbType = "";
        $scope.form2_ticket.cbSource = "";
        $scope.form2_ticket.cbMaxAttempts = "";
        $scope.form2_ticket.cbAttemptsTimeout = "";

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

        initCompCodeSelector();

    };

    function initCompCodeSelector() {

     var elem = document.querySelector('#comcodeSelector');
            document.querySelectorAll('option')[0].selected = true;
            M.FormSelect.init(elem);
    }

    $scope.switch = function(number) {
                
        clearForm();
        //initCompCodeSelector();

        switch (number) { 
            case 'add'   :  $scope.add = true; break;
            case 'get'   :  $scope.get = true; break;
            case 'find'  :  $scope.find = true; break;
            case 'cancel':  $scope.cancel = true; break;
            case 'delete':  $scope.delete = true; break;
            case 'job'   :  $scope.job = true; break;
            case 'update':  $scope.update = true; break;
        }
    }     

    function clearForm() {
        $scope.add = false;
        $scope.get = false;
        $scope.find = false;
        $scope.cancel = false;
        $scope.delete = false;
        $scope.job = false;
        $scope.update = false;
    }
});