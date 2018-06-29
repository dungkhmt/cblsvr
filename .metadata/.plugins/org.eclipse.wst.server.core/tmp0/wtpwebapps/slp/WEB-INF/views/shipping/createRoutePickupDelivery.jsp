<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/moment.js" />"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/bootstrap-datetimepicker.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/bootstrap/dist/js/collapse.js" />"></script>


<div id="page-wrapper">
	
    <div class="row">
        <div class="col-lg-12 center">
            <h1 class="page-header">Lật một kế hoạch chuyển hàng</h1>
        </div>
        <!-- /.col-lg-12 -->
    </div>
    <!-- /.row -->
    
     <div class="row">
     	<div class="panel panel-default">
     	<div class="col-lg-8">
    	<div id="map" style="height:100%">
     		</div>
     	</div>
     	<div class="col-lg-4" id="panelRight" style="padding:5px;min-height:0;max-height:800;overflow-y:scroll;">
     		<div class="table-responsive">
	        	<table class="table table-striped table-bordered table-hover" id="rightPanel" >
	        		<thead>
	            		<tr>
	                    	<th>Khách hàng</th>
	                        <th>Thời gian dự kiến </th>
	                        <th>Thời gian yêu cầu</th>
	                        
	                    </tr>
	                </thead>
	                
	                <tbody>
	                		
	                </tbody>
	           	</table>
	       	</div>
     	</div>
    	
     	<div class="panel-body">
     		
     		<div class="form-group col-lg-12" >
     			
     			<label class="control-label col-lg-1" >Chọn Shipper</label>
     			<div class="col-lg-2" >
     				<select class="form-control shipperselect" id="shipperselect" name="shipperselect" >
     					<c:forEach items="${listShipper}" var="lShp">
                                     	<option value="${lShp.SHP_Code}">${lShp.SHP_Code}</option>
                        </c:forEach>
     				</select>
     			</div>
     			<label class="control-label col-lg-1" >Chọn thời gian bắt đầu</label>
     			<div class="col-lg-2"> 
     				<input class="form-control" name="dateTimeStart" id="dateTimeStart" />
     			</div>
     			
     			<button type="button" class="btn btn-primary active" title="Thay đổi thời gian bắt đầu chuyển hàng" onclick="changeTimeStart()">Thay đổi</button>
     			<button type="button" class="btn btn-danger active" title="Xóa route ứng với shipper này" onclick="resetRoute()">Reset</button>
     			<!-- <button type="button" class="btn  active" id="deletebutton" title="Chuyển sang chế độ xóa" onclick="changeButtonDeleteStateClickMarker()">Xóa</button>  -->
     		</div>
     		<div class="col-lg-9">
     		<div class="table-responsive">
	            		<table class="table table-striped table-bordered table-hover" id="listShipperRoute">
	            			<thead>
	            			<tr>
	                        	<th>Người chuyển hàng</th>
	                            <th>Các địa điểm</th>
	                            <th>Tổng quãng đường</th>
	                            <th>Tổng thời gian</th>
	                        </tr>
	                        </thead>
	                        <tbody>
	                        	<c:forEach items="${listShipper}" var="items">
	                        		<tr>
	                        			<td><c:out value="${items.SHP_Code}"/></td>
	                        			<td><c:out value=""/></td>
	                        			<td><c:out value=""/></td>
	                        			<td><c:out value=""/></td>
	                        		</tr>
	                        	</c:forEach>
	                        </tbody>
	            		</table>
	            	</div>
	         </div>
     	</div>
     	</div>
     </div>
     
     
     <button type="button" class="btn btn-primary active" onclick="saveData()" >Save</button>
     <button type="button" class="btn btn-default" >Close</button>
     <button type="button" class="btn btn-danger active" onclick="confirmRoute()"> Chốt</button>
 
</div>
<script>

