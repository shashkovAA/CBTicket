
var app = angular.module('DataBaseModule', ['ui.grid','ui.grid.pagination']);

app.controller("DataController", ['$scope', '$http', 'apilogService', 'attemptService', 'ticketService', 'ticketParamsService', function($scope, $http, apilogService, attemptService, ticketService, ticketParamsService) {

    <!-------------------------------------------------------ApiLog table ------------------------------------------------------------->

    $scope.apiLogFilter = {
                level: "",
                username: "",
                method:"",
                apiUrl:"",
                host:"",
                startDate:"",
                startTime:"00:00",
                endDate:"",
                endTime:"00:00",
                sessionId:"",
                page : 0,
                size : 15
        };

    var apilog_paginationOptions = {
                    pageNumber: 1,
                    pageSize: 15,
                    sort: null
                };
                
    $scope.apilog_gridOptions = {
                paginationPageSizes: [15, 30, 100],
                paginationPageSize: apilog_paginationOptions.pageSize,
                enableColumnMenus:false,
                /*enableCellEditOnFocus: true,*/
                useExternalPagination: true,
                enableSorting: true,
                /*enableFiltering: true,*/
                enableGridMenu: true,
                columnDefs: [
                   { name: 'date', displayName: 'Date', type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\'', width:130 },
                   { name: 'username', width:100 },
                   { name: 'level', width:80 },
                   { name: 'method', width:80 },
                   { name: 'apiUrl', width:150 },
                   { name: 'requestBody' },
                   { name: 'responseBody' },
                   { name: 'sessionId' },
                   { name: 'host', width:80}
                ],
                onRegisterApi: function(gridApi) {
                   $scope.gridApi = gridApi;
                   gridApi.pagination.on.paginationChanged(
                     $scope,
                     function (newPage, pageSize) {
                       apilog_paginationOptions.pageNumber = newPage;
                       apilog_paginationOptions.pageSize = pageSize;
                       apilogService.getApiLogs(newPage,pageSize,$scope.apiLogFilter)
                         .then(function(result){
                           $scope.apilog_gridOptions.data = result.data.content;
                           $scope.apilog_gridOptions.totalItems = result.data.totalElements;
                         });
                    });
                }
            };

    $scope.apilog_search = function(){

        console.log("Run apilog_search()");

        apilogService.getApiLogs(
                  apilog_paginationOptions.pageNumber,
                  apilog_paginationOptions.pageSize, $scope.apiLogFilter).then(function(result){
                  $scope.apilog_gridOptions.data = result.data.content;
                  $scope.apilog_gridOptions.totalItems = result.data.totalElements;
                  });
    }

    $scope.apilog_clear = function() {
        $scope.apiLogFilter.username = "";
        $scope.apiLogFilter.startDate = "";
        $scope.apiLogFilter.startTime = "00:00";
        $scope.apiLogFilter.endDate = "";
        $scope.apiLogFilter.endTime = "00:00";
        $scope.apiLogFilter.level = "";
        $scope.apiLogFilter.method = "";
        $scope.apiLogFilter.apiUrl = "";
        $scope.apiLogFilter.host = "";
        $scope.apiLogFilter.sessionId = "";
    }

    $scope.apilog_export = function() {

                $http({
                    method: "POST",
                    url: "/api/database/apilogs/export",
                    data: angular.toJson( $scope.apiLogFilter),
                }).then(
                    function(res, status, headers, config) {
                         var anchor = angular.element('<a/>');
                         anchor.attr({
                             href: 'data:attachment/csv;charset=utf-8,' + encodeURI(res.data),
                             target: '_blank',
                             download: 'apilog.csv'
                         })[0].click()},
                    function(res) { // error
                        console.log("Error: " + res.status + " : " + res.data);
                    });

    }

    <!-------------------------------------------------------Attempt table ------------------------------------------------------------->

    $scope.attemptFilter = {
                ticketId: "",
                startDate:"",
                startTime:"00:00",
                endDate:"",
                endTime:"00:00",
                callId: "",
                operatorNumber:"",
                phantomNumber:"",
                compCode:"",
                ucid:"",
                page : 0,
                size : 15
        };

    var attempt_paginationOptions = {
                    pageNumber: 1,
                    pageSize: 15,
                    sort: null
    };

    $scope.attempt_gridOptions = {
                paginationPageSizes: [15, 30, 100],
                paginationPageSize: attempt_paginationOptions.pageSize,
                enableColumnMenus:false,
                /*enableCellEditOnFocus: true,*/
                useExternalPagination: true,
                enableSorting: true,
                /*enableFiltering: true,*/
                enableGridMenu: true,
                columnDefs: [
                   { name: 'id', width:50 },
                   { name: 'ticketId', width:100 },
                   { name: 'attemptStart', displayName: 'Start date', type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\'', width:130 },
                   { name: 'attemptStop', displayName: 'End date', type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\'', width:130 },
                   { name: 'callId', width:100 },
                   { name: 'operatorNumber' },
                   { name: 'phantomNumber' },
                   { name: 'completionCodeId'},
                   { name: 'ucid' }
                ],
                onRegisterApi: function(gridApi) {
                   $scope.gridApi = gridApi;
                   gridApi.pagination.on.paginationChanged(
                     $scope,
                     function (newPage, pageSize) {
                       attempt_paginationOptions.pageNumber = newPage;
                       attempt_paginationOptions.pageSize = pageSize;
                       attemptService.getAttempts(newPage,pageSize,$scope.attemptFilter)
                         .then(function(result){
                           $scope.attempt_gridOptions.data = result.data.content;
                           $scope.attempt_gridOptions.totalItems = result.data.totalElements;
                         });
                    });
                }
    };

    $scope.attempt_search = function(){

        console.log("Run attempt_search()");

        attemptService.getAttempts(
                  attempt_paginationOptions.pageNumber,
                  attempt_paginationOptions.pageSize, $scope.attemptFilter).then(function(result){
                  $scope.attempt_gridOptions.data = result.data.content;
                  $scope.attempt_gridOptions.totalItems = result.data.totalElements;
                  });
    }

    $scope.attempt_clear = function() {

        $scope.attemptFilter.ticketId = "";
        $scope.attemptFilter.startDate = "";
        $scope.attemptFilter.startTime = "00:00";
        $scope.attemptFilter.endDate = "";
        $scope.attemptFilter.endTime = "00:00";
        $scope.attemptFilter.callId = "";
        $scope.attemptFilter.operatorNumber = "";
        $scope.attemptFilter.phantomNumber = "";
        $scope.attemptFilter.compCode = "";
        $scope.attemptFilter.ucid = "";
    }

    $scope.attempt_export = function() {

                $http({
                    method: "POST",
                    url: "/api/database/attempts/export",
                    data: angular.toJson( $scope.attemptFilter),
                }).then(
                    function(res, status, headers, config) {
                         var anchor = angular.element('<a/>');
                         anchor.attr({
                             href: 'data:attachment/csv;charset=utf-8,' + encodeURI(res.data),
                             target: '_blank',
                             download: 'attempt.csv'
                         })[0].click()},
                    function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
                    });

    }

    <!-------------------------------------------------------Ticket table ------------------------------------------------------------->

    $scope.ticketFilter = {
                     id: "",
                     cbNumber:"",
                     startDate:"",
                     startTime:"00:00",
                     endDate:"",
                     endTime:"00:00",
                     attemptCount: "",
                     finished: null,
                     compCodeName:"",
                     page : 0,
                     size : 15
    };

    var ticket_paginationOptions = {
                        pageNumber: 1,
                        pageSize: 15,
                        sort: null
    };

    $scope.ticket_gridOptions = {
                    paginationPageSizes: [15, 30, 100],
                    paginationPageSize: ticket_paginationOptions.pageSize,
                    enableColumnMenus:false,
                    /*enableCellEditOnFocus: true,*/
                    useExternalPagination: true,
                    enableSorting: true,
                    /*enableFiltering: true,*/
                    enableGridMenu: true,
                    columnDefs: [
                       { name: 'id', width:50 },
                       { name: 'cbNumber',displayName: 'Callback number'},
                       { name: 'createDate', displayName: 'Create date', type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\'' },
                       { name: 'cbDate', displayName: 'Callback date', type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\'' },
                       { name: 'attemptCount' },
                       { name: 'lastCompletionCode.name', displayName: 'CompletionCode Name' },
                       { name: 'finished', width:100 }
                    ],
                    onRegisterApi: function(gridApi) {
                       $scope.gridApi = gridApi;
                       gridApi.pagination.on.paginationChanged(
                         $scope,
                         function (newPage, pageSize) {
                           ticket_paginationOptions.pageNumber = newPage;
                           ticket_paginationOptions.pageSize = pageSize;
                           ticketService.getTickets(newPage,pageSize,$scope.ticketFilter)
                             .then(function(result){
                               $scope.ticket_gridOptions.data = result.data.content;
                               $scope.ticket_gridOptions.totalItems = result.data.totalElements;
                             });
                        });
                    }
    };

    $scope.ticket_search = function(){

            console.log("Run ticket_search()");

            ticketService.getTickets(
                      ticket_paginationOptions.pageNumber,
                      ticket_paginationOptions.pageSize, $scope.ticketFilter).then(function(result){
                      $scope.ticket_gridOptions.data = result.data.content;
                      $scope.ticket_gridOptions.totalItems = result.data.totalElements;
                      });
    }

    $scope.ticket_clear = function() {

            $scope.ticketFilter.id = "";
            $scope.ticketFilter.startDate = "";
            $scope.ticketFilter.startTime = "00:00";
            $scope.ticketFilter.endDate = "";
            $scope.ticketFilter.endTime = "00:00";
            $scope.ticketFilter.cbNumber = "";
            $scope.ticketFilter.attemptCount = "";
            $scope.ticketFilter.finished = "";
            $scope.ticketFilter.compCodeName = "";

    }

    $scope.ticket_export = function() {

        $http({
             method: "POST",
             url: "/api/database/tickets/export",
             data: angular.toJson( $scope.ticketFilter),
        }).then(function(res, status, headers, config) {
                    var anchor = angular.element('<a/>');
                    anchor.attr({
                    href: 'data:attachment/csv;charset=utf-8,' + encodeURI(res.data),
                    target: '_blank',
                    download: 'tickets.csv'
                })[0].click()},
                function(res) { // error
                    alert("Error: " + res.status + ". Message : " + res.data.message);
                });

    }


    <!-------------------------------------------------------Ticket_Params table ------------------------------------------------------->

    $scope.ticketParamsFilter = {
                         ticketId:"",
                         cbMaxAttempts:"",
                         cbAttemptsTimeout:"",
                         cbSource:"",
                         cbType:"",
                         cbOriginator: "",
                         cbUrl: "",
                         ucidOld:"",
                         page : 0,
                         size : 15
    };

    var ticketParams_paginationOptions = {
                            pageNumber: 1,
                            pageSize: 15,
                            sort: null
    };

    $scope.ticketParams_gridOptions = {
                        paginationPageSizes: [15, 30, 100],
                        paginationPageSize: ticketParams_paginationOptions.pageSize,
                        enableColumnMenus:false,
                        useExternalPagination: true,
                        enableSorting: true,
                        /*enableFiltering: true,*/
                        enableGridMenu: true,
                        columnDefs: [
                           { name: 'id', width:50 },
                           { name: 'ticketId', width:100 },
                           { name: 'cbMaxAttempts', displayName : 'Max Attempts'},
                           { name: 'cbAttemptsTimeout', displayName : 'Attempts Timeout' },
                           { name: 'cbSource', displayName : 'Callback Source' },
                           { name: 'source.id', displayName : 'Source Id' },
                           { name: 'cbType', displayName : 'Callback Type' },
                           { name: 'cbOriginator', displayName : 'Callback Originator'},
                           { name: 'cbUrl', displayName : 'Callback URL'},
                           { name: 'ucidOld', displayName : 'Ucid Old'}
                        ],
                        onRegisterApi: function(gridApi) {
                           $scope.gridApi = gridApi;
                           gridApi.pagination.on.paginationChanged(
                             $scope,
                             function (newPage, pageSize) {
                               ticketParams_paginationOptions.pageNumber = newPage;
                               ticketParams_paginationOptions.pageSize = pageSize;
                               ticketParamsService.getTicketParams(newPage,pageSize,$scope.ticketParamsFilter)
                                 .then(function(result){
                                   $scope.ticketParams_gridOptions.data = result.data.content;
                                   $scope.ticketParams_gridOptions.totalItems = result.data.totalElements;
                                   console.log( $scope.ticketParams_gridOptions.data);
                                 });
                            });
                        }
    };

    $scope.ticketParams_search = function(){

                console.log("Run ticketParams_search()");

                ticketParamsService.getTicketParams(
                          ticketParams_paginationOptions.pageNumber,
                          ticketParams_paginationOptions.pageSize, $scope.ticketParamsFilter).then(function(result){
                          $scope.ticketParams_gridOptions.data = result.data.content;
                          $scope.ticketParams_gridOptions.totalItems = result.data.totalElements;
                          });
    }

    $scope.ticketParams_clear = function() {

                $scope.ticketParamsFilter.id = "";
                $scope.ticketParamsFilter.ticketId = "";
                $scope.ticketParamsFilter.cbMaxAttempts = "";
                $scope.ticketParamsFilter.cbAttemptsTimeout = "";
                $scope.ticketParamsFilter.cbSource = "";
                $scope.ticketParamsFilter.cbType = "";
                $scope.ticketParamsFilter.cbOriginator = "";
                $scope.ticketParamsFilter.cbUrl = "";
                $scope.ticketParamsFilter.ucidOld = "";

    }

    $scope.ticketParams_export = function() {

            $http({
                 method: "POST",
                 url: "/api/database/ticketparams/export",
                 data: angular.toJson( $scope.ticketParamsFilter),
            }).then(function(res, status, headers, config) {
                        var anchor = angular.element('<a/>');
                        anchor.attr({
                        href: 'data:attachment/csv;charset=utf-8,' + encodeURI(res.data),
                        target: '_blank',
                        download: 'ticketParams.csv'
                    })[0].click()},
                    function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
                    });

        }


    <!-------------------------------------------------------Completion_code table ----------------------------------------------------->

    $scope.compCode_gridOptions = {
            enableColumnMenus:false,
            enableSorting: true,
            enableGridMenu: true,
            columnDefs: [
            { name: 'id', width:50 },
            { name: 'name', width:250},
            { name: 'sysname', width:250},
            { name: 'description', width: 500},
            { name: 'recall' }
            ],
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
            }
    }

    function compCode_search() {

            console.log("Run compCode_search()");
            $http.get('/api/compcode/all')
                .then(function(response) {
                    $scope.compCode_gridOptions.data  = response.data;
                  });
    }

    $scope.compCode_export = function() {

            $http({
                method: "GET",
                url: "/api/compcode/export",
            }).then(function(res, status, headers, config) {
                         var anchor = angular.element('<a/>');
                         anchor.attr({
                         href: 'data:attachment/csv;charset=utf-8,' + encodeURI(res.data),
                         target: '_blank',
                         download: 'compcode.csv'
                    })[0].click()},
                            function(res) { // error
                                console.log("Error: " + res.status + " : " + res.data);
                            });
    }

    <!-------------------------------------------------------Prompt table ---------------------------------------------------------->

        $scope.prompt_gridOptions = {
            enableColumnMenus:false,
            enableSorting: true,
            enableGridMenu: true,
            columnDefs: [
            { name: 'id', width:50 },
            { name: 'name', width:200},
            { name: 'filepath'},
            { name: 'filename', width:200},
            { name: 'description'}
            ],
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
            }
        }

        function prompt_search() {

            console.log("Run prompt_search()");
            $http.get('/api/prompt/all')
                .then(function(response) {
                    $scope.prompt_gridOptions.data  = response.data;
                  });
        }

        $scope.prompt_export = function() {

            $http({
                method: "GET",
                url: "/api/prompt/export",
            }).then(function(res, status, headers, config) {
                         var anchor = angular.element('<a/>');
                         anchor.attr({
                         href: 'data:attachment/csv;charset=utf-8,' + encodeURI(res.data),
                         target: '_blank',
                         download: 'prompts.csv'
                    })[0].click()},
                            function(res) { // error
                                alert("Error: " + res.status + ". Message : " + res.data.message);
                            });

            }

    <!-------------------------------------------------------Property table ---------------------------------------------------------->

    $scope.property_gridOptions = {
        enableColumnMenus:false,
        enableSorting: true,
        enableGridMenu: true,
        columnDefs: [
        { name: 'id', width:50 },
        { name: 'name', width:250},
        { name: 'value', width:250},
        { name: 'description'}
        ],
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
        }
    }

    function property_search() {

        console.log("Run property_search()");
        $http.get('/api/properties')
            .then(function(response) {
                $scope.property_gridOptions.data  = response.data;
              });
    }

    $scope.property_export = function() {

        $http({
            method: "GET",
            url: "/api/properties/export",
        }).then(function(res, status, headers, config) {
                     var anchor = angular.element('<a/>');
                     anchor.attr({
                     href: 'data:attachment/csv;charset=utf-8,' + encodeURI(res.data),
                     target: '_blank',
                     download: 'properties.csv'
                })[0].click()},
                        function(res) { // error
                            alert("Error: " + res.status + ". Message : " + res.data.message);
                        });

        }

    <!-------------------------------------------------------Source table ---------------------------------------------------------->

        $scope.source_gridOptions = {
            enableColumnMenus:false,
            enableSorting: true,
            enableGridMenu: true,
            columnDefs: [
            { name: 'id', width:50 },
            { name: 'name', width:200},
            { name: 'url'},
            { name: 'skpid', width:100},
            { name: 'prompt.name', displayName: 'Prompt Name', width:200},
            { name: 'description'}
            ],
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
            }
        }

        function source_search() {

            console.log("Run source_search()");
            $http.get('/api/source/all')
                .then(function(response) {
                        $scope.source_gridOptions.data  = response.data,
                      function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);}
                  });
        }

        $scope.source_export = function() {

            $http({
                method: "GET",
                url: "/api/source/export",
            }).then(function(res, status, headers, config) {
                         var anchor = angular.element('<a/>');
                         anchor.attr({
                         href: 'data:attachment/csv;charset=utf-8,' + encodeURI(res.data),
                         target: '_blank',
                         download: 'sources.csv'
                    })[0].click()},
                    function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
                    });

            }


    <!-------------------------------------------------------User table ---------------------------------------------------------->

    $scope.user_gridOptions = {
            enableColumnMenus:false,
            enableSorting: true,
            enableGridMenu: true,
            columnDefs: [
            { name: 'id', width:50 },
            { name: 'username', width:100},
            { name: 'password'},
            { name: 'fullname'},
            { name: 'roles[0].viewName', displayName: 'Role', width:100},
            { name: 'enabled', width:100}
            ],
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
            }
        }

    function user_search() {

        console.log("Run user_search()");
        $http.get('/api/database/users')
            .then(function(response) {
                $scope.user_gridOptions.data  = response.data;
              });
    }

    <!-------------------------------------------------------Role table ---------------------------------------------------------->

    $scope.role_gridOptions = {
                enableColumnMenus:false,
                enableSorting: true,
                enableGridMenu: true,
                columnDefs: [
                { name: 'id', width:50 },
                { name: 'name'},
                { name: 'viewName'}
                ],
                onRegisterApi: function(gridApi) {
                    $scope.gridApi = gridApi;
                }
            }

    function role_search() {

        console.log("Run role_search()");
        $http.get('/api/roles')
            .then(function(response) {
                $scope.role_gridOptions.data  = response.data;
              });
    }

     <!-------------------------------------------------------------------------------------------------------------------------->

    $scope.switch = function(formName) {

            clearForm();

            switch (formName) {
                case 'apilog':          $scope.apilog = true; break;
                case 'attempt':         $scope.attempt = true; break;
                case 'completion_code': $scope.completion_code = true; compCode_search(); break;
                case 'message':         $scope.message = true; break;
                case 'prompt':          $scope.prompt = true; prompt_search(); break;
                case 'property':        $scope.property = true; property_search(); break;
                case 'roles':           $scope.roles = true; role_search(); break;
                case 'ticket':          $scope.ticket = true; break;
                case 'ticket_params':   $scope.ticket_params = true; break;
                case 'source':          $scope.source = true; source_search(); break;
                case 'users':           $scope.users = true; user_search(); break;
            }
        }

        function clearForm() {
            $scope.apilog = false;
            $scope.attempt = false;
            $scope.completion_code = false;
            $scope.message = false;
            $scope.prompt = false;
            $scope.property = false;
            $scope.roles = false;
            $scope.ticket = false;
            $scope.ticket_params = false;
            $scope.source = false;
            $scope.users = false;
        }

}]);


