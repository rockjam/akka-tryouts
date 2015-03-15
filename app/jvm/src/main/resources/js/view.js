var subs = {};
//websocket could be replaced with SSE
var ws;
function init() {
    ws = new WebSocket("ws://localhost:9004/");
    ws.onopen = function (evt) {
        console.log("opened")
    };
    ws.onmessage = function (evt) {
        json = JSON.parse(evt.data);
        if (!subs[json.id]) {
            createField(json.id);
        }
        subs[json.id] = true;
        updateTable(json);
        if(json.state == "GameOver") {
            setTimeout(remElem(json.id), 5000);
        }
    };
    ws.onclose = function (evt) {
        console.log("closed");
    };
}
function remElem(elemId) {
    return function() {
        document.getElementById("content")
                .removeChild(document.getElementById(elemId));
        delete subs[elemId];
    }
}
function updateTable(data) {
    drawField(data.field, document.getElementById(data.id));
}
function drawField(field, table) {
    var trs = table.getElementsByTagName("tr");
    for (i = 0; i < trs.length; i++) {
        var tds = trs[i].children;
        for (j = 0; j < tds.length; j++) {
            visualize(tds[j], field[i * 3 + j]);
        }
    }
}
function visualize(elem, sign) {
    if(sign == '-') {
        return false;
    }
    while(elem.firstChild) {
        elem.removeChild(elem.firstChild);
    }
    var size =  85;
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
function createField(id) {
    var table = document.createElement("table");
    table.setAttribute("id", id);
    for(var i = 0; i < 3; i++) {
        var tr = document.createElement("tr");
        table.appendChild(tr);
        for(var j = 0; j < 3; j++) {
            var td = document.createElement("td");
            tr.appendChild(td);
        }
    }
    var content = document.getElementById("content");
    content.appendChild(table);
}
window.addEventListener("load", init, false);
