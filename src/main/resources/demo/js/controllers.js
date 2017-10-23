var app = angular.module('date-application', ['ngSanitize', 'ui.bootstrap', 'ngAnimate']);

var examples = [
    "Consists of one manuscript, 56 pages, entitled \"A Brief History of the Moller Family During the Nazi Era, 1933-1945, and Beyond,\" written in 2006 by brothers Ruben H. Moller and Levi W. Moller.\n The manuscript details their childhood in Bochum, Germany, the death of their father in 1935, and their experiences on Kristallnacht. In December 1941, the brothers, with their mother, Ester, were deported to the Riga ghetto, where they lived from January 1942 until July 1943. Then, they were transferred, first to the Kaiserwald concentration camp, and then to work at Duenawerke. In May 1944, they were transferred to the Panovezys work camp and the Siauliai ghetto before going in June 1944 to the Stutthof concentration camp, where they were separated from their mother. In July 1944, the brothers were transferred again to a work commando operating out of the Kaufering concentration camp, where they worked until their final transfer to the Leitmeritz subcamp of Flossenbürg. Upon returning to Bochum after liberation, they reunited with their mother, and eventually Ester and Ruben immigrated to the United States while Levi immigrated to Israel.",
    "Photo Album of Boy Scouts Summer Camp created by Jewish refugee in Shanghai, China: orange silk cover with dragon pattern; pages tied together with orange yarn; handwritten captions written in silver ink; 150 photographs attached to pages; inscription in silver ink written handwritten inside front cover: “This book will tell a story - a story of a camp - the SUMMER CAMP of the 13th Shanghai (United) Group, held at Holt’s wharf - Pootung - from August 17th to 31st, with the kind permission and help of the Wharf officials. If I or any member of the group, who took part at this camp, shall look over this book of pictures in future days, we shall remember - what was considered at the end of camp - the nicest adventure we had so far. It was a hard week for me and my assistants - the Scouts and Rovers of our group - but when we were assured by every participant that they all enjoyed it tremendously much, we realized that our work was not done in vain, but for the interest of youth and for the everlasting ideals of scouting.”",
    "The Gulf War (2 August 1990 – 28 February 1991), codenamed Operation Desert Shield (2 August 1990 – 17 January 1991) for operations leading to the buildup of troops and defence of Saudi Arabia and Operation Desert Storm (17 January 1991 – 28 February 1991) in its combat phase, was a war waged by coalition forces from 35 nations led by the United States against Iraq in response to Iraq's invasion and annexation of Kuwait.\n The initial conflict to expel Iraqi troops from Kuwait began with an aerial and naval bombardment on 17 January 1991, continuing for five weeks. This was followed by a ground assault on 24 February. This was a decisive victory for the coalition forces, who liberated Kuwait and advanced into Iraqi territory. The coalition ceased its advance, and declared a ceasefire 100 hours after the ground campaign started. Aerial and ground combat was confined to Iraq, Kuwait, and areas on Saudi Arabia's border. Iraq launched Scud missiles against coalition military targets in Saudi Arabia and against Israel.",
    "In 1968 Kaunda was re-elected as president, running unopposed. During the following years Zambia adopted a one party system. In 1972 all political parties except UNIP were banned, and this was formalised in a new constitution that was adopted in 1973. The constitution framed a system called \"one-party participatory democracy\", which in practise meant that UNIP became the sole political factor in the country. It provided for a strong president and a unicameral National Assembly. National policy was formulated by the Central Committee of UNIP. The cabinet executed the central committee's policy. In legislative elections, only candidates running for UNIP were allowed to participate. Even though inter-party competition was out of question, the contest for seats within UNIP was energetic. In the presidential elections, the only candidate allowed to run was the one elected as president of UNIP at the party's general conference. In this way Kaunda was re-elected unopposed with a yes or no vote in 1973, 1978, 1983 and 1988.",
    "From 1039 Amalfi came under the control of the Principality of Salerno. In 1073 Robert Guiscard conquered the city, taking the title Dux Amalfitanorum (\"Duke of the Amalfitans\"). In 1096 Amalfi revolted and reverted to an independent republic, but this was put down in 1101. It revolted again in 1130 and was finally subdued in 1131.\n Amalfi was sacked by Pisans in 1137, at a time when it was weakened by natural disasters (severe flooding) and was annexed to the Norman lands in southern Italy. Thereafter, Amalfi began a rapid decline and was replaced in its role as the main commercial hub of Campania by the Duchy of Naples."
];

