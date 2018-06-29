<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/assets/libs/jquery-ui-1.12.0/jquery-ui.css"/>" />
<script
	src="<c:url value="/assets/libs/jquery-ui-1.12.0/jquery-ui.js"/>">
	
</script>

<script type="text/javascript"
	src="<c:url value="/assets/libs/inputDate/dist/js/moment.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/assets/libs/inputDate/dist/js/bootstrap-datetimepicker.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/assets/libs/bootstrap/dist/js/collapse.js" />"></script>
<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12 center">
			<h1 class="page-header">Thêm mới một user</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
	<form:form action="${baseUrl}/usermanager/save-a-newUser" id="form-add-user"
		method="POST" commandName="userValidation" role="form"
		class="form-horizontal">
		<div class="row">
			<div class="panel panel-default">
				<div class="panel-body">

					<div class="form-group">
						<label class="control-label col-lg-3">Tên user</label>
						<div class="col-lg-6">
							<form:input path="username" class="form-control" id="username"
								name="username" placeholder="nhập tên"></form:input>
						</div>
					</div>

					<div class="form-group">
						<label class="control-label col-lg-3">Mật khẩu</label>
						<div class="col-lg-6">
							<form:input path="password" class="form-control" type="password"
								name="password"
								id="password" placeholder="mật khẩu"></form:input>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-lg-3">Nhập lại mật khẩu</label>
						<div class="col-lg-6">
							<form:input path="repassword" class="form-control"
								type="password" id="repassword" placeholder="nhập lại mật khẩu"></form:input>
						</div>
					</div>
					<div class ="form-group">
						<label class= "control-label col-lg-3">Chọn mã CODE</label>
						<div class ="col-lg-6">
							<select class="form-control">
							<option>
							<c:forEach items = "${rqi}" var ="mRQ">
							
								<td><c:out value ="${mRQ.REQBAT_Code}"/></td>
								
							</c:forEach>
							</option>
							</select>
						</div>
				</div> 



			</div>

		</div>
		<form:button type="submit" class="btn btn-primary active" id="btnAddUser">Thêm
			người dùng</form:button>
		<button type="reset" class="btn btn-default" id="btnCancel">Hủy
			bỏ</button>

	</form:form>
</div>

<script type="text/javascript">
	$(document).ready(function() {

		/*$('#btnAddUser').click(function() {
			alert("Add User");
		});*/

		$('#btnCancel').click(function() {
			alert("Cancel");
		});

	});
</script>


<script
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4&libraries=places&callback=initMapAuto"
	async defer>
	
</script>