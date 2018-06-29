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
			<h1 class="page-header">Xem các tuyến đường đã tạo</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
	<div id="googleMap" style="width:100%; height:100%; margin-bottom:10px";></div>
	
	<div class="row">
		<div class="col-sm-3">
			<div class="form-group">
				<select class="form-control" id="select-listProvince">
					<c:forEach items="${lstProvinces}" var="province">
						<option value="${province.PROV_Code}"><c:out value="${province.PROV_Name}"/></option>
					</c:forEach>
				</select>
			</div>
			<!-- /.form-group -->
		</div>
		<!-- /.col-sm-3 -->
		<div class="col-sm-offset-8 col-sm-1">
			<button class="btn btn-primary" id="btn-viewStreets">Xem</button>
		</div>
		<!-- /.col-sm-1 -->
	</div>
	<!-- /.row -->
	
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="dataTable_wrapper">
				<table class="table table-striped table-bordered table-hover" id="tbl-listStreets">
					<thead>
						<tr>
							<th>Tên đường</th>
							<th>Loại đường</th>
							<th>Tốc độ tối đa</th>
							<th><input type="checkbox" onClick="checkAll(this)"/></th>
							<th>Tỉnh thành</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${lstRoads}" var="road">
							<tr>
								<td><c:out value="${road.roadName}"/></td>
								<td><c:out value="${road.roadTypeCode}"/></td>
								<td><c:out value="${road.roadMaxSpeed}"/></td>
								<td><input type="checkbox" name="checkStreet" onchange="addStreets('${road.roadCode}',this)"/>
										<button class="btn btn-warning btn-xs" onclick="removeRoad('${road.roadCode}')">Xóa</button></td>
								<td><c:out value="${road.roadProvince}"/></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<!-- /#dataTable_warpper -->
		</div>
		<!-- /.panel-body -->
	</div>
	<!-- /.panel -->
</div>
<!-- /#page-wrapper -->

<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAruL-HLFSNh6G2MLhjS-eBTea7r7EFa5A&libraries=places&callback=initialize" async defer></script>
<script>
var map;
var roads = ${jsonRoads};
//var colorInit=["#B0171F","#FF1493","#8B5F65","#000080","#006400","#a52a2a","#ff0000","#ff1493","#9400d3"];
//console.log(JSON.stringify(roads));
var roadViewed=[];
function initialize(){
	var mapProp = {
		center : {lat:21.03333, lng: 105.849998},
		zoom : 12,
		mapTypeIDd : google.maps.MapTypeId.ROADMAP
	}
	map = new google.maps.Map(document.getElementById("googleMap"),mapProp);
}

function removeRoad(roadCode){
	console.log(roadCode);
	$.ajax({
		type : 'POST',
		url : baseUrl + "/mapstreetmanipulation/removeRoad",
		data : roadCode,
		contentType : 'application/text',
		success : function (response){
			alert("ok");
			window.location = baseUrl + "/mapstreetmanipulation/viewStreets";
		}
	});
}

function addStreets(roadCode,elem){
	var indexRoad = roadViewed.indexOf(roadCode);
	if(elem.checked){
		if(indexRoad == -1){
			roadViewed.push(roadCode);
		}
	}else{
		if(indexRoad != -1){
			roadViewed.splice(indexRoad,1);
		}
	}
}

function checkAll(elem){
	var checkBoxes = document.getElementsByName('checkStreet');
	if(elem.checked){
		for(var i=0; i<roads.length; i++){
			if(roadViewed.indexOf(roads[i].RoadCode) == -1){
				roadViewed.push(roads[i].RoadCode);
			}	
		}	
	}else{
		for(var i=0; i<roads.length; i++){
			var test = roadViewed.indexOf(roads[i].RoadCode);
			if( test != -1){
				roadViewed.splice(test,1);
			}	
		}
	}
	
	for(var i=0; i<checkBoxes.length; i++){
		checkBoxes[i].checked = elem.checked;
	}
}


$(document).ready(function(){
	$.fn.dataTable.ext.search.push(function(settings,data,dataInde){
		var province = $("#select-listProvince").val();
		if(province == data[4]){
			return true;
		}
		return false;
	});
	
	var table = $('#tbl-listStreets').DataTable({
		"columnDefs":[{
			"targets":[4],
			"visible":false,
		}],
	});
	
	$("#select-listProvince").click(function(){
		table.draw();
		initialize();
	})
	$('#btn-viewStreets').click(function(){
		initialize();
		for(var i=0; i<roadViewed.length; i++){
			for(var j=0; j<roads.length; j++){
				if(roads[j].RoadCode==roadViewed[i]){
					var points = roads[j].RoadPoints.split(":");
					var street = new google.maps.Polyline({
						strokeColor : "#FF0000",
						strokeOpacity : 1.0,
						strokeWeight : 3
					});
					street.setMap(map);
					for(var k=0; k<points.length; k++){
						//var color = colorInit[(i+2)%colorInit.length];
						var indexCut = points[k].indexOf(",");
						var lat = parseFloat(points[k].substring(0,indexCut));
						var lng = parseFloat(points[k].substring(indexCut+1,points[k].length));
						var point = new google.maps.LatLng(lat,lng);
						var marker = new google.maps.Marker({
							map:map,
							position : point,
							icon:"https://www.google.com/mapfiles/marker_green.png"
						})
						street.getPath().push(point);
					}	
					
				}	
			}
		}
	})
})
</script>
