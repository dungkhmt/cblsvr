<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 
<html>
<head>
	<title><tiles:insertAttribute name="title"/></title>
	
	<!-- Bootstrap Core CSS -->
	<link href="<c:url value="/assets/libs/bootstrap/dist/css/bootstrap.css" />" rel="stylesheet" type="text/css" media="all" />
	
	 <!-- MetisMenu CSS -->
	 <link href="<c:url value="/assets/libs/metisMenu/dist/metisMenu.css" />" rel="stylesheet" type="text/css" media="all" />
	
	 <!-- Timeline CSS -->
	 <link href="<c:url value="/assets/css/timeline.css" />" rel="stylesheet" type="text/css" media="all" />
	
	
	<!-- Custom CSS -->
	<link href="<c:url value="/assets/css/sb-admin-2.css" />" rel="stylesheet" type="text/css" media="all" />
	
	<!-- Custom Fonts -->
	<link href="<c:url value="/assets/libs/font-awesome/css/font-awesome.css" />" rel="stylesheet" type="text/css" media="all">
	
	<!-- jQuery -->
   	<script src="<c:url value="/assets/libs/jquery/dist/jquery.js"/>"></script>
   	
   	<!-- Bootstrap Core JavaScript -->
	<script src="<c:url value="/assets/libs/bootstrap/dist/js/bootstrap.min.js"/>"></script>
	
	<!-- Custom Theme JavaScript -->
	<script src="<c:url value="/assets/js/sb-admin-2.js"/>"></script>
	
	<!-- Metis Menu Plugin JavaScript -->
	<script src="<c:url value="/assets/libs/metisMenu/dist/metisMenu.min.js"/>"></script>
   	    	
</head>
 
<body>
	<div class="wrapper"> 
		<tiles:insertAttribute name="header"/>
		<tiles:insertAttribute name="content"/>
		<tiles:insertAttribute name="footer"/>	
	</div>
	
	<script type="text/javascript">
		var baseUrl = '${baseUrl}';
		var assetsUrl = '${assetsUrl}';
	</script>
</body>
</html>
