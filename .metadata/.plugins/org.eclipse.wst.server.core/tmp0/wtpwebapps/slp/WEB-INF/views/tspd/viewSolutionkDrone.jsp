<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">TSPD Solution</h1>
		</div>
	</div>
	<div class="row"> 
		<button class="btn btn-primary" onclick="view_tspdls_solution();">TSPD-LS</button>
		<button class="btn btn-primary" onclick="view_grasp_solution();">GRASP</button>
		<button class="btn btn-warning" id="buttonChangePolyline" onclick="hireNormalPolyline();">Hire</button>
		<a class="btn btn-primary col-sm-offset-8 " onclick="saveSolution(this);">Save Solution</a>
	</div>
	<div class="row">
		<div id="map" style="height:100%"></div>
	</div>
	
</div>

<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4&callback=initMap&sensor=true&libraries=geometry"></script>
<script>
var map;
var dataResponse = JSON.parse('${sol}');
var tours=dataResponse.tours
console.log(tours);
var markerDrone=[];
var makerTruck;
var algo=0;
var directionsService ;
var stateBotNormalPolyline=0;
function saveSolution(view){
	console.log("here");
	save_data = "text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(dataResponse));
	console.log(save_data);
	view.setAttribute("href", "data:"+save_data);
    view.setAttribute("download", "solution.json");
    //window.location = baseUrl + "/tsp-drone/tspd-solve-home";
}
function hireNormalPolyline(){
	console.log(stateBotNormalPolyline);
	if(stateBotNormalPolyline==0) {
		$( "#buttonChangePolyline" ).removeClass( "btn-warning" );
		$( "#buttonChangePolyline" ).addClass( "btn-error" );
        $("#buttonChangePolyline").text("View");
		polylineNormal.setMap(null);
	} else {
		$( "#buttonChangePolyline" ).removeClass( "btn-error" );
		$( "#buttonChangePolyline" ).addClass( "btn-warning" );
		$("#buttonChangePolyline").text("Hide");
		polylineNormal.setMap(map);
	}
	stateBotNormalPolyline=(stateBotNormalPolyline+1)%2;
}
function initMap(){
	map = new google.maps.Map(document.getElementById('map'),{
		center: {lat:21.03, lng:105.8},
		zoom: 12
	});
	directionsService = new google.maps.DirectionsService();
	google.maps.LatLng.prototype.distanceFrom = function(newLatLng) {
		var EarthRadiusMeters = 6378137.0; // meters
		var lat1 = this.lat();
		var lon1 = this.lng();
		var lat2 = newLatLng.lat();
		var lon2 = newLatLng.lng();
		var dLat = (lat2-lat1) * Math.PI / 180;
		var dLon = (lon2-lon1) * Math.PI / 180;
		var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		Math.cos(lat1 * Math.PI / 180 ) * Math.cos(lat2 * Math.PI / 180 ) *
		Math.sin(dLon/2) * Math.sin(dLon/2);
		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		var d = EarthRadiusMeters * c;
		return d;
	}
	google.maps.Polygon.prototype.Distance = function(){
		var distance =0;
		for(var i=1; i< this.getPath().getLength(); i++){
			distance += this.getPath().getAt(i).distanceFrom(this.getPath().getAt(i-1));
		}
		return distance;
	}
	google.maps.Polygon.prototype.GetPointAtDistance = function(metres) {
		
	    if (metres == 0) return this.getPath().getAt(0);  
	    if (metres < 0) return null;
	    if (this.getPath().getLength() < 2) return null;
	    var dist=0;
	    var olddist=0;
	    for (var i=1; (i < this.getPath().getLength() && dist < metres); i++) {
			olddist = dist;
			dist += this.getPath().getAt(i).distanceFrom(this.getPath().getAt(i-1));
		}
		if (dist < metres) {
			dist=metres+10;
		}
		var p1= this.getPath().getAt(i-2);
		var p2= this.getPath().getAt(i-1);
		var m = (metres-olddist)/(dist-olddist);
		return [new google.maps.LatLng( p1.lat() + (p2.lat()-p1.lat())*m, p1.lng() + (p2.lng()-p1.lng())*m),this.getPath().getAt(i-1).isWayPoint];
	}

	/* Prototype của các hàm */
	google.maps.Polyline.prototype.Distance             = google.maps.Polygon.prototype.Distance;
	google.maps.Polyline.prototype.GetPointAtDistance   = google.maps.Polygon.prototype.GetPointAtDistance;
}

