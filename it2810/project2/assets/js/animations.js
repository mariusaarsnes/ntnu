var circles = $('.circle');
var topleftcontent = $('.top-left-content');
var readmore = $('.read-more');

function circleResizer() {
	circles.each(function() {
		$(this).stop();
		$(this).css('width', topleftcontent.width()*$(this).data("size"));
		$(this).css('height', $(this).width());
	});
}
$(window).resize(function() {
	if(this.resizeTO) clearTimeout(this.resizeTO);
    this.resizeTO = setTimeout(function() {
    	startCircles();
    }, 500);

	circleResizer();
});
//scroll handeling
$( window ).scroll(function() {
	if(window.innerWidth > 480 ){
	    var navBar = $( "nav" );
	    navBar.css( "background-color", "#393c80" );
	    if($(window).scrollTop() === 0) {
	  	    navBar.css("background-color", "#4b4fa1");
	    }
	}
});
function scroll_to_anchor(anchor_id){
    var tag = $("#"+anchor_id+"");
    $('html,body').animate({scrollTop: tag.offset().top},'slow');
}

//click functions
readmore.click(function() {
	scroll_to_anchor("pagecontent");
});



function animateCircles(circle, bigger, time) {
	if(bigger) {
		circle.animate({
			width: "+=2%",
			height: "+=2%"
		}, time, function(){
			animateCircles(circle, false, time);
		});
	}else {
		circle.animate({
			width: "-=2%",
			height: "-=2%"
		}, time, function(){
			animateCircles(circle, true, time);
		});
	}
}
 function startCircles() {
 	circles.each(function() {
 		animateCircles($(this), true, Math.floor((Math.random() * 1500) + 700));
 	})
 }
function init() {
	circleResizer();
 	startCircles();
}

init();
