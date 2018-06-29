<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet" type="text/css" href="<c:url value="/assets/libs/inputDate/dist/css/bootstrap-datetimepicker.css"/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/assets/libs/inputDate/dist/css/bootstrap-datetimepicker.min.css"/>"/>

<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/moment.js" />"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/bootstrap-datetimepicker.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/bootstrap/dist/js/collapse.js" />"></script>

<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Lập tuyến giao hàng ngày <span id="tile_routeDate"></span></h1>
		</div>
	</div>
	<div class="row" id="infoOfRoute">
		<div class="col-sm-3">
			<div class="form-group">
				<select class="form-control" id="select-listOrderDate">
					<option>Chọn ngày lập tuyến</option>
					<c:forEach items="${listOrderDueDate}" var="orderDate">
						<option value="${orderDate}"><c:out value="${orderDate}"/></option>
					</c:forEach>
				</select>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-8" style="padding:0px;">
			<div id="googleMap" style="width:100%;height:500"></div>	
		</div>
		<div class="col-lg-4" style="padding:0px;">
			<div class="panel panel-default" style="width:350px;" id="routeDetailPanel">
				<div class="panel-body" style="padding:5px;min-height:500;max-height:500;overflow-y:scroll;">
					<table class="table table-bordered" id="tblRouteDetail">
						<thead>
							<tr>
								<th>Tên KH</th>
								<th>T/g dự kiến</th>	
								<th>T/g yêu cầu</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<!-- /.panel-body -->
			</div>
			<!-- /#routeDetailPanel -->
		</div>
	</div>
	<!-- /.row(googlemap+panel) -->
	<div class="row" style="margin-top:0px;">
		<div class="col-sm-2">
			<div class="form-group">
				<select class="form-control" id="lstShippers">
				</select>
			</div>
		</div>
		<!-- /.col-sm-2 -->
		<div class="col-sm-2" style="padding:0px" id="divTimeStartOfShipper">
		</div>
		<div class="col-sm-1">
			<button class="btn btn-info" value="change" id="btnChangeTime">Change</button>
		</div>
		<div class="col-sm-1">
			<button class="btn btn-warning" id="btnResetRoute" onclick="btnResetRoute_cf();">Reset</button>
		</div>
		<!-- /.col-sm-1 -->
		<div class="col-sm-1">
			<button class="btn btn-warning" id="btnRemove" value="cancel">Xóa</button>
		</div>
		<!-- /.col-sm-1 -->
		
		<div class="table-reponsive">
			<table class="table table-bordered table-hover" id="tblRouteOfShippers">
				<thead>
					<tr>
						<th>Người giao hàng</th>
						<th>Các địa điểm</th>
						<th>Tổng quãng đường</th>
						<th>Tổng thời gian</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<!-- /.table-reponsive -->
		<button class="btn btn-primary" onclick="cf_saveRouteCreated();">Lưu</button>
		<button class="btn btn-primary btnCancelCreateRoute">Hủy</button>
		<button class="btn btn-warning" onclick="cf_confirmRouteCreated();">Chốt tuyến</button>
	</div>
	<!-- /.row -->
</div>

