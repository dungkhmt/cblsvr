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
	<div class="form-group ">
		<label class="control-label col-sm-2" >Chọn Lô</label>
		<div class="col-sm-2">
			<select class="form-control batchselect" onchange="loadRoute()"  >
				<option value="">Chọn Lô</option>
				<c:forEach items="${listBatch}" var="lbatch">
                	<option value="${lbatch.REQBAT_Code}">${lbatch.REQBAT_Code}</option>
                </c:forEach>
            </select>
         </div>
	</div>
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Chi tiết các tuyến</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">
					<h3 class=""><b> Bộ lọc  </b> </h3>
					<div class="form-group">
						<label class="control-label col-lg-1">Ngày</label>
						<div class="col-lg-2">
							<input  class="form-control datepicker " name="dateFilter" onchange="updateFilter()" placeholder="Date" ></input>
						</div>
			
						<label class="control-label col-lg-1">Từ:</label>
						<div class="col-lg-2">
							<input   class="form-control timepicker" id="timeearly" onchange="updateFilter()" placeholder="TimeEarly" ></input>
						</div>
						<label class="control-label col-sm-1">Đến:</label>
						<div class="col-lg-2">
							<input   class="form-control timepicker" id="timelate"  onchange="updateFilter()" placeholder="TimeLate" ></input>
						</div>
					</div>
				</div>
			</div>
		</div>
		
    </div>
    <div id="map" style="height:100%">
    		</div>
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">
				<div class="form-group">
					<label class="control-label col-lg-1">Chọn Shipper</label>
					<div class="col-lg-2">
						<select id="shipperselect" name="shipperselect" class="form-control">
							
							<c:forEach items="${listShipper}" var="shippers">
								<option value="${shippers.SHP_Code}"><c:out value="${shippers.SHP_Code}"/></option>
							</c:forEach>
						</select>	
					</div>
					<div class="col-lg-2">
						<button type="button" class="form-group btn btn-primary active" title="" onclick="assignShipper()">Assign Shipper</button>
					</div>
					<div class="col-lg-2">
						<button type="button"  class="form-group btn btn-primary active" title="" onclick="viewRoute()">View Routes</button>
					</div>
				</div>
				
				</div>
				<div class="panel-body">
					<div class="dataTable_wrapper">
						<table class="table table-striped table-bordered table-hover" id="tableRoute">
							<thead>
								<tr>
									<th>TicketCode</th>
									<th>Address</th>
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
			<!-- /.panel -->
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
</div>
<!-- /#page-wrapper -->

