/**
 * Contains java script functions to make AJAX calls on checkIMEI
 */

// clear all field on close button
function clearFields() {
	$('#msg').empty();
	$('#imeiNum').val("");
	$('#mobileRecords').empty();
	$('#imeiStatusModal').modal('hide');
}

// function for IMEI validation call function to get IMEI records
function showMobileRecord() {
	$('#msg').empty();
	$('#mobileRecords').empty();
	$()
	var imeid = $('#imeiNum').val();
	if ($('#imeiNum').val() == '') {
		var empty = '<label style="color:red;">Empty!   Please enter an IMEI number</label>'
		$("#msg").html(empty);
	} else {
		var pattern = /^[0-9]+$/;

		if (pattern.test(imeid)) {
			if (imeid.length == 15) {
				mobileRecord(imeid);
			} else {
				var empty = '<label style="color:red;">IMEI must be of minimum 15 digits !</label>'
				$("#msg").empty();
				$("#msg").html(empty);
			}
		} else {
			var empty = '<label style="color:red;">Not a valid IMEI number !</label>'
			$("#msg").empty();
			$("#msg").html(empty);
		}
	}
}

// function to get IMEI record through AJAX
var mobileRecord = function(imeid) {
	var mobileAjaxCallBack = {
		success : mobileAjaxSuccess,
		error : mobileAjaxError
	};
	jsRoutes.controllers.MobileController.checkMobileStatus(imeid, "user")
			.ajax(mobileAjaxCallBack);
};

// handle success of checkMobileStatus AJAX call
var mobileAjaxSuccess = function(data) {

	if (data.status == "Ok") {
		var mobile = data.mobileData
		
		if (mobile.mobileStatus == "approved") {

			if (mobile.regType == "stolen") {
				var image = '<img src="/assets/user/img/red.png" alt="...Stolen..."/>';
			} else {
				var image = '<img src="/assets/user/img/ok.png" alt="...Clean..."/>';
			}
			var mobileData = '<br><div id="mobile-status"><h4>This IMEI number has been approved successfully as '
					+ mobile.regType
					+ '.</h4><div class="table-responsive"><table class="table table-bordered" style="color: #2c3e50;"><tr><th>IMEI</th><th>Other IMEI</th><th>Brand</th><th>Model</th><th>Contact No</th><th>Email</th><th>Label</th></tr><tr><td>'
					+ mobile.imei
					+ '</td><td>'
					+ (mobile.otherImei == "" ? "None" : mobile.otherImei)
					+ '</td><td>'
					+ mobile.brandName
					+ '</td><td>'
					+ mobile.modelName
					+ '</td><td>'
					+ mobile.contactNo
					+ '</td><td>'
					+ mobile.email
					+ '</td><td>'
					+ image
					+ '</td></tr></table></div>';
			$("#mobileRecords").empty();
			$("#mobileRecords").removeClass("alert alert-danger");
			$('#mobileRecords').html(mobileData);
		} else {
			var image = '<img src="/assets/user/img/warning.png" alt="Not Approved"/>';
			var mobileData = '<br><div id="mobile-status"><h4><font color="#F41616">This IMEI is registered as '
					+ mobile.regType
					+ ' Phone but has not been approved yet. </font></h4><table style="color: #2c3e50;" class="table table-bordered"><tr><th>IMEI</th><th>Other IMEI</th><th>Brand</th><th>Model</th><th>Contact No</th><th>Email</th><th>Label</th></tr><tr><td>'
					+ mobile.imei
					+ '</td><td>'
					+ (mobile.otherImei == "" ? "None" : mobile.otherImei)
					+ '</td><td>'
					+ mobile.brandName
					+ '</td><td>'
					+ mobile.modelName
					+ '</td><td>'
					+ mobile.contactNo
					+ '</td><td>'
					+ mobile.email
					+ '</td><td>'
					+ image
					+ '</td></tr></table></div></div>';
			$("#mobileRecords").empty();
			$("#mobileRecords").removeClass("alert alert-danger");
			$('#mobileRecords').html(mobileData);
		}
	} else {
		var notFound = '<br><div class="alert alert-dismissible alert-danger" style="width:400px;">'
				+ '<button type="button" class="close" data-dismiss="alert">×</button>'
				+ '<strong>Not Found!</strong> This IMEI number is not registered yet.'
				+ '</div>'
		$("#mobileRecords").html(notFound);
		console.debug("Data no found");
	}
	console.debug("Success of Ajax Call");
	console.debug("Success of Ajax Call" + data);
};

// handle error on checkMobileStatus AJAX call
var mobileAjaxError = function(err) {
	console.debug("Error of ajax Call");
	console.debug(err);
};
