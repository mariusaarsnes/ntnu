var ofset = 5;
var rotatingElements = document.getElementsByClassName("hover_elements");


//A delay so that the transition can complete before rotating again.
function delay() {
    setTimeout(rotate, 1000);
}

/* 
    Function for rotating speech-bubble image and text.
    Using ofset, the elments will rotate depending on which way they rotated previously.
*/
function rotate() {
    if (ofset > 0) {
        ofset = -5;
    } else {
        ofset = 5;
    }
    var keepScaleX = "scaleX(-1)";
    rotatingElements[0].style.transform = keepScaleX + "rotate(" + ofset + "deg)";
    rotatingElements[1].style.transform = "rotate(" + -ofset + "deg)";
    delay();
}

/*
    Function for showing and hiding the text.
    The functions uses the parameter i to decide which text to show/hide, 
    depending on if the button has already been pressed once or not.
    
*/ 
function showHideText(i) {
    var buttons = document.getElementsByClassName("show_hide_button");
    var texts = document.getElementsByClassName("text");
    if (buttons[i].textContent == "Tell me more!") {
        texts[i].style.opacity =1;
        buttons[i].textContent ="Tell me less!";
    } else {
        texts[i].style.opacity = 0;
        buttons[i].textContent = "Tell me more!";
    }
}

// Initiating the rotate-loop to make the speech-bubble and text wiggle
rotate();