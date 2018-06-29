<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- DataTables CSS -->
<link href="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.css"/>" rel="stylesheet">
<!-- DataTables Responsive CSS -->
<link href="<c:url value="/assets/libs/datatables-responsive/css/dataTables.responsive.css" />" rel="stylesheet">

<!-- DataTables JavaScript -->
<script src="<c:url value="/assets/libs/datatables/media/js/jquery.dataTables.js"/>"></script>
<script src="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.js"/>"></script>

<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Tìm điểm giao cắt</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
	<div id="googleMap" style="width:100%; height:100%; margin-bottom:10px;"></div>
	<div class="row">
		<div class="col-sm-offset-11 col-sm-1">
			<button class="btn btn-primary" style="margin-bottom:10px;" onclick="findIntersections()">Tìm</button>
		</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="dataTable_wrapper">
				<table class="table table-striped table-bordered table-hove" id="tbl-listStreeets">
					<thead>
						<tr>
							<th>Tên đường</th>
							<th>Loại đường</th>
							<th>Tốc độ tối đa</th>
							<th><input type="checkbox" onClick="checkAll(this)" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${listRoads}" var="road">
							<tr>
								<td><c:out value="${road.roadName}"/></td>
								<td><c:out value="${road.roadTypeCode }"/></td>
								<td><c:out value="${road.roadMaxSpeed }"/></td>
								<td><input type="checkbox" name="checkStreet" onchange="addStreets('${road.roadCode}',this)"></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAruL-HLFSNh6G2MLhjS-eBTea7r7EFa5A&libraries=places&callback=initialize" async defer></script>
<script>
var map;
var roadSelected = [];
var roads = ${jsonRoads};
var segments = ${jsonRoadSegments};
var points = ${jsonRoadPoints};
var listMarker = [];
var listSegment = [];
var mapPoint2Segment = {};
var mapID2SegmentCode = {};
var mapID2PointCode ={};
var markerClicked;

