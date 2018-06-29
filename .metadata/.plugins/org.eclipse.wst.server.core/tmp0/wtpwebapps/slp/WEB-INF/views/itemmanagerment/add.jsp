<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!-- Datatable CSS -->
<link href="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.css"/>" rel="stylesheet">

<!-- DataTables Responsive CSS -->
<link href="<c:url value="/assets/libs/datatables-responsive/css/dataTables.responsive.css" />" rel="stylesheet">

<!-- DataTables JavaScript -->
<script src="<c:url value="/assets/libs/datatables/media/js/jquery.dataTables.js"/>"></script>
<script src="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.js"/>"></script>
<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12 center">
			<h1 class="page-header">Thêm mới một khách hàng mới</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	
	<form:form action="${baseUrl}/customermanager/save-a-customer" id="form-add-cus"
		method="POST" commandName="cusForm" role="form"
		class="form-horizontal">
		<div class="row">
			<div class="panel panel-default">
				<div class="panel-body">
					<div class="form-group">
						<label class="control-label col-lg-3">Mã khách hàng</label>
						<div class="col-lg-6">
							<form:input path="Cus_Code" class="form-control" id="Cus_Code"
								name="Cus_Code" placeholder="nhập mã"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Tên khách hàng</label>
						<div class="col-lg-6">
							<form:input path="Cus_Name" class="form-control" id="Cus_Name"
								name="Cus_Name" placeholder="nhập tên"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Số điện thoại</label>
						<div class="col-lg-6">
							<form:input path="Cus_Phone" class="form-control" id="Cus_Phone"
								name="Cus_Phone" placeholder="nhập số điện thoại"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Địa chỉ</label>
						<div class="col-lg-6">
							<form:input path="Cus_Address" class="form-control" id="Cus_Address"
								name="Cus_Address" placeholder="nhập địa chỉ"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Kinh độ</label>
						<div class="col-lg-6">
							<form:input path="Cus_Lat" class="form-control" id="Cus_Lat"
								name="Cus_Lat" placeholder="nhập kinh độ"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Vĩ độ</label>
						<div class="col-lg-6">
							<form:input path="Cus_Lng" class="form-control" id="Cus_Lng"
								name="Cus_Lng" placeholder="nhập vĩ độ"></form:input>
						</div>
					</div>

					
				</div> 



			</div>

		</div>
		<form:button type="submit" class="btn btn-primary active" id="btnAddUser">Thêm
			khách hàng</form:button>
		<button type="reset" class="btn btn-primary cancel">Hủy</button>

	</form:form>
</div>

<script>
$(document).ready(function () {
	$('.cancel').click(function(){
		window.location = '${baseUrl}/customermanager';
	})
});
</script>