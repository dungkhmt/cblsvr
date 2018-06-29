<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 
<html>
<head>
	<title><tiles:insertAttribute name="title"/></title>
	
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.2/css/bootstrap.min.css" integrity="sha384-y3tfxAZXuh4HwSYylfB+J125MxIs6mR5FOHamPBG064zB+AFeWH94NdvaCBm8qnd" crossorigin="anonymous">
	
	<!-- Bootstrap Core CSS -->
	<link href="<c:url value="/assets/libs/bootstrap/dist/css/bootstrap.css" />" rel="stylesheet" type="text/css" media="all" />
	
	<!-- Custom Fonts -->
	<link href="<c:url value="/assets/libs/font-awesome/css/font-awesome.css" />" rel="stylesheet" type="text/css" media="all">
	
	<!-- jQuery -->
   	<script src="<c:url value="/assets/libs/jquery/dist/jquery.js"/>"></script>
   	
   	<!-- Bootstrap Core JavaScript -->
	<script src="<c:url value="/assets/libs/bootstrap/dist/js/bootstrap.min.js"/>"></script>    	
</head>

<style>
a:hover{
	text-decoration:none;
}
img{
	opacity:1;
	transition-delay:0.3s;
}
body{
	background-color: #F9F9F9;
}
</style>

<body>
	<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
		<div class="navbar-header">
			<span class="navbar-brand">Hệ thống lập kế hoạch vận tải người và hàng hoá</span>
		</div>
		
		<ul class="nav navbar-top-links navbar-right">
			<li class="dropdown">
				<a class="drpdown-toggle" data-toggle="dropdown" href="#">
					${currentUser.username}
					<i class="fa fa-user fa-fw"></i>
					<i class="fa fa-caret-down"></i>
				</a>
				<ul class="dropdown-menu dropdown-user">
					<li><a href="<c:url value="/j_spring_security_logout" />"><i class="fa fa-sign-out fa-fw"> Đăng xuất</i></a></li>
				</ul>
			</li>
		</ul>
	</nav>
	
	<div class="container">
	    <div class="row">
	        <div class="col-lg-12">
	            <h1 class="page-header">Hệ thống lập kế hoạch vận tải người và hàng hoá</h1>
	        </div>
	        <!-- /.col-lg-12 -->
		</div>
		<!-- /.row -->
		
	    <div class="row">
	    		<c:forEach items="${functionParentPermissionList}" var="fPPL">
            		<div class="col-sm-4">
	    				<a href="<c:url value = "${fPPL.FUNC_Url}"/>">
		     			<div class="card">
		     				<div class="card-block">
		     					<h4 class="card-title"><b>${fPPL.FUNC_Name}</b></h4>
		     				</div>
		     				<!-- /.card-block -->
		     				<img class="card-img" style="height:200px;width:348px" src="<c:url value="${fPPL.FUNC_Icon}" />"/>
		     			</div>
		     			<!-- /.card -->
	     				</a>
	     			</div> 
                </c:forEach>
	     	<!-- /.col-sm-6(/manager) -->  
		</div>
		<!-- /.row -->
	</div>
	<!-- /.container -->

</body>
</html>