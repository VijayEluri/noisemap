function drawInitial(svg) {
	var values = [];
	$('table td').each(function(){
		var $cell = $(this);
		var decibel = parseFloat($cell.text());		// arrayMax requires that !isNaN()
		$cell.data('decibel', decibel);
		values.push(decibel);
	});
	var max = Math.max.apply(Math, values);
	if(max!==0){
		$('table td').each(function(){
			var $cell = $(this);
			var percent = $cell.data('decibel')/max;
			/* x, y, width, height, r* are *-radius of the ellipse for the rounded corners. Returns new node */
			draw(svg, $cell, percent);
		});
	}
}
function draw(parent, cell, percent){
	/*
	if(percent===0)
		//parent.rect(cell.data('x'), cell.data('y'), cell.data('width'), cell.data('height'), rx="10", ry="10",
		//	{fill: 'none', stroke:"black", 'stroke-width':"1"});
		parent.circle(cell.data('x'), cell.data('y'), cell.data('width'),
			{fill: 'none', stroke:"black", 'stroke-width':"1"});
	else
		//parent.rect(cell.data('x'), cell.data('y'), cell.data('width'), cell.data('height'), rx="10", ry="10",
		//{fill: 'rgb(' + colorPicker(percent) + ')', stroke:"black", 'stroke-width':"1", 'fill-opacity':'0.7'});
		parent.circle(cell.data('x'), cell.data('y'), cell.data('width'),
			{fill: 'rgb(' + colorPicker(percent) + ')', stroke:"black", 'stroke-width':"1", 'fill-opacity':'0.7'});
	*/
	if(percent===0)
		parent.rect(cell.data('x'), cell.data('y'), cell.data('width'), cell.data('height'), rx="10", ry="10",
			{fill: 'none', stroke:"black", 'stroke-width':"1"});
	else
		parent.rect(cell.data('x'), cell.data('y'), cell.data('width'), cell.data('height'), rx="10", ry="10",
		{fill: 'rgb(' + colorPicker(percent) + ')', stroke:"black", 'stroke-width':"1", 'fill-opacity':'0.7'});
	//parent.rect(cell.data('x'), cell.data('y'), cell.data('width'), cell.data('height'), rx="10", ry="10",
	//	{fill: 'red', stroke:"black", 'stroke-width':"1", 'fill-opacity':percent});	// doesn't show the difference
}
var colorPicker = function(percent) {
	var color;
	//colorMap = [[143,217,16],  [246,233,24],  [203,19,32]];	// geographic heatmap without radius gradient
	colorMap = [[23,54,125],   [121,190,213], [214,228,231], [242,146,133], [203,19,32]];
	// thermometer from graphup.js: blue, sky blue, gray, orange, red
	if(percent<0)
		percent *= -100;
	else
		percent *= 100;
	// If the color map contains only one color, it's easy
	// Also map values under 0% to the first color
	if (colorMap.length === 1 || percent <= 0) {
		color = colorMap[0];
	}
	// Map values above 100% to the last color
	else if (percent >= 100) {
		color = colorMap[colorMap.length - 1];
	}
	// Map values to the color map
	else {
		// Search for the color segment the percentage falls in between
		var step = 100 / (colorMap.length - 1);
		for (i = 0, j = 0; i < 100; i += step, j++) {
			if (percent >= i && percent <= i + step)
				break;
		}
		// Blend two colors
		var colorStart = colorMap[j];
		var colorEnd = colorMap[j + 1];
		var colorPercent = (percent - i) / step;
		color = [
			Math.max(Math.min(parseInt((colorPercent * (colorEnd[0] - colorStart[0])) + colorStart[0],10), 255), 0),
			Math.max(Math.min(parseInt((colorPercent * (colorEnd[1] - colorStart[1])) + colorStart[1],10), 255), 0),
			Math.max(Math.min(parseInt((colorPercent * (colorEnd[2] - colorStart[2])) + colorStart[2],10), 255), 0)
		];
	}
	return color;
};