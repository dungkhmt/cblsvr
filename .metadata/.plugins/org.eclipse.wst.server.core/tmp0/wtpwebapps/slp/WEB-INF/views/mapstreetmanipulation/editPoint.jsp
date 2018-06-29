<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Sửa tuyến đường đã tạo</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
	<div id="googleMap" style="width:100%;height:100%;margin-bottom:10px;"></div>
	
	<div class="row">
		<div class="col-sm-4">
			<div class="form-group">
				<select class="form-control" id="select-listStreet">
					<option>Chọn tên đường</option>
				</select>
			</div>
		</div>
		<div class="col-sm-3">
			<div class="form-group">
				<select class="form-control" id="select-listProvince">
					<option>Chọn Tỉnh/Thành Phố</option>
					<c:forEach items="${lstProvinces}" var="province">
						<option value="${province.PROV_Code}"><c:out value="${province.PROV_Name}"/></option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="col-sm-1">
			<button class="btn btn-primary" id="btn-saveRoad">Lưu</button>
		</div>
	</div>
	<div class="row" style="margin-top:10px">
		<div class="form-group">
			<label class="control-label col-sm-2">Tên đường</label>
			<div class="col-sm-4">
				<input id="roadName" class="form-control"/>
			</div>
			<label class="control-label col-sm-2">Kiểu đường</label>
			<div class="col-sm-4">
				<select class="form-control" id="sel-roadType">
					<c:forEach items="${lstRoadType}" var="roadType">
						<option value="${roadType.roadTypeCode}">${roadType.roadTypeName}</option>
					</c:forEach>
				</select>
			</div>
		</div>
	</div>
	<div class="row" style="margin-top:10px">
		<div class="form-group">
			<label class="control-label col-sm-2">Hai chiều/Một chiều</label>
			<div class="col-sm-4">
				<select class="form-control" id="sel-direction">
					<option value="DIRECTIONAL">Một chiều</option>
					<option value="BIDIRECTIONAL">Hai chiều</option>
				</select>
			</div>
			<label class="control-label col-sm-2"> Vận tốc</label>		
			<div class="col-sm-4">
				<input id="maxSpeed" class="form-control"/>
			</div>
		</div>
	</div>
	<div class="row" style="margin-top:10px">
		<div class="form-group">
			<label class="control-label col-sm-2">Tỉnh/Thành Phố</label>
			<div class="col-sm-4">
				<select class="form-control" id="sel-province">
					<c:forEach items="${lstProvinces}" var="province">
						<option value="${province.PROV_Code}"><c:out value="${province.PROV_Name}"/></option>
					</c:forEach>
				</select>
			</div>
			<label class="control-label col-sm-2">Các tỉnh đi qua</label>
			<div class="col-sm-4">
				<input class="form-control" id="interProvince"/>
			</div>	
		</div>
	</div>
</div>

