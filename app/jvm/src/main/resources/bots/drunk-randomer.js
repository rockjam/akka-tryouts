function Bot() {
    var EMPTY = '-';
    var FIELD_SIZE = 3;
    var ws;

    Array.prototype.inArray = function (value) {
        for (var i = 0; i < this.length; i++) {
            if (this[i] === value) {
                return true;
            }
        }
        return false;
    };
    function init() {
        ws = new WebSocket("ws://localhost:9001/");
        ws.onopen = function (evt) {
            ws.send("join");
        };
        ws.onmessage = function (evt) {
            json = JSON.parse(evt.data);
            if (['Game', 'New', 'WrongMove'].inArray(json.status)) {
                if (json.field) {
                    var x = function(field) {
                        return function() {
                            makeMove(field);
                        }
                    }(json.field);
                    setTimeout(x, 500);
                    return false;
                }
            }
            if (['Tie', 'Win', 'Lose'].inArray(json.status)) {
                console.log("result is " + json.status);
                ws.close();
            }
        };
        ws.onclose = function (evt) {
            console.log(evt)
        };
    }
    function makeMove(field_string) {
        var field =
                [field_string.substr(0, 3),
                    field_string.substr(3, 3),
                    field_string.substr(6, 3)];
        var moves = [];
        for (i = 0; i < FIELD_SIZE; ++i) {
            for (j = 0; j < FIELD_SIZE; ++j) {
                if (field[i][j] == EMPTY) {
                    moves.push([i,j]);
                }
            }
        }

        var choice = Math.floor((Math.random() * (moves.length-1)));
        ws.send(JSON.stringify({x: moves[choice][1], y: moves[choice][0]}));
        return false;
    }
    init();
}