$("#shipperselect").on('change', function (){
	//console.log(dateTimeStartShipper[$("#shipperselect option:selected").index()]);
	resetViewPath();
	makeRightPanel();
	$("#dateTimeStart").val(dateTimeStartShipper[$("#shipperselect option:selected").index()]);
	
});
var status=0;
var marker=[];
var markerShipper=[];
var map; 
var route=[];
var routePath=[];
var distanceMatrix=[];
var routeLatLng=[];
var listInfoWindow=[];
var distanceMatrix=[];
var serviceDistance ;
var lOPD=JSON.parse('${listPDOrder}');
var lShipper=JSON.parse('${listShipperJson}');
var reqCount=0;
var resCount=0;
var dateTimeStartShipper=[];
var xdWait=true;
var remainOrder=[];

function changeTimeStart(){
	var indexSelectBox=$("#shipperselect option:selected").index();	
	dateTimeStartShipper[indexSelectBox]=$("#dateTimeStart").val();
	makeRightPanel();
}
function resetViewPath(){
	var indexSelectBox=$("#shipperselect option:selected").index();
	for(var i=0;i<routePath.length;i++){
		routePath[i].setMap(null);
	}
	routePath[indexSelectBox].setMap(map);
}
function pushOldRoute(){
	var routeOld=JSON.parse('${routeOld}');
	dateTimeStartShipper=JSON.parse('${routeOldDateTimeStart}');
	$("#dateTimeStart").val(dateTimeStartShipper[$("#shipperselect option:selected").index()]);
	//console.log(dateTimeStartShipper);
	
	//console.log("routeOld");
	//console.log(routeOld);
	for(var i=0;i<lShipper.length;i++ ){
		for(var j=0;j<routeOld[i].length;j++){ 
			var orderID;
			console.log(i+" "+j);
			for(var k=lShipper.length;k<marker.length;k++){
				var xdbreak=true;
				for(var t=0;t<marker[k].setPointOrder.length;t++){
				if(marker[k].setPointOrder[t].orderCode==routeOld[i][j].orderCode && marker[k].setPointOrder[t].isPickup==routeOld[i][j].isPickup){
					remainOrder[marker[k].setPointOrder[t].orderIndex][i]=routeOld[i][j].quantity;
					lShipper[i].SHP_currentQuantity+=routeOld[i][j].quantity;
					routeOld[i][j].marker=marker[k];
					route[i].push(routeOld[i][j]);
					routeLatLng[i].push({
						lat:marker[k].getPosition().lat(),
						lng:marker[k].getPosition().lng()
					})
				}	
				if(routeOld[i][j].isPickup==1)
				if(marker[k].setPointOrder[t].orderCode==routeOld[i][j].orderCode && marker[k].setPointOrder[t].isPickup==0){
					marker[k].setAnimation(google.maps.Animation.BOUNCE);
				}
				if(routeOld[i][j].isPickup==0)
				if(marker[k].setPointOrder[t].orderCode==routeOld[i][j].orderCode && marker[k].setPointOrder[t].isPickup==0){
							marker[k].setAnimation(null);
				}
				}
				
			}
		}
		routePath[i].setMap(null);
		routePath[i] = new google.maps.Polyline({
			path: routeLatLng[i],
			strokeColor: lShipper[i].color,
		    strokeOpacity: 1.0,
		    strokeWeight: 5,
		});
		routePath[i].setMap(map);
		
	}
	
	//console.log(route);
	updateDistance();
}
function changeButtonDeleteStateClickMarker(){
	
	
	status=parseInt(status)+1;
	status=status % 2;
	if(status==0) 
		$("#deletebutton").removeClass("btn-warning");
	else	
		$("#deletebutton").addClass("btn-warning");
}	
$('#dateTimeStart').datetimepicker({
	format: 'YYYY-MM-DD HH:mm:ss'
});
function initColorShipper(){
	for(i=0;i<lShipper.length;i++){
		var p1;
		var p2;
		var p3;
		[p1,p2,p3]=randomColor(p1,p2,p3);
		lShipper[i].color="rgb("+p1+","+p2+","+p3+ ")";
	}
}

