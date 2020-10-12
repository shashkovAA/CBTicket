
// Controller Part
app.controller("LogsController", function($scope, $http) {

    $scope.logs = [];
    $scope.log = {
        filename: "",
        filesize: "",
        lastModifyDate:""
    };

    // Now load the data from server
    refreshData();

     $scope.pagelink = function() {
        console.log("ttttttttttttttttttt: ");
     }

    $scope.deleteLogFile = function(log) {

            $http({
                method: 'DELETE',
                url: '/api/logs/' + log.filename
            }).then(_success, _error);
    };

    function refreshData() {
        $http({
            method: 'GET',
            url: '/api/logs'
        }).then(
            function(res) { // success
                $scope.logs = res.data;
                console.log("res.data: " + angular.toJson($scope.logs));
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            }
        );
    }

    function _success(res) {
        refreshData();
        clearFormData();
    }

    function _error(res) {
        var data = res.data;
        var status = res.status;
        alert("Error: " + status + ". Message : " + data.message);
    }

});