var ws;
function init() {
    ws = new WebSocket("ws://localhost:9004/");
    ws.onopen = function (evt) {
        console.log("opened")
    };
    ws.onmessage = function (evt) {
        json = JSON.parse(evt.data);
        console.log(json);
    };

    ws.onclose = function (evt) {
        console.log("closed");
    };
}
window.addEventListener("load", init, false);