app.service('apilogService',['$http', function ($http) {

    function getApiLogs(pageNumber,size,filter) {
        pageNumber = pageNumber > 0?pageNumber - 1:0;
        filter.page = pageNumber;
        filter.size = size;
        console.log("apiLogFilter = " + angular.toJson(filter));
        return $http({
          method: 'POST',
            url: '/api/database/apilogs',
            data: angular.toJson(filter),
            headers: {'Content-Type': 'application/json'}
        });
    }
    return {
        getApiLogs: getApiLogs
    };
}]);

app.service('attemptService',['$http', function ($http) {

    function getAttempts(pageNumber,size,filter) {
        pageNumber = pageNumber > 0?pageNumber - 1:0;
        filter.page = pageNumber;
        filter.size = size;
        console.log("attemptFilter = " + angular.toJson(filter));
        return $http({
          method: 'POST',
            url: '/api/database/attempts',
            data: angular.toJson(filter),
            headers: {'Content-Type': 'application/json'}
        });
    }
    return {
        getAttempts: getAttempts
    };
}]);

app.service('ticketService',['$http', function ($http) {

    function getTickets(pageNumber,size,filter) {
        pageNumber = pageNumber > 0?pageNumber - 1:0;
        filter.page = pageNumber;
        filter.size = size;
        console.log("ticketFilter = " + angular.toJson(filter));
        return $http({
          method: 'POST',
            url: '/api/database/tickets',
            data: angular.toJson(filter),
            headers: {'Content-Type': 'application/json'}
        });
    }
    return {
        getTickets: getTickets
    };
}]);

app.service('ticketParamsService',['$http', function ($http) {

    function getTicketParams(pageNumber,size,filter) {
        pageNumber = pageNumber > 0?pageNumber - 1:0;
        filter.page = pageNumber;
        filter.size = size;
        console.log("ticketParamsFilter = " + angular.toJson(filter));
        return $http({
          method: 'POST',
            url: '/api/database/ticketparams',
            data: angular.toJson(filter),
            headers: {'Content-Type': 'application/json'}
        });
    }
    return {
        getTicketParams: getTicketParams
    };
}]);