function view_tspdls_solution(){
	algo+=1;
	initMap();
	var tour_tspdls = tours[0];
	view_tour(tour_tspdls);
}

function view_grasp_solution(){
	algo+=1;
	initMap();
	var tour_grasp = tours[1];
	view_tour(tour_grasp);
}
var truckTour;
var droneDeliveries ;
var dr=[];
var dl=[];
var markerTruckTour=[];
var polylineNormal;
function view_tour(data){
	markerTruckTour=[];
	truckTour = data.td.truck_tour;
	droneDeliveries = data.dd;
	var lineSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
	polylineNormal=new google.maps.Polyline({
		strokeColor: '#096D9F',
	    strokeOpacity: 0.5,
	    strokeWeight: 2,
	    icons: [{
			icon: lineSymbol,
			offset: '100%',
			repeat: '200px'
		}]
	});
	for(var j=0;j<truckTour.length;j++){
		truckTour[j].obLauch_nodes=[];
		truckTour[j].obRendezvous_nodes=[];
	}
	for(var i=0;i<droneDeliveries.length;i++){
		droneDeliveries[i].id=i;
	}
	for(var i=0;i<droneDeliveries.length;i++){
		for(var j=0;j<truckTour.length;j++){
			if(truckTour[j].id==droneDeliveries[i].lauch_node.id){
				truckTour[j].obLauch_nodes.push(droneDeliveries[i]);
			}
			if(truckTour[j].id==droneDeliveries[i].rendezvous_node.id){
				truckTour[j].obRendezvous_nodes.push(droneDeliveries[i]);
			}
		}
			
	}
	
	for(var i=0;i<4;i++)
		markerDrone.push(new google.maps.Marker({
			icon : baseUrl+"/assets/icon/drone-icon.png",
			position : null,
			speed: dataResponse.droneSpeed,
			isDrone: true,
			delivery:-1,
			isRunning: false
		}));
	markerTruck = new google.maps.Marker({
		icon : "https://maps.gstatic.com/mapfiles/ms2/micons/truck.png",
		position : null,
		speed: dataResponse.truckSpeed,
		isDrone: false
	});
	for(var i=0;i<droneDeliveries.length;i++){
		var pi = new google.maps.Marker({
			icon : "https://www.google.com/mapfiles/marker_yellow.png",
			position : new google.maps.LatLng(droneDeliveries[i].drone_node.lat,droneDeliveries[i].drone_node.lng),
			infowindow: new google.maps.InfoWindow({ content:""+ droneDeliveries[i].drone_node.id })
		})
		pi.setMap(map);
		pi.addListener('click', function() {
	          this.infowindow.open(map, this);
	    });
	}
	
	for(var i=0;i<truckTour.length;i++){
		dr[i]=0;
		dl[i]=0;
	}
	runTruck(new google.maps.LatLng(truckTour[0].lat,truckTour[0].lng),new google.maps.LatLng(truckTour[truckTour.length-1].lat,truckTour[truckTour.length-1].lng));
	
}

function runTruck(start,end){
	set(start,end,markerTruck);
}
function distance2point(lat1,lon1 ,lat2,lon2){
	var EarthRadiusMeters = 6378137.0; // meters
	var dLat = (lat2-lat1) * Math.PI / 180;
	var dLon = (lon2-lon1) * Math.PI / 180;
	var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	Math.cos(lat1 * Math.PI / 180 ) * Math.cos(lat2 * Math.PI / 180 ) *
	Math.sin(dLon/2) * Math.sin(dLon/2);
	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	var d = EarthRadiusMeters * c;
	return d;
}

