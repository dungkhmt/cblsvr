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

<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/moment.js" />"></script>
<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Kết quả</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<div id="map" style="height:100%">
    </div>
    <div class="row" style="margin-top:10px; margin-bottom:10px">
	  	<div class="col-sm-1">
						<button class="btn btn-primary" onclick="loadMap()">View Routes</button>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				
				<div class="panel-body">
					<div class="dataTable_wrapper">
						<table class="table table-striped table-bordered table-hover" id="table-pDL">
							<thead>
								<tr>
									<th>Mã</th>
									<th>Địa điểm đón</th>
									<th>Địa điểm trả</th>
									<th>Khoảng cách đến điểm tiếp theo</th>
									<th>Check <br> <input type='checkbox' id="bozz" onchange='checkedAll()' checked> </th>
								</tr>
							</thead>	
							<tbody>
							</tbody>						
						</table>
					</div>
					<!--/.dataTable_wrapper -->
				</div>
				<!-- /.panel-body -->
			</div>
			<!-- /.panel -->
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
</div>
<!-- /#page-wrapper -->

<script>
var map;
var checkedList=[];
var directionsService;
var table;
var data;
var pathList=[];// danh sach cac poliline
var markerList=[]; // danh sach cac marker
var indexRowTable=[0];
var colorInit=["#F7786B","#91A8D0","#91A8D0","#034F84","#FAE03C","#98DDDE","#9896A4","#DD4132","#B18F6A","#79C753","#B93A32","#AD5D5D","#006E51","#B76BA3","#5C7148","#D13076"]; // mang init mau
var serviceDistance;

function checkedAll(){
	if($('#bozz').is(':checked')) {
		for(var i=0;i<checkedList.length;i++ )
			checkedList[i]=1;
		$('#client').prop('checked', true);
		console.log('here');
	} else {
		for(var i=0;i<checkedList.length;i++ )
			checkedList[i]=0;
		$('#client').prop('checked', false);
	}
}

function viewRoute(){
	for(var i=0;i<pathList.length;i++)
		pathList[i].setMap(null);
	for(var i=0;i<markerList.length;i++ )
		markerList[i].setMap(null);
	for(var i=0;i<checkedList.length;i++){
		list=data[checkedList[i]].listPoint;
		for(var j=0;j<list.length;j++){
			list[j].marker.setMap(map);
			if(list[j].marker.path!=-1)
				pathList[list[j].marker.path].setMap(map);
		}
	}
		
}

function loadTable(data){
	table = $("#table-pDL").DataTable({
		"bSort" : false
	});
	//console.log("data: "+data);
	var data=data["routes"];
	//console.log(table);
	$("table#table-pDL tbody").html("");
	gray="#F0F0F0";
	white="#FFFFFF";
	var color=["#F0F0F0","#FFFFFF"];
	var idcolor=0;
	str=null;
	count=0;
	indexRowTable[0]=count;
	
	for(var i=0;i<data.length;i++){
		idcolor=(idcolor+1) % color.length;
		var totalDistance=0;
		var list=data[i].route;
		indexRowTable[i]=count;
		var pickupCount=-1;
		for(var j=0;j<list.length;j++){
			totalDistance+=list[j].distance2Next;
			if(list[j].action=="PICKUP") {
				pickupCount++;
				pickupAddress=list[j].address;
				deliveryAddress="--";
			} else {
				pickupAddress="--";
				deliveryAddress=list[j].address;
			}
			count++;
			if(j==0){
				var rowNode = table.row.add([
			         					list[j].code,
			         					pickupAddress,
			         				    deliveryAddress,
			         				   list[j].distance2Next,
			         				    "<input type='checkbox' id='client' onchange='updateCheckList("+i+", this)' checked>"
			         				]).draw().node();
				$(rowNode).css('background-color',color[idcolor]);
			} else {
					var rowNode = table.row.add([
					         					list[j].code,
					         					pickupAddress,
					         				    deliveryAddress,
					         				   list[j].distance2Next,
					         				   ""
					         				]).draw().node();
					$(rowNode).css('background-color',color[idcolor]);
			}
		}
		var rowNode = table.row.add([
		         					"",
		         					"Tổng số request",
		         					pickupCount,
		           				    "Tổng khoảng cách:"+totalDistance,
			         				""
			         				]).draw().node();
		$(rowNode).css('background-color','gray');
	}
}
function randomColor( len){
		var colorArr=[];
		for(i=0;i<len;){
			var ii=Math.floor((Math.random() * colorInit.length));
			var xd=false;
			for(j=0;j<colorArr.length;j++)
				if(colorArr[j]==ii) xd=true;
			if(xd==false){
				colorArr.push(ii);
				i++;
			}
		}
		var colorThis=[];
		for(i=0;i<len;i++){
			colorThis.push(colorInit[colorArr[i]]);
		}
		return colorThis;
	}
	
