<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/assets/libs/jquery-ui-1.12.0/jquery-ui.css"/>"/>
<script src="<c:url value="/assets/libs/jquery-ui-1.12.0/jquery-ui.js"/>"> </script>

<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/moment.js" />"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/bootstrap-datetimepicker.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/bootstrap/dist/js/collapse.js" />"></script>
<div id="page-wrapper">
    <div class="row">
        <div class="col-lg-12 center">
            <h1 class="page-header">Thêm mới hóa đơn chuyển hàng</h1>
        </div>
        <!-- /.col-lg-12 -->
    </div>
    <!-- /.row -->
    <form:form action="${baseUrl}/containerdelivery/save-a-pickupdelivery-order.html" method="POST" commandName="orderPickupDeliveryFormAdd" role="form" class="form-horizontal">
    <div class="row">
    	<div class="panel panel-default">
    		
			<div class="panel-body">
			
				
				<div class="form-group">
					<label class="control-label col-lg-2 phoneinput">Số điện thoại</label>
					<div class="col-lg-6">
						<div class="ui-widget">
							<form:input path="orderClientCode" id="inputSearch" class="form-control" name="orderClientCode" placeholder="Number Phone"></form:input>
						</div>
					</div>
					<button type="button" class="btn btn-primary active saveAClientButton" onclick="pushPhoneModal()" data-toggle="modal" data-target="#saveAClient">Thêm Khách Hàng</button>
				</div>
			
			<div class="form-group">
				<label class="control-label col-lg-2">Tên Khách Hàng</label>
				<div class="col-lg-6">
						<form:input path="orderClientName" class="form-control" id="orderClientName" name="orderClientName" placeholder="Client Name"></form:input>
				</div>
			</div>
			
			<div class="form-group">
						<label class="control-label col-lg-2">Địa chỉ nhận</label>
						<div class="col-lg-6">
							<form:input path="orderPickupAddress" class="form-control" id="orderPickupAdress"  placeholder="Pickup Adress"></form:input>
						</div>
						<button type="button" class="btn btn-primary active ok-selectMap" onclick="pushLatLngValue(1)">OK</button>
			</div>
			
			<div class="form-group">
						<label class="control-label col-lg-2">Địa chỉ giao</label>
						<div class="col-lg-6">
							<form:input path="orderDeliveryAddress" class="form-control" id="orderDeliveryAdress"  placeholder="Delivery Adress"></form:input>
						</div>
						<button type="button" class="btn btn-primary active ok-selectMap" onclick="pushLatLngValue(2)">OK</button>
			</div>
			<div class="form-group">
						<label class="control-label col-lg-2">Thông tin tọa độ nhận</label>
						<div class="col-lg-6">
							<div class="col-lg-2 orderPickup">
							<label class="control-label col-lg-1">Lat:</label>
							<form:input path="orderPickupLat" class="form-control" id="orderPickupLat" name="orderPickupLat" ></form:input>
							</div>
						
							<div class="col-lg-2 orderPickup">
							<label class="control-label col-lg-1 orderDeliveryLng">Lng:</label>
							<form:input path="orderPickupLng" class="form-control" id="orderPickupLng" name="orderPickupLng" ></form:input>
							</div>
						</div>
						
			</div>
			<div class="form-group">
						<label class="control-label col-lg-2">Thông tin tọa độ giao</label>
						<div class="col-lg-6">
							<div class="col-lg-2 orderDelivery">
							<label class="control-label col-lg-1">Lat:</label>
							<form:input path="orderDeliveryLat" class="form-control" id="orderDeliveryLat" name="orderDeliveryLat" ></form:input>
							</div>
							<div class="col-lg-2 orderDelivery">
							<label class="control-label col-lg-1 orderDeliveryLng">Lng:</label>
							<form:input path="orderDeliveryLng" class="form-control" id="orderDeliveryLng" name="orderDeliveryLng" ></form:input>
							</div>
						</div>
			</div>
			<div class="form-group col-lg-12">
				<div id="map" style="height: 75%; weight:75%;">
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-lg-2">Thời gian nhận</label>
				<div class="form-group col-lg-3">
		                    <form:input path="orderPickupDateTimeEarly" type='text' class="form-control" id='datetimepicker1'/>
				</div>
				<label class="control-label col-lg-2"> Đến :</label>
				<div class="form-group col-lg-3">
		                    <form:input path="orderPickupDateTimeLate" type='text' class="form-control" id='datetimepicker2'/>
            	</div>
			</div>
			<div class="form-group">
				<label class="control-label col-lg-2">Thời gian giao</label>
				<div class="form-group col-lg-3">
		                    <form:input path="orderDelieveryDateTimeEarly" type='text' class="form-control" id='datetimepicker3'/>
				</div>
				<label class="control-label col-lg-2"> Đến :</label>
				<div class="form-group col-lg-3">
		                    <form:input path="orderDelieveryDateTimeLate" type='text' class="form-control" id='datetimepicker4'/>
            	</div>
			</div>
			<div class="form-group">
				<label class="control-label col-lg-2">Số lượng</label>
				<div class="form-group col-lg-3">
		        	<form:input path="orderVolumn" type='text' class="form-control" id='orderVolumn'/>
				</div>
			</div>
			<div class="form-group">
					<label class="control-label col-lg-2">Chọn Lô</label>
					<div class="form-group col-lg-2">
					<form:select path="orderBatchCode" id="batchcode" name="batchCode" class="form-control">
						<option>Chọn lô</option>
						<c:forEach items="${listBatch}" var="reBatch">
							<option value="${reBatch.REQBAT_Code}"><c:out value="${reBatch.REQBAT_Code}"/></option>
						</c:forEach>
					</form:select>
					</div>
				</div>
    	</div>
    	
    </div>
    </div>
    <button type="submit" class="btn btn-primary" id="addANewOrder">Lưu</button>
    <button type="reset" class="btn btn-primary cancel">Hủy</button>
    </form:form>
