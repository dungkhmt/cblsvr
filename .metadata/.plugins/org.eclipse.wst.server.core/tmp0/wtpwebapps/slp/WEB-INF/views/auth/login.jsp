<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<!-- Bootstrap Core CSS -->
    <link href="<c:url value="/assets/libs/bootstrap/dist/css/bootstrap.min.css" />" rel="stylesheet" type="text/css" media="all" />

    <!-- MetisMenu CSS -->
    <link href="<c:url value="/assets/libs/metisMenu/dist/metisMenu.min.css" />" rel="stylesheet" type="text/css" media="all" />

    <!-- Custom CSS -->
    <link href="<c:url value="/assets/css/sb-admin-2.css" />" rel="stylesheet" type="text/css" media="all" />

    <!-- Custom Fonts -->
    <link href="<c:url value="/assets/libs/font-awesome/css/font-awesome.min.css" />" rel="stylesheet" type="text/css" type="text/css" media="all">
	
	<title>Đăng nhập</title>
</head>
<body>
	<div class="row">
		<div class="col-md-4 col-md-offset-4">
			<div class="login-panel panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Thông tin đăng nhập</h3>
				</div>
				<!-- /.panel-heading -->
				<div class="panel-body">
					<form class="form login-form" action="<c:url value="/j_spring_security_check" />" method="post">
						<c:if test="${status != null}">
							<div class="alert-success">
								${status}
							</div>
						</c:if>
						<div class="has-error">
							<span class='help-block form-error' style="text-align:center;color:red;">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</span>	
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="j_username" id="j_username" value="${param.j_username}" placeholder="Tên đăng nhập"/>
						</div>               
						<div class="form-group">
							<input type="password" class="form-control" name="j_password" id="j_password" value="${param.j_password }" placeholder="Mật khẩu"/>
						</div>              
						<div class="form-group">
							<button type="submit" class="btn btn-success" value="submit">Login</button>
						    <span>If you don't have an account, <a href="${baseUrl}/auth/register.html">Register</a></span>
						</div>
					</form>
				</div>
				<!-- /.panel-body -->
			</div>
			<!-- /.login-panel -->
		</div>
		<!-- /.col-md-4 -->
	</div>
	
	<!-- jQuery -->
	<script src="<c:url value="/assets/libs/jquery/dist/jquery.min.js" />" ></script>
	
	<!-- Bootstrap Core JavaScript -->
	<script src="<c:url value="/assets/libs/bootstrap/dist/js/bootstrap.min.js" />" ></script>
	
	<!-- Metis Menu Plugin JavaScript -->
	<script src="<c:url value="/assets/libs/metisMenu/dist/metisMenu.min.js" />" ></script>
	
	<!-- Custom Theme JavaScript -->
	<script src="<c:url value="/assets/js/sb-admin-2.js" />" ></script>
	<c:if test="${status != null}">
	<script>
	$(document).ready(function() {
		$("#j_username").val('${username}');
		$("#j_password").val('${password}');	
		
	});
	</script>
	</c:if>
</body>
</html>