function confirmRoute(){
	var confirmRoute= modelDataToSave();
	var indexSelectBox=$("#shipperselect option:selected").index();
	var shipperCode= lShipper[indexSelectBox].SHP_Code;
	for(var i=0;i<confirmRoute.length;i++)
		if(confirmRoute[i].shipperCode==shipperCode){
			
			$.ajax({ 
			    type:"POST", 
			    url:"${baseUrl}/ship/confirm-container-route",
			    data: JSON.stringify(confirmRoute[i]),
			    contentType: "application/json; charset=utf-8",
			    dataType: "json",
			    //Stringified Json Object
			    success: function(response){
			        // Success Message Handler
			    }
		    });
			break;
		}
	window.location=baseUrl+"/containerdelivery/list-pickupdelivery-order";
}
function saveData(){
	
	var data=modelDataToSave();
	console.log(data);
	alert();
	$.ajax({ 
	    type:"POST", 
	    url:"${baseUrl}/ship/save-container-routes",
	    data: JSON.stringify(data),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    //Stringified Json Object
	    success: function(response){
	        if(response==true) window.location=baseUrl+"/containerdelivery/list-pickupdelivery-order"
 	    }
    });
	
}
function getColorTimeCheck(early,late,x){
	var mEarly=	moment(early,"YYYY-MM-DD HH:mm:ss");
	var mLate= moment(late,"YYYY-MM-DD HH:mm:ss");
	var xDate=moment(x,"YYYY-MM-DD HH:mm:ss");
	if (mLate.isBefore(xDate) ) return "rgb(255, 102, 0)";
	if (mEarly.isAfter(xDate)) return "rgb(0, 153, 51)";
}
function pushMomentObject(){
	
}
function modelDataToSave(){
	var routesData=[];
	var nRoute=0;
	for(var i=0;i<route.length;i++)
		if(route[i].length > 1){
		routesData[nRoute]={
			"shipperCode": null,
			"dateTimeStart": null,
			"orderList":null
		}
		routesData[nRoute].shipperCode=lShipper[i].SHP_Code;
		routesData[nRoute].dateTimeStart=dateTimeStartShipper[i];
		routesData[nRoute].orderList=[];
		for(var j=1;j<route[i].length;j++){
			routesData[nRoute].orderList.push({
				"orderCode": route[i][j].orderCode,
				"isPickup": route[i][j].isPickup,
				"quantity": route[i][j].quantity
			})
		}
		nRoute++;
	}
	return routesData;
}
 
 function initMap(){
	var mapDiv = document.getElementById('map');
	map = new google.maps.Map(mapDiv, {
		center: {lat: 21.03, lng: 105.8},
		zoom: 12,
		mapTypeId: google.maps.MapTypeId.ROADMAP
    });
	initColorShipper();
	makeRemainArray();
	for(var i=0;i<lShipper.length;i++){
		var lat= lShipper[i].SHP_DepotLat;
		var lng= lShipper[i].SHP_DepotLng;
		 markerShipper[i]= new google.maps.Marker({
			position:{lat:lat , lng: lng},
			icon:"http://maps.google.com/mapfiles/ms/icons/truck.png"
			});
		 markerShipper[i].setMap(map);
		 marker.push(markerShipper[i]);
		 route[i]=[];
		 route[i][0]={
				 "orderCode":"",
				 "isPickup":0,
				 "marker": markerShipper[i]
		 };
		 routeLatLng[i]=[];
		 routeLatLng[i][0]={
				 lat:markerShipper[i].getPosition().lat(),
				 lng:markerShipper[i].getPosition().lng()
		 }
		 routePath[i] = new google.maps.Polyline({
				path: routeLatLng[i],
				strokeColor: 'gray',
			    strokeOpacity: 1.0,
			    strokeWeight: 5,
			});
		 routePath[i].setMap(map);
	}
	var setPointOrderList=makeSetMarker();
	viewAllOrder(setPointOrderList);
	
	serviceDistance = new google.maps.DistanceMatrixService;
	pushOldRoute();
}
function markerSelectOrder(orderCode,pickup,marker_id,quantity){
	var indexSelectBox=$("#shipperselect option:selected").index();
	if(pickup==0){
		lShipper[indexSelectBox].SHP_currentQuantity= lShipper[indexSelectBox].SHP_currentQuantity-quantity;
	} else  {
		lShipper[indexSelectBox].SHP_currentQuantity= lShipper[indexSelectBox].SHP_currentQuantity+quantity;
	}
	if(status==0 ){
		route[indexSelectBox].push({
			"orderCode": orderCode,
			"isPickup": pickup,
			"marker" : marker[marker_id],
			"quantity": quantity 
		});
		routeLatLng[indexSelectBox].push({
			lat:marker[marker_id].getPosition().lat(),
			lng:marker[marker_id].getPosition().lng()
		});
		routePath[indexSelectBox].setMap(null);
		routePath[indexSelectBox] = new google.maps.Polyline({
			path: routeLatLng[indexSelectBox],
			strokeColor: lShipper[indexSelectBox].color,
		    strokeOpacity: 1.0,
		    strokeWeight: 5,
		});
		routePath[indexSelectBox].setMap(map);
		}
	updateDistance();
	marker[marker_id].infoWindow.close();
	if(pickup==1)
	for(var i=lShipper.length;i<marker.length;i++){
		
		for(var j=0;j<marker[i].setPointOrder.length;j++){
			if(marker[i].setPointOrder[j].isPickup==0 && marker[i].setPointOrder[j].orderCode==orderCode){
				marker[i].setAnimation(google.maps.Animation.BOUNCE);
			}
		}
	}
	if(pickup==0){
		marker[marker_id].setAnimation(null);
	}
}