</div>
<div id="saveAClient" class="modal fade" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Nhập thông tin khách hàng</h4>
      </div>
      <div class="modal-body">
      
      <div class="row">
      <div class="panel panel-default">
      <div class="panel-body">
      	<div class="form-group">
			<label class="control-label col-lg-4">Số điện thoại</label>
			<div class="col-lg-6">
				<input class="form-control" type="text" id="clientPhone" name="clientPhone" placeholder="Phone"></input>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-lg-4">Tên khách hàng  </label>
			<div class="col-lg-6">
				<input class="form-control" type="text" id="clientName" name="clientName" placeholder="Name"></input>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-lg-4">Địa chỉ </label>
			<div class="col-lg-6">
				<input class="form-control" type="text" id="clientAddress" name="clientAddress" placeholder="Address"></input>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-lg-4">Email</label>
			<div class="col-lg-6">
				<input class="form-control" type="text" id="clientEmail" name="clientEmail" placeholder="Email"></input>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-lg-4">FaceBook Account</label>
			<div class="col-lg-6">
				<input class="form-control" type="text" id="clientFacebook" name="clientFacebook" placeholder="Facebook"></input>
			</div>
		</div>
		
		</div>
		</div>
      </div>
      <div class="modal-footer">
     	<button type="button" class="btn btn-primary active" onclick="saveAClient()">Save</button>
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
	</div>
  </div>
