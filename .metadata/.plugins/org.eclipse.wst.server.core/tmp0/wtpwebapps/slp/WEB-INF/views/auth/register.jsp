<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
	
	<title>Đăng ký</title>
    
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-lg-12">
				<div class="page-header">
					<h2>Đăng ký tài khoản</h2>
				</div>
			</div>
			<!-- /.col-lg-12 -->
		</div>
		<!-- /.row -->
		<form:form id="form" class="form-horizontal" method="POST" commandName="userForm" action="${baseUrl}/auth/register">
			<div class="row">
				<div class="has-error col-md-7">
					<span class='help-block form-error' style="text-align:center;color:red;">${status}</span>	
				</div>
			</div>
			<div class="row" style="margin-top:20px;">                   
				<div class="form-group">
			    	<div class="col-md-2">
			        	<label for="Name">Tên đăng nhập</label>
			        </div>
			        <div class="col-md-5">
			            <form:input path="username" id="username" class="form-control" name="username" type="text"></form:input>
			            <span style="color:red;"><form:errors path="username"></form:errors></span>
			        </div>
			    </div>
			</div>
			<div class="row">                   
				<div class="form-group">
			    	<div class="col-md-2">
			        	<label for="Name">Mật khẩu</label>
			        </div>
			        <div class="col-md-5">
			            <form:input path="password" id="password" class="form-control" name="password" type="password"></form:input>
			            <span style="color:red;"><form:errors path="password"></form:errors></span>
			        </div>
			    </div>
			</div>
			<div class="row">                   
				<div class="form-group">
			    	<div class="col-md-2">
			        	<label for="Name">Xác nhận mật khẩu</label>
			        </div>
			        <div class="col-md-5">
			            <form:input path="repassword" id="repassword" class="form-control" name="repassword" type="password"></form:input>
			            <span style="color:red;"><form:errors path="repassword"></form:errors></span>
			        </div>
			    </div>
			</div>
			
			<div class="row">                   
				<div class="form-group">
			    	<div class="col-md-2 col-md-offset-2">
			        	<div class="btn btn-success" id="save_button">Register</div>
			        	<div class="btn btn-primary" id="cancel_button">Back</div>
			        </div>        
			    </div>
			</div>
		</form:form>
	</div>
	<!-- jQuery -->
	<script src="<c:url value="/assets/libs/jquery/dist/jquery.min.js" />" ></script>
	
	<!-- Bootstrap Core JavaScript -->
	<script src="<c:url value="/assets/libs/bootstrap/dist/js/bootstrap.min.js" />" ></script>
	
	<!-- Metis Menu Plugin JavaScript -->
	<script src="<c:url value="/assets/libs/metisMenu/dist/metisMenu.min.js" />" ></script>
	
	<!-- Custom Theme JavaScript -->
	<script src="<c:url value="/assets/js/sb-admin-2.js" />" ></script>
	
	<script>
	$(document).ready(function() {
		$("#username").val('${username}');
		$("#password").val('${password}');
		$("#repassword").val('${repassword}');
	});
	
	$("#save_button").click(function () {    
		$("#form").submit();  
	});
	$("#cancel_button").click(function () {    
		window.location = '${baseUrl}'+"/auth/login.html";   
	});	
	</script>
</body>
</html>