<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAruL-HLFSNh6G2MLhjS-eBTea7r7EFa5A&libraries=places&callback=initialize" async defer></script>
<!--  
<script src="<c:url value="/assets/js/source/shippingmanagement/createRoute.js"/>"></script>
-->
<script>
$(document).ready(function(){
	$('#select-listOrderDate').change(function(){
		var dateSelected = $(this).val();
		$('#tile_routeDate').html(dateSelected);
		
		$.ajax({
			type: 'POST',
			url: baseUrl+'/ship/viewAssignedRoute',
			data: dateSelected,
			contentType: 'application/text',
			success: constructFields
				
		});
	});
	
	//click button change time start of shipper
	$('#btnChangeTime').click(function(){
		if($(this).attr("value")==="change"){
			$('.inputTimeStartOfShipper').attr('readonly',false);
			$(this).attr('value',"save");
			$(this).attr('class','btn btn-primary');
			$(this).html("Save");
		}else{
			$('.inputTimeStartOfShipper').attr('readonly',true);
			$(this).attr('value',"change");
			$(this).attr('class','btn btn-info');
			$(this).html("Change");
			var indexOfShipper = parseInt($('select#lstShippers').find(":selected").val());
			caculateTimeAndDistance(indexOfShipper);
		}
	});
	
	//click button remove point change value of button
	$('#btnRemove').click(function(){
		if($(this).attr("value")==="cancel"){
			$(this).attr("value","remove");
			$(this).attr('class','btn btn-primary');
			$(this).html("Cancel")
		}else{
			$(this).attr("value","cancel");
			$(this).attr('class','btn btn-warning');
			$(this).html("Xóa");
		}
	});
	
	//change shipper -> show input time of this shipper
	$('#lstShippers').change(function(){
		var indexOfShipper = parseInt($('select#lstShippers').find(":selected").val());
		for(var i=0; i<lstOfShippers.length; i++){
			if(i==indexOfShipper){
				$('#inputTimeStartOfShipper'+i).removeAttr('type');
			}else{
				$('#inputTimeStartOfShipper'+i).attr('type','hidden');
			}
		}
	});
	
	$('.btnCancelCreateRoute').click(function(){
		window.location = '${baseUrl}/';
	})
});

