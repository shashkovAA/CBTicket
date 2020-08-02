
// Controller Part
app.controller("SettingsController", function($scope, $http) {

    $scope.files = [];
    $scope.properties = [];

    $scope.file = {
        filename: "",
        lastModifyDate:""
    };
    $scope.property = {
         id: 1,
         name: "",
         value: "",
         description: "",
         editable: true
    };

    refreshData();

    function refreshData() {
            $http({
                method: 'GET',
                url: '/api/settings/files'
            }).then(
                function(res) { // success
                    $scope.files = res.data;
                },
                function(res) { // error
                }
            );

            $http({
                method: 'GET',
                url: '/api/properties'
            }).then(
                function(res) { // success
                    $scope.properties = res.data;
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                }
            );
             $scope.showForm = false;
             $scope.form2 = false;
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

    $scope.form1_deleteFile = function(file) {
            console.log("file.filename: " + file.filename);
            $http({
                method: 'DELETE',
                url: '/api/settings/' + file.filename
            }).then(_success, _error);
        };

    $scope.form2_submitBtn = function() {
            //console.log("property: " + angular.toJson($scope.property));

            var method = "PUT";
            var url = "/api/properties";

            $http({
                method: method,
                url: url,
                data: angular.toJson($scope.property),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(_success, _error);

             $scope.showForm = false;
             refreshData();

    };

    $scope.form2_cancelBtn = function() {
        $scope.showForm = false;
    };

    $scope.form2_editProperty = function(property) {
            $scope.showForm = true;
            $scope.property.id = property.id;
            $scope.property.name = property.name;
            $scope.property.value = property.value;
            $scope.property.description = property.description;
            $scope.property.editable = property.editable;
            //console.log("property: " + $scope.property);
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