function makeSetMarker(){
	var setPointOrder=[];
	for(var i=0;i< lOPD.length ;i++){
		var cpilat=lOPD[i].OPD_PickupLat;
		var cpilng=lOPD[i].OPD_PickupLng;
		var cdelat=lOPD[i].OPD_DeliveryLat;
		var cdelng=lOPD[i].OPD_DeliveryLng;
		xd=false;
		xd2=false;
		for(var j=0;j<setPointOrder.length;j++){
			var lat=setPointOrder[j][0].point.lat;
			var lng=setPointOrder[j][0].point.lng;
			if( lat==cpilat && lng== cpilng) {
				setPointOrder[j].push({
					"orderIndex": i,
					"orderCode": lOPD[i].OPD_Code,
					"isPickup": 1,
					"point":{
						lat: cpilat,
						lng: cpilng
					}
				});
				xd=true;
			} else if (lat==cdelat && lng== cdelng){
				setPointOrder[j].push({
					"orderIndex": i,
					"orderCode": lOPD[i].OPD_Code,
					"isPickup": 0,
					"point":{
						lat: cdelat,
						lng: cdelng
					}
				});
				xd2=true;
			} ;
		}
		if(xd==false){
			setPointOrder.push([{
				"orderIndex": i,
				"orderCode": lOPD[i].OPD_Code,
				"isPickup": 1,
				"point":{
					lat: cpilat,
					lng: cpilng
				}
			}]);
		}
		if(xd2==false){
			
			setPointOrder.push([{
				"orderIndex": i,
				"orderCode": lOPD[i].OPD_Code,
				"isPickup": 0,
				"point":{
					lat: cdelat,
					lng: cdelng
				}
			}]);
		}
	}
	return setPointOrder;
}
function makeRemainArray(){
	for(var i=0;i<lOPD.length;i++){
		remainOrder[i]=[];
		for(var j=0;j<lShipper.length;j++){
			remainOrder[i][j]=0;
		}
	}
	
}
function sumArr(arr){
	var sum=0;
	for(var i=0;i< arr.length;i++){
		sum+=arr[i];
	}
	return sum;
}
function makeRightPanel(){
	dateTimeStartShipper[$("#shipperselect option:selected").index()]=$("#dateTimeStart").val();
	$("table#rightPanel tbody").html("");
	var dateTime = $("#dateTimeStart").val();
	var dateTime= moment(dateTime,"YYYY-MM-DD HH:mm:ss");
	var nShipper=lShipper.length;
	var str;
	var indexSelectBox=$("#shipperselect option:selected").index();
	/*for(var i=0;i<lShipper.length;i++ ){ */
		str=str+"<tr>";
		str+="<td colspan='3' align='center'style='color:"+ lShipper[indexSelectBox].color +"'>"+lShipper[indexSelectBox].SHP_Code+"</td>";
		str=str+"</tr>";
		for(var j=1;j<route[indexSelectBox].length;j++){
			var dt_tmp=dateTime;
			var index=marker.indexOf(route[indexSelectBox][j].marker);
			var indexOld=marker.indexOf(route[indexSelectBox][j-1].marker);
			var distance=distanceMatrix[indexOld][index];
			dt_tmp.add(distance.duration,"seconds");
			str+="<tr>";
			str+="<td>"+lOPD[parseInt((index-nShipper)/2)].OPD_ClientCode +"</td>"
			
			if((index-nShipper)%2==1){
				str+="<td style='color:"+getColorTimeCheck(lOPD[(parseInt((index-nShipper)/2))].OPD_EarlyDeliveryDateTime,lOPD[parseInt((index-nShipper)/2)].OPD_LateDeliveryDateTime,dt_tmp.year()+"-"+parseInt( dt_tmp.month()+1)+"/"+dt_tmp.date()+" "+dt_tmp.hours()+":"+ dt_tmp.minutes())+"'>"+dt_tmp.year()+"-"+parseInt( dt_tmp.months()+1)+"-"+dt_tmp.date()+" "+dt_tmp.hours()+":"+ dt_tmp.minutes()+"</td>";
				str+="<td>"+lOPD[(parseInt((index-nShipper)/2))].OPD_EarlyDeliveryDateTime.substring(0,lOPD[(parseInt((index-nShipper)/2))].OPD_EarlyDeliveryDateTime.length-3)+"-"+lOPD[parseInt((index-nShipper)/2)].OPD_LateDeliveryDateTime.substring(0,lOPD[parseInt((index-nShipper)/2)].OPD_LateDeliveryDateTime.length-3)+"</td>";
			} else {
				str+="<td style='color:"+getColorTimeCheck(lOPD[(parseInt((index-nShipper)/2))].OPD_EarlyPickupDateTime,lOPD[parseInt((index-nShipper)/2)].OPD_LatePickupDateTime,dt_tmp.year()+"/"+parseInt( dt_tmp.month()+1)+"/"+dt_tmp.date()+" "+dt_tmp.hours()+":"+ dt_tmp.minutes())+"'>"+dt_tmp.year()+"-"+parseInt( dt_tmp.months()+1)+"-"+dt_tmp.date()+" "+dt_tmp.hours()+":"+ dt_tmp.minutes()+"</td>";
				str+="<td>"+lOPD[parseInt((index-nShipper)/2)].OPD_EarlyPickupDateTime.substring(0,lOPD[parseInt((index-nShipper)/2)].OPD_EarlyPickupDateTime.length-3)+"-"+lOPD[parseInt((index-nShipper)/2)].OPD_LatePickupDateTime.substring(0,lOPD[parseInt((index-nShipper)/2)].OPD_LatePickupDateTime.length-3)+"</td>";
			}
			str+="</tr>";
			//console.log(dt_tmp.year()+" "+dt_tmp.month()+" "+dt_tmp.date()+" "+dt_tmp.hours()+" "+ dt_tmp.minutes());
		}
	//}
	$("table#rightPanel tbody").append(str);
}
function checkInputQuatity(max,code){
	var key=$("#"+code).val();
	
	if(parseInt(key)>parseInt(max)){
		$("#"+code).css("border", "2px solid red");
		$("#"+code+"Button").attr("disabled", true);
	}
	else{ 
		$("#"+code).css("border", "2px solid green");
		$("#"+code+"Button").attr("disabled", false);
	}
}
function makeInfoWindowContent(marker_id,setPointOrder){
	var tookOrder;
	var indexSelectBox=$("#shipperselect option:selected").index();
	var str='<table class="table table-striped table-bordered table-hover">';
	str+="<tr>";
	str+="<th> Hóa đơn</th>";
	str+="<th> Số lượng </th>";
	str+="<th> Chọn </th>";
	str+="</tr>";
	for(var i=0;i< setPointOrder.length;i++){
		str+= "<tr>";
		str+="<td>";
		if(setPointOrder[i].isPickup==1)
			str+=setPointOrder[i].orderCode+" Pickup";
		else str+=setPointOrder[i].orderCode+" Delivery"
		str+="</td>"
		if(setPointOrder[i].isPickup==1){
			var value=Math.min(lOPD[setPointOrder[i].orderIndex].OPD_Volumn -sumArr(remainOrder[setPointOrder[i].orderIndex]),lShipper[$("#shipperselect option:selected").index()].SHP_Capacity_1-lShipper[$("#shipperselect option:selected").index()].SHP_currentQuantity);
			console.log(lShipper[$("#shipperselect option:selected").index()].SHP_Capacity_1);
			console.log(lShipper[$("#shipperselect option:selected").index()].SHP_currentQuantity);
			console.log(lOPD[setPointOrder[i].orderIndex].OPD_Volumn );
			console.log(sumArr(remainOrder[setPointOrder[i].orderIndex]));
			
			console.log(remainOrder);
			str+='<td> <input type="text" id="'+setPointOrder[i].orderCode+"Pickup"+'"' +"value="+(Math.min(lOPD[setPointOrder[i].orderIndex].OPD_Volumn -sumArr(remainOrder[setPointOrder[i].orderIndex]),lShipper[$("#shipperselect option:selected").index()].SHP_Capacity_1-lShipper[$("#shipperselect option:selected").index()].SHP_currentQuantity)) +' onkeyup=checkInputQuatity("'+(value) +'","'+setPointOrder[i].orderCode+"Pickup"+'")> </td>';
			if(value<=0) str+='<td> <button type="button" id="'+setPointOrder[i].orderCode+"PickupButton"+'" class="btn btn-warning active" onclick=changeRemainArrayforRoute(\''+setPointOrder[i].orderCode+'\','+1+','+marker_id+') disabled> Select</button>';
			else str+='<td> <button type="button" id="'+setPointOrder[i].orderCode+"PickupButton"+'" class="btn btn-warning active" onclick=changeRemainArrayforRoute(\''+setPointOrder[i].orderCode+'\','+1+','+marker_id+')> Select</button>'
		}
		else { 
			str+='<td> <input type="text" id="'+setPointOrder[i].orderCode+"Delivery"+'"'+'value='+ remainOrder[setPointOrder[i].orderIndex][indexSelectBox]  +' disabled> </td>';
			if(remainOrder[setPointOrder[i].orderIndex][indexSelectBox]==0) str+='<td> <button type="button" class="btn btn-warning active" disabled> Select</button>'
			else str+='<td> <button type="button" class="btn btn-warning active" onclick=changeRemainArrayforRoute(\''+setPointOrder[i].orderCode+'\','+0+','+marker_id+')> Select</button>';
		}
		str+= "</tr>"
	/* 	if(setPointOrder[i].isPickup==1)
		for(var j=0;j< lShipper.length;j++ ){
			tookOrder+= "<tr>";
			tookOrder+='<td>' + setPointOrder[i].orderCode + 'Pickup'  +' </td>'
			tookOrder+='<td>'+  remainOrder[i][j] +'</td>';
			tookOrder+='<td>'+ lShipper[j].SHP_Code +'</td>';
			tookOrder+="</tr>";
		} */
	}
	str+="</table>";
	return str;
}
function changeRemainArrayforRoute(orderCode,isPickup,marker_Id){
	
	var quantity;
	if(isPickup==1)
		quantity=$("#"+orderCode+"Pickup").val();
	else 
		quantity=$("#"+orderCode+"Delivery").val();
	
	var oindex;
	for(var i =0;i< lOPD.length;i++){
		//console.log(lOPD[i].orderCode+" "+orderCode)
		if(lOPD[i].OPD_Code==orderCode){
			oindex= i;
			break;
		}
	}
	var indexSelectBox=$("#shipperselect option:selected").index();
	
	remainOrder[oindex][indexSelectBox]=parseInt(quantity);
	
	markerSelectOrder(orderCode,isPickup,marker_Id,quantity);
}
function viewAllOrder(setPointOrder){
	for(i=0;i<setPointOrder.length;i++){
		marker_tmp=new google.maps.Marker({
			position:{lat: setPointOrder[i][0].point.lat, lng: setPointOrder[i][0].point.lng},
			});
		for(var j=0;j<setPointOrder[i].length;j++){
			lOPD[setPointOrder[i][j].orderIndex].marker= marker_tmp;
		}
		marker.push(marker_tmp);
		listInfoWindow[marker.length]=new google.maps.InfoWindow({
			    content: makeInfoWindowContent(marker.length-1,setPointOrder[i])
			  });
		marker_tmp.infoWindow=listInfoWindow[marker.length];
		marker_tmp.setMap(map);
		if(setPointOrder[i][0].isPickup==0 ){
			marker_tmp.setIcon("https://www.google.com/mapfiles/marker_green.png");
		}
		//marker_tmp.predious=null;
		
		marker_tmp.setPointOrder=setPointOrder[i];
		marker_tmp.addListener('click',function(){
			//if(this.setPointOrder.length>1){
				var marker_id= marker.indexOf(this);
				this.infoWindow.setContent(makeInfoWindowContent(marker_id, this.setPointOrder) );
				this.infoWindow.open(map,this);
			/* }else{
			var indexSelectBox=$("#shipperselect option:selected").index();
			
			if(status==0 ){
			route[indexSelectBox].push({
				"orderCode": this.setPointOrder[0].orderCode,
				"isPickup": this.setPointOrder[0].isPickup,
				"marker" : this
			});
			routeLatLng[indexSelectBox].push({
				lat:this.getPosition().lat(),
				lng:this.getPosition().lng()
			});
			routePath[indexSelectBox].setMap(null);
			routePath[indexSelectBox] = new google.maps.Polyline({
				path: routeLatLng[indexSelectBox],
				strokeColor: lShipper[indexSelectBox].color,
			    strokeOpacity: 1.0,
			    strokeWeight: 5,
			});
			routePath[indexSelectBox].setMap(map);
			}  else if(status==1 && route[indexSelectBox].indexOf(this) !=-1) {
				var indexInRoute=route[indexSelectBox].indexOf(this);
				route[indexSelectBox].splice(indexInRoute,1);
				routeLatLng[indexSelectBox].splice(indexInRoute,1);
				routePath[indexSelectBox].setMap(null);
				routePath[indexSelectBox] = new google.maps.Polyline({
					path: routeLatLng[indexSelectBox],
					strokeColor: lShipper[indexSelectBox].color,
				    strokeOpacity: 1.0,
				    strokeWeight: 5,
				});
				routePath[indexSelectBox].setMap(map);
			} 
			updateDistance();
			} */
		});
		
		/* marker_tmp2=new google.maps.Marker({
			position:{lat: lOPD[i].OPD_DeliveryLat, lng: lOPD[i].OPD_DeliveryLng},
			icon:"https://www.google.com/mapfiles/marker_green.png"
			});
		marker_tmp2.setMap(map);
		marker_tmp2.predious=marker_tmp;
		marker_tmp2.addListener('click',function(){
			if(setPointOrder[i].lenght>1){
				
			}else{
			var indexSelectBox=$("#shipperselect option:selected").index();
			if(status==0 &&  route[indexSelectBox].indexOf(this) ==-1){
			if(route[indexSelectBox].indexOf(this.predious)==-1) {
				alert("Điểm đầu chưa được chọn!!");
			}else{
			route[indexSelectBox].push(this);
			routeLatLng[indexSelectBox].push({
				lat:this.getPosition().lat(),
				lng:this.getPosition().lng()
			});
			routePath[indexSelectBox].setMap(null);
			routePath[indexSelectBox] = new google.maps.Polyline({
				path: routeLatLng[indexSelectBox],
				strokeColor: lShipper[indexSelectBox].color,
			    strokeOpacity: 1.0,
			    strokeWeight: 5,
			});
			routePath[indexSelectBox].setMap(map);
			}}  else if(status==1 && route[indexSelectBox].indexOf(this) !=-1){
				var indexInRoute=route[indexSelectBox].indexOf(this);
				route[indexSelectBox].splice(indexInRoute,1);
				routeLatLng[indexSelectBox].splice(indexInRoute,1);
				routePath[indexSelectBox].setMap(null);
				routePath[indexSelectBox] = new google.maps.Polyline({
					path: routeLatLng[indexSelectBox],
					strokeColor: lShipper[indexSelectBox].color,
				    strokeOpacity: 1.0,
				    strokeWeight: 5,
				});
				routePath[indexSelectBox].setMap(map);
				
			} 
			updateDistance();
			
			saveData();
			}
		}); 
		marker.push(marker_tmp2); */
	}
}
function randomColor( p1,p2,p2){
	p1=Math.floor((Math.random() * 85));
	p2=Math.floor((Math.random() * 255));
	p3=Math.floor((Math.random() * 255));
	return [p1,p2,p3];
}
function resetRoute(){
	var indexSelectBox=$("#shipperselect option:selected").index();
	lShipper[indexSelectBox].SHP_currentQuantity=0;
	route[indexSelectBox]=[route[indexSelectBox][0]];
	routeLatLng[indexSelectBox]=[routeLatLng[indexSelectBox][0]];
	routePath[indexSelectBox].setMap(null);
	
	for(var i=0;i<lOPD.length;i++){
		remainOrder[i][indexSelectBox]=0;
	}
	
	updateDistance();
	makeRightPanel();
}

