<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="page-wrapper">
    <div class="row">
        <div class="col-lg-12 center">
            <h1 class="page-header">Chỉnh sửa điểm nút bản đồ</h1>
        </div>
        <!-- /.col-lg-12 -->
    </div>
    <!-- /.row -->
    <div class="row">
    	<div id="googleMap" style="width:100%;height:100%"></div>
    	<div class="panel panel-default">
    		<div class="panel-body">
    			<div class="form-group">
    				<div class="col-sm-3">
						<select class="form-control" id="select-listProvince">
							<c:forEach items="${lstProvinces}" var="province">
								<option value="${province.PROV_Code}"><c:out value="${province.PROV_Name}"/></option>
							</c:forEach>
						</select>
					</div>
				</div>
				<button type="button" class="btn btn-primary active" onclick="getListInRectangle()" >View</button>
				
    		</div>
    	</div>
    </div>
</div>
<!-- /#page-wrapper -->
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAruL-HLFSNh6G2MLhjS-eBTea7r7EFa5A&libraries=places&callback=initialize" async defer></script>
<script>
var colorInit=["#F7786B","#91A8D0","#91A8D0","#034F84","#FAE03C","#98DDDE","#9896A4","#DD4132","#B18F6A","#79C753","#B93A32","#AD5D5D","#006E51","#B76BA3","#5C7148","#D13076"]; // mang init mau
var map;
var rectangle;
var infoWindow;
var neO;
var swO;
function initialize() {
	//construct google map
	var mapProp = {
		center: {lat: 21.033333, lng: 105.849998},
		zoom: 10,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
	var bounds = {
	          north: 21.0,
	          south:  21.5,
	          east: 106.3,
	          west: 105.0
	        };
	getListAll();
	rectangle = new google.maps.Rectangle({
      bounds: bounds,
      editable: true,
      draggable: true
    });
	infoWindow=new google.maps.InfoWindow();
	map.addListener('rightclick', function(){
		rectangle.setMap(null);
		infoWindow.setMap(null);
	});
	map.addListener('dblclick', function(){
		
		
	});
	rectangle.addListener('bounds_changed', showInfoBox);
	map.addListener('click', function(){
		rectangle.setMap(null);
		var bounds = {
		          north: map.getBounds().getNorthEast().lat(),
		          south: map.getBounds().getSouthWest().lat(),
		          east: map.getBounds().getNorthEast().lng(),
		          west: map.getBounds().getSouthWest().lng()
		        };
		console.log(bounds);
		rectangle.setBounds(bounds);
		map.fitBounds(rectangle.getBounds());
		rectangle.setMap(map);
		
	});
	console.log(rectangle);
}
function showInfoBox(event) {
    var ne = rectangle.getBounds().getNorthEast();
    var sw = rectangle.getBounds().getSouthWest();
    infoWindow.setContent('<button type="button" class="btn btn-warning active" onclick="mergePoints()" >Merge</button>');
    infoWindow.setPosition(ne);

    infoWindow.open(map);
}
function mergePoints(){
	console.log("merge Point")
	
	var ne = rectangle.getBounds().getNorthEast();
    var sw = rectangle.getBounds().getSouthWest();
    var lRPM=[];
    console.log(ne.lat()+" "+ne.lng());
	console.log(sw.lat()+" "+sw.lng());
    for(i=0;i<lRP.length;i++){
    	
    	var latlng=lRP[i].rp_LatLng;
    	var p=latlng.split(', ');
    	if(p[0]>ne.lat() || p[0]<sw.lat() || p[1]>ne.lng()|| p[1]<sw.lng()){
    		continue;
    	}
    	console.log("index"+i);
    	var ob={
    			ProvinceCode:lRP[i].provinceCode ,
    			RP_Code:lRP[i].rp_Code,
    			RP_ID:lRP[i].rp_ID,
    			RP_LatLng:lRP[i].rp_LatLng
    	}
    	console.log(ob);
    	lRPM.push(ob);
    }
    console.log(lRPM);
    var json=JSON.stringify(lRPM);
    $.ajax({
		type: 'POST',
		url: baseUrl+'/mapstreetmanipulation/merge-points',
		data: json,
		contentType: 'application/json; charset=utf-8',
		dataType: "json",
		success: function(response){
			if (response==true){
				getListAll();
			}
		}
	});
    
}
var lRP=[];
var lRS=[];
var lMarker=[];
var lPoliline=[];
function randomColor(){
	/*	p1=Math.floor((Math.random() * 85));
		p2=Math.floor((Math.random() * 255));
		p3=Math.floor((Math.random() * 255));
		return "rgb("+p1+","+p2+","+p3+ ")";*/
		return colorInit[Math.floor((Math.random() * colorInit.length))];
	}
function viewMap(){
	for(i=0;i<lMarker.length;i++)
		lMarker[i].setMap(null);
	for(i=0;i<lPoliline.length;i++)
		lPoliline[i].setMap(null);
	lMarker=[];
	lPoliline=[];
	for(i=0;i<lRP.length;i++){
		var p=lRP[i].rp_LatLng.split(",");
		var point = new google.maps.LatLng(p[0],p[1]);
		var marker = new google.maps.Marker({
			position:point,
			map: map,
			label:null,
			icon: baseUrl+"/assets/icon/oval_green.png",
			path: -1,
			index: i,
			draggable: true,
			roadPointIndex:i
		});
		marker.addListener('dragend',handleEventDrag);
		lMarker.push(marker);
		lRP[i]['marker']=marker;
	}
	for(i=0;i<lRS.length;i++){
		var lineSymbol = {
				path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
				strokeOpacity: 2,
				scale: 1.5
			}
		if (lRS[i].rseg_Bidirectional=="BIDIRECTIONAL")
			var seg = new google.maps.Polyline({
				path: [lRP[lRS[i].rseg_FromPoint].marker.getPosition(),lRP[lRS[i].rseg_ToPoint].marker.getPosition()],
				strokeColor: randomColor(),
		    	strokeOpacity: 1.0,
		    	strokeWeight: 3,
		    	map:map
			});
		else 
			var seg = new google.maps.Polyline({
				path: [lRP[lRS[i].rseg_FromPoint].marker.getPosition(),lRP[lRS[i].rseg_ToPoint].marker.getPosition()],
				strokeColor: randomColor(),
		    	strokeOpacity: 1.0,
		    	strokeWeight: 3,
		    	map:map,
		    	icons: [{
					icon: lineSymbol,
					offset: '100%',
					repeat: '200px'
				}]
			});
		lPoliline.push(seg);
		
	}
}
function handleEventDrag(event ){
	console.log("handleEventDrag");
	var index=this.roadPointIndex;
	var pointCode= lRP[index].rp_Code;
	var infowindow = new google.maps.InfoWindow({
	    content: '<button class="btn btn-primary" onclick="changeRoadPoint('+pointCode+','+event.latLng.lat()+','+event.latLng.lng()+','+index+')">Save</button>'
	});
	infowindow.open(map,this);
}
function changeRoadPoint(pointCode,lat,lng,index){
	console.log(pointCode);
	console.log(lat);
	var data=[pointCode,lat+", "+lng];
	$.ajax({
		type: 'POST',
		url: baseUrl+'/mapstreetmanipulation//edit-location-road-point',
		data: JSON.stringify(data),
		contentType: 'application/json; charset=utf-8',
		dataType: "json",
		success: function(response){
			if(response==true) lRP[index].rp_LatLng=lat+", "+lng;
			viewMap();
		}
	});
}
function getListAll(){
	$.ajax({
		type: 'POST',
		url: baseUrl+'/mapstreetmanipulation/get-point-segment-all',
		data: "",
		contentType: 'application/json; charset=utf-8',
		dataType: "json",
		success: function(response){
			console.log("ok");
			console.log(response);
			lRP=response.listRoadPoint;
			lRS=response.listRoadSegment;
			viewMap();
		}
	});
}
function getListInRectangle(){
	console.log("is here");
	var ne = rectangle.getBounds().getNorthEast();
    var sw = rectangle.getBounds().getSouthWest();
    var json=[ {
    		lat : ne.lat(),
    		lng : ne.lng()
    	}, {
    		lat: sw.lat(),
    		lng: sw.lng()
    		}];
    console.log(json);
    json=JSON.stringify(json);
	$.ajax({
		type: 'POST',
		url: baseUrl+'/mapstreetmanipulation/get-point-segment-in-range',
		data: json,
		contentType: 'application/json; charset=utf-8',
		dataType: "json",
		success: function(response){
			console.log("ok");
			console.log(response);
			lRP=response.listRoadPoint;
			lRS=response.listRoadSegment;
			viewMap();
		}
	});
	neO=ne
	swO=sw;
}
</script>