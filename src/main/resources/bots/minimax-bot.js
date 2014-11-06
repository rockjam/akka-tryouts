var ws;

var EMPTY = '-';
var FIELD_SIZE = 3;
var OPPONENT_SIDE;
var Field;
var player;//символ игрока

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

function makeMove(field) {
    OPPONENT_SIDE = (player == 'x')? 'o': 'x';
    Field =
            [field.substr(0, 3),
                field.substr(3, 3),
                field.substr(6, 3)];
    var result = minimax(FIELD_SIZE - 1, player, Number.NEGATIVE_INFINITY, Number.POSITIVE_INFINITY);
    ws.send(JSON.stringify({x: result[2], y: result[1]}));
}

/** Minimax (recursive) at level of depth for maximizing or minimizing player
 with alpha-beta cut-off. Return int[3] of [score, row, col]  */
function minimax(depth, side, alpha, beta)
{
    // Generate possible next moves in an array of int[2] of {row, col}.
    var possibleMoves = generateMoves(Field);

    // mySeed is maximizing; while oppSeed is minimizing
    var    score;
    var    bestRow = -1;
    var    bestCol = -1;

    if (possibleMoves.length == 0 || depth == 0) {
        // Gameover or depth reached, evaluate score
        score = evaluate(Field);
        console.log("Gameover or depth reached, score "+score);
        return [score, bestRow, bestCol];
    } else {
        var i;
        for (i = 0; i < possibleMoves.length; ++i)
        {
            var move = possibleMoves[i];

            // try this move for the current player
            Field[move[0]][move[1]] = side;
            if (side == player) {  // mySeed (computer) is maximizing player
                score = minimax(depth - 1, OPPONENT_SIDE, alpha, beta)[0];
                console.log(JSON.stringify(move)+" - score " + score + "alpha = "+alpha);
                if (score > alpha) {
                    alpha = score;
                    bestRow = move[0];
                    bestCol = move[1];
                }
            } else {  // oppSeed is minimizing player
                score = minimax(depth - 1, player, alpha, beta)[0];
                if (score < beta) {
                    beta = score;
                    bestRow = move[0];
                    bestCol = move[1];
                }
            }
            // undo move
            Field[move[0]][move[1]] = EMPTY;
            // cut-off
            if (alpha >= beta) {
                console.log(JSON.stringify(move)+" - cutted off");
                break;
            }
        }
        return [ (side == player) ? alpha : beta, bestRow, bestCol ];
    }
}

function generateMoves(field)
{
    var moves = [];
    // If gameover, i.e., no next move
    //if (hasWon(mySeed) || hasWon(oppSeed)) {
    //    return nextMoves;   // return empty list
    //}

    //console.log("-----Possible moves:");
    // Search for empty cells and add to the List
    for (var row = 0; row < FIELD_SIZE; ++row) {
        for (var col = 0; col < FIELD_SIZE; ++col) {
            if (field[row][col] == EMPTY) {
                moves.push([row, col]);
                //console.log("possibleMove="+[row, col]+", size="+moves.length);
            }
        }
    }
    console.log(moves);
    return moves;
}

function evaluate(field)
{
    var score = 0;

    score += evaluateLine(field, 0, 0, 0, 1, 0, 2);  // row 0
    score += evaluateLine(field, 1, 0, 1, 1, 1, 2);  // row 1
    score += evaluateLine(field, 2, 0, 2, 1, 2, 2);  // row 2
    score += evaluateLine(field, 0, 0, 1, 0, 2, 0);  // col 0
    score += evaluateLine(field, 0, 1, 1, 1, 2, 1);  // col 1
    score += evaluateLine(field, 0, 2, 1, 2, 2, 2);  // col 2
    score += evaluateLine(field, 0, 0, 1, 1, 2, 2);  // diagonal
    score += evaluateLine(field, 0, 2, 1, 1, 2, 0);  // alternate diagonal

    return score;
}

function evaluateLine(field, row1, col1, row2, col2, row3, col3)
{
    var score = 0;

    // First cell
    if (field[row1][col1] == player) {
        score = 1;
    } else if (field[row1][col1] == OPPONENT_SIDE) {
        score = -1;
    }

    // Second cell
    if (field[row2][col2] == player) {
        if (score == 1) {   // cell1 is mySeed
            score = 10;
        } else if (score == -1) {  // cell1 is oppSeed
            return 0;
        } else {  // cell1 is empty
            score = 1;
        }
    } else if (field[row2][col2] == OPPONENT_SIDE) {
        if (score == -1) { // cell1 is oppSeed
            score = -10;
        } else if (score == 1) { // cell1 is mySeed
            return 0;
        } else {  // cell1 is empty
            score = -1;
        }
    }

    // Third cell
    if (field[row3][col3] == player) {
        if (score > 0) {  // cell1 and/or cell2 is mySeed
            score *= 10;
        } else if (score < 0) {  // cell1 and/or cell2 is oppSeed
            return 0;
        } else {  // cell1 and cell2 are empty
            score = 1;
        }
    } else if (field[row3][col3] == OPPONENT_SIDE) {
        if (score < 0) {  // cell1 and/or cell2 is oppSeed
            score *= 10;
        } else if (score > 1) {  // cell1 and/or cell2 is mySeed
            return 0;
        } else {  // cell1 and cell2 are empty
            score = -1;
        }
    }
    return score;

}
init();