<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Home</title>

</head>
<body>


	<jsp:include page="/WEB-INF/pages/template.jsp">
		<jsp:param value="Basic Column Chart" name="pageTitle"/>
		<jsp:param value="<div id='chartContainer'/>" name="content"/>
	</jsp:include>


	<%
	Gson gsonObj = new Gson();
	Map<Object,Object> map = null;
	List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();
	
	map = new HashMap<Object,Object>(); map.put("x", 10); map.put("y", 71); list.add(map);
	map = new HashMap<Object,Object>(); map.put("x", 20);  map.put("y", 55); list.add(map);
	map = new HashMap<Object,Object>(); map.put("x", 30);  map.put("y", 50); list.add(map);
	map = new HashMap<Object,Object>(); map.put("x", 40);  map.put("y", 65); list.add(map);
	map = new HashMap<Object,Object>(); map.put("x", 50);  map.put("y", 95); list.add(map);
	map = new HashMap<Object,Object>(); map.put("x", 60);  map.put("y", 68); list.add(map);
	map = new HashMap<Object,Object>(); map.put("x", 70);  map.put("y", 28); list.add(map);
	map = new HashMap<Object,Object>(); map.put("x", 80);  map.put("y", 34); list.add(map);
	map = new HashMap<Object,Object>(); map.put("x", 90);  map.put("y", 14); list.add(map);	
	String dataPoints = gsonObj.toJson(list);
	%>

	<script type="text/javascript">
	$(function () {
		var chart = new CanvasJS.Chart("chartContainer", {
			theme: "theme2",
			animationEnabled: true,
			title: {
				text: "Simple Column Chart in JSP"
			},
			subtitles: [
				{ text: "Try Resizing the Browser" }
			],
			data: [{
				type: "column", //change type to bar, line, area, pie, etc
				dataPoints: <%out.print(dataPoints);%>
			}]
		});
		chart.render();
	});
	</script>
</body>
</html>