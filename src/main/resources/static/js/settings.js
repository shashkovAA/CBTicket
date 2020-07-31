
// Controller Part
app.controller("SettingsController", function($scope, $http) {

    $scope.files = [];
    $scope.file = {
        filename: "",
        lastModifyDate:""
    };

    refreshData();

    function refreshData() {
            $http({
                method: 'GET',
                url: '/api/settings/files'
            }).then(
                function(res) { // success
                    $scope.files = res.data;
                    console.log("res.data: " + angular.toJson($scope.files));
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                }
            );
        }

    $scope.form1_importFile = function() {

        var url = "/api/settings/upload"
        var formData = new FormData();

        formData.append("file", $scope.settingsFile);

        var config = { headers: {'Content-Type': undefined},
                   transformRequest: angular.identity,
                   transformResponse: angular.identity
        }

        $http.post(url, formData, config).then(
            // Success
            function(response) {
                var respObj = JSON.parse(response.data)
                window.alert(respObj.message);
                refreshData();
            },
            // Error
            function(response) {
                 _error(response.data);
            });
    };

     $scope.deleteFile = function(file) {
            console.log("file.filename: " + file.filename);
            $http({
                method: 'DELETE',
                url: '/api/settings/' + file.filename
            }).then(_success, _error);
        };

    $scope.restartApp = function() {

        $http({
                method: 'POST',
                url: '/api/settings/restart'
        }).then(
                function(res) { // success
                     console.log("res.data: " + res.data);
                     window.alert(respObj.message);
                },
                function(res) { // error
                   _error(response.data);
                }
        );
    };
    function _success(res) {
        refreshData();
    }

    function _error(res) {
        var data = res.data;
        var status = res.status;

        alert("Error: " + status + ". Message : " + data.message);
    }

});