var map;
function initialize() {
	//construct google map
	var mapProp = {
		center: {lat: 21.033333, lng: 105.849998},
		zoom: 12,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
}

var lstOrders = [];
var markerOfOrder = [];
var routePath=[];//route to save lat and lng of each shipper
var map;
var lstOfShippers = [];
var nShipper;
var routeOfShipper = [[]];//route to display table of each shipper
var routeCodeOfShipper=[];//route code of each route of shipper
var colorOfShipper = [];
var depotOfShipper = [];

function constructFields(response){
	lstOrders = response.lstOrders;
	//console.log("lstOrders: "+JSON.stringify(lstOrders));
	lstOfShippers = response.lstShippers;
	nShipper = lstOfShippers.length;
	//console.log("lstOfShippers: "+JSON.stringify(lstOfShippers));
	//construct routePath and routeOfShipper and depotOfShipper and marker for shipper
	
	for(var i=0; i<nShipper;i++){
		routeOfShipper[i] = [];
	}
	
	for(var i=0; i<nShipper; i++){
		$('#lstShippers').append($('<option>', {
		    value: i,
		    text: lstOfShippers[i].shp_Code
		}));
		
		var today = new Date();
		var timeStart = "";
		timeStart += today.getFullYear()+"-"+(today.getMonth()+1)+"-"+today.getDate()+" "+today.getHours()+":"+today.getMinutes();
		for(var j=0; j<response.lstRTUnCreation.length; j++){
			if(response.lstRTUnCreation[j].shipper_Code==lstOfShippers[i].shp_Code){
				timeStart = response.lstRTUnCreation[j].route_Start_Time;
			}
		}
		if(i==0){
			$('#divTimeStartOfShipper').append('<input class="form-control inputTimeStartOfShipper" id="inputTimeStartOfShipper'+i+'" value="'+timeStart+'" readonly/>');			
		}else{
			$('#divTimeStartOfShipper').append('<input class="form-control inputTimeStartOfShipper" id="inputTimeStartOfShipper'+i+'" value="'+timeStart+'" type="hidden" readonly />');
		}
		constructDateTimePicker();
		var html='<tr>'
					+'<td>'+lstOfShippers[i].shp_Code+'</td>'
					+'<td id="route'+i+'"></td>'
					+'<td id="distance'+i+'">0m</td>'
					+'<td id="time'+i+'">0s</td>'
				+'</tr>';
		$('#tblRouteOfShippers tbody').append(html);
	}
	console.log("lstRTUnCreation: "+JSON.stringify(response.lstRTUnCreation));
	for(var i=0; i<response.lstRTUnCreation.length; i++){
		console.log("RTUnCreation["+i+"].shipper_Code: "+JSON.stringify(response.lstRTUnCreation[i].shipper_Code));
		for(var j=0; j<nShipper; j++){
			for(var k=0; k<response.lstOrders.length; k++){
				//if order k in route i and route i is shipper j
				if(response.lstRTUnCreation[i].shipper_Code == response.lstShippers[j].shp_Code
						&& response.lstRTUnCreation[i].order_Code == response.lstOrders[k].o_Code){
					//add order k to routOfShipper j at sequence in route i
					routeOfShipper[j][response.lstRTUnCreation[i].order_Sequence] = k;
					routeCodeOfShipper[j] = response.lstRTUnCreation[i].route_Code;
				}
			}
			//console.log("routeOfShipper["+j+"]: "+routeOfShipper[j]);
		}
		//console.log("routeCodeOfShipper["+j+"]: "+routeCodeOfShipper);
	}
	
	var markerOfShipper = [];
	for(var i=0; i<nShipper; i++){
		colorOfShipper[i] = getRandomColor();
		$('#infoOfRoute').append('<div class="col-sm-2" style="background-color:'+colorOfShipper[i]+'">'+lstOfShippers[i].shp_Code+'</div>')
		routePath[i] = new google.maps.Polyline({
		    strokeColor: colorOfShipper[i],
		    strokeOpacity: 1.0,
		    strokeWeight: 3,
		});
		routePath[i].setMap(map);	
		var shp_depotLat = lstOfShippers[i].shp_DepotLat;
		var shp_depotLng = lstOfShippers[i].shp_DepotLng;
		//console.log("initialize()::shp-"+i+" shp_depotLat: "+shp_depotLat+" shp_depotLng"+shp_depotLng);
		depotOfShipper[i] = new google.maps.LatLng(shp_depotLat,shp_depotLng);
		routePath[i].getPath().push(depotOfShipper[i]);
		markerOfShipper[i] = new google.maps.Marker({
			position : depotOfShipper[i],
			icon : "http://maps.google.com/mapfiles/ms/micons/motorcycling.png"
		});
		markerOfShipper[i].setMap(map);
	}
	
	//add listener for each markerOfOrder
	for(var i=0; i<lstOrders.length; i++){
		var location = new google.maps.LatLng(lstOrders[i].o_DeliveryLat,lstOrders[i].o_DeliveryLng);
		for(var j=0; j<lstOfShippers.length; j++){
			for(var k=0; k<routeOfShipper[j].length; k++){
				if(routeOfShipper[j][k] == i){
					routePath[j].getPath().push(location);
				}
			}
		}
		markerOfOrder[i] = new google.maps.Marker({position:location});
		markerOfOrder[i].setMap(map);
		markerOfOrder[i].addListener('click',changeRoute);
	}
	for(var i=0; i<nShipper; i++){
		caculateTimeAndDistance(i);
		//$('#route'+i).html(routeOfShipper[i]);
	}
}

//construct date time picker for input time start
function constructDateTimePicker(){
	for(var i=0; i<nShipper; i++){
		$("#inputTimeStartOfShipper"+i).datetimepicker({
			format : "YYYY-MM-DD HH:mm"
		});
	}
}

//add and remove one point of route for each shipper
//var indexOfMarker;
function changeRoute(event){
	var indexOfPath = parseInt($('select#lstShippers').find(":selected").val());
	var indexOfMarker = markerOfOrder.indexOf(this);
	var path = routePath[indexOfPath].getPath();
	var indexOfRemove = path.indexOf(event.latLng);
	if($('#btnRemove').attr("value")==="remove"){
		if(indexOfRemove >= 0){
			path.removeAt(indexOfRemove);
			for(var i=0; i<routeOfShipper[indexOfPath].length; i++){
				if(routeOfShipper[indexOfPath][i]==indexOfMarker){
					routeOfShipper[indexOfPath].splice(i,1);
					i--;
				}
			}
			
		}
	}else{
		var test = 0;
		//check clicked marker is in another route  
		for(var i=0; i<nShipper; i++){
			if((routeOfShipper[i].indexOf(indexOfMarker))>=0){
				test++;
			}
		}
		if(indexOfRemove < 0 && test == 0){
			path.push(event.latLng);	
			routeOfShipper[indexOfPath].push(indexOfMarker);
		}
	}
	caculateTimeAndDistance(indexOfPath);
	//$('#route'+indexOfPath).html(routeOfShipper[indexOfPath]);
}

//caculate time and distance to display table #tblRouteDetail and #tblRouteOfShippers
function caculateTimeAndDistance(indexOfShipper){
	var service = new google.maps.DistanceMatrixService; 
	$('#distance'+indexOfShipper).html("0km");
	$('#time'+indexOfShipper).html("0s");
	//remove all old displayed info of route row
	var rowOfRemove = document.querySelectorAll(".rowOfShipper"+indexOfShipper);
	for(var i=0; i<rowOfRemove.length; i++){
		$(rowOfRemove[i]).remove();
	}
	if(routeOfShipper[indexOfShipper].length>0){
		//caculate time to first client
		var t_SHP_toFirstClient=0;
		service.getDistanceMatrix({
			origins: [depotOfShipper[indexOfShipper]],
		    destinations: [markerOfOrder[routeOfShipper[indexOfShipper][0]].getPosition()],
		    travelMode: google.maps.TravelMode.DRIVING,
		    unitSystem: google.maps.UnitSystem.METRIC,
		    avoidHighways: false,
		    avoidTolls: false
		},function(response,status){
			t_SHP_toFirstClient += response.rows[0].elements[0].duration.value;
			//var timeToFirstClienthms = sendsToHms(t_SHP_toFirstClient);
			var dateTimeStartOfShipper = $('#inputTimeStartOfShipper'+indexOfShipper).val();
			var indexOfCut = dateTimeStartOfShipper.indexOf(" ");
			var timeStartOfShipper = dateTimeStartOfShipper.substring(indexOfCut+1);
			console.log("timeStartOfShipper"+timeStartOfShipper);
			//console.log("indexOfShipper in response function:"+indexOfShipper);
			console.log("time from shipper depot to first client"+t_SHP_toFirstClient);
			var timeToComeThis = plusTime(timeStartOfShipper,t_SHP_toFirstClient);
			//console.log("time to come first client: "+timeToComeThis);
			var newRow = "<tr bgcolor='"+colorOfShipper[indexOfShipper]+"'class='rowOfShipper"+indexOfShipper+"'><td>"+lstOrders[routeOfShipper[indexOfShipper][0]].o_ClientCode+"</td>"+"<td>"+timeToComeThis+"</td>"
					+"<td>"+lstOrders[routeOfShipper[indexOfShipper][0]].o_TimeEarly+"-"+lstOrders[routeOfShipper[indexOfShipper][0]].o_TimeLate+"</td></tr>";
			$('#tblRouteDetail tbody').append(newRow);
		});
		
		if(routeOfShipper[indexOfShipper].length>1){
			var distance = 0;
			var time = 0;
			var indexOfClientInRoute = 0;
			//caculate time to each client and time of total route
			for(var i=1; i<routeOfShipper[indexOfShipper].length; i++){
				
				service.getDistanceMatrix({
					origins: [markerOfOrder[routeOfShipper[indexOfShipper][i-1]].getPosition()],
				    destinations: [markerOfOrder[routeOfShipper[indexOfShipper][i]].getPosition()],
				    travelMode: google.maps.TravelMode.DRIVING,
				    unitSystem: google.maps.UnitSystem.METRIC,
			        avoidHighways: false,
			        avoidTolls: false
				},function(response,status){
					console.log("time from previous to this: "+response.rows[0].elements[0].duration.value);
					indexOfClientInRoute++;
					//caculate time of total route
					//console.log("distance"+distance);
					distance += response.rows[0].elements[0].distance.value;
					time += response.rows[0].elements[0].duration.value;
					$('#distance'+indexOfShipper).html(distance/1000+"km");
					var timeHms = secondsToHms(time);
					$('#time'+indexOfShipper).html(timeHms[0]+"h"+timeHms[1]+"p"+timeHms[2]+"s");
					
					//caculate time to each client
					//t_SHP_toFirstClient += response.rows[0].elements[0].duration.value;
					timeFromDepotToThis = t_SHP_toFirstClient + time;
					//var timeToThisClienthms = secondsToHms(t_SHP_toFirstClient);
					var dateTimeStartOfShipper = $('#inputTimeStartOfShipper'+indexOfShipper).val();
					var indexOfCut = dateTimeStartOfShipper.indexOf(" ");
					var timeStartOfShipper = dateTimeStartOfShipper.substring(indexOfCut+1);
					//console.log("indexOfShipper in response function:"+indexOfShipper);
					//console.log("indexOfClientInRoute"+indexOfClientInRoute);
					var timeToComeThis = plusTime(timeStartOfShipper,timeFromDepotToThis);
					//console.log("time from shipper depot to this client-"+routeOfShipper[indexOfShipper][indexOfClientInRoute]+"): "+timeToComeThis);
					var indexOfClientInList = routeOfShipper[indexOfShipper][indexOfClientInRoute];
					console.log("time from depot to this ("+lstOrders[indexOfClientInList].o_ClientCode+"): " +timeFromDepotToThis);
					var newRow = "<tr bgcolor='"+colorOfShipper[indexOfShipper]+"'class='rowOfShipper"+indexOfShipper+"'><td>"+lstOrders[indexOfClientInList].o_ClientCode+"</td>"+"<td>"+timeToComeThis+"</td>"
							+"<td>"+lstOrders[indexOfClientInList].o_TimeEarly+"-"+lstOrders[indexOfClientInList].o_TimeLate+"</td></tr>";
					$('#tblRouteDetail tbody').append(newRow);
				});
			}
		}	
	}
}

//Reset route of shippers
function btnResetRoute_cf(){
	var indexOfShipper = parseInt($('select#lstShippers').find(":selected").val());
	var path = routePath[indexOfShipper].getPath();
	//alert("path length"+path.length);
	for(var i=0; i<path.length; i++){
		path.removeAt(i);
		i--;
	}
	routeOfShipper[indexOfShipper].splice(0);
	//$('#route'+indexOfShipper).html(routeOfShipper[indexOfShipper]);
	caculateTimeAndDistance(indexOfShipper);
	//alert("path length"+path.length);
}

/*
function openInfowindow(){
	var indexOfMarker = markerOfOrder.indexOf(this);
	var infowindow = new google.maps.InfoWindow({
		content:"Date: "+lstOrders[indexOfMarker].O_DueDate+"<br>Time: "+lstOrders[indexOfMarker].O_TimeLate,
	});
	infowindow.open(map,this);
}*/

function getRandomColor() {
    var letters = '0123456789ABCDEF';
    var color = '#F0';
    for (var i = 0; i < 4; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function plusTime(time,addition){
	console.log("plus time parameter (time: "+time+",addtion:"+addition);
	var indexOfCut = time.indexOf(":");
	var hourOfTime = parseInt(time.substring(0,indexOfCut));
	var minOfTime = parseInt(time.substring(indexOfCut+1));
	var secondTime = hourOfTime*60*60 + minOfTime*60;
	var timeResult = secondTime + addition;
	var timehmsResult = secondsToHms(timeResult);
	var minResult=timehmsResult[1];
	var hourResult=timehmsResult[0];
	if(minResult<10){
		minResult = "0"+minResult;
	}
	if(hourResult<10){
		hourResult = "0"+hourResult;
	}
	console.log("plus time result: "+(hourResult + ":" + minResult));
	return (hourResult + ":" + minResult);
}

/*
function btnRemove_cf(){
	$('#divBtnCancel').html('<button class="btn btn-primary" id="btnCancel" onclick="btnCancel_cf();">Hủy</button>');
	$('#btnRemove').prop('disabled',true);
}

function btnCancel_cf(){
	$('#btnRemove').prop('disabled',false);
	$("#btnCancel").remove();
}*/

//return h[0]:hour, h[1]:min, h[2]:second
function secondsToHms(t){
	//console.log("time pass to sendsToHms"+t);
	var h = [];
	h[0] = Math.floor(t/3600);
	h[1] = Math.floor((t%3600)/60);
	h[2] = Math.floor(t%3600%60);
	//console.log("return result: h-"+h);
	return h;
}

function cf_saveRouteCreated(){
	var data = '{"lstJSONresroute":[';
	for(var i=0; i<lstOfShippers.length;i++){
		data += '{"route_Code" : "' +routeCodeOfShipper[i]+'", ';
		data += '"shipper_Code" : "'+ lstOfShippers[i].shp_Code+'", ';
		data += '"route_Start_Time" : "'+ $('#inputTimeStartOfShipper'+i).val()+'", ';
		data += '"orders_In_Route": [';
		var test=0;
		for(var j=0; j<routeOfShipper[i].length; j++){
			data += '"'+lstOrders[routeOfShipper[i][j]].o_Code + '",';
			test++;
		}
		if(test != 0){
			data = data.substring(0,data.length-1);
		}
		data += ']},';
	}
	data = data.substring(0,data.length-1);
	data += "]}";
	console.log("data"+data);
	var jsonData = JSON.parse(data);
	$.ajax({
		type: 'POST',
		url: baseUrl+'/ship/saveRouteCreated/save',
		data: data,
		contentType: 'application/json',
		success: function(response){
			console.log("success--"+response);
			if(response=="400"){
				window.location = baseUrl + '/';
			}
			if(response=="404"){
				alert("Error");
			}
		},
		error: function(response){
			console.log("error--"+JSON.stringify(response));
		}
	});
}

function cf_confirmRouteCreated(){
	var data = '{"lstJSONresroute":[';
	for(var i=0; i<lstOfShippers.length;i++){
		data += '{"route_Code" : "' +routeCodeOfShipper[i]+'", ';
		data += '"shipper_Code" : "'+ lstOfShippers[i].shp_Code+'", ';
		data += '"route_Start_Time" : "'+ $('#inputTimeStartOfShipper'+i).val()+'", ';
		data += '"orders_In_Route": [';
		var test = 0;
		for(var j=0; j<routeOfShipper[i].length; j++){
			data += '"'+lstOrders[routeOfShipper[i][j]].o_Code + '",';
			test++;
		}
		if(test != 0){
			data = data.substring(0,data.length-1);
		}
		data += ']},';
	}
	data = data.substring(0,data.length-1);
	data += "]}";
	console.log("data"+data);
	var jsonData = JSON.parse(data);
	$.ajax({
		type: 'POST',
		url: baseUrl+'/ship/saveRouteCreated/confirm',
		data: data,
		contentType: 'application/json',
		success: function(response){
			console.log("success--"+response);
			if(response=="400"){
				window.location = baseUrl+'/';
			}
			if(response=="404"){
				alert("Error");
			}
		},
		error: function(response){
			console.log("error--"+JSON.stringify(response));
		}
	});
}
</script>