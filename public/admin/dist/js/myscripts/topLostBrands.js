/**
 * Contains AJAX call for fetching data for high charts JS scripts
 */

$(document).ready(function() {
	ajaxCallPie();
})

var ajaxCallPie = (function() {
	$(document).ready(function() {
		var topSelectedBrand = $("#selectBrand option:selected").text();
		$.ajax({
			url : "/get_top_lost_brands_data",
			type : "GET",
			data : {
				n : topSelectedBrand
			},
			success : function(brandData) {
				top_lost_data = brandData;
				pieChart(top_lost_data);
			},
			dataType : "json"
		});
	});
});

// Build the chart
var pieChart = function(top_lost_data) {
	$('#containerPie').highcharts({
		chart : {
			plotBackgroundColor : null,
			plotBorderWidth : null,
			plotShadow : false
		},
		title : {
			text : 'Top Lost Brands'
		},
		tooltip : {
			pointFormat : '{series.name}: <b>{point.percentage:.1f}%</b>'
		},
		plotOptions : {
			pie : {
				allowPointSelect : true,
				cursor : 'pointer',
				dataLabels : {
					enabled : false
				},
				showInLegend : true
			}
		},
		series : [ {
			type : 'pie',
			name : 'Percentage of Theft/Loss',
			data : top_lost_data
		} ]
	});
};