function getDistanceGoogleMap(p1,p2,indexOld,index){
	serviceDistance.getDistanceMatrix(
			  {
			    origins: [p1],
			    destinations: [p2],
			    travelMode: 'DRIVING',
			    unitSystem: google.maps.UnitSystem.METRIC,
			    avoidHighways: false,
			    avoidTolls: false,
			  }, function(response,status){
				  if(status!=='OK'){
					  alert("Fail!");
					  return null;
				  }else{
					  distanceMatrix[indexOld][index]={
							  duration: response.rows[0].elements[0].duration.value,
							  distance: response.rows[0].elements[0].distance.value
					  } 
					  resCount++;
				  }
			  });
}

function updateDistance(){

	var indexSelectBox=$("#shipperselect option:selected").index();
	
	reqCount=0;
	resCount=0;
	for(k=0;k<lShipper.length;k++ ){
	for(i=1;i<routeLatLng[k].length;i++){
		var index=marker.indexOf(route[k][i].marker);
		var indexOld=marker.indexOf(route[k][i-1].marker);
		
		if(distanceMatrix[indexOld]== undefined ) distanceMatrix[indexOld]=[];
		if(distanceMatrix[indexOld][index]== undefined ){
			reqCount++;
			getDistanceGoogleMap(routeLatLng[k][i-1],routeLatLng[k][i],indexOld,index);
		}
	}
	}
	xd=true;
	wait();
	
}

function wait(){
	if(resCount!=reqCount){
		setTimeout(wait, 200);
	} else if(xdWait==true){
		pushDistance();
		makeRightPanel();
		xdWaint=false;
	}
}
function pushDistance(){
	var indexSelectBox=$("#shipperselect option:selected").index();
	var distanceKm=0;
	var distanceTime=0;
	for(i=1;i<routeLatLng[indexSelectBox].length;i++){
		var index=marker.indexOf(route[indexSelectBox][i].marker);
		var indexOld=marker.indexOf(route[indexSelectBox][i-1].marker);
		distanceKm+=distanceMatrix[indexOld][index].distance;
		distanceTime+=distanceMatrix[indexOld][index].duration;
	}
	var cellsOfShipper=document.getElementById("listShipperRoute").rows[indexSelectBox+1].cells;
	cellsOfShipper[2].innerHTML=distanceKm ;
	cellsOfShipper[3].innerHTML=moment.duration(distanceTime,'seconds').days() + "ngày "+ moment.duration(distanceTime,'seconds').hours()+" giờ "+moment.duration(distanceTime,'seconds').minutes()+"phút";
}
</script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4&callback=initMap">
</script>
<!-- /#page-wrapper -->
