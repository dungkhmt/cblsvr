<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Stacked Bar Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>
<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "India"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 5);  map.put("label", "US"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "Germany"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 6);  map.put("label", "Brazil"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 7);  map.put("label", "China"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 5);  map.put("label", "Australia"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 5);  map.put("label", "France"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 7);  map.put("label", "Italy"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 9);  map.put("label", "Singapore"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 8);  map.put("label", "Switzerland"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 12);  map.put("label", "Japan"); list.add(map);	
String dataPoints1 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 0.5);  map.put("label", "India"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 1.5);  map.put("label", "US"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 1);  map.put("label", "Germany"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 2);  map.put("label", "Brazil"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 2);  map.put("label", "China"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 2.5);  map.put("label", "Australia"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 1.5);  map.put("label", "France"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 1);  map.put("label", "Italy"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 2);  map.put("label", "Singapore"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 2);  map.put("label", "Switzerland"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "Japan"); list.add(map);	
String dataPoints2 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 2);  map.put("label", "India"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "US"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "Germany"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "Brazil"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 4);  map.put("label", "China"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "Australia"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 4.5);  map.put("label", "France"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 4.5);  map.put("label", "Italy"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 6);  map.put("label", "Singapore"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "Switzerland"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 6);  map.put("label", "Japan"); list.add(map);	
String dataPoints3 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 2);  map.put("label", "India"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3);  map.put("label", "US"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 6);  map.put("label", "Germany"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 4);  map.put("label", "Brazil"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 4);  map.put("label", "China"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 8);  map.put("label", "Australia"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 8);  map.put("label", "France"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 8);  map.put("label", "Italy"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 4);  map.put("label", "Singapore"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 11);  map.put("label", "Switzerland"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 6);  map.put("label", "Japan"); list.add(map);	
String dataPoints4 = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           title: {
               text: "Cost Of Pancake Ingredients, 2011"
           },
           animationEnabled: true,
           axisY2: {
               valueFormatString: "$ 0",
           },
           toolTip: {
               shared: true
           },
           legend: {
               verticalAlign: "top",
               horizontalAlign: "center"
           },

           data: [
           {
               type: "stackedBar",
               showInLegend: true,
               name: "Butter (500gms)",
               axisYType: "secondary",
               color: "#7E8F74",
               dataPoints: <%out.print(dataPoints1);%>
           },
           {
               type: "stackedBar",
               showInLegend: true,
               name: "Flour (1kg)",
               axisYType: "secondary",
               color: "#F0E6A7",
               dataPoints: <%out.print(dataPoints2);%>
           },
           {
               type: "stackedBar",
               showInLegend: true,
               name: "Milk (2l)",
               axisYType: "secondary",
               color: "#EBB88A",
               dataPoints: <%out.print(dataPoints3);%>
           },
           {
               type: "stackedBar",
               showInLegend: true,
               name: "Eggs (20)",
               axisYType: "secondary",
               color: "#DB9079",
               indexLabel: "$#total",
               indexLabelPlacement: "outside",
               dataPoints: <%out.print(dataPoints4);%>
           }
           ]
       });
       chart.render();
   });
</script>