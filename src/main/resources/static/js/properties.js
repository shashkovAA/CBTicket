
// Controller Part
app.controller("PropertyController", function($scope, $http) {

    $scope.properties = [];
    $scope.propertyForm = {
        id: 1,
        name: "",
        value: "",
        description: "",
        editable: true,
        removable: true
    };

    // Now load the data from server
    refreshPropertyData();

    $scope.submitProperty = function() {
        console.log("propertyForm: " + angular.toJson($scope.propertyForm));

        var method = "";
        var url = "";

        if ($scope.propertyForm.id == 0) {
            method = "POST";
            url = '/api/properties';
        } else {
            method = "PUT";
            url = '/api/properties';
        }

        $http({
            method: method,
            url: url,
            data: angular.toJson($scope.propertyForm),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);

         $scope.showForm = false;

    };

    $scope.cancelFormBtn = function() {

        _clearFormData();
        $scope.showForm = false;
    };

    $scope.createProperty = function() {
        _clearFormData();
         $scope.showForm = true;
         $scope.formHeader = "Add new property :";
    };

    $scope.deleteProperty = function(property) {
        console.log("propertyForm: " + property.id);
        console.log("propertyForm: " + property.name);
        $http({
            method: 'DELETE',
            url: '/api/properties/' + property.id
        }).then(_success, _error);
    };

    // In case of edit
    $scope.editProperty = function(property) {
        $scope.showForm = true;
        $scope.formHeader = "Edit property :";
        $scope.propertyForm.id = property.id;
        $scope.propertyForm.name = property.name;
        $scope.propertyForm.value = property.value;
        $scope.propertyForm.description = property.description;
        $scope.propertyForm.editable = property.editable;
        $scope.propertyForm.removable = property.removable;
        console.log("propertyForm: " + $scope.propertyForm);

    };

    function refreshPropertyData() {
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

    $scope.showImport = function() {
        $scope.form2 = true;
    };

    $scope.form2_importFile = function() {

        $scope.form2 = true;

        var url = "/api/properties/upload"
        var formData = new FormData();

        formData.append("file", $scope.propFile);

        var config = { headers: {'Content-Type': undefined},
                   transformRequest: angular.identity,
                   transformResponse: angular.identity
        }

        $http.post(url, formData, config).then(
            // Success
            function(response) {
                refreshPropertyData();
                _clearFormData();
                var respObj = JSON.parse(response.data)
                window.alert(respObj.message);
            },
            // Error
            function(response) {
                 _error(response.data);
            });
    };

    $scope.cancelForm2Btn = function() {
        $scope.form2 = false;
    };

    $scope.form2_exportFile = function() {

        console.log("Run form2_exportFile ");

        $http({
                method: 'GET',
                url: '/api/properties/export'
        }).then(
                function(res) { // success
                    console.log("res.data: " + res.data);
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                }
        );
    };

    function _success(res) {
        refreshPropertyData();
        _clearFormData();
    }

    function _error(res) {
        var data = res.data;
        var status = res.status;

        alert("Error: " + status + ". Message : " + data.message);
    }

    // Clear the form
    function _clearFormData() {
        $scope.propertyForm.id = 0;
        $scope.propertyForm.name = "";
        $scope.propertyForm.value = ""
        $scope.propertyForm.description = ""
    };
});