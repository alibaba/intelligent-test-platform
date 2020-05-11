jQuery(document).ready(function() {
    "use strict";


    /* ===================================
		PRELOADER
	====================================== */
	// makes sure the whole site is loaded
/*
	jQuery(window).load(function() {
	        // will first fade out the loading animation
	    jQuery(".status").fadeOut();
	        // will fade out the whole DIV that covers the website.
	    jQuery(".preloader").delay(500).fadeOut("slow");
	});
	*/

	/* ===================================
		STICKY
	====================================== */
	$(window).on('ready , scroll', function() {
	    if ($(window).scrollTop() > 30) {
	        $('.clean-main-menu').addClass('minified');
	    } else {
	        $('.clean-main-menu').removeClass('minified');
	    }
	});

	/* ===================================
		HIDE MENU ON CLICK
	====================================== */
  	jQuery(".nav a").on("click", function () {
      	jQuery("#nav-menu").removeClass("in").addClass("collapse");
 	 });

	/* ===================================
		ONE PAGE NAV
	====================================== */
	/*
	$('#nav-menu').onePageNav({
	    currentClass: 'active',
	    scrollSpeed: 500,
	    easing: 'linear'
	});
*/
	/* ===================================
		WOW JS
	====================================== */
	$(document).ready(function(){
        var wow = new WOW(
            {
                boxClass:     'wow',      // animated element css class (default is wow)
                animateClass: 'animated', // animation css class (default is animated)
                offset:       250,          // distance to the element when triggering the animation (default is 0)
                mobile:       true,       // trigger animations on mobile devices (default is true)
                live:         true,       // act on asynchronously loaded content (default is true)
                callback:     function(box) {
                    // the callback is fired every time an animation is started
                    // the argument that is passed in is the DOM node being animated
                }
            }
        );
        wow.init();
	})

	/* ===================================
		SCROLLUP
	====================================== */
	/*
	$.scrollUp({
		scrollName: 'scrollUp', // Element ID
		scrollDistance: 300, // Distance from top/bottom before showing element (px)
		scrollFrom: 'top', // 'top' or 'bottom'
		scrollSpeed: 5000, // Speed back to top (ms)
		easingType: 'linear', // Scroll to top easing (see http://easings.net/)
		animation: 'fade', // Fade, slide, none
		animationInSpeed: 200, // Animation in speed (ms)
		animationOutSpeed: 200, // Animation out speed (ms)
		scrollText: 'Scroll to top', // Text for element, can contain HTML
		scrollTitle: false, // Set a custom <a> title if required. Defaults to scrollText
		scrollImg: true, // Set true to use image
		activeOverlay: false, // Set CSS color to display scrollUp active point, e.g '#00FFFF'
		zIndex: 2147483647 // Z-Index for the overlay
	});
	*/
	/* ===================================
		FEATURED WORKS
	====================================== */
	/*
	$('.clean-featured-work-img').magnificPopup({
	  type: 'image',
	  gallery:{
	    enabled:true
	  }
	});
*/

	/* ===================================
		COROUSEL SLIDER
	====================================== */
	// Team Slider
	$("#team-slider").owlCarousel({
		items : 3,
		itemsDesktop: [1199,2],
		itemsDesktopSmall: [979,2],
		itemsTablet: [768,2],
		itemsMobile : [520,1],
		autoPlay: 4000,
		navigation : false
	});

	// Feature Works
	$("#featured-work-slider").owlCarousel({
		items : 4,
		itemsDesktop: [1199,3],
		itemsDesktopSmall: [979,3],
		itemsTablet: [768,3],
		itemsTabletSmall: [767,2],
		itemsMobile : [500,1],
		autoPlay: 4000,
		navigation : false
	});

	// Related Works
	$("#related-works-slider").owlCarousel({
		items : 4,
		itemsDesktop: [1199,4],
		itemsDesktopSmall: [979,3],
		itemsTablet: [768,2],
		itemsMobile : [479,1],
		autoPlay: 4000,
		navigation : false
	});

	// Feature Works
	$("#clean-testimonial").owlCarousel({
		items : 1,
		itemsDesktop: [1199,1],
		itemsDesktopSmall: [979,1],
		itemsTablet: [768,1],
		itemsMobile : [520,1],
		autoPlay: 5000
	});

	/* ===================================
		STELLAR
	====================================== */
	$(window).stellar({
		responsive: true,
	    positionProperty: 'position'
	});


	/* ===================================
		ISOTOPE
	====================================== */
	/*
	  var $container = $('.clean-portfolio-items');

	   $container.isotope({
	          filter: '*',
	          itemSelector: '.item',
	          animationOptions: {
	              duration: 750,
	              easing: 'linear',
	              queue: false
	          }
	      });
*/

	  $('#clean-portfolio-filter ul li a').on('click',function(){
	      var selector = $(this).attr('data-filter');
	      $container.isotope({
	          filter: selector,
	          animationOptions: {
	              duration: 750,
	              easing: 'linear',
	              queue: false
	          }
	      });
	    return false;
	  });

	  var $optionSets = $('#clean-portfolio-filter ul'),
	         $optionLinks = $optionSets.find('a');

	         $optionLinks.on('click',function(){
	            var $this = $(this);
	        // don't proceed if already selected
	        if ( $this.hasClass('selected') ) {
	            return false;
	        }
	     var $optionSet = $this.parents('#clean-portfolio-filter ul');
	     $optionSet.find('.selected').removeClass('selected');
	     $this.addClass('selected');
	  });


	/* ===================================
		SLIDER
	====================================== */
	$('#clean-slider').sliderPro({
		width: '100%',
	    height: 400,
	    fade: true,
	    arrows: true,
	    waitForLayers: true,
	    buttons: false,
	    autoplay: true,
	    autoScaleLayers: true,
	    imageScaleMode: 'cover',
	    slideAnimationDuration: 1500,
	    breakpoints: {
	        600: {
	            height: 480
	        }
		}
	});

});
