<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${param.pageTitle}</title>
<link
	href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="${pageContext.request.contextPath}/assets/font-awesome/css/font-awesome.min.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/style.css"
	rel="stylesheet">
<!-- scripts -->
<script
	src="${pageContext.request.contextPath}/assets/script/jquery-3.1.0.min.js"
	type="text/javascript"></script>
<script
	src="${pageContext.request.contextPath}/assets/script/bootstrap.min.js"
	type="text/javascript"></script>
<script src="http://canvasjs.com/assets/script/canvasjs.min.js"
	type="text/javascript"></script>
<script>
	$(function() {
		// #sidebar-toggle-button
		$('#sidebar-toggle-button').on('click', function() {
			$('#sidebar').toggleClass('sidebar-toggle');
			$('#page-content-wrapper').toggleClass('page-content-toggle');
			fireResize();
		});

		// sidebar collapse behavior
		$('#sidebar').on('show.bs.collapse', function() {
			$('#sidebar').find('.collapse.in').collapse('hide');
		});

		// To make current link active
		(function addActiveToNestedList () {
			var pageURL = $(location).attr('href');
			var URLSplits = pageURL.split('/');
		
			
			var routeURL = '/' + URLSplits[URLSplits.length - 2] + '/' + URLSplits[URLSplits.length - 1];
			var activeNestedList = $(
					'.sub-menu > li > a[href$="' + routeURL + '"]').parent();
			
			if (activeNestedList.length !== 0
					&& !activeNestedList.hasClass('active')) {
				$('.sub-menu > li').removeClass('active');
				activeNestedList.addClass('active');
			}
		})();
		
		function fireResize() {
			if (document.createEvent) { // W3C
				var ev = document.createEvent('Event');
				ev.initEvent('resize', true, true);
				window.dispatchEvent(ev);
			} else { // IE
				element = document.documentElement;
				var event = document.createEventObject();
				element.fireEvent("onresize", event);
			}
		}

	})
</script>
</head>

<body>
	<!-- header -->
	<jsp:include page="header.jsp" />
	<!-- /header -->
	<!-- sidebar -->
	<jsp:include page="sidebar.jsp" />
	<!-- /sidebar -->
	<div id="page-content-wrapper" class="page-content-toggle">
		<div class="container-fluid">
			<h1>${param.pageTitle}</h1>

			<div class="row">
				<div id="content" class="col-md-8 col-md-offset-1 col-xs-12">
					${param.content}
				</div>
			</div>
			<!-- /row -->
		</div>
		<!-- footer -->
		<jsp:include page="/WEB-INF/pages/footer.jsp" />
		<!-- /footer -->
	</div>
</body>
</html>