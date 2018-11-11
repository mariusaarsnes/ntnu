var bubblePool = [[50, 70, 133], [70, 74, 136], [74, 77, 139],
                  [77, 80, 142], [80, 83, 145], [83, 85, 150],
                  [85, 87, 153], [87, 93, 163], [93, 95, 166],
                  [95, 100, 170], [100, 103, 175], [103, 120, 192],
                  [120, 125, 198], [125, 145, 202]];

var startWidth = [50, 145];
var startHeight = [75, 85];
var bubbleHeight = 10, bubbleCount = 7;

function assignPosition() {
    return [randomInterval(startWidth[0], startWidth[1]),
            randomInterval(startHeight[0], startHeight[1])];
}

function initiateBubbles() {
    for (let i = 0; i < bubbleCount; i++) {
        $("#bubble-div").append("<div class='beer-bubble'></div>");
    }
    bubbles = $(".beer-bubble");
    bubbles.each(function(index) {
        animateBubble($(this), index);
    });
}

function animateBubble(bubble, index) {
    var position = assignPosition();
    bubble.css({ "left" : "" + position[0] + "px", "bottom" : "" + (position[1] + (index * bubbleHeight)) + "px", "display":"block" });
    bubble.animate({
        bottom: "+=" + (getHeight(position[0]) - position[1])
    }, randomInterval(1500, 3000), function() {
        animateBubble(bubble, index);
    });
}

function getHeight(startWidth) {
    for (var i = 0; i < bubblePool.length; i++) {
        if (startWidth >= bubblePool[i][0] && startWidth < bubblePool[i][1])
            return bubblePool[i][2];
    }
    return undefined;
}

function randomInterval(value1, value2) {
    return value1 + random(value2 - value1);
}

function random(value) {
    return Math.floor(Math.random() * value);
}
