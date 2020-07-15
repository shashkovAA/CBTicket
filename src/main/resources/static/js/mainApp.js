// main app.
//var mainApp = angular.module('MainApp', []);
var app = angular.module("ManagementModule", []);

// DIRECTIVE - FILE MODEL
app.directive('fileModel', ['$parse', function ($parse) {
    return {
       restrict: 'A',
       link: function(scope, element, attrs) {
          var model = $parse(attrs.fileModel);
          var modelSetter = model.assign;

          element.bind('change', function(){
             scope.$apply(function(){
                modelSetter(scope, element[0].files[0]);
             });
          });
       }
    };

}]);

// Директива для подтверждения удаления записи
app.directive('ngConfirmClick', [
    function(){
        return {
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "Are you sure?";
                var clickAction = attr.confirmedClick;
                element.bind('click',function (event) {
                    if ( window.confirm(msg) ) {
                        scope.$eval(clickAction)
                    }
                });
            }
        };
}])

// Директива для сравнения введенных паролей
app.directive("compareTo", function ()   {
      return {
          require: "ngModel",
          scope: {
             repeatPassword: "=compareTo"
          },
          link: function (scope, element, attributes, paramval) {
              paramval.$validators.compareTo = function (val)  {
                  return val == scope.repeatPassword;
              };

              scope.$watch("repeatPassword", function ()   {
                  paramval.$validate();
              });
          }
      };

  });