app.controller('nerdController', function ($scope, $http, $sce) {

    getDate = function (isoDate) {
        if (isoDate.rawValue !== null) {
            result = "";
            if (isoDate.day > 0) {
                result = "day: " + isoDate.day
            }
            if (isoDate.month > 0) {
                if (result !== "") {
                    result = result + ", ";
                }
                result = result + "month: " + isoDate.month
            }
            if (isoDate.year > 0) {
                if (result !== "") {
                    result = result + ", ";
                }
                result = result + "year: " + isoDate.year
            }
            return result;
        } else {
            return "The value cannot be parsed";
        }

    };

    getRawValue = function (entity) {
        switch (entity.type) {
            case 'value':
                if (entity.value.isoDate.rawValue !== null) {
                    return "Value " + getDate(entity.value.isoDate)
                }
                break;
            case 'interval':
                result = "";
                if (entity.fromDate.isoDate.rawValue !== null) {
                    result = result + "From: " + getDate(entity.fromDate.isoDate)
                }

                if (entity.toDate.isoDate.rawValue !== null) {
                    if (result !== "") {
                        result = result + ", "
                    }
                    result = result + "To " + getDate(entity.toDate.isoDate)
                }
                return "Interval " + result;

                break;
            case 'list':
                result = "";
                for (date in entity.list) {
                    result += getDate(entity.list[date].isoDate) + ", "
                }
                return "List " + result.substring(0, result.length - 2);

                break;
        }
    };

    $scope.fetchExample = function (n) {
        $scope.text = examples[n];
    };

    $scope.showResults = function (text, data) {
        annotatedText = text;

        analytics = data['analytics'];

        $scope.minDate = "<b>Earlier date: </b>" + analytics['minDate'];
        $scope.maxDate = "<b>Latest date: </b>" + analytics['maxDate'];

        console.log($scope.minDate);
        console.log($scope.maxDate);

        datesList = data['dates'];

        // Output text
        var lastMaxIndex = text.length;
        var currentAnnotationIndex = datesList.length - 1;

        for (var m = datesList.length - 1; m >= 0; m--) {
            var entity = datesList[m];

            // var label = entity.rawText;
            var label = getRawValue(entity);
            var type = entity.type;

            var start = parseInt(entity.offsetStart, 10);
            var end = parseInt(entity.offsetEnd, 10);

            if (start > lastMaxIndex) {
                // we have a problem in the initial sort of the entities
                // the server response is not compatible with the client
                console.log("Sorting of entities as present in the server's response not valid for this client.");
            } else if (start === lastMaxIndex) {
                // the entity is associated to the previous map
                // entityMap[currentAnnotationIndex].push(responseJson.entities[m]);
            } else if (end > lastMaxIndex) {
                end = lastMaxIndex;
                lastMaxIndex = start;
                // the entity is associated to the previous map
                // entityMap[currentAnnotationIndex].push(responseJson.entities[m]);
            } else {
                annotatedText = annotatedText.substring(0, start)
                    + '<span uib-popover="' + label + '" popover-trigger="\'mouseenter\'" id="annot-' + m + '" class="label ' + type + '" style="cursor:hand;cursor:pointer;">'
                    + annotatedText.substring(start, end)
                    + '</span>'
                    + annotatedText.substring(end, annotatedText.length + 1);

                lastMaxIndex = start;
                currentAnnotationIndex = m;
            }
        }

        console.log(annotatedText);
        $scope.result = $sce.trustAsHtml(annotatedText);

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


app.controller('popOverController', function ($scope, $sce) {

});