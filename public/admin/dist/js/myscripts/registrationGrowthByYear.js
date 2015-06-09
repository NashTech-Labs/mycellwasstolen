/**
 * Contains AJAX call for fetching data for high charts JS scripts
 */

$(document).ready(function() {
	
	ajaxCallSpline();
})

var ajaxCallSpline = (function() {
	// Make ajax call the initialize the array
	$.ajax({
		url : "/get_per_day_registrations",
		type : "GET",
		success : function(registrationRecords) {
			timeSeriesChart(registrationRecords);
		},
		dataType : "json"
	});
});

var timeSeriesChart = (function(registrationData) {
	var minYear = $("#minYear").val();
	$('#containerSpline').highcharts(
			{
				chart : {
					type : 'spline'
				},
				title : {
					text : 'Per Day Registration Trends'
				},
				subtitle : {
					text : 'Registrations are shown on Irregular basis'
				},
				xAxis : {
					type : 'datetime',
					dateTimeLabelFormats : { // don't display the dummy year
						month : '%e. %b',
						year : '%b'
					},
					title : {
						text : 'Date'
					}
				},
				yAxis : {
					title : {
						text : 'Number of Registrations'
					},
					min : 0
				},
				tooltip : {
					headerFormat : '<b>{series.name}</b><br>',
					pointFormat : '{point.x:%e. %b}: {point.y} '
				},

				plotOptions : {
					spline : {
						marker : {
							enabled : true
						}
					}
				},

				series : [ {
					name : 'Number of Registrations',
//					 Define the data points. All series have a dummy year
//					 of 1970/71 in order to be compared on the same x axis.
//					 Note
//					 that in JavaScript, months start at 0 for January, 1 for
//					 February etc.
					data : registrationData
				} ]
			});
});