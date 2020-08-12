
// Controller Part
app.controller("UserController", function($scope, $http) {

    $scope.users = [];
    $scope.roles = [];

    $scope.user = {
        id: 1,
        username: "",
        password: "",
        fullname: "",
        roles: [],
        enabled: true
    };
    $scope.role = {
        name: "",
        viewName : "",
        authority: ""
    };

    refreshData();

    $scope.submitUser = function() {
        console.log("User: " + angular.toJson($scope.user));

        var method = "";
        var url = "";

        if ($scope.user.id == 0) {
            method = "POST";
            url = 'api/users';
        } else {
            method = "PUT";
            url = 'api/users';
        }

        $http({
            method: method,
            url: url,
            data: angular.toJson($scope.user),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);

         $scope.showForm = false;

    };

    $scope.cancelUser = function() {

        clearFormData();
        $scope.showForm = false;
    };

    $scope.createUser = function() {
        clearFormData();
        $scope.showForm = true;
        $scope.formHeader = "Add new user :";
    };

    $scope.deleteUser = function(user) {
        console.log("user: " + user.id);
        console.log("user: " + user.username);

        $http({
            method: 'DELETE',
            url: 'api/users/' + user.id
        }).then(_success, _error);
    };

    $scope.editUser = function(user) {
        $scope.showForm = true;
        $scope.formHeader = "Edit user :";
        $scope.user.id = user.id;
        $scope.user.username = user.username;
        $scope.user.password = user.password;
        $scope.confirmPassword = user.password;
        $scope.user.fullname = user.fullname;
        $scope.user.enabled = user.enabled;
        console.log("$scope.user: " + angular.toJson($scope.user));
        console.log("user: " + angular.toJson(user));

        console.log(" $scope.roles[0]: " + angular.toJson($scope.roles[0]));
        console.log(" $scope.roles[1]: " + angular.toJson($scope.roles[1]));
        console.log("user: " + angular.toJson($scope.user));
        $scope.user.roles[0] =  user.roles[0];
        /*console.log(" $scope.user.roles[0]: " + angular.toJson($scope.user.roles[0]));
        console.log(" $scope.roles[1]: " + angular.toJson($scope.roles[1]));*/
        console.log("user: " + angular.toJson($scope.user));



    };

    function refreshData() {
        $http({
            method: 'GET',
            url: 'api/users'
        }).then(
            function(res) { // success
                $scope.users = res.data;

            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            }
        );

        $http({
            method: 'GET',
            url: 'api/roles'
                }).then(
                    function(res) { // success
                        $scope.roles = res.data;
                        //console.log("Roles: " + angular.toJson($scope.roles));

                    },
                    function(res) { // error
                        console.log("Error: " + res.status + " : " + res.data);
                    }
                );
         $scope.showForm = false;
    }

    $scope.cancelForm2Btn = function() {
        $scope.form2 = false;
    };

    function _success(res) {
        refreshData();
        clearFormData();
    }

    function _error(res) {
        var data = res.data;
        var status = res.status;
        alert("Error: " + status + ". Message : " + data.message);
    }

    // Clear the form
    function clearFormData() {
        $scope.user.id = 0;
        $scope.user.username = "";
        $scope.user.password = "";
        $scope.user.fullname = "";
        $scope.user.enabled = false;

        $scope.confirmPassword = "";
    };

     $scope.updateSelectedRole = function() {

                console.log("Select: " + $scope.role_select);
                //console.log("Select.name: " + $scope.role_select.name);
                //console.log("Select: " +  angular.toJson($scope.role_select));

                /*var method = "GET";
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
*/
     }
});