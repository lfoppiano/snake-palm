app.directive("compiledResults", function ($compile) {
    return {
        restrict: 'A',
        scope: {
            messageFn: '&',
            message: '='
        },
        link: function (scope, iElement, iAttrs) {
            scope.$watch('message', function (message) {
                // var html = scope.messageFn({'message': message});
                iElement.html(message);
                $compile(iElement.contents())(scope);
            });
        }
    }
});