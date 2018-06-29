<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="page-wrapper">
	<div class="form-group ">
		<label class="control-label col-sm-2" for="categoryArticle">Chọn Lộ Trình</label>
		<div class="col-sm-2">
			<select class="form-control selectRoute" onchange="selectRoute()" name="categoryArticle" >
				<option value="">Chọn Route</option>
				<c:forEach items="${listRoutes}" var="routes">
                	<option value="${routes.route_Code}">${routes.route_Code}</option>
                </c:forEach>
            </select>
         </div>
	</div>	
	<div id="googleMap" style="width:100%;height:80%"></div>	
	
	
	
</div>
<!-- Modal -->
<div id="sureModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <!-- Modal content-->
    <div class="modal-content">
      
      <div class="modal-body">
        Bạn có chắc chắn không?
        <select class="form-control selectStatus"  >
				<option value="">Chọn Route</option>
				<option value="DELIVERIED">Đã giao thành công</option>
				<option value="ARRIVED_BUT_NOT_DELIVERIED">Đã đến nhưng chưa giao được</option>
        </select>
      </div>
      <div class="modal-footer">
      	<button type="button" class="btn btn-primary active" onclick="changeStateOrder()">Ok I'm sure</button>
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
</div>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4&libraries=places&callback=initialize" async defer></script>

<script>
// get route in select box
var marker=[];
var routePath=[];
var map;
var listPoi=[];
var listOrderCode=[];
var listOrderDeliveriedMark=[];
function selectRoute(){
	var routeCode= $(".selectRoute").val();
	var jsonx={
			"routeCode": routeCode
	}
	$.ajax({ 
	    type:"POST", 
	    url:"${baseUrl}/ship/loadRouteDetail",
	    data: JSON.stringify(jsonx),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    //Stringified Json Object
	    success: function(response){
	        // Success Message Handler
	        for(i=0;i<marker.length;i++)
	        	marker[i].setMap(null);
	        for(i=0;i<routePath.length;i++)
	        	routePath[i].setMap(null);
	        
	        marker=[];
	        listPoi=[];
	        listOrderCode=[];
	        listOrderDeliveriedMark=[];
	      	console.log(response);
	      	for(var i=0;i<response.length;i++){
	        	listPoi.push({
	        		"lat":response[i].lat,
	        		"lng":response[i].lng,
	        	});
	        	listOrderCode.push(response[i].orderCode);
	        	listOrderDeliveriedMark.push(response[i].deliveried);
	        }
	      viewRoute(listPoi);
	    }
    });
	
}
function initialize() {
	//construct google map
	var mapProp = {
		center: {lat: 21.033333, lng: 105.849998},
		zoom: 13,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
	
	//construct routePath and routeOfShipper	
}
//value use tmp
var storeMarker;
// view route in selectbox
function viewRoute(listPoi){
	for(var i=0;i<listPoi.length;i++){
		console.log(listPoi[i]);
		if(listOrderDeliveriedMark[i]==0)
		marker[i]=new google.maps.Marker({
			position:listPoi[i],
			
			});
		else if(listOrderDeliveriedMark[i]==1){
			marker[i]=new google.maps.Marker({
				position:listPoi[i],
				icon:"https://www.google.com/mapfiles/marker_green.png"
				});
		} else {
			marker[i]=new google.maps.Marker({
				position:listPoi[i],
				icon:"http://maps.google.com/mapfiles/ms/icons/yellow.png"
				});
		}
		
		marker[i].setMap(map);
		marker[i].addListener('click',function(){
			storeMarker=this;
			$('#sureModal').modal('show');
			
			
		});
		
		
	}
	for(var i=0;i<listPoi.length-1;i++){
		if(listOrderDeliveriedMark[i+1]!=0)
		routePath[i] = new google.maps.Polyline({
			path: [listPoi[i],listPoi[i+1]],
			strokeColor: 'gray',
		    strokeOpacity: 1.0,
		    strokeWeight: 5,
		});
		else {
			routePath[i] = new google.maps.Polyline({
				path: [listPoi[i],listPoi[i+1]],
				strokeColor: '#FF0000',
			    strokeOpacity: 1.0,
			    strokeWeight: 5,
			});
		}
		routePath[i].setMap(map);
		routePath[i].addListener('click',function(){
			viewDirection(marker[routePath.indexOf(this)],marker[routePath.indexOf(this)+1])
		});
		console.log("route"+i);
	}
	
	
			
}
function viewDirection(marker1,marker2){
	console.log(marker1);
	var point1=marker1.getPosition();
	var point2=marker2.getPosition();
	var directionsService = new google.maps.DirectionsService;
    var directionsDisplay = new google.maps.DirectionsRenderer;
    directionsDisplay.setMap(map);
    directionsDisplay.setOptions( { suppressMarkers: true } );
    directionsService.route({
        origin: point1,
        destination: point2,
        travelMode: 'DRIVING'
      }, function(response, status) {
        if (status === 'OK') {
        	console.log(response);
        	//console.log();
        	var oarr=response.routes[0].overview_path;
        	var marker = new google.maps.Marker({
 				icon:"http://maps.google.com/mapfiles/ms/icons/yellow.png",
 				map:map
 			});
        	
        	move = function(marker, latlngs, index, wait, newDestination) {
        		//console.log("index"+index);
        		marker.setPosition(latlngs[index]);
        		if(index != latlngs.length-1) {
          		// call the next "frame" of the animation
	          		setTimeout(function() { 
	            		move(marker, latlngs, index+1, wait, newDestination); 
	          		}, wait);
        		}
        		else{
          			// assign new route
			        marker.position = marker.destination;
			        marker.destination = newDestination;
        		}
        	};
        	move(marker, oarr, 0, 1000, marker.position);
        	directionsDisplay.setDirections(response);
        } else {
          window.alert('Directions request failed due to ' + status);
        }
     });
}

function changeStateOrder(){
	var status= $(".selectStatus").val();
	$('#sureModal').modal('hide');
	console.log("storeMarker "+storeMarker);
	if(status=== "DELIVERIED")
	storeMarker.setIcon("https://www.google.com/mapfiles/marker_green.png");
	else if(status=== "ARRIVED_BUT_NOT_DELIVERIED")
	storeMarker.setIcon("http://maps.google.com/mapfiles/ms/icons/yellow.png");
	if(marker.indexOf(storeMarker)>0){
		routePath[marker.indexOf(storeMarker)-1].setOptions({strokeColor: 'gray'});
		
	};
	console.log(storeMarker);
	console.log(marker);
	console.log("index of marker"+marker.indexOf(storeMarker));
	console.log(listOrderCode[marker.indexOf(storeMarker)]);
	var jsonx={
			"orderCode": listOrderCode[marker.indexOf(storeMarker)],
			"status":status
	}
	console.log(jsonx);
	$.ajax({ 
	    type:"POST", 
	    url:"${baseUrl}/ship/set-order-delivered",
	    data: JSON.stringify(jsonx),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    //Stringified Json Object
	    success: function(response){
	        // Success Message Handler
	    }
    });
}
</script>