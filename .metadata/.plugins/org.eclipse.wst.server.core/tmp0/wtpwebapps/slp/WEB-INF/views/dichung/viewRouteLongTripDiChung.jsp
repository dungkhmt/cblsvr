<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script src="<c:url value="/assets/libs/datatables/media/js/jquery.dataTables.js"/>"></script>
<script src="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.js"/>"></script>
<script src="<c:url value="/assets/libs/jquery-ui-1.12.0/jquery-ui.js"/>"> </script>
<script src="<c:url value="/assets/libs/bootstrap-slider/bootstrap-slider.js"/>"> </script>
<link href="<c:url value="/assets/libs/bootstrap-slider/css/bootstrap-slider.css" />" rel="stylesheet" type="text/css" media="all" />
<link href="<c:url value="/assets/libs/bootstrap-timepicker/css/bootstrap-timepicker.css" />" rel="stylesheet" type="text/css" media="all" />
<link href="<c:url value="/assets/libs/bootstrap-datepicker/css/bootstrap-datepicker.css" />" rel="stylesheet" type="text/css" media="all" />
<script src="<c:url value="/assets/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/moment.js" />"></script>
<script src="<c:url value="/assets/libs/bootstrap-timepicker/js/bootstrap-timepicker.js"/>"></script>
	
<div id="page-wrapper">
	
<div id="map" style="height:100%">
</div>
	<!-- /.row -->


	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="col-lg-2">
						<button type="button"  class="form-group btn btn-primary active" title="" onclick="viewRoute()">View Routes</button>
				</div>
					
				<div class="panel-body">
					<div class="dataTable_wrapper">
						<table class="table table-striped table-bordered table-hover" id="tableRoute">
							<thead>
								<tr>
									<th>TicketCode</th>
									<th>PickupAddress</th>
									<th>DeliveryAddress</th>
									<th>PickupDateTime</th>
									
									<th>Sequence </th>
									<th>Shipper</th>
									<th>Check</th>
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
		</div>
	</div>
	
</div>	
<!-- /#page-wrapper -->

<script>
var colorInit=["#F7786B","#91A8D0","#91A8D0","#034F84","#FAE03C","#98DDDE","#9896A4","#DD4132","#B18F6A","#79C753","#B93A32","#AD5D5D","#006E51","#B76BA3","#5C7148","#D13076"]; // mang init mau
var table;
var routes;
var polylineRoutes = [];
var markerRoutes = [];
var count;
var checkedList=[];
var indexRowTable=[];

function initMap() {
	directionsService = new google.maps.DirectionsService;
	serviceDistance = new google.maps.DistanceMatrixService;
    var mapDiv = document.getElementById('map');
    map = new google.maps.Map(mapDiv, {
        center: {lat: 21.03, lng: 105.8},
        zoom: 13
    });
	var nbR = "<c:out value="${sharedLongTripSolution.nbRequests}" />";
	routes = new Array();
    <c:forEach items = "${sharedLongTripSolution.routes}" var = "sol">
    	var aRoute = new Object();
    	var info = new Array();
    	<c:forEach items = "${sol.routeElements}" var = "re">
    		var latlng = "${re.pickupPosition}";
    		if(latlng === "-")
    		//if(latlng.localeCompare("-") == 0)
    			latlng = "${re.deliveryPosition}";
    			//alert(latlng);
    		var e = new Object();
    		e.latlng = latlng;
    		e.departTime = "${re.departTime}";
    		e.ticketCode = "${re.ticketCode}";
    		e.pickupAddress = "${re.pickupAddress}";
    		e.deliveryAddress = "${re.deliveryAddress}";
    		e.seq = info.length + 1;
    		
    		info.push(e);
    		
    	</c:forEach>
    	
    	aRoute.info = info;
    	
    	routes.push(aRoute);
    </c:forEach>

    //alert(routes.length);
    for(i = 0; i < routes.length; i++){
    	var aRoute = routes[i];
    	var points = new Array();
    	var markers = new Array();
    	//alert(aRoute.length);
    	for(j = 0; j < aRoute.info.length; j++){
    		var latlng = aRoute.info[j].latlng;

    		var lat = latlng.substring(0,latlng.indexOf(',')) ;
			var lng = latlng.substring(latlng.indexOf(',')+1,latlng.length) ;
			var point = new google.maps.LatLng(lat,lng);
			//alert(lat + "," + lng);
			points.push(point);
			
			var info = new google.maps.InfoWindow({
				content: aRoute.info[j].departTime
			});
			var marker = new google.maps.Marker({
				position: point,
				map: map,
				infowindow: info
			});
			marker.addListener('click', function() {
			    this.infowindow.open(map, this);
			});
			markerRoutes.push(marker);
			markers.push(marker);
    	}
    	aRoute.markers = markers;
    	
    	var pl = new google.maps.Polyline({
    		path: points,
    		geodesic: true,
            strokeColor: '#FF0000',
            strokeOpacity: 1.0,
            strokeWeight: 2
    	});
    	pl.setMap(map);
    	polylineRoutes.push(pl);
    	
    	loadTableRoute(routes);
    }
}

function updateCheckList(i){
	var tmp= checkedList.indexOf(i);
	
	if (tmp==-1) checkedList.push(i);
	else checkedList.splice(tmp,1);
	
}
function viewRoute(){
	for(var i=0;i<routes.length;i++)
		polylineRoutes[i].setMap(null);
	for(i = 0; i < markerRoutes.length; i++)
		markerRoutes[i].setMap(null);

	for(var i=0;i<checkedList.length;i++){
		polylineRoutes[checkedList[i]].setMap(map);
		var markers = routes[checkedList[i]].markers;
		for(j = 0; j < markers.length; j++)
			markers[j].setMap(map);
	}
		
}
function loadTableRoute(routes){
	//console.log(data);
	$("table#tableRoute tbody").html("");
	gray="#F0F0F0";
	white="#FFFFFF";
	var color=["#F0F0F0","#FFFFFF"];
	var idcolor=0;
	str=null;
	count=0;
	for(var i=0;i<routes.length;i++){
		//console.log("i data[i].rddc_Group" +data[i].rddc_Group); 
		
		idcolor=(idcolor+1) % color.length;
			//console.log("id"+idcolor);
			//console.log("length "+ color.length+" "+idcolor % color.length);
		var r = routes[i].info;
		
		
		indexRowTable[i]=count;
		for(var j=0;j<r.length;j++){
			str+="<tr"+" style='background-color:"+color[idcolor]+"' "+">";
			str+="<td>"+r[j].ticketCode+"</td>"
			str+="<td>"+r[j].pickupAddress+"</td>"
			str+="<td>"+r[j].deliveryAddress+"</td>"
			str+="<td>"+r[j].departTime+"</td>"
			str+="<td>"+r[j].seq+"</td>"
			str+="<td>"+"-"+"</td>";
			if(j==0)
				str+="<td>"+"<div class='checkbox'> <label><input type='checkbox' onchange=updateCheckList("+i+") value=''></label></div>"+"</td>";
				else str+="<td>"+"</td>";
			str+="</tr>"
			count++;
		}
	}
	$("table#tableRoute tbody").append(str);

}


$(document).ready(function(){
	table=$("#tableRoute").DataTable({
		
	});
	
});

</script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4&callback=initMap">
</script>