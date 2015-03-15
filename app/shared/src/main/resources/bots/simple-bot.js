var EMPTY = '-';
var ws;
var player;//символ игрока
var preferredMoves =
        [
            [1, 1], [0, 0], [0, 2], [2, 0], [2, 2],
            [0, 1], [1, 0], [1, 2], [2, 1]
        ];

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
        if (json.ch) {
            player = json.ch;
        }
        if (['Game', 'New', 'WrongMove'].inArray(json.status)) {
            if (json.field) {
                makeMove(json.field);
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
    for (var i = 0; i < preferredMoves.length; ++i) {
        var move = preferredMoves[i];
        if (field[move[0]][move[1]] == EMPTY) {
            ws.send(JSON.stringify({x: move[1], y: move[0]}));
            return false;
        }
    }
}
init();