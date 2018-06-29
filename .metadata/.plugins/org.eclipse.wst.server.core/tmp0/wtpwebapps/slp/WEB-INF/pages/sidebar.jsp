<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div id="sidebar" class="sidebar-toggle">
	<ul class="nav nav-sidebar">
		<li><a href="${pageContext.request.contextPath}"> <i class="fa fa-home" aria-hidden="true"></i>
				<span>HOME</span>
		</a></li>
		<li role="separator" class="divider"></li>
		<!-- chart types -->
		<li data-toggle="collapse" href="#chart-types" aria-expanded="true"
			aria-controls="chart-types"><a href="#"> <i
				class="fa fa-area-chart" aria-hidden="true"></i><span>CHART TYPES</span>
		</a></li>
		<li>
			<ul id="chart-types" class="sub-menu collapse ${fn:contains(pageContext.request.requestURI,'chart-types') ? 'in' : ''}">
				<li><a href="${pageContext.request.contextPath}/chart-types/column.jsp">Column Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/line.jsp">Line Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/step-line.jsp">Step Line Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/spline.jsp">Spline Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/step-area.jsp">Step Area Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/area.jsp">Area Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/spline-area.jsp">Spline Area Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/bar.jsp">Bar Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/pie.jsp">Pie Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/doughnut.jsp">Doughnut Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/bubble.jsp">Bubble Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/scatter.jsp">Scatter Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/stacked-column.jsp">Stacked Column Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/stacked-column100.jsp">Stacked Column 100% Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/stacked-area.jsp">Stacked Area Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/stacked-area100.jsp">Stacked Area 100% Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/stacked-bar.jsp">Stacked Bar Chart</a></li>
				<li><a href="${pageContext.request.contextPath}/chart-types/stacked-bar100.jsp">Stacked Bar 100% Chart</a></li>
<%-- 				<li><a href="${pageContext.request.contextPath}/chart-types/column.jsp">Range Column Chart</a></li> --%>
<%-- 				<li><a href="${pageContext.request.contextPath}/chart-types/column.jsp">Range Bar Chart</a></li> --%>
<%-- 				<li><a href="${pageContext.request.contextPath}/chart-types/column.jsp">Range Area Chart</a></li> --%>
<%-- 				<li><a href="${pageContext.request.contextPath}/chart-types/column.jsp">Range Spline Area Chart</a></li> --%>
<%-- 				<li><a href="${pageContext.request.contextPath}/chart-types/ohlc.jsp">OHLC Chart</a></li> --%>
<%-- 				<li><a href="${pageContext.request.contextPath}/chart-types/candlestick.jsp">Candlestick Chart</a></li> --%>
			</ul>
		</li>
		<!-- /chart types -->
		<li role="separator" class="divider"></li>
		<!--  features -->
		<li data-toggle="collapse" href="#features" aria-expanded="false"
			aria-controls="features"><a href="#"> <i class="fa fa-flask"
				aria-hidden="true"></i> <span>FEATURES</span>
		</a></li>
		<li>
			<ul id="features" class="sub-menu collapse ${fn:contains(pageContext.request.requestURI,'features') ? 'in' : ''}">
				<li><a href="${pageContext.request.contextPath}/features/zooming-panning.jsp">Zooming &amp; Panning</a></li>
				<li><a href="${pageContext.request.contextPath}/features/export-chart.jsp">Export Chart as Image</a></li>
				<li><a href="${pageContext.request.contextPath}/features/event-handling.jsp">Event Handling</a></li>
				<li><a href="${pageContext.request.contextPath}/features/log-axis.jsp">Logarithmic Axis</a></li>
				<li><a href="${pageContext.request.contextPath}/features/date-time.jsp">Date-Time Axis</a></li>
				<li><a href="${pageContext.request.contextPath}/features/reverse-axis.jsp">Reverse Axis</a></li>
				<li><a href="${pageContext.request.contextPath}/features/striplines.jsp">StripLines</a></li>
				<li><a href="${pageContext.request.contextPath}/features/multiple-y-axis.jsp">Multiple Y Axis</a></li>
				<li><a href="${pageContext.request.contextPath}/features/multi-series-chart.jsp">Multiseries Charts</a></li>
			</ul>
		</li>
		<!--  /features -->
		<li role="separator" class="divider"></li>
		<!--  how to  -->
		<li data-toggle="collapse" href="#how-to" aria-expanded="false"
			aria-controls="how-to"><a href="#"> <i class="fa fa-wrench"
				aria-hidden="true"></i> <span>HOW TO</span>
		</a></li>
		<li>
			<ul id="how-to" class="sub-menu collapse ${fn:contains(pageContext.request.requestURI,'how-to') ? 'in' : ''}">
				<li><a href="${pageContext.request.contextPath}/how-to/enable-disable-ds.jsp">Hide / Show DataSeries</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/live-chart.jsp">Dynamic / Live Chart</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/sync-multiple-charts.jsp">Sync Multiple Charts</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/datapoints-from-user-input.jsp">Add Data Points From User Input</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/multiple-charts-in-a-page.jsp">Multiple Charts in a Page</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/play-stop-live-chart.jsp">Play / Stop Live Chart</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/create-chart-from-json.jsp">Charts using JSON &amp; AJAX</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/create-chart-from-xml.jsp">Charts using XML &amp; AJAX</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/create-chart-from-csv.jsp">Charts using CSV &amp; AJAX</a></li>
                <li><a href="${pageContext.request.contextPath}/how-to/data-from-database.jsp">Render Data From Database</a></li>
			</ul>
		</li>
		<!--  /how to  -->
		<li role="separator" class="divider"></li>
	</ul>
</div>