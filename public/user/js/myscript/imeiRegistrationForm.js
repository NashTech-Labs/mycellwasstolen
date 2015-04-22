/**
 * Contains AJAX form submit, form validation, and get mobile brands AJAX call.
 */

// clear all field on close button
function clearForm() {
	document.getElementById("mobileRegistrationForm").reset();
	$('#imeiRegisterModal').modal('hide');
}

// Program for custom submit function for the form
$("#mobileRegistrationForm").submit(function(event) {

	// disable the default form submission
	event.preventDefault();

	// grab all form data
	var formData = new FormData($(this)[0]);

	$.ajax({
		url : '/save_users',
		type : 'POST',
		data : formData,
		async : false,
		cache : false,
		contentType : false,
		processData : false,
		success : function(returndata) {
			alert(returndata);
		}
	});

	return false;
});

// set the file
function setValue() {
	var file = $("#fileUpload").val();
	$("#document").val(file);
}

// collapse for other IMEI number
$('input[type=radio]').on('change', function() {
	if (!this.checked)
		return
	$('.collapse').not($('div.' + $(this).attr('class'))).slideUp();
	$('.collapse.' + $(this).attr('class')).slideDown();
});

// international mobile number input function
$("#contactNo").intlTelInput();

// function for mobile brand change
$(document).ready(function() {
	$("#brandId").change(function() {
		var brandId = $(this).val();
		mobileModels(brandId);
	});
});

// AJAX call to get mobile models based on selected brand
var mobileModels = function(brandId) {
	var mobileAjaxCallBack = {
		success : modelAjaxSuccess,
		error : modelAjaxError
	};
	jsRoutes.controllers.MobileController.getModels(brandId).ajax(
			mobileAjaxCallBack);
};

// handle the success of mobileModels function
var modelAjaxSuccess = function(data) {
	$("#modelId").empty();
	$('#modelId').append($('<option>', {
		value : "-- Choose Mobile Model --",
		text : "-- Choose Mobile Model --"
	}));

	$.each(data, function(i, d) {
		$('#modelId').append($('<option>', {
			value : d.id,
			text : d.name
		}));
	});
	console.debug("Success of Ajax Call");
	console.debug(data);
};

// handle error on getModels AJAX call
var modelAjaxError = function(err) {
	console.debug("Error of ajax Call");
	console.debug(err);
};

// form validation
$("#mobileRegistrationForm").validate({
	rules : {
		imei : {
			required : true,
			remote : {
				url : '/check_imei',
				type : 'GET',
				data : {
					imeiId : function() {
						return $("#imei").val();
					}
				}
			}
		},
		otherImei : {
			remote : {
				url : '/check_imei',
				type : 'GET',
				data : {
					imeiId : function() {
						return $("#otherImei").val();
					}
				}
			}
		},
		brandId : {
			required : true
		},
		modelId : {
			required : true
		},
		email : {
			required : true,
			maxlength : 100,
			email : true,
		},
		userName : {
			required : true,
			maxlength : 100
		},
		contactNo : {
			required : true,
			maxlength : 20
		},
		fileUpload : {
			required : true,
			maxlength : 100
		}
	},
	messages : {
		email : {
			required : "Please provide an email address",
			email : "Please enter a valid email address"
		},
		imei : {
			required : "Please provide an IMEI number of mobile",
			remote : "Invalid imei Id or may be already exist."
		},
		otherImei : {
			required : "Please provide an IMEI number of mobile",
			remote : "Invalid imei Id or may be already exist."
		},
		brandId : {
			required : "Please select a brand"
		},
		modelId : {
			required : "Please select a model"
		}
	}
});

// upload file validation
$('#fileUpload').change(function() {
	return validateFileExtension(this)
})

// validate file extension
function validateFileExtension(fld) {
	if (!/(\.bmp|\.gif|\.jpg|\.jpeg|\.png)$/i.test(fld.value)) {
		alert("Invalid image file type.");
		fld.form.reset();
		fld.focus();
		return false;
	}
	return true;
}

// function for IMEI change
$('#imei').change(function() {
	return validateImeiMeid(this)
})

// validate IMEI number
function validateImeiMeid(imei) {
	var imeiMeid = document.getElementById("imei").value;
	var pattern = /^[0-9]+$/;

	if (imeiMeid.length == 15) {
		if (pattern.test(imeiMeid)) {
			return true;
		} else {
			alert("IMEI is not in correct format");
			imei.form.elements['imei'].value = imei.form.elements['imei'].defaultValue;
			imei.focus();
			return false;
		}
	} else {
		alert("IMEI must be 15 digits !");
		imei.form.elements['imei'].value = imei.form.elements['imei'].defaultValue;
		imei.focus();
		return false;
	}

}

// function for other IMEI change
$('#otherImei').change(function() {
	return validateOtherImeiMeid(this)
})

// validate other IMEI number
function validateOtherImeiMeid(otherImei) {
	var otherImeiMeid = document.getElementById("otherImei").value;
	var pattern = /^[0-9]+$/;

	if (otherImeiMeid.length == 15) {
		if (pattern.test(otherImeiMeid)) {
			return true;
		} else {
			alert("IMEI is not in correct format");
			otherImei.form.elements['otherImei'].value = otherImei.form.elements['otherImei'].defaultValue;
			otherImei.focus();
			return false;
		}
	} else {
		alert("IMEI must be of 15 digits !");
		otherImei.form.elements['otherImei'].value = otherImei.form.elements['otherImei'].defaultValue;
		otherImei.focus();
		return false;
	}
}