<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAruL-HLFSNh6G2MLhjS-eBTea7r7EFa5A&libraries=places&callback=initialize" async defer></script>
<script>
var map;
var dataResponse;
//var listPoint=[];
var road;
var listMarker = [];
function initialize() {
	//construct google map
	var mapProp = {
		center: {lat: 21.033333, lng: 105.849998},
		zoom: 12,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
	road = new google.maps.Polyline({
		strokeColor: '#FF0000',
	    strokeOpacity: 1.0,
	    strokeWeight: 3,
	    draggable: true
	});
	road.setMap(map);
	map.addListener('click', function(event){
		var pos = event.latLng;
		console.log("position clicked: ("+pos.lat()+", "+pos.lng()+")");
		var markerPoint = new google.maps.Marker({
			map:map,
			position:pos,
			draggable: true,
			icon:"https://www.google.com/mapfiles/marker_green.png"
		});
		var path = road.getPath();
		if(path.length <= 1){
			//console.log("path length == 0");
			road.getPath().push(pos);
			listMarker.push(markerPoint);
		}else{
			//console.log("path: "+JSON.stringify(path));
			//console.log("path length: "+path.length);
			var indexMax=0;
			var max = -10000000;
			for(var i=0; i<path.length-1; i++){
				var latlngA = path.getAt(i);
				var latlngB = path.getAt(i+1);
				//console.log("latlngA["+i+"]: "+JSON.stringify(latlngA));
				//console.log("latlngB["+i+"]: "+JSON.stringify(latlngB));
				var tmp = angleABC(latlngA.lat(), latlngA.lng(), pos.lat(), pos.lng(), latlngB.lat(), latlngB.lng());
				//console.log("goc giua "+i+" va "+(i+1)+": "+tmp);
				if(tmp > max ){
					max = tmp;
					indexMax = i+1;
				}
				//console.log("latlng["+i+"]:"+JSON.stringify(latlng));
			}
			if(max < (Math.PI)/2){
				//console.log("max < pi/2 --- max="+max+" ---- index="+indexMax);
				var latlng0 = path.getAt(0);
				var latlngN = path.getAt(path.length-1);
				var distancePosTo0 = distance(latlng0.lat(),latlng0.lng(),pos.lat(),pos.lng());
				var distancePosToN = distance(latlngN.lat(),latlngN.lng(),pos.lat(),pos.lng());
				if(distancePosTo0 > distancePosToN){
					path.push(pos);
					listMarker.push(markerPoint);
				}else{
					path.insertAt(0,pos);
					listMarker.splice(0,0,markerPoint);
				}
			}else{
				//console.log("index of point insert: "+ indexMax);
				path.insertAt(indexMax,pos);
				listMarker.splice(indexMax,0,markerPoint);
				//console.log("path : "+JSON.stringify(path));
					
			}
		}
		//road.getPath().push(event.latLng);
		markerPoint.addListener('click',function(){
			this.setMap(null);
			var indexMarker = listMarker.indexOf(this);
			listMarker.splice(indexMarker,1);
			var index = road.getPath().indexOf(this.getPosition());
			road.getPath().removeAt(index);
		});
		markerPoint.addListener('dragend',handleEventDrag);
		//markerPoint.addListener('dragEnd',handleEventDrag);
	});
}

function handleEventDrag(event){
	//console.log("start dragEnd function");
	var indexMarker = listMarker.indexOf(this);
	road.getPath().removeAt(indexMarker);
	road.getPath().insertAt(indexMarker,this.getPosition());
}


$(document).ready(function(){
	$('#select-listProvince').change(function(){
		var province = $(this).val();
		//console.log("province code"+province);
		$.ajax({
			type: 'GET',
			url: baseUrl + "/mapstreetmanipulation/getListStreetName/"+province,
			contentType: 'application/json',
			success: function(response){
				//console.log("response:"+JSON.stringify(response));
				dataResponse = response;
				for(var i=0; i<response.length; i++){
					//console.log("response["+i+"].RoadCode"+response[i].roadCode);
					//console.log("response["+i+"].RoadName"+response[i].roadName);
					$('#select-listStreet').append($('<option>', {
					    value: response[i].roadCode,
					    text: response[i].roadName
					}));
				}
			}
		});
	});
	
	
	$('#select-listStreet').change(function(){
		initialize();
		var street = $(this).val();
		for(var i=0; i < dataResponse.length; i++){
			if(dataResponse[i].roadCode==street){
				//roadCodeSelected = dataResponse[i].RoadCode;
				var roadName = dataResponse[i].roadName;
				var roadTypeCode = dataResponse[i].roadTypeCode;
				var roadDirection = dataResponse[i].roadBidirectional;
				var roadMaxSpeed = dataResponse[i].roadMaxSpeed;
				var provinceCode = dataResponse[i].roadProvince;
				var interProvince = dataResponse[i].roadInterProvince;
				$('#roadName').val(roadName);
				$('#sel-roadType').val(roadTypeCode);
				$('#sel-direction').val(roadDirection);
				$('#maxSpeed').val(roadMaxSpeed);
				$('#sel-province').val(provinceCode);
				$('#interProvince').val(interProvince);
				
				var roadPoints = dataResponse[i].roadPoints;
				console.log("roadPoints length = "+roadPoints.length);
				if(roadPoints.length == 0){
					break;
				}
				var roadPointLatLngs = roadPoints.split(":");
				//console.log("roadPointLatLngs"+roadPointLatLngs+" length="+roadPointLatLngs.length);
				for(var j=0; j<roadPointLatLngs.length; j++){
					//console.log("roadPointLatLngs["+j+"]"+roadPointLatLngs[j]);
					var index = roadPointLatLngs[j].indexOf(",");
					var lat = parseFloat(roadPointLatLngs[j].substring(0,index));
					var lng = parseFloat(roadPointLatLngs[j].substring(index+1,roadPointLatLngs[j].length));
					var point = new google.maps.LatLng(lat,lng);
					//console.log("point["+j+"]"+JSON.stringify(point));
					var markerPoint = new google.maps.Marker({
						map:map,
						position:point,
						draggable : true,
						icon:"https://www.google.com/mapfiles/marker_green.png"
					});
					if(j==0){
						console.log("j==0");
						map.setCenter({
							lat: parseFloat(lat),
							lng: parseFloat(lng)
						});
						map.setZoom(15);
					}
					road.getPath().push(point);
					listMarker.push(markerPoint);
					markerPoint.addListener('click',function(){
						this.setMap(null);
						var indexMarker = listMarker.indexOf(this);
						listMarker.splice(indexMarker, 1);
						var index = road.getPath().indexOf(this.getPosition());
						road.getPath().removeAt(index);
					});
					//markerPoint.addListener('drag',handleEventDrag);
					markerPoint.addListener('dragend',handleEventDrag);
				}
				break;
			}
		}
	});
	$('#btn-saveRoad').click(function(){
		var roadCodeSelected = $('#select-listStreet').val();
		var path = road.getPath();
		var dataPreSend=[];
		console.log("path pre send: "+JSON.stringify(path));
		for(var i=0; i<path.length; i++){
			var latlng = path.getAt(i);
			var tmp = latlng.lat()+", "+latlng.lng();
			dataPreSend.push(tmp);
		}
		console.log("dataPreSend: "+dataPreSend);
		dataSend = dataPreSend.join(":");
		console.log("dataSend when click button: "+dataSend);
		var data = {
				"RoadID" : 1,
				"RoadCode" : roadCodeSelected ,
				"RoadName" : $('#roadName').val(),
				"RoadProvince" : $('#sel-province').val(),
				"RoadInterProvince" : $('#interProvince').val(),
				"RoadPoints" : dataSend,
				"RoadTypeCode" : $('#sel-roadType').val(),
				"RoadBidirectional" : $('#sel-direction').val(),
				"RoadMaxSpeed" : $('#maxSpeed').val(),
				"RoadCreateUserID" : "",
				"RoadCreateDateTime" : "",
				"RoadStatus" : ""
		}
		data = JSON.stringify(data);
		$.ajax({
			type: 'POST',
			url : baseUrl + "/mapstreetmanipulation/updateRoad",
			data: data,
			contentType: 'application/json',
			success: function(response){
				alert("ok");
				window.location = baseUrl + "/mapstreetmanipulation/editPoint";
			}
		})
	});
});
/*
function angle(x1,y1,x2,y2){
	var dx = x1 - x2;
	var dy = y1 - y2;
	if(Math.abs(dx) < 0.0000001){
		if(dy > 0) return Math.PI/2 + Math.PI/2+Math.PI;
		else return Math.PI/2;
	}
	var angle = Math.atan(Math.abs(dy)/Math.abs(dx));
	if(dx > 0){
		if(dy < 0) return Math.PI-angle;
		else if(dy > 0) return Math.PI + angle;
		else return Math.PI;
	}else{
		if(dy < 0) return angle;
		else if(dy > 0) return 2*Math.PI - angle;
		else return 0;
	}
}

function angleABC(XA,YA,XB,YB,XC,YC){
	//console.log("angleABC("+XA+", "+YA+", "+XB+", "+YB+", "+XC+", "+YC+")")
	var a = angle(XB,YB,XA,YA);
	//console.log("a="+a);
	var c = angle(XB,YB,XC,YC);
	//console.log("c="+c);
	var r = a-c;
	while(r < 0) r += 2*Math.PI;
	while(r > 2*Math.PI) r -= 2*Math.PI;
	if(r > Math.PI) r = r - Math.PI;
	//console.log("r="+r);
	return r;
}*/

function distance(x1,y1,x2,y2){
	return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));	
}

function angleABC(XA,YA,XB,YB,XC,YC){
	var AB = distance(XA,YA,XB,YB);
	var BC = distance(XB,YB,XC,YC);
	var AC = distance(XA,YA,XC,YC);
	var cosABC = (Math.pow(AB,2) + Math.pow(BC,2) - Math.pow(AC,2))/(2*AB*BC);
	return Math.acos(cosABC);
}
</script>