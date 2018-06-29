<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 
<%@ page session="false"%>

<!DOCTYPE html>
<html lang="en">
    <head>
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
    	<script src="<c:url value="/assets/libs/jquery/dist/jquery.min.js"/>"></script>    
	    
	    <!-- Metis Menu Plugin JavaScript -->
	    <script src="<c:url value="/assets/libs/metisMenu/dist/metisMenu.min.js"/>"></script>
	    
	    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href='http://fonts.googleapis.com/css?family=Love+Ya+Like+A+Sister' rel='stylesheet' type='text/css'>
		<style type="text/css">
			body {
				font-family: 'Love Ya Like A Sister', cursive;
			}
			
			body {
				background: #eaeaea;
			}
			
			.wrap {
				margin: 0 auto;
				width: 1000px;
			}
			
			.logo {
				text-align: center;
				margin-top: 200px;
			}
			
			.logo img {
				width: 350px;
			}
			
			.logo p {
				color: #272727;
				font-size: 40px;
				margin-top: 1px;
			}
			
			.logo p span {
				color: lightgreen;
			}
			
			.sub a {
				color: #fff;
				background: #272727;
				text-decoration: none;
				padding: 10px 20px;
				font-size: 13px;
				font-family: arial, serif;
				font-weight: bold;
				-webkit-border-radius: .5em;
				-moz-border-radius: .5em;
				-border-radius: .5em;
			}
			
			.footer {
				color: black;
				position: absolute;
				right: 10px;
				bottom: 10px;
			}
			
			.footer a {
				color: rgb(114, 173, 38);
			}
		</style>
	    
    </head>   
    <body>
    	<div id="wrapper">
	        <tiles:insertAttribute name="content" />
        </div>
    	<!-- /#wrapper -->
    	
	    <!-- Bootstrap Core JavaScript -->
	    <script src="<c:url value="/assets/libs/bootstrap/dist/js/bootstrap.js"/>"></script>
	
	    <!-- Custom Theme JavaScript -->
	    <script src="<c:url value="/assets/js/sb-admin-2.js"/>"></script>
	    
		 		
  		<!-- Set base url -->     
        <script>
            var baseUrl = '${baseUrl}';
            var assetsUrl = '${assetsUrl}';
        </script>
    </body>
</html>