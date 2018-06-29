<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Scatter Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("x", 800);  map.put("y", 350); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 900);  map.put("y", 450); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 850);  map.put("y", 450); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1250);  map.put("y", 700); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1100);  map.put("y", 650); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1350);  map.put("y", 850); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1200);  map.put("y", 900); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1410);  map.put("y", 1250); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1250);  map.put("y", 1100); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1400);  map.put("y", 1150); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1500);  map.put("y", 1050); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1330);  map.put("y", 1120); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1580);  map.put("y", 1220); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1620);  map.put("y", 1400); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1250);  map.put("y", 1450); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1350);  map.put("y", 1600); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1650);  map.put("y", 1300); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1700);  map.put("y", 1620); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1750);  map.put("y", 1700); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1830);  map.put("y", 1800); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1900);  map.put("y", 2000); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2050);  map.put("y", 2200); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2150);  map.put("y", 1960); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2250);  map.put("y", 1990); list.add(map);	
String dataPoints = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           zoomEnabled: true,
           zoomType: "xy",
           title: {
               text: "Real Estate Rates",
           },
           subtitles: [
               {
                   text: "Try Zooming and Panning"
               }
           ],
           animationEnabled: true,
           axisX: {
               title: "Square Feets",
           },
           axisY: {
               title: "Prices in USD",
               valueFormatString: "$#,##0k",
               lineThickness: 2,
               includeZero: false
           },
           data: [
		{
		    type: "scatter",
		    toolTipContent: "<span style='\"'color: {color};'\"'><strong>Area: </strong></span>{x} sq.ft<br/><span style='\"'color: {color};'\"'><strong>Price: </strong></span>{y} $ ",
		    dataPoints: <%out.print(dataPoints);%>
		}]
       });

       chart.render();
   });
</script>