function runDrone(lauch,drone,rendezvous,c,id,marker){
	dl[c]=1;
	marker.isRunning=true;
	marker.setMap(map);
	marker.delivery=id;
	var polyline = new google.maps.Polyline({
					path: [],
					strokeColor: '#FF0000'
	});
	polyline.getPath().push(new google.maps.LatLng(lauch.lat,lauch.lng));
	polyline.getPath().push(new google.maps.LatLng(drone.lat,drone.lng));
	polyline.getPath().push(new google.maps.LatLng(rendezvous.lat,rendezvous.lng));
	polyline.setMap(map);
	startAnimation(marker, polyline, new google.maps.LatLng(rendezvous.lat,rendezvous.lng),algo)

}
var storeReq=null;
function set(start,end,marker){	
	marker.setPosition(start);
	marker.setMap(map);
	var waypoints=[]
	
	var polyLine = new google.maps.Polyline({
		path: [],
		strokeColor: '#1A9D51',
		strokeOpacity: 0.7,
	    strokeWeight: 3
	});	
	
	calculateAndDisplay(1,start,end,marker,polyLine,waypoints);
}
var sucQDir=false;
function storeResponce(request,polyLine){
	var display=function(rep, status){
		if(status == google.maps.DirectionsStatus.OK){
			var legs = rep.routes[0].legs;   
			for(var h=0; h<legs.length; h++){			
				var steps = legs[h].steps;
				polylineNormal.getPath().push(legs[h].start_location)
				var marker_z = new google.maps.Marker({
					position : legs[h].start_location,
					label:labels[markerTruckTour.length % labels.length],
					map: map
				});
				markerTruckTour.push(marker_z);
				legs[h].start_location.isWayPoint=markerTruckTour.length-1;
				polyLine.getPath().push(legs[h].start_location);
				for(var j = 0; j < steps.length; j++){
					var nextPoint = steps[j].path;  			
					for(var k = 0; k < nextPoint.length; k++){
						if(j==0&& k==0) continue;
						nextPoint[k].isWayPoint=markerTruckTour.length-1;
						polyLine.getPath().push(nextPoint[k]); 		
					}
				}
			}
		}
		sucQDir=true;
	};
	directionsService.route(request, display);	
}

function lastReq(marker,end,request,polyLine){
	
	var display=function(rep, status){
		if(status == google.maps.DirectionsStatus.OK){
	
			var legs = rep.routes[0].legs;   
			for(var h=0; h<legs.length; h++){
				
				var steps = legs[h].steps;
				polylineNormal.getPath().push(legs[h].start_location)
				var marker_z = new google.maps.Marker({
					position : legs[h].start_location,
					label:labels[markerTruckTour.length % labels.length],
					map: map
				});
				markerTruckTour.push(marker_z);
				legs[h].start_location.isWayPoint=markerTruckTour.length-1;
				polyLine.getPath().push(legs[h].start_location);
				for(var j = 0; j < steps.length; j++){
					var nextPoint = steps[j].path;  			
					for(var k = 0; k < nextPoint.length; k++){
						if(j==0&& k==0) continue;
						nextPoint[k].isWayPoint=markerTruckTour.length-1;
						polyLine.getPath().push(nextPoint[k]); 		
					}
				}
			}
		}
		polyLine.setMap(map);
		polylineNormal.getPath().push(end);
		polylineNormal.setMap(map);
		startAnimation(marker,polyLine,end,algo);	
	};
	
	directionsService.route(request, display);
}
var labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

function calculateAndDisplay(i,start, end, marker,polyLine,waypoints){
	if(i %22==0 || i>=truckTour.length-1){
			var request = {
				origin: start,
				destination: new google.maps.LatLng(truckTour[i].lat,truckTour[i].lng),
				waypoints:waypoints,	
				travelMode: google.maps.DirectionsTravelMode.DRIVING
			};
			sucQDir=false;	
			if(i>=truckTour.length-1){
				lastReq(marker,end,request,polyLine);
				return;
			}
			storeResponce(request,polyLine);
			move = function( wait,i) {
        		if(sucQDir==false) {
	          		setTimeout(function() { 
	            		move(wait,i); 
	          		}, wait);
        		} else{
        			start= new google.maps.LatLng(truckTour[i].lat,truckTour[i].lng);
        			waypoints=[];
        			calculateAndDisplay(i+1,start, end, marker,polyLine,waypoints);
        			return
        		}
			}
			move(10,i);
		} else {
			waypoints.push({
	            location:new google.maps.LatLng(truckTour[i].lat,truckTour[i].lng),
	            stopover: true
	  		});
			calculateAndDisplay(i+1,start, end, marker,polyLine,waypoints);
		}
		
}