<script>
var checkedList=[];
var indexRowTable=[];
var pathList=[];// danh sach cac poliline
var markerList=[]; // danh sach cac marker
var sortListData=[]; //danh sach index data sorted
var colorInit=["#F7786B","#91A8D0","#91A8D0","#034F84","#FAE03C","#98DDDE","#9896A4","#DD4132","#B18F6A","#79C753","#B93A32","#AD5D5D","#006E51","#B76BA3","#5C7148","#D13076"]; // mang init mau
var table;
var data;
$('.timepicker').timepicker({
	showMeridian: false 
});
$(function() {
    $( ".datepicker" ).datepicker({
    	format:"yyyy-mm-dd"
    });
    
});
function initMap() {
	directionsService = new google.maps.DirectionsService;
	serviceDistance = new google.maps.DistanceMatrixService;
    var mapDiv = document.getElementById('map');
    map = new google.maps.Map(mapDiv, {
        center: {lat: 21.03, lng: 105.8},
        zoom: 13
    });
	
}
$(document).ready(function(){
	table=$("#tableRoute").DataTable({
		
	});
	
});
function loadRoute(){
	var batchCode= $(".batchselect").val();
	
	$.ajax({ 
	    type:"POST", 
	    url:"${baseUrl}/dichung/load-route-in-batch",
	    data: batchCode,
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    success: function(response){
	        // Success Message Handler
	        //console.log(response);
	        data=response;
	        init(data);
	        loadTable(response);
	        viewMap(response);
	        
	    }
    });
	
}
function pushMomentObject(){
	for(var i=0;i<data.length;i++)
		for(var j=0;j<data[i].listPoint.length;j++){
		data[i].listPoint[j].momentObject=	moment(data[i].listPoint[j].rddc_PickupDateTime,"YYYY-MM-DD HH:mm:ss");
	}
	
}
function randomColor(){
/*	p1=Math.floor((Math.random() * 85));
	p2=Math.floor((Math.random() * 255));
	p3=Math.floor((Math.random() * 255));
	return "rgb("+p1+","+p2+","+p3+ ")";*/
	return colorInit[Math.floor((Math.random() * colorInit.length))];
}
function viewMap(data){
	
	var route;
	
	var xd=false;
	for(var i=0;i<data.length;i++){
		//console.log(i);
		//console.log(JSON.stringify(response[i]));
		var list= data[i].listPoint;
		if(list.length>=2){
		route = new google.maps.Polyline({
			strokeColor: randomColor(),
		    strokeOpacity: 1.0,
		    strokeWeight: 3,
		});
		pathList.push(route);
		
		for(var j=0;j<list.length;j++){
			var latlng=list[j].rddc_LatLng;
			var lat = latlng.substring(0,latlng.indexOf(',')) ;
			var lng = latlng.substring(latlng.indexOf(',')+1,latlng.length) ;
			var point = new google.maps.LatLng(lat,lng);
			var infowindow = new google.maps.InfoWindow({
			    content: "Pickup Address: "+ list[j].rddc_Address +"<br> Delivery Address: " + list[j].rddc_DeliveryAddress+"<br> Pickup Date Time:"+list[j].rddc_PickupDateTime
			  });
				
			var marker = new google.maps.Marker({
				position:point,
				map: map,
				label:null,
				icon: baseUrl+"/assets/icon/oval_green.png",
				path: pathList.indexOf(route),
				infowindow: infowindow
			});
			marker.addListener('click', function() {
			    this.infowindow.open(map, this);
			});
			list[j].marker=marker;
			markerList.push(marker);
			route.getPath().push(point);
			route.setMap(map);
		
		}		
		} else{
			var latlng=list[0].rddc_LatLng;
			var lat = latlng.substring(0,latlng.indexOf(',')) ;
			var lng = latlng.substring(latlng.indexOf(',')+1,latlng.length) ;
			var point = new google.maps.LatLng(lat,lng);
			var infowindow = new google.maps.InfoWindow({
			    content: "Pickup Address: "+ list[0].rddc_Address +"<br> Delivery Address: " + list[0].rddc_DeliveryAddress+"<br> Pickup Date Time:"+list[0].rddc_PickupDateTime
			  });
				
			var marker = new google.maps.Marker({
				position:point,
				map: map,
				label:null,
				icon: baseUrl+"/assets/icon/oval_blue.png",
				path: -1,
				infowindow: infowindow
			});
			marker.addListener('click', function() {
			    this.infowindow.open(map, this);
			});
			data[i].listPoint[0].marker=marker;
			markerList.push(marker);
			
		}
		
	}
	
}
function sortList(data){ // thua
	for(var i=0;i<data.length;i++)
		sortListData[i]=i;
	for(var i=0;i<data.length-1;i++){
		for(var j=i+1;j<data.length;j++){
			if(data[sortListData[i]].momentObject.isAfter(data[sortListData[j]].momentObject)){
				var tmp=sortListData[i];
				sortListData[i]=sortListData[j];
				sortListData[j]=tmp;
			}
		}
	}
	
	
}

