
// Controller Part
app.controller("DataController", function($scope, $http) {

    $scope.apiLogs = [];
    $scope.apiLog = {
        date: "",
        level: "",
        username: "",
        method:"",
        apiUrl:"",
        requestBody:"",
        responseBody:"",
        statusCode:"",
        host:"",
        sessionId:""
    };
   /* $scope.filteredTodos = [];
    $scope.currentPage = 1;
    $scope.numPerPage = 10;
    $scope.maxSize = 5;

    $scope.makeTodos = function() {
                $scope.todos = [];
                for (i=1;i<=1000;i++) {
                  $scope.todos.push({ text:'todo '+i, done:false});
                }
              };
    $scope.makeTodos();

    $scope.$watch('currentPage + numPerPage', function() {
                var begin = (($scope.currentPage - 1) * $scope.numPerPage)
                , end = begin + $scope.numPerPage;

                $scope.filteredTodos = $scope.todos.slice(begin, end);
              });
*/


    // Now load the data from server
    //refreshData();


    function getForm4Data() {
        $http({
            method: 'GET',
            url: '/api/apilog'
        }).then(
            function(res) { // success
                $scope.apiLogs = res.data;
                console.log("res.data: " + angular.toJson($scope.apiLogs));
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


    $scope.switch = function(number) {

            clearForm();

            switch (number) {
                case 1:  $scope.form1 = true; break;
                case 2:  $scope.form2 = true; break;
                case 3:  $scope.form3 = true; break;
                case 4:  $scope.form4 = true; getForm4Data(); break;
                /*case 5:  $scope.form5 = true; break;
                case 6:  $scope.form6 = true; break;
                case 7:  $scope.form7 = true; break;
                case 8:  $scope.form8 = true; break;*/
            }
        }

        function clearForm() {
            $scope.form1 = false;
            $scope.form2 = false;
            $scope.form3 = false;
            $scope.form4 = false;
           /* $scope.form5 = false;
            $scope.form6 = false;
            $scope.form7 = false;
            $scope.form8 = false;*/
        }

});