</div>
<!-- /#page-wrapper -->
<script>
var map;
var marker=[];
var backupz;
$('#datetimepicker1').datetimepicker({
	format: 'YYYY-MM-DD HH:mm:ss'
});
$('#datetimepicker2').datetimepicker({
	format: 'YYYY-MM-DD HH:mm:ss'
});
$('#datetimepicker3').datetimepicker({
	format: 'YYYY-MM-DD HH:mm:ss'
});
$('#datetimepicker4').datetimepicker({
	format: 'YYYY-MM-DD HH:mm:ss'
});
function pushLatLngValue(z){
	if(z==2)
	var address = document.getElementById('orderDeliveryAdress').value;
	else 
		var address = document.getElementById('orderPickupAdress').value;
	var geocoder = new google.maps.Geocoder();
	backupz=z;
	geocoder.geocode({'address': address}, function(results, status) {
          if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
            console.log(results[0].geometry.location.lat());
			updateLatLng(backupz,results[0].geometry.location.lat(),results[0].geometry.location.lng());
          } else {
        	  alert('Geocode was not successful for the following reason: ' + status);
          }
	});
	
	
}
function updateLatLng(z,lat,lng){
	if(z==1){
		if(marker[z]!=null){
			marker[z].setMap(null);
		}
		marker[z]=new google.maps.Marker({
			position:{lat: lat, lng: lng},
			
			});
		marker[z].setMap(map);
		$('#orderPickupLat').val(lat);
		$('#orderPickupLng').val(lng);
	}else{
		if(marker[z]!=null){
			marker[z].setMap(null);
		}
		marker[z]=new google.maps.Marker({
			position:{lat: lat, lng: lng},
			icon:"https://www.google.com/mapfiles/marker_green.png"
			});
		marker[z].setMap(map);
		
		$('#orderDeliveryLat').val(lat);
		$('#orderDeliveryLng').val(lng);
	}
}
function initMapAuto(){
	var mapProp = {
			center: {lat: 21.03, lng: 105.8},
			zoom: 12,
			mapTypeId: google.maps.MapTypeId.ROADMAP
		};
	map=new google.maps.Map(document.getElementById("map"),mapProp);
	
	var defaultBounds = new google.maps.LatLngBounds(
			  new google.maps.LatLng(8.0, 101.1759),
			  new google.maps.LatLng(24.0, 109.2631));
	var input = document.getElementById('orderDeliveryAdress');
	var input2 = document.getElementById('orderPickupAdress');
	var input3= document.getElementById('clientAddress');
	var options = {
			  bounds: defaultBounds
			 
			};
	autocomplete = new google.maps.places.Autocomplete(input, options);
	autocomplete2 = new google.maps.places.Autocomplete(input2, options);
	autocomplete3= new google.maps.places.Autocomplete(input3,options);
	console.log(autocomplete);
		
}
function pushPhoneModal(){
	$("#clientPhone").val($("#inputSearch").val());
}
function saveAClient(){
	console.log("saveAClient");
	var phone =$("#clientPhone").val();
	var name=$("#clientName").val();
	var address=$("#clientAddress").val();
	var facebook=$("#clientFacebook").val();
	var email=$("#clientEmail").val();
	var jsonx={
		"phone":phone,
		"name":name,
		"address":address,
		"facebook":facebook,
		"email":email,
	};
	console.log(jsonx);
	$.ajax({ 
	    type:"POST", 
	    url:"${baseUrl}/cm/save-A-Client",
	    data: JSON.stringify(jsonx),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    //Stringified Json Object
	    success: function(respose){
	        // Success Message Handler
	      console.log(respose);
	        if(respose){
	        	$( "#orderClientName" ).val( name );
	        	$( "#orderAdress" ).val( address );
	        	$("#saveAClient").modal('hide');
	        }
	    }
    });
	
	console.log(phone);
}
$( function() {
	console.log("start");
    	$( "#inputSearch" ).autocomplete({
    		source: function( request, response ) {
    			var input= document.getElementById("inputSearch").value;
    			console.log(input);
    			var jsonx={
    		    		"inputString":input
    		    };
    	    	$.ajax({ 
    	    	    type:"POST", 
    	    	    url:"${baseUrl}/clientSearch-byPhone",
    	    	    data: JSON.stringify(jsonx),
    	    	    contentType: "application/json; charset=utf-8",
    	    	    dataType: "json",
    	    	    //Stringified Json Object
    	    	    success: function(resposeJsonObject){
    	    	        // Success Message Handler
    	    	        if((resposeJsonObject.length==0) || (resposeJsonObject==null)) $(".saveAClientButton").show();
    	    	        else {
    	    	        	$(".saveAClientButton").hide();
    	    	        }
    	    	        console.log(resposeJsonObject);
    	    	        availableTags=[
    	    	                       ];
    	    	        for(var i=0;i<resposeJsonObject.length;i++){
    	    	        	availableTags.push({
    	    	        		"label": resposeJsonObject[i].phone,
    	    	        		"value": resposeJsonObject[i].phone,
    	    	        		"value1":resposeJsonObject[i].adress,
    	    	        		"value2":resposeJsonObject[i].name
    	    	        	});	
    	    	        }
    	    	        response(availableTags);
    	    	    }
    	        });
    	    },
    	    select: function(event, ui) {
    	        //assign value back to the form element
    	    	$( "#orderClientName" ).val( ui.item.value2 );
    	    	$( "#orderPickupAdress" ).val( ui.item.value1 );
    	    }
    
    	});
  //} );
});

</script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4&libraries=places&callback=initMapAuto" async defer></script>