function updateCheckList(t,z){
	if(checkedList[t]==1) checkedList[t]=0;
	else checkedList[t]=1;
}
function loadMap(){
	var data2=data["routes"];
	for(var i=0;i<data2.length;i++)
		if(checkedList[i]==0){
			var list= data2[i].route;
			for(var j=0;j<list.length;j++){
				list[j].marker.setMap(null);
			}
			data2[i].poliline.setMap(null);
		} else {
			var list= data2[i].route;
			for(var j=0;j<list.length;j++){
				list[j].marker.setMap(map);
			}
			data2[i].poliline.setMap(map);
		}
}
function viewMap(){
	
	var data2=data["routes"];
	console.log("viewMap");
	var route;
	var lineSymbol = {
			path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
			strokeOpacity: 2,
			scale: 1.5
		}
	var xd=false;
	var colorLine=randomColor(data2.length);
	for(var i=0;i<data2.length;i++)
		if(checkedList[i]==1){
		var list= data2[i].route;
		route = new google.maps.Polyline({
			strokeColor: colorLine[i],
		    strokeOpacity: 1.0,
		    strokeWeight: 3,
		    icons: [{
				icon: lineSymbol,
				offset: '100%',
				repeat: '200px'
			}]
		});
		var listicon=["pickupmarker.png","deliverymarker.png"];
		pathList.push(route);
		var icon=0;
		for(var j=0;j<list.length;j++){
			strcontent="";
			if(list[j].action=="PICKUP"){
				var latlng=list[j].latlng;
				var ll= latlng.split(", ");
				var lat = parseFloat(ll[0]);
				var lng = parseFloat(ll[1]);
				icon=0;
			} else if(list[j].action=="DELIVERY"){
				var latlng=list[j].latlng;
				var ll= latlng.split(", ");
				var lat = parseFloat(ll[0]);
				var lng = parseFloat(ll[1]);
				icon=1;
			}
			var point = new google.maps.LatLng(lat,lng);
			var marker = new google.maps.Marker({
				position:point,
				map: map,
				label:null,
				icon: baseUrl+"/assets/icon/"+listicon[icon],
				path: pathList.indexOf(route)
			});
			list[j].marker=marker;
			markerList.push(marker);
			route.getPath().push(point);
			route.setMap(map);
			data2[i].poliline=route;
		}		
	}
	
}
function initMap() {
	directionsService = new google.maps.DirectionsService;
	serviceDistance = new google.maps.DistanceMatrixService;
    var mapDiv = document.getElementById('map');
    map = new google.maps.Map(mapDiv, {
        center: {lat: 21.03, lng: 105.8},
        zoom: 14
    });
    data=JSON.parse('${sol}');
    for(var t=0;t<data.routes.length;t++)
    	checkedList[t]=1;
    console.log(data);
    loadTable(data);
    viewMap();
  }
</script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4&callback=initMap">
</script>