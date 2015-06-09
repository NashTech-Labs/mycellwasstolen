/**
 * Contains AJAX call for fetching data for high charts JS scripts
 */
$(document).ready(function(){
	ajaxCallBar();
})

var ajaxCallBar =(function() {
	
	var yearSelected = $( "#selectYear option:selected" ).text();
	
	// Make ajax call the initialize the array
	$.ajax({
		url : "/get_monthly_registrations",
		type : "GET",
		data : {year : yearSelected},
		success : function(monthdata) {
			month_data = monthdata.data;
			barchart(month_data);
		},
		dataType : "json"
	});
}); 
var barchart = function(month_data) {
	$('#containerBar').highcharts(
			{
				chart : {
					type : 'bar'
				},
				title : {
					text : 'Registrations'
				},
				xAxis : {
					categories : [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
							'Jul', 'Aug', 'Sept', 'Oct', 'Nov', 'Dec' ]
				},
				yAxis : {
					title : {
						text : 'Registrations for the Year'
					}
				},
				series : [ {
					name : 'Monthly Registrations',
					data : month_data
				} ]
			});
	}