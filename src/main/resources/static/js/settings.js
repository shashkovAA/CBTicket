
// Controller Part
app.controller("SettingsController", function($scope, $http) {

    $scope.properties = [];
    $scope.property = {
         id: 1,
         name: "",
         value: "",
         description: "",
         editable: true
    };

    $scope.prompts = [];
    $scope.prompt = {
        id: 1,
        name: "",
        filepath: "",
        filename: "",
        description: ""
    }

    $scope.sources = [];
    $scope.source = {
            id: 1,
            name: "",
            url: "",
            skpid: "",
            prompt: {},
            description: ""
    }

    refreshData();

    function refreshData() {

        refreshProperties();
        refreshPrompts();
        refreshSources();

    }

    function refreshProperties() {

        $http({
                        method: 'GET',
                        url: '/api/property/all'
                    }).then(function(res) { // success
                                $scope.properties = res.data;
                        },  function(res) { // error
                                alert("Error: " + res.status + ". Message : " + res.data.message);
                        }
                    );
        $scope.showEditProperty = false;
        }

    function refreshPrompts() {

        $http({
                    method: 'GET',
                    url: '/api/prompt/all'
                }).then(
                    function(res) { // success
                        $scope.prompts = res.data;
                    },
                    function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
                    }
                );
        $scope.showEditPrompt = false;
    }

    function refreshSources() {

        $http({
                    method: 'GET',
                    url: '/api/source/all'
                }).then(
                    function(res) { // success
                        $scope.sources = res.data;
                    },
                    function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
                    }
                );
        $scope.showEditSource = false;
    }


    <!--------------------------- Property table ------------------------------------>

    $scope.submitProperty = function() {

            var method = "PUT";
            var url = "/api/property/update";

            $http({
                method: method,
                url: url,
                data: angular.toJson($scope.property),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function(res) { // success
                       refreshProperties();
                  },function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
                    });


    };

    $scope.cancelProperty = function() {
        $scope.showEditProperty = false;
    };

    $scope.editProperty = function(property) {
            $scope.showEditProperty = true;
            $scope.property.id = property.id;
            $scope.property.name = property.name;
            $scope.property.value = property.value;
            $scope.property.description = property.description;
            $scope.property.editable = property.editable;
    };

    <!--------------------------- Prompt table ------------------------------------>

    $scope.createPrompt = function() {
            clearPromptData();
            $scope.showEditPrompt = true;
            $scope.promptActionHeader = "Add new Prompt :";
    };

    $scope.cancelPrompt = function() {
            clearPromptData();
            $scope.showEditPrompt = false;
    };

    $scope.submitPrompt = function() {
            console.log("Prompt: " + angular.toJson($scope.prompt));

            var method = "";
            var url = "";

            if ($scope.prompt.id == 0) {
                method = "POST";
                url = 'api/prompt/add';
            } else {
                method = "PUT";
                url = 'api/prompt/update';
            }

            $http({
                method: method,
                url: url,
                data: angular.toJson($scope.prompt),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function(res) { // success
                        //alert("Status: " + res.status + ". Message : " + res.data.message);
                        refreshPrompts();
                   },function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
            });
        };

    $scope.deletePrompt = function(prompt) {

        $http({
            method: 'DELETE',
            url: 'api/prompt/delete/' + prompt.id
            }).then(function(res) { // success
                       refreshPrompts();
                   },function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
                   });
    };

    $scope.editPrompt = function(prompt) {
            $scope.showEditPrompt = true;
            $scope.promptActionHeader = "Edit prompt :";
            $scope.prompt.id = prompt.id;
            $scope.prompt.name = prompt.name;
            $scope.prompt.filepath = prompt.filepath;
            $scope.prompt.filename = prompt.filename;
            $scope.prompt.description = prompt.description;
        };

    function clearPromptData() {

        $scope.prompt.id = 0;
        $scope.prompt.name = "";
        $scope.prompt.filepath = "";
        $scope.prompt.filename = "";
        $scope.prompt.description = "";
    }

    <!--------------------------- Source table ------------------------------------>

    $scope.createSource = function() {
        clearSourceData();
        $scope.showEditSource = true;
        $scope.sourceActionHeader = "Add new Source :";
    };

    $scope.cancelSource = function() {
        clearSourceData();
        $scope.showEditSource = false;
    };

    $scope.submitSource = function() {
            console.log("Source: " + angular.toJson($scope.source));

            $scope.source.prompt = $scope.sourcePrompt;
            console.log("Source: " + angular.toJson($scope.source));

            var method = "";
            var url = "";

            if ($scope.source.id == 0) {
                method = "POST";
                url = 'api/source/add';
            } else {
                method = "PUT";
                url = 'api/source/update';
            }

            $http({
                method: method,
                url: url,
                data: angular.toJson($scope.source),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function(res) { // success
                       refreshSources();
                   },function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
            });
    };

    $scope.deleteSource = function(source) {

        $http({
            method: 'DELETE',
            url: 'api/source/delete/' + source.id
            }).then(function(res) { // success
                       refreshSources();
                   },function(res) { // error
                        alert("Error: " + res.status + ". Message : " + res.data.message);
            });
    };

    var selectedPromptIndex;

    $scope.editSource = function(source) {

            $scope.showEditSource = true;
            $scope.sourceActionHeader = "Edit Source :";
            $scope.source.id = source.id;
            $scope.source.name = source.name;
            $scope.source.url = source.url;
            $scope.source.skpid = source.skpid;
            $scope.source.description = source.description;

            $scope.sourcePrompt = source.prompt;

            for (var i = 0; i < $scope.prompts.length; i++){
                if ($scope.prompts[i].name == source.prompt.name)
                    selectedPromptIndex = i+1;
            }

            var elem = document.querySelector('#promptSelector');
            document.querySelectorAll('option')[selectedPromptIndex].selected = true;
            M.FormSelect.init(elem);
    };

    $scope.updateSelectedPrompt = function() {

        $scope.prompts.forEach(function (prmpt) {
            if (prmpt.name == $scope.source.prompt)
                $scope.sourcePrompt = prmpt;
        });
    }

    function clearSourceData() {

        $scope.source.id = 0;
        $scope.source.name = "";
        $scope.source.url = "";
        $scope.source.skpid = "";
        $scope.source.prompt = {};
        $scope.source.description = "";
    }

});