function startAnimation(marker,polyLine,end,malgo){
	var step = marker.speed;
	distance = polyLine.Distance();
	setTimeout(function(){
		animate(marker,1,step,distance,polyLine,end,malgo);
	}, 100);
}
function getListRendezvousOf(t){
	var lrn=truckTour[t].obRendezvous_nodes;
	return lrn;
}

function getListLauchOf(t){
	var lln=truckTour[t].obLauch_nodes;
	return lln;
}

function clearDroneisRendezvoused(t){
	var lrn=getListRendezvousOf(t);
	for(var i=0;i<markerDrone.length;i++)
		for(var j=0;j<lrn.length;j++) 
			if(markerDrone[i].delivery==lrn[j].id){
				if(markerDrone[i].isRunning==false){
					markerDrone[i].setMap(null);
					markerDrone[i].delivery=-1;
				}
			}
}

function checkRunningOf(t){
	var lrn=getListRendezvousOf(t);
	for(var i=0;i<markerDrone.length;i++)
		for(var j=0;j<lrn.length;j++) 
			if(markerDrone[i].delivery==lrn[j].id){
				if(markerDrone[i].isRunning==true){
					return true;
				}
			}
	return false;
}
function animate(marker,d,step,distance,polyLine,end,malgo){
	if(d > distance || malgo!=algo){
		marker.setPosition(end);
		if (marker.isDrone==true) {
			marker.isRunning=false;
		}
		return;
	}
	var p;
	var t;
	//console.log(malgo+" "+marker.isDrone);
	console.log(marker.isDrone+" "+polyLine.GetPointAtDistance(d));
	[p,t] = polyLine.GetPointAtDistance(d);
	marker.setPosition(p);
	if(t!=-1 && t!= undefined ) {
		if(truckTour[t].obRendezvous_nodes.length>0 && marker.isDrone==false && dr[t]==0){
			move = function( wait) {
        		if(checkRunningOf(t)==true) {
        			clearDroneisRendezvoused(t);
	          		setTimeout(function() { 
	            		move(wait); 
	          		}, wait);
        		} else{
        			clearDroneisRendezvoused(t);
        			dr[t]=1;
        			var lln=getListLauchOf(t);
        			console.log(lln);
        			if( lln.length>0  && dl[t]==0 ){
        				for(var i=0;i<lln.length;i++){
        					for(var j=0;j<markerDrone.length;j++)
        						if(markerDrone[j].isRunning==false && markerDrone[j].map==null){
        							runDrone(lln[i].lauch_node,lln[i].drone_node,lln[i].rendezvous_node,t,lln[i].id,markerDrone[j]);		
        							break;
        						}
        				}
        								
        			} 
        			var a = d + step;
        			setTimeout(function(){
        				animate(marker,a,step,distance,polyLine,end,malgo);
        			}, 100);
        		}
			}
			move(1000);
		}else{ 
			var lln=getListLauchOf(t);
			console.log(lln);
			if(lln.length>0 && dl[t]==0 ){
				for(var i=0;i<lln.length;i++){
					for(var j=0;j<markerDrone.length;j++)
						if(markerDrone[j].isRunning==false && markerDrone[j].map==null){
							runDrone(lln[i].lauch_node,lln[i].drone_node,lln[i].rendezvous_node,t,lln[i].id,markerDrone[j]);		
							break;
						}
				}	
			}
			var a = d + step;
			setTimeout(function(){
				animate(marker,a,step,distance,polyLine,end,malgo);
			}, 100);
		}
	} else{
		var a = d + step;
		setTimeout(function(){
			animate(marker,a,step,distance,polyLine,end,malgo);
		}, 100);
	}
}

</script>