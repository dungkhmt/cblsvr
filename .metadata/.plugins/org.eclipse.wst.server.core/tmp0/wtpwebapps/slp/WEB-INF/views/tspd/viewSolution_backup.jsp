<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">TSPD Solution</h1>
		</div>
	</div>
	<div class="row"> 
		<button class="btn btn-primary" onclick="view_tspdls_solution();">TSPD-LS</button>
		<button class="btn btn-primary" onclick="view_grasp_solution();">GRASP</button>
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
var makerDrone;
var makerTruck;
var directionsService ;
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
			return null;
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
var droneDeliverySort=[];
function view_tspdls_solution(){
	initMap();
	var tour_tspdls = tours[0];
	view_tour(tour_tspdls);
}

function view_grasp_solution(){
	initMap();
	var tour_grasp = tours[1];
	view_tour(tour_grasp);
}
var truckTour;
var droneDeliveries ;
var dr=[];
var dl=[];
function view_tour(data){
	truckTour = data.td.truck_tour;
	droneDeliveries = data.dd;
	console.log(data)
	for(var j=0;j<truckTour.length;j++){
		truckTour[j].obLauch_node=null;
		truckTour[j].obRendezvous_node=null;
	}
	var lineSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
	for(var i=0;i<droneDeliveries.length;i++){
		for(var j=0;j<truckTour.length;j++){
			if(truckTour[j].id==droneDeliveries[i].lauch_node.id){
				truckTour[j].obLauch_node=droneDeliveries[i];
			}
			if(truckTour[j].id==droneDeliveries[i].rendezvous_node.id){
				truckTour[j].obRendezvous_node=droneDeliveries[i];
			}
		}
			
	}
	for(var i=0;i<truckTour.length;i++){
		if(truckTour[i].obLauch_node!=null){
			droneDeliverySort.push(truckTour[i].obLauch_node);
		}
	}
	
	markerDrone = new google.maps.Marker({
		icon : baseUrl+"/assets/icon/drone-icon.png",
		position : null,
		speed: dataResponse.droneSpeed,
		isDrone: true,
		isRunning: false
	});
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

function runDrone(lauch,drone,rendezvous,c){
	dl[c]=1;
	markerDrone.isRunning=true;
	markerDrone.setMap(map);
	polyline = new google.maps.Polyline({
					path: [],
					strokeColor: '#FF0000'
	});
	polyline.getPath().push(new google.maps.LatLng(lauch.lat,lauch.lng));
	polyline.getPath().push(new google.maps.LatLng(drone.lat,drone.lng));
	polyline.getPath().push(new google.maps.LatLng(rendezvous.lat,rendezvous.lng));
	polyline.setMap(map);
	startAnimation(markerDrone, polyline, new google.maps.LatLng(rendezvous.lat,rendezvous.lng))

}

function set(start,end,marker){	
	marker.setPosition(start);
	marker.setMap(map);
	calculateAndDisplay(start,end,marker);
}


function calculateAndDisplay(start, end, marker){
		var waypoints=[]
		for(var i=1;i<truckTour.length-1;i++){
			waypoints.push({
	              location:new google.maps.LatLng(truckTour[i].lat,truckTour[i].lng),
	              stopover: true
	            });
		}
		var request = {
			origin: start,
			destination: end,
			waypoints:waypoints,
			travelMode: google.maps.DirectionsTravelMode.DRIVING
		};
		var display=function(rep, status){
			console.log(rep);
			if(status == google.maps.DirectionsStatus.OK){
				var polyLine = new google.maps.Polyline({
					path: [],
					strokeColor: '#696969'
				});		
				var directionsDisplay = new google.maps.DirectionsRenderer();		
				directionsDisplay.setMap(map);
				directionsDisplay.setDirections(rep);

				var startLocation = new Object();
				var endLocation = new Object();

				var legs = rep.routes[0].legs;   
				for(var h=0; h<legs.length; h++){
					
					if(h==0){
						startLocation.latlng = legs[h].start_location;
					}
					endLocation.latlng = legs[h].end_location; 
								
					var steps = legs[h].steps;
					legs[h].start_location.isWayPoint=h;
					polyLine.getPath().push(legs[h].start_location);
					for(var j = 0; j < steps.length; j++){
						var nextPoint = steps[j].path;  			
						for(var k = 0; k < nextPoint.length; k++){
							if(j==0&& k==0) continue;
							nextPoint[k].isWayPoint=h;
							polyLine.getPath().push(nextPoint[k]); 		
						}
					}
				}
			}
			polyLine.setMap(map);
			
			startAnimation(marker,polyLine,end);	
		};
	
		directionsService.route(request, display);	
}

function startAnimation(marker,polyLine,end){
	var step = marker.speed;
	distance = polyLine.Distance();
	setTimeout(function(){
		animate(marker,1,step,distance,polyLine,end);
	}, 100);
}


function animate(marker,d,step,distance,polyLine,end){
	
	if(d > distance){
		marker.setPosition(end);
		if (marker.isDrone==true) {
			markerDrone.isRunning=false;
		}
		return;
	}
	var p;
	var t;
	[p,t] = polyLine.GetPointAtDistance(d);
	marker.setPosition(p);
	if(t!=-1 && t!= undefined ) {
		if(truckTour[t].obRendezvous_node!=null&& marker.isDrone==false && dr[t]==0){
			move = function( wait, newDestination) {
        		if(markerDrone.isRunning==true) {
	          		setTimeout(function() { 
	            		move(wait); 
	          		}, wait);
        		} else{
        			dr[t]=1;
        			if(truckTour[t].obLauch_node!=null&&dl[t]==0 ){
        				runDrone(truckTour[t].obLauch_node.lauch_node,truckTour[t].obLauch_node.drone_node,truckTour[t].obLauch_node.rendezvous_node,t);			
        			} else {
        				markerDrone.setMap(null);
        			}
        			var a = d + step;
        			setTimeout(function(){
        				animate(marker,a,step,distance,polyLine,end);
        			}, 100);
        		}
        		
			}
			move(1000);
		}else{ 
			if(truckTour[t].obLauch_node!=null && markerDrone.isRunning==false && marker.isDrone==false&&dl[t]==0){
				runDrone(truckTour[t].obLauch_node.lauch_node,truckTour[t].obLauch_node.drone_node,truckTour[t].obLauch_node.rendezvous_node,t);	
			}
			var a = d + step;
			setTimeout(function(){
				animate(marker,a,step,distance,polyLine,end);
			}, 100);
		}
	} else{
		var a = d + step;
		setTimeout(function(){
			animate(marker,a,step,distance,polyLine,end);
		}, 100);
	}
}

</script>