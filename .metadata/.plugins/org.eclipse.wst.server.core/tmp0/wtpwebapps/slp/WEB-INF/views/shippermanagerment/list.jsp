<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!-- DataTables CSS -->
<link href="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.css"/>" rel="stylesheet">

<!-- DataTables Responsive CSS -->
<link href="<c:url value="/assets/libs/datatables-responsive/css/dataTables.responsive.css" />" rel="stylesheet">

<!-- DataTables JavaScript -->
<script src="<c:url value="/assets/libs/datatables/media/js/jquery.dataTables.js"/>"></script>
<script src="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.js"/>"></script>

<div id = "page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Chức năng quản lý người giao hàng</h1>
		</div>	
	</div>
	<div class = "row">
		<div class="col-sm-offset-11 col-sm-1">
			<button type="button" class="btn btn-primary addShipper">Thêm</button>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-heading">Danh sách người giao hàng:<b id=batchCodeInfo></b></div>
				<div class="panel-body">
				 <div class="dataTable-wrapper">
				 	<table class="table table-striped table-bordered table-hover" id="dataTables-shipper">
				 		<thead>
				 			<tr>
				 				<th>Mã người giao hàng</th>
				 				<th>Địa chỉ kho hàng</th>
				 				<th>Kinh độ kho hàng</th>
				 				<th>Vĩ độ kho hàng</th>
				 				<th>Vị trí hiện tại</th>
				 				<th>Thời gian cập nhật cuối</th>
				 				<th>Status Code</th>
				 				<th>Loại phương tiện</th>
				 				<th>Khả năng vận chuyển 1</th>
				 				<th>Khả năng vận chuyển 2</th>
				 				<th>Mã công ty</th>
				 				<th>Tài khoản</th>
				 				<th></th>
				 				<th></th>
				 				
				 			</tr>
				 			
				 		</thead>
				 		<tbody>
				 			<c:forEach items = "${shipper}" var = "ship">
				 				<tr>
				 					<td><c:out value="${ship.SHP_Code}"></c:out></td>
				 					<td><c:out value="${ship.SHP_DepotAddress}"></c:out></td>
				 					<td><c:out value="${ship.SHP_DepotLat}"></c:out></td>
				 					<td><c:out value="${ship.SHP_DepotLng}"></c:out></td>
				 					<td><c:out value="${ship.SHP_CurrentLocation}"></c:out></td>
				 					<td><c:out value="${ship.SHP_LastUpdateDateTime}"></c:out></td>
				 					<td><c:out value="${ship.SHP_StatusCode}"></c:out></td>
				 					<td><c:out value="${ship.SHP_VehicleType}"></c:out></td>
				 					<td><c:out value="${ship.SHP_Capacity_1}"></c:out></td>
				 					<td><c:out value="${ship.SHP_Capacity_2}"></c:out></td>
				 					<td><c:out value="${ship.SHP_Customer_Code}"></c:out></td>
				 					<td><c:out value="${ship.SHP_User_Name}"></c:out></td>
				 					<td>
				 						<button type="button" onclick="editShip('${ship.SHP_Code}')"
    									class="btn btn-info btn-edit" title="Edit">Sửa</button>
				 					</td>
				 					<td>
				 						<button type="button" onclick="deleteShip('${ship.SHP_Code}')"
    									class="btn btn-info btn-delete" title="Delete">Xóa</button>
				 					</td>
				 				</tr>
				 			</c:forEach>
				 		</tbody>
				 		
				 	</table>
				 </div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function(){
	$('.addShipper').click(function(){
		window.location = '${baseUrl}' + "/shippermanagerment/add-a-shipper.html";
	});
});	
</script>

<script>
function editShip(shipCode){
	console.log("shipcode::"+shipCode);
	window.location = '${baseUrl}'+"/shippermanagerment/edit-a-shipper/"+shipCode;
}
function deleteShip(shipCode){
	window.location = '${baseUrl}'+"/shippermanagerment/del-a-shipper/"+shipCode;
}	
</script>
