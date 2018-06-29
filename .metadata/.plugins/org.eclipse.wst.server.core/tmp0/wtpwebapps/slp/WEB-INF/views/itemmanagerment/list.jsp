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
			<h1 class="page-header">Chức năng quản lý khách hàng</h1>
		</div>	
	</div>
	<div class = "row">
		<div class="col-sm-offset-11 col-sm-1">
			<button type="button" class="btn btn-primary addCustomer">Thêm</button>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-heading">Danh sách khách hàng:<b id=batchCodeInfo></b></div>
				<div class="panel-body">
				 <div class="dataTable-wrapper">
				 	<table class="table table-striped table-bordered table-hover" id="dataTables-customer">
				 		<thead>
				 			<tr>
				 				<th>Mã khách hàng</th>
				 				<th>Tên khách hàng</th>
				 				<th>Số điện thoại</th>
				 				<th>Địa chỉ</th>
				 				<th>Kinh độ</th>
				 				<th>Vĩ độ</th>
				 				<th></th>
				 				<th></th>
				 				
				 			</tr>
				 		</thead>
				 		<tbody>
				 			<c:forEach items="${customer}" var="cus">
				 				<tr>
				 					<td><c:out value="${cus.cus_Code}"></c:out></td>
				 					<td><c:out value="${cus.cus_Name}"></c:out></td>
				 					<td><c:out value="${cus.cus_Phone}"></c:out></td>
				 					<td><c:out value="${cus.cus_Address}"></c:out></td>
				 					<td><c:out value="${cus.cus_Lat}"></c:out></td>
				 					<td><c:out value="${cus.cus_Lng}"></c:out></td>
				 					<td>
    									<button type="button" onclick="editCustomer('${cus.cus_Code}')"
    									class="btn btn-info btn-edit" title="Edit">Sửa</button>
				 					</td>
				 					<td>
    									<button type="button" onclick="deleteCustomer('${cus.cus_Code}')"
    									class="btn btn-info btn-del" title="Delete">Xóa</button>
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
	$('.addCustomer').click(function(){
		window.location = '${baseUrl}' + "/customermanager/add-a-customer.html";
	});
});	
</script>

<script>
function editCustomer(customerCode){
	console.log("Customercode::"+customerCode);
	window.location = '${baseUrl}'+"/customermanager/edit-a-customer/"+customerCode;
}
function deleteCustomer(customerCode){
	window.location = '${baseUrl}'+"/customermanager/del-a-customer/"+customerCode;
}	
</script>