function updateFilter(){
	var dateFilter=$(".datePicker").val();
	timeearly=$("#timeearly").val();
	timelate=$("#timelate").val();
	timeearly=moment(timeearly,"HH:mm");
	timelate=moment(timelate,"HH:mm");
	
	var dateTimeFilterEarly=moment(dateFilter,"YYYY-MM-DD");
	var dateTimeFilterLate=moment(dateFilter,"YYYY-MM-DD");
	dateTimeFilterEarly=moment(dateTimeFilterEarly.format('YYYY-MM-DD ')+ timeearly.format("HH:mm"),"YYYY-MM-DD HH:mm:ss");
	dateTimeFilterLate=moment(dateTimeFilterLate.format('YYYY-MM-DD ')+ timelate.format("HH:mm"),"YYYY-MM-DD HH:mm:ss");
	if(pathList!=null)
	for(var i=0;i<pathList.length;i++)
		pathList[i].setMap(null);
	if(data!=null)
	for(var i=0;i<data.length;i++ ){
		var list=data[i].listPoint;
		for(var j=0;j<list.length;j++){
		if(list[j].momentObject.isSameOrAfter(dateTimeFilterEarly) && list[j].momentObject.isSameOrBefore(dateTimeFilterLate)){
			list[j].marker.setMap(map);
			if(list[j].marker.path!=-1)
				pathList[list[j].marker.path].setMap(map);
		} else{
			list[j].marker.setMap(null);
		}
		}
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
function assignShipper(){
	var shipper= $("#shipperselect").val();
	
	res={
		"shipper":shipper	
	};
	
	
	res["listRoute"]=[];
	
	for(var i=0;i<checkedList.length;i++){
		res.listRoute.push(data[checkedList[i]].route_Code);
	}
	console.log(res);
	$.ajax({ 
	    type:"POST", 
	    url:"${baseUrl}/ship/update-route-assignshipper",
	    data: JSON.stringify(res),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    success: function(response){
	        // Success Message Handler
	        //console.log(response);
	      	if(response==true){
	      		for(var i=0;i<checkedList.length;i++){
	      			for(var j=0;j<data[checkedList[i]].listPoint.length;j++){
	      				var cells=document.getElementById("tableRoute").rows[indexRowTable[checkedList[i]]+1+j].cells;
	      				cells[5].innerHTML=shipper;
	      			}
	      		}
	      		
	      	}
	        
	    }
    });
}
function loadTable(data){
	//console.log(data);
	$("table#tableRoute tbody").html("");
	gray="#F0F0F0";
	white="#FFFFFF";
	var color=["#F0F0F0","#FFFFFF"];
	var idcolor=0;
	str=null;
	count=0;
	for(var i=0;i<data.length;i++){
		//console.log("i data[i].rddc_Group" +data[i].rddc_Group); 
		
		idcolor=(idcolor+1) % color.length;
			//console.log("id"+idcolor);
			//console.log("length "+ color.length+" "+idcolor % color.length);
		
		var list=data[i].listPoint;
		indexRowTable[i]=count;
		for(var j=0;j<list.length;j++){
			str+="<tr"+" style='background-color:"+color[idcolor]+"' "+">";
			str+="<td>"+list[j].rddc_TicketCode+"</td>"
			str+="<td>"+list[j].rddc_Address+"</td>"
			str+="<td>"+list[j].rddc_DeliveryAddress+"</td>"
			str+="<td>"+list[j].rddc_PickupDateTime+"</td>"
			str+="<td>"+list[j].rddc_Sequence+"</td>"
			str+="<td>"+data[i].route_Shipper_Code+"</td>";
			if(j==0)
				str+="<td>"+"<div class='checkbox'> <label><input type='checkbox' onchange=updateCheckList("+i+") value=''></label></div>"+"</td>";
				else str+="<td>"+"</td>";
			str+="</tr>"
			count++;
		}
	}
	$("table#tableRoute tbody").append(str);

}

function updateCheckList(i){
	var tmp= checkedList.indexOf(i);
	
	if (tmp==-1) checkedList.push(i);
	else checkedList.splice(tmp,1);
	
}
function init(data){
	pathList=[];
	markerList=[];
	sortListData=[]; 
	pushMomentObject();
	//sortList(data);
	//console.log(data);
}
function pushNewFilterTime(timeEarly, timeLate){
	//console.log(timeEarly+" "+timeLate);
	$("#timeearly").html(timeEarly);
	$("#timelate").html(timeLate);
} 
</script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4&callback=initMap">
</script>