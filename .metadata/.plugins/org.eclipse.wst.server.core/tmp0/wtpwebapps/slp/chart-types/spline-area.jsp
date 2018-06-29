<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Spline Area Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("x", 694204200000L);  map.put("y", 2506000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 725826600000L);  map.put("y", 2798000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 757362600000L);  map.put("y", 3386000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 788898600000L);  map.put("y", 6944000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 820434600000L);  map.put("y", 6026000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 852057000000L);  map.put("y", 2394000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 883593000000L);  map.put("y", 1872000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 915129000000L);  map.put("y", 2140000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 946665000000L);  map.put("y", 7289000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 978287400000L);  map.put("y", 4830000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1009823400000L);  map.put("y", 2009000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1041359400000L);  map.put("y", 2840000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1072895400000L);  map.put("y", 2396000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1104517800000L);  map.put("y", 1613000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1136053800000L);  map.put("y", 2821000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1167589800000L);  map.put("y", 2000000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1199125800000L);  map.put("y", 1397000); list.add(map);	
String dataPoints = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           title: {
               text: "Music Album Sales by Year"
           },
           animationEnabled: true,
           axisY: {
               title: "Units Sold",
               valueFormatString: "#0,,.",
               suffix: " m"
           },
           data: [{
		    toolTipContent: "{y} units",
		    type: "splineArea",
		    markerSize: 5,
		    xValueType: "dateTime",
		    color: "rgba(54,158,173,.7)",
		    dataPoints: <%out.print(dataPoints);%>
			}]
       });

       chart.render();
   });
</script>