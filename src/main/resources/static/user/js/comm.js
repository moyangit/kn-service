$(function() {
	    $(document).on('swipeLeft', function(e) {
	        var display = $.Display.all.menuExample;
	        if(display && display.isShow()) {
	            display.hide();
	        }
	    }).on('swipeRight', function(e) {
	        $('#menuExampleBtn').trigger($.TapName);
	    });
	});