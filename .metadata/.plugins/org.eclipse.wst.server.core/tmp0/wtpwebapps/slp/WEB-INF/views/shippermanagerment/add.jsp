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
			<h1 class="page-header">Thêm mới một người giao hàng mới</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	
	<form:form action="${baseUrl}/shippermanagerment/save-a-shipper" id="form-add-shipper"
		method="POST" commandName="shipForm" role="form"
		class="form-horizontal">
		<div class="row">
			<div class="panel panel-default">
				<div class="panel-body">
					<div class="form-group">
						<label class="control-label col-lg-3">Mã người giao hàng</label>
						<div class="col-lg-6">
							<form:input path="SHP_Code" class="form-control" id="SHP_Code"
								name="SHP_Code" placeholder="nhập mã"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Địa chỉ kho hàng</label>
						<div class="col-lg-6">
							<form:input path="SHP_DepotAddress" class="form-control" id="SHP_DepotAddress"
								name="SHP_DepotAddress" placeholder="nhập địa chỉ kho hàng"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Kinh độ kho hàng</label>
						<div class="col-lg-6">
							<form:input path="SHP_DepotLat" class="form-control" id="SHP_DepotLat"
								name="SHP_DepotLat" placeholder="nhập số kinh độ kho hàng"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Vĩ độ kho hàng</label>
						<div class="col-lg-6">
							<form:input path="SHP_DepotLng" class="form-control" id="SHP_DepotLng"
								name="SHP_DepotLng" placeholder="nhập vĩ độ kho hàng"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Vị trí hiện tại</label>
						<div class="col-lg-6">
							<form:input path="SHP_CurrentLocation" class="form-control" id="SHP_CurrentLocation"
								name="SHP_CurrentLocation" placeholder="nhập vị trí"></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Thời gian cập nhật cuối</label>
						<div class="col-lg-6">
							<form:input path="SHP_LastUpdateDateTime" class="form-control" id="SHP_LastUpdateDateTime"
								name="SHP_LastUpdateDateTime" placeholder=""></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Status Code</label>
						<div class="col-lg-6">
							<form:input path="SHP_StatusCode" class="form-control" id="SHP_StatusCode"
								name="SHP_StatusCode" placeholder=""></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Loại phương tiện</label>
						<div class="col-lg-6">
							<form:input path="SHP_VehicleType" class="form-control" id="SHP_VehicleType"
								name="SHP_VehicleType" placeholder=""></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Khả năng vận chuyển 1</label>
						<div class="col-lg-6">
							<form:input path="SHP_Capacity_1" class="form-control" id="SHP_Capacity_1"
								name="SHP_Capacity_1" placeholder=""></form:input>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-lg-3">Khả năng vận chuyển 2</label>
						<div class="col-lg-6">
							<form:input path="SHP_Capacity_2" class="form-control" id="SHP_Capacity_2"
								name="SHP_Capacity_2" placeholder=""></form:input>
						</div>
					</div>

					
				</div>
				
				<div class="form-group ">
					<label class="control-label col-sm-2" for="customer">Chọn mã công ty</label>
					<div class="col-sm-2">
						<select class="form-control customer" path="SHP_Customer_Code" name="SHP_Customer_Code" id="SHP_Customer_Code">
							<option value="">Chọn mã công ty</option>
							<c:forEach items="${lstCustomer}" var="cus">
                                     	<option value="${cus.cus_Code}">${cus.cus_Name}</option>
                            </c:forEach>
                    
                    	</select>
                    </div>
				</div>	
				<div class="form-group ">
					<label class="control-label col-sm-2" for="user">Chọn tài khoản</label>
					<div class="col-sm-2">
						<select class="form-control user" path="SHP_User_Name" name="SHP_User_Name" id="SHP_User_Name" >
							<option value="">Chọn tài khoản</option>
							<c:forEach items="${lstUser}" var="u">
                                     	<option value="${u.id}">${u.username}</option>
                            </c:forEach>
                    
                    	</select>
                    </div>
				</div>	



			</div>

		</div>
		<form:button type="submit" class="btn btn-primary active" id="btnAddShipper">Thêm</form:button>
		<button type="reset" class="btn btn-primary cancel">Hủy</button>

	</form:form>
</div>

<script>
$(document).ready(function () {
	$('.cancel').click(function(){
		window.location = '${baseUrl}/shippermanagerment';
	})
});
</script>