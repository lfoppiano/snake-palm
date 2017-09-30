var nerdController = angular.module('nerd-controller', ['ngSanitize']);

var examples = [
    "Consists of one manuscript, 56 pages, entitled \"A Brief History of the Moller Family During the Nazi Era, 1933-1945, and Beyond,\" written in 2006 by brothers Ruben H. Moller and Levi W. Moller.\n The manuscript details their childhood in Bochum, Germany, the death of their father in 1935, and their experiences on Kristallnacht. In December 1941, the brothers, with their mother, Ester, were deported to the Riga ghetto, where they lived from January 1942 until July 1943. Then, they were transferred, first to the Kaiserwald concentration camp, and then to work at Duenawerke. In May 1944, they were transferred to the Panovezys work camp and the Siauliai ghetto before going in June 1944 to the Stutthof concentration camp, where they were separated from their mother. In July 1944, the brothers were transferred again to a work commando operating out of the Kaufering concentration camp, where they worked until their final transfer to the Leitmeritz subcamp of Flossenbürg. Upon returning to Bochum after liberation, they reunited with their mother, and eventually Ester and Ruben immigrated to the United States while Levi immigrated to Israel.\n",
    "Photo Album of Boy Scouts Summer Camp created by Jewish refugee in Shanghai, China: orange silk cover with dragon pattern; pages tied together with orange yarn; handwritten captions written in silver ink; 150 photographs attached to pages; inscription in silver ink written handwritten inside front cover: “This book will tell a story - a story of a camp - the SUMMER CAMP of the 13th Shanghai (United) Group, held at Holt’s wharf - Pootung - from August 17th to 31st, with the kind permission and help of the Wharf officials. If I or any member of the group, who took part at this camp, shall look over this book of pictures in future days, we shall remember - what was considered at the end of camp - the nicest adventure we had so far. It was a hard week for me and my assistants - the Scouts and Rovers of our group - but when we were assured by every participant that they all enjoyed it tremendously much, we realized that our work was not done in vain, but for the interest of youth and for the everlasting ideals of scouting.”"
];

nerdController.controller('nerdController', function ($scope, $http) {

    getRawValue = function(entity) {
        switch (type) {
            case 'value':
                // var start = date.value.offsetStart;
                // var end = date.value.offsetEnd;
                return 

                break;
            case 'interval':
                var startFrom = date.valueFrom.offsetStart;
                var endFrom = date.valueFrom.offsetEnd;
                console.log(text.substring(startFrom, endFrom));

                var startTo = date.valueFrom.offsetStart;
                var endTo = date.valueFrom.offsetEnd;
                console.log(text.substring(startTo, endTo));

                break;
            case 'list':


                break;
        }
    };

    $scope.example = function (n) {
        $scope.text = examples[n];
    };

    $scope.showResults = function (text, data) {
        annotatedText = text;

        datesList = data['dates'];
        var lastMaxIndex = text.length;

        var currentAnnotationIndex = datesList.length - 1;

        for (var m = datesList.length - 1; m >= 0; m--) {
            var entity = datesList[m];

            var label = entity.rawText;
            var type = entity.type;

            var start = parseInt(entity.offsetStart, 10);
            var end = parseInt(entity.offsetEnd, 10);

            if (start > lastMaxIndex) {
                // we have a problem in the initial sort of the entities
                // the server response is not compatible with the client
                console.log("Sorting of entities as present in the server's response not valid for this client.");
            } else if (start == lastMaxIndex) {
                // the entity is associated to the previous map
                // entityMap[currentAnnotationIndex].push(responseJson.entities[m]);
            } else if (end > lastMaxIndex) {
                end = lastMaxIndex;
                lastMaxIndex = start;
                // the entity is associated to the previous map
                // entityMap[currentAnnotationIndex].push(responseJson.entities[m]);
            } else {
                annotatedText = annotatedText.substring(0, start)
                    + '<span id="annot-' + m + '" rel="popover" data-color="' + label + '">'
                    + '<span class="label ' + type + '" style="cursor:hand;cursor:pointer;" >'
                    + annotatedText.substring(start, end) + '</span></span>'
                    + annotatedText.substring(end, annotatedText.length + 1);

                lastMaxIndex = start;
                currentAnnotationIndex = m;
                // entityMap[currentAnnotationIndex] = [];
                // entityMap[currentAnnotationIndex].push(responseJson.entities[m]);
            }
        }


        console.log(annotatedText);
        $scope.result = annotatedText;
    };

    $scope.process = function (text) {
        var textToBeSent = text;

        $http(
            {
                method: 'POST',
                url: '/process',
                data: textToBeSent
            }
        ).then(
            function success(response) {
                if (response.status == 200) {
                    $scope.showResults(text, response.data);
                }
            },
            function failure(error) {
                $scope.results = error;
            }
        )
    }

});