var start = null;
var end = null;
function initialize(){
	var mapProp = {
		center : {lat:21.03333, lng: 105.849998},
		zoom : 12,
		mapTypeIDd : google.maps.MapTypeId.ROADMAP
	}
	map = new google.maps.Map(document.getElementById("googleMap"),mapProp);
	google.maps.event.addListener(map,'click',function(event){
		//alert("click");
		var img = new google.maps.MarkerImage('ccmarker.png');
		var marker = new google.maps.Marker({
			'map':map,
			'position': event.latLng,
			//'icon': img,
			'visible': true,
			
		});
		google.maps.event.addListener(marker,'click',function(event){
			if(start == marker)
				start = null;
			else if(end == marker) end = null;
			
			marker.setMap(null);
		});
		
		if(start == null){
			start = marker;
		}else{
			end = marker;
			
			json = "{\"source\":\"" + start.position + "\","  +
				"\"destination\":\"" + end.position + "\"}";
			
				//alert("json = " + json);
			// ajax for direction here
			$.ajax({
				type : 'POST',
				url : baseUrl + '/mapstreetmanipulation/direction',
				data : "info="+json,
				cache: false,
				//contentType : 'application/text',
				success: function(response){
					//alert("response = " + response);
					//window.location = baseUrl + "/mapstreetmanipulation/direction";
					var obj = jQuery.parseJSON(response);
					var src_lat = obj.source.lat;
					var src_lng = obj.source.lng;
					var dest_lat = obj.destination.lat;
					var dest_lng = obj.destination.lng;
					
					//alert(response + ":" + src + "----" + dest);
					var marker_src = new google.maps.Marker({
						'map':map,
						'position': new google.maps.LatLng(src_lat,src_lng),
						//'icon': img,
						'visible': false,
					});
					var marker_dest = new google.maps.Marker({
						'map':map,
						'position': new google.maps.LatLng(dest_lat, dest_lng),
						//'icon': img,
						'visible': false,
						
					});
					
					alert("path.length = " + obj.points.length);
					
					var coordinate = new Array();
					for(i = 0; i < obj.points.length; i++){
						coordinate.push(new google.maps.LatLng(obj.points[i].lat,obj.points[i].lng));
					}
					var polyline = new google.maps.Polyline({
						path: coordinate,
						strokeColor: '#FFFF00',
					    strokeOpacity: 1.0,
					    strokeWeight: 6,
					    //'ID': allPolylines.length
					});
					polyline.setMap(map);	
				}
			});
		}
	});
	
	
	var checkIntersectPoint = [];
	for(var i=0; i<points.length; i++){
		checkIntersectPoint[i] = 0;
	}
	for(var i=0; i<segments.length; i++){
		var fromPointCode = segments[i].RSEG_FromPoint;
		var toPointCode = segments[i].RSEG_ToPoint;
		var fromPointLatLng = null;
		var toPointLatLng = null;
		var indexFromPoint=-1;
		var indexToPoint=-1;
		for(var j=0; j<points.length; j++){
			if(fromPointCode == points[j].RP_Code){
				fromPointLatLng = points[j].RP_LatLng;
				checkIntersectPoint[j]++;
				indexFromPoint = j;
			}
			if(toPointCode == points[j].RP_Code){
				toPointLatLng = points[j].RP_LatLng;
				checkIntersectPoint[j]++;
				indexToPoint = j;
			}
			if(fromPointLatLng != null && toPointLatLng != null){
				break;
			}
		}
		var indexCutFromPoint = fromPointLatLng.indexOf(',');
		var indexCutToPoint = toPointLatLng.indexOf(',');
		var fromPointLat = fromPointLatLng.substring(0,indexCutFromPoint);
		var fromPointLng = fromPointLatLng.substring(indexCutFromPoint+1,fromPointLatLng.length);
		var toPointLat = toPointLatLng.substring(0,indexCutToPoint);
		var toPointLng = toPointLatLng.substring(indexCutToPoint+1,toPointLatLng.length);
		
		var polyLine;
		if(segments[i].RSEG_Bidirectional == "DIRECTIONAL"){
			var lineSymbol = {
				path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
				strokeOpacity: 2,
				scale: 1.5
			}
			
			polyLine = new google.maps.Polyline({
				strokeColor: '#FF0000',
				strokeOpacity : 1.0,
				strokeWeight :3,
				path : [new google.maps.LatLng(fromPointLat,fromPointLng), new google.maps.LatLng(toPointLat,toPointLng)],
				icons: [{
					icon: lineSymbol,
					offset: '100%',
					repeat: '200px'
				}]
			});
			
		}else{
			polyLine = new google.maps.Polyline({
				strokeColor: '#FF0000',
				strokeOpacity : 1.0,
				strokeWeight :3,
				path : [new google.maps.LatLng(fromPointLat,fromPointLng), new google.maps.LatLng(toPointLat,toPointLng)]
			})
		}
		polyLine.setMap(map);
		listSegment.push(polyLine);
		mapID2SegmentCode[i]=segments[i].RSEG_Code;
		//mapPoint2Segment[new google.maps.LatLng(fromPointLat,fromPointLng)]=segments[i].RSEG_Code;
		//mapPoint2Segment[new google.maps.LatLng(toPointLat,toPointLng)]=segments[i].RSEG_Code;
		
		/*
		 * check intersect point to set color to marker of point	
		*/
		mapPoint2Segment[indexFromPoint] = i;
		mapPoint2Segment[indexToPoint] = i;
		if(listMarker[indexFromPoint] && listMarker[indexFromPoint].setMap){
			listMarker[indexFromPoint].setMap(null);
		}
		if(listMarker[indexToPoint] && listMarker[indexToPoint].setMap){
			listMarker[indexToPoint].setMap(null);
		}
		
		var markerFromPoint;
		if(checkIntersectPoint[indexFromPoint] >=3 ){
			markerFromPoint = new google.maps.Marker({
				map : map,
				position : new google.maps.LatLng(fromPointLat,fromPointLng),
				icon : baseUrl + "/assets/icon/oval_green.png",
				//draggable: true
			});
			//listMarker[indexFromPoint].addListener('dragend',handleEventDrag);
		}else if(checkIntersectPoint[indexFromPoint] == 1){
			markerFromPoint = new google.maps.Marker({
				map : map,
				position : new google.maps.LatLng(fromPointLat,fromPointLng),
				icon : baseUrl + "/assets/icon/oval_blue.png",
				draggable: true
			});
			markerFromPoint.addListener('dragend',handleEventDrag);
		}else{
			markerFromPoint = new google.maps.Marker({
				map : map,
				position : new google.maps.LatLng(fromPointLat,fromPointLng),
				icon : baseUrl + "/assets/icon/oval_blue.png",
				//draggable: true
			});
		}
		mapID2PointCode[indexFromPoint] = fromPointCode;
		listMarker[indexFromPoint] = markerFromPoint;
		//mapPoint2Segment[markerFromPoint] = segments[i].RSEG_Code;
		/*listMarker[indexFromPoint].addListener('click',function(){
			markerClicked = this;
		});*/
		
		var markerToPoint;
		if(checkIntersectPoint[indexToPoint] >=3 ){
			markerToPoint = new google.maps.Marker({
				map : map,
				position : new google.maps.LatLng(toPointLat,toPointLat),
				icon : baseUrl + "/assets/icon/oval_green.png",
				//draggable: true
			});
		}else if(checkIntersectPoint[indexToPoint] == 1){
			markerToPoint = new google.maps.Marker({
				map : map,
				position : new google.maps.LatLng(toPointLat,toPointLng),
				icon : baseUrl + "/assets/icon/oval_blue.png",
				draggable: true
			});
			markerToPoint.addListener('dragend',handleEventDrag);
		}else{
			markerToPoint = new google.maps.Marker({
				map : map,
				position : new google.maps.LatLng(toPointLat,toPointLat),
				icon : baseUrl + "/assets/icon/oval_blue.png",
				//draggable: true
			});
		}
		listMarker[indexToPoint] = markerToPoint;
		mapID2PointCode[indexToPoint] = toPointCode;
	}
}

function handleEventDrag(event){
	//var segmentCode = mapPoint2Segment[this];
	var indexMarker = listMarker.indexOf(this);
	console.log("handleEventDrag::indexMarker "+indexMarker);
	var originPos = listMarker[indexMarker].getPosition();
	var segmentID = mapPoint2Segment[indexMarker];
	var pointCode = mapID2PointCode[indexMarker];
	console.log("handleEventDrag::pointCode "+pointCode);
	//console.log("this "+this);
	var segment = listSegment[segmentID];
	//console.log("segment "+segment);
	var segmentCode = mapID2SegmentCode[segmentID];
	console.log("segment code"+segmentCode, "  point Code"+pointCode);
	var dis0 = distanceE(segment.getPath().getAt(0),originPos);
	var dis1 = distanceE(segment.getPath().getAt(1),originPos);
	if(dis0 < dis1){
		segment.getPath().removeAt(0);
		segment.getPath().insertAt(0,this.getPosition());
	}else{
		segment.getPath().removeAt(1);
		segment.getPath().insertAt(1,this.getPosition());
	}
	console.log("this.getPosition = "+JSON.stringify(this.getPosition()));
	console.log("lat: "+event.latLng.lat()+"  lng: "+event.latLng.lng())
	
	var infowindow = new google.maps.InfoWindow({
	    content: '<button class="btn btn-primary" onclick="findintersectsegment('+segmentCode+','+pointCode+','+event.latLng.lat()+','+event.latLng.lng()+')">Tìm</button>'
	});
	infowindow.open(map,this);
	//console.log("segment path "+JSON.stringify(segment.getPath()));
	//segment.getPath().removeAt(1);
	//segment.getPath().insertAt(1,this.getPosition);
}

function findintersectsegment(segmentCode,pointCode,pointLat,pointLng){
	var dataSend = segmentCode + "; "+pointCode+"; "+ pointLat+", "+pointLng;
	$.ajax({
		type : 'POST',
		url : baseUrl + '/mapstreetmanipulation/findIntersectionSegment',
		data : dataSend,
		contentType : 'application/text',
		success: function(response){
			alert("ok");
			window.location = baseUrl + "/mapstreetmanipulation/findIntersectionPoints";
		}
	});
}

function distanceE(x,y){
	return Math.sqrt(Math.pow(x.lat()-y.lat(),2)+Math.pow(x.lng()-y.lng(),2));
}

function addStreets(roadCode,elem){
	var indexRoad = roadSelected.indexOf(roadCode);
	if(elem.checked){
		if(indexRoad == -1){
			roadSelected.push(roadCode);
		}
	}else{
		if(indexRoad != -1){
			roadSelected.splice(indexRoad,1);
		}
	}
}

function checkAll(elem){
	var checkBoxes = document.getElementsByName('checkStreet');
	if(elem.checked){
		for(var i=0; i<roads.length; i++){
			if(roadSelected.indexOf(roads[i].RoadCode) == -1){
				roadSelected.push(roads[i].RoadCode);
			}
		}
	}else{
		for(var i=0; i<roads.length; i++){
			var test = roadSelected.indexOf(roads[i].RoadCode);
			if( test != -1){
				roadSelected.splice(test,1);
			}	
		}
	}
	
	for(var i=0; i<checkBoxes.length; i++){
		checkBoxes[i].checked = elem.checked;
	}
}

function findIntersections(){
	dataSend = roadSelected.join(";");
	$.ajax({
		type : 'POST',
		url : baseUrl + '/mapstreetmanipulation/findAndSaveIntersectionPoints',
		data : dataSend,
		contentType : 'application/text',
		success: function(response){
			alert("ok");
			window.location = baseUrl + "/mapstreetmanipulation/findIntersectionPoints";
		}
	});
}
</script>