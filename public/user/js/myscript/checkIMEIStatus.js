/**
 * Contains java script functions to make ajax calls on checkIMEI
 */
	function showMobileRecord() {
			var imeid = $('#imeiMeid').val();
			if($('#imeiMeid').val() == ''){
				var empty='<div class="alert alert-dismissible alert-danger" style="width:400px;">'
					  +'<button type="button" class="close" data-dismiss="alert">×</button>'
					  +'<strong>Empty !</strong> Please enter a valid IMEI number.'
					+'</div>'
				$("#mobileRecords").html(empty);
			}
			else{
			mobileRecord(imeid);
			}
		}

		var mobileAjaxSuccess = function(data) {
				
			if (data.status == "Ok") {
				var mobile = data.mobileData
				if (mobile.mobileStatus=="approved"){	
			
				if(mobile.regType=="stolen"){
					var image='<img src="/assets/user/img/warn.jpg"  height=20 width=20 alt="...Stolen..."/>';
				}
				else{
					var image='<img src="/assets/user/img/success.jpg"  height=20 width=20 alt="...Clean..."/>';
				}
				var mobileData = '<div id="mobile-status"><h4>This IMEI number has been approved successfully</h4><div class="table-responsive"><table class="table" style="background-color: rgba(246, 235, 174, 0.49);"><tr><th>IMEI</th><th>Other IMEI</th><th>Brand</th><th>Model</th><th>Contact No</th><th>Email</th><th>Label</th></tr><tr><td>'
						+ mobile.imei
						+ '</td><td>'
						+ (mobile.otherImei == "" ? "None" : mobile.otherImei)
						+ '</td><td>'
						+mobile.brandName
						+ '</td><td>'
						+mobile.modelName
						+ '</td><td>'
						+ mobile.contactNo
						+ '</td><td>'
						+ mobile.email
						+'</td><td>'
						+ image + '</td></tr></table></div>';
				$("#mobileRecords").empty();
				$("#mobileRecords").removeClass("alert alert-danger");
				$('#mobileRecords').html(mobileData);
				}
				else
				{
					var image='<img src="/assets/user/img/warn.png" height=20 width=20 alt="Not Approved"/>';
					var mobileData = '<div id="mobile-status"><h4><font color="#F41616">This IMEI is registered as Stolen/Clean Phone but has not been approved yet </font></h4><table style="background-color: rgba(246, 235, 174, 0.49);" class="table table-bordered"><tr><th>IMEI</th><th>Other IMEI</th><th>Brand</th><th>Model</th><th>Contact No</th><th>Email</th><th>Label</th></tr><tr><td>'
						+ mobile.imei
						+ '</td><td>'
						+ (mobile.otherImei == "" ? "None" : mobile.otherImei)
						+ '</td><td>'
						+mobile.brandName
						+ '</td><td>'
						+mobile.modelName
						+ '</td><td>'
						+ mobile.contactNo
						+ '</td><td>'
						+ mobile.email
						+'</td><td>'
						+ image + '</td></tr></table></div></div>';
				$("#mobileRecords").empty();
				$("#mobileRecords").removeClass("alert alert-danger");
				$('#mobileRecords').html(mobileData);
					}
			} 
			else {
				var notFound='<div class="alert alert-dismissible alert-danger" style="width:400px;">'
					  +'<button type="button" class="close" data-dismiss="alert">×</button>'
					  +'<strong>Not Found!</strong> This IMEI number is not registered yet.'
					+'</div>'
				$("#mobileRecords").html(notFound);
				console.debug("Data no found");
			}
			console.debug("Success of Ajax Call");
			console.debug("Success of Ajax Call" + data);
		};

		var mobileAjaxError = function(err) {
			console.debug("Error of ajax Call");
			console.debug(err);
		};
		
		var mobileRecord = function(imeid) {
			var mobileAjaxCallBack = {
				success : mobileAjaxSuccess,
				error : mobileAjaxError
			};
			jsRoutes.controllers.MobileController.checkMobileStatus(imeid,"user").ajax(
					mobileAjaxCallBack);
		};
