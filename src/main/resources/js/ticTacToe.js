var ws;
var player;
function init() {
    ws = new WebSocket("ws://localhost:9001/");
    ws.onopen = function (evt) {
        publishEvent(evt, "opened");
        ws.send("join");
    };
    ws.onmessage = ordinaryOnMessage;
    ws.onclose = function (evt) {
        publishEvent(evt, "closed")
    };
    function bindClick() {
        var trs = document.getElementsByTagName("tr");
        for (i = 0; i < trs.length; i++) {
            var tds = trs[i].children;
            for (j = 0; j < tds.length; j++) {
                tds[j].addEventListener("click", makeTurn(j, i));
            }
        }
    }
    bindClick();
}
function ordinaryOnMessage(evt) {
    publishEvent(evt, "message");
    json = JSON.parse(evt.data);
    if (json.field) {
        drawField(json.field)
    }
    if (json.ch) {
        player = json.ch;
    }
}
function waitingStatusOnMessage (elem) {
    return function(evt) {
        publishEvent(evt, "message");
        json = JSON.parse(evt.data);
        if (json.status == 'success' || json.status == 'Win' || json.status == 'Tie') {
            visualize(elem, player);
        }
        ws.onmessage = ordinaryOnMessage;
    }
}
function visualize(elem, sign, s) {
    if(sign == '-') {
        return false;
    }
    while(elem.firstChild) {
        elem.removeChild(elem.firstChild);
    }
    var size;
    if(s) {
        size = s;
    } else {
        size = 90;
    }
    var pic = new Image(size,size);
    if(sign == 'x') {
        pic.src  = 'images/x.png';
        elem.appendChild(pic);
    }
    if(sign == 'o') {
        pic.src  = 'images/o.png';
        elem.appendChild(pic);
    }
}
function drawField(field) {
    var trs = document.getElementsByTagName("tr");
    for (i = 0; i < trs.length; i++) {
        var tds = trs[i].children;
        for (j = 0; j < tds.length; j++) {
            visualize(tds[j],field[i * 3 + j]);
        }
    }
}
function makeTurn(x_move, y_move) {
    return function aux() {
        ws.onmessage = waitingStatusOnMessage(this);
        ws.send(JSON.stringify({x: x_move, y: y_move}));
    }
}
function publishEvent(evt, type) {
    publish(evt.data ? evt.data : "", type);
}
function publish(message, type) {
    var text;
    if(type == "opened") {
        text = "Wait for your opponent";
    }
    if(type == "message") {
        var mess = JSON.parse(message);
        if(mess.status == "success") {
            text = "Wait for your turn";
        }
        if(mess.status == "failure") {
            text = mess.message;
        }
        if(mess.status == "New") {
            text = "Game began, your turn";
        }
        if(mess.status == "Game") {
            text = "Your turn";
        }
        if(mess.status == "Win") {
            text = "Congratulations, you win!";
        }
        if(mess.status == "Lose") {
            text = "Bad luck, you lose!";
        }
        if(mess.status == "Tie") {
            text = "Game over, it is tie";
        }
        if(mess.ch) {
            sign = document.getElementById("sign");
            visualize(sign, mess.ch, 40);
        }
    }
    doc = document.getElementById("messages");
    doc.innerHTML = text;
}
window.addEventListener("load", init, false);