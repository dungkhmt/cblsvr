<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Stacked Column 100% Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("y", 40);  map.put("label", "Cafeteria"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 10);  map.put("label", "Lounge"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 72);  map.put("label", "Games Room"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 30);  map.put("label", "Lecture Hall"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 21);  map.put("label", "Library"); list.add(map);	
String dataPoints1 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 20);  map.put("label", "Cafeteria"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 14);  map.put("label", "Lounge"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 40);  map.put("label", "Games Room"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 43);  map.put("label", "Lecture Hall"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 17);  map.put("label", "Library"); list.add(map);	
String dataPoints2 = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           animationEnabled: true,
           title: {
               text: "Number of Students in Each Room"
           },
           axisX: {
               title: "Rooms"
           },
           axisY: {
               title: "percentage"
           },
           data: [
           {
               type: "stackedColumn100",
               legendText: "Boys",
               showInLegend: "true",
               indexLabel: "#percent %",
               indexLabelPlacement: "inside",
               indexLabelFontColor: "white",
               dataPoints: <%out.print(dataPoints1);%>
           },
           {
               type: "stackedColumn100",
               legendText: "Girls",
               showInLegend: "true",
               indexLabel: "#percent %",
               indexLabelPlacement: "inside",
               indexLabelFontColor: "white",
               dataPoints: <%out.print(dataPoints2);%>
           }
           ]
       });
       chart.render